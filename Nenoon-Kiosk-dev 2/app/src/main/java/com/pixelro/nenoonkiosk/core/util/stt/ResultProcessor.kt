package com.pixelro.nenoonkiosk.core.util.stt

import android.os.Bundle
import android.speech.SpeechRecognizer
import android.util.Log

class ResultProcessor {
    private val traceTag = "STT_TRACE"
    sealed class Action {
        data class Deliver(val result: String) : Action()
        data class Remember(val candidate: String) : Action()
        object None : Action()
    }

    var phraseHints: Set<String> = emptySet()
        set(value) {
            field = value
            digitHints = value.filter { it.all(Char::isDigit) }.flatMap { it.toList() }.filter { it.isDigit() }.toSet()
        }

    var phraseBoost: Int = SttConfig.DEFAULT_PHRASE_BOOST

    private var digitHints: Set<Char> = emptySet()

    private val defaultDigits = setOf('2', '3', '4', '5', '6', '7')

    private fun currentDigitSet(): Set<Char> = if (digitHints.isNotEmpty()) digitHints else defaultDigits

    private val phoneticToDigit: Map<String, String> = mapOf(
        "two" to "2", "three" to "3", "four" to "4", "five" to "5", "six" to "6",
        "seven" to "7", "eight" to "8", "nine" to "9",
        "ee" to "2", "eee" to "2",
        "sam" to "3",
        "sa" to "4", "sah" to "4",
        "o" to "5", "oh" to "5",
        "yuk" to "6", "yook" to "6",
        "chil" to "7",
        "pal" to "8",
        "gu" to "9", "goo" to "9"
    )

    fun selectBestResult(matches: List<String>, confidences: FloatArray?): String {
        if (matches.isEmpty()) return ""

        Log.d(traceTag, "selectBestResult matches=$matches confidences=${confidences?.contentToString()}")

        val digitSet = currentDigitSet()
        val koreanSinglesStrict = setOf("이", "삼", "사", "오", "육", "칠")
        val koreanSingles = setOf("이", "삼", "사", "오", "육", "칠", "둘", "셋", "넷", "다섯", "여섯", "일곱")
        fun score(candidate: String, idx: Int): Int {
            val trimmed = candidate.trim()
            var baseScore = 10

            val targetKoreanDigits = setOf("이", "삼", "사", "오", "육", "칠")
            when {
                trimmed.length == 1 && trimmed[0] in digitSet -> baseScore = 200
                trimmed in targetKoreanDigits -> baseScore = 180
                trimmed.any { it in digitSet } -> baseScore = 150
                koreanSinglesStrict.contains(trimmed) -> baseScore = 120
                trimmed.isNotEmpty() && trimmed.all { ch -> koreanSinglesStrict.contains(ch.toString()) } -> baseScore = 100
                koreanSingles.contains(trimmed) -> baseScore = 90
                koreanSingles.any { trimmed.contains(it) } -> baseScore = 70
            }

            val conf = confidences?.getOrNull(idx) ?: 0f
            val confMultiplier = when {
                trimmed.length == 1 && trimmed[0] in digitSet -> 250f
                trimmed in targetKoreanDigits -> 220f
                else -> 60f
            }
            var finalScore = baseScore + (conf * confMultiplier).toInt()

            if (phraseHints.isNotEmpty()) {
                val tokens = trimmed.replace("[\\p{Punct}]".toRegex(), " ")
                    .split("\\s+".toRegex()).filter { it.isNotBlank() }

                when {
                    tokens.any { phraseHints.contains(it) } || phraseHints.contains(trimmed) -> {
                        finalScore += phraseBoost
                    }
                    phraseHints.any { trimmed.contains(it) } -> {
                        finalScore += (phraseBoost / 2)
                    }
                    trimmed.isNotEmpty() && trimmed.all { ch -> koreanSinglesStrict.contains(ch.toString()) } -> {
                        val firstChar = trimmed.first().toString()
                        if (phraseHints.contains(firstChar)) {
                            finalScore += phraseBoost
                        }
                    }
                }
            }

            return finalScore
        }

        val bestIdx = matches.indices.maxByOrNull { idx -> score(matches[idx], idx) }
        val selected = bestIdx?.let { matches[it] } ?: ""
        Log.d(traceTag, "selectBestResult selected='$selected'")
        return selected
    }

    fun processPartialResults(
        bundle: Bundle?,
        currentState: SessionState,
    ): Action {
        if (currentState.deliveredInSession) {
            Log.d(traceTag, "processPartialResults skipped - already delivered")
            return Action.None
        }
        val matches = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
        val confidences = bundle?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

        Log.d(traceTag, "processPartialResults matches=$matches confidences=${confidences?.contentToString()}")

        if (matches.isEmpty()) {
            return currentState.lastPartialCandidate
                ?.let { normalizeCandidate(it) }
                ?.takeIf { it.isNotBlank() }
                ?.let { Action.Deliver(it) }
                ?: Action.None
        }

        currentState.resetNoMatch()

        val digitSet = currentDigitSet()
        val digitPattern = digitSet.joinToString(separator = "")
        val digitRegex = if (digitPattern.isNotEmpty()) Regex("[$digitPattern]") else Regex("[0-9]")
        val koreanStrictSet = setOf("이", "삼", "사", "오", "육", "칠")
        val koreanToDigit = mapOf(
            "이" to "2", "삼" to "3", "사" to "4", "오" to "5",
            "육" to "6", "칠" to "7"
        )

        val single = findSingleSyllableCandidate(matches, confidences, koreanStrictSet, koreanToDigit, digitSet)
        if (single != null) {
            val normalized = normalizeCandidate(single)
            if (normalized.isNotBlank()) {
                Log.d("ResultProcessor", "조기 확정(한 글자): '$normalized' from '$single'")
                Log.d(traceTag, "partial deliver (single) raw='$single' normalized='$normalized'")
                return Action.Deliver(normalized)
            }
        }

        matches.forEachIndexed { idx, candidate ->
            val normalized = normalizeCandidate(candidate)
            if (normalized.isNotBlank()) {
                Log.d(traceTag, "partial deliver (normalized) raw='${matches[idx]}' normalized='$normalized'")
                return Action.Deliver(normalized)
            }
        }

        Log.d(traceTag, "processPartialResults no early deliver")
        return Action.None
    }

    fun normalizeCandidate(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return trimmed

        val dontKnowKeys = listOf(
            "모르겠", "모름", "몰라", "안보여", "안 보여", "안보임", "안 보임",
            "안보입니다", "안 보입니다", "안보인다", "안 보인다"
        )

        dontKnowKeys.forEach { key ->
            if (trimmed.contains(key)) {
                return logNormalize("dontKnow", "안 보임")
            }
        }

        val koreanStrictSet = setOf("이", "삼", "사", "오", "육", "칠")
        val koreanToDigit = mapOf(
            "이" to "2", "삼" to "3", "사" to "4", "오" to "5",
            "육" to "6", "칠" to "7",
            "둘" to "2", "셋" to "3", "넷" to "4", "다섯" to "5", "여섯" to "6", "일곱" to "7"
        )
        val tokens = trimmed.replace("[\\p{Punct}]".toRegex(), " ")
            .split("\\s+".toRegex()).filter { it.isNotBlank() }
        val allowedDigits = currentDigitSet()

        tokens.firstOrNull { phoneticToDigit.containsKey(it.lowercase()) }
            ?.let {
                val d = phoneticToDigit[it.lowercase()]!!
                if (d.first() in allowedDigits) return logNormalize("phoneticToken", d)
            }

        if (phoneticToDigit.containsKey(trimmed.lowercase())) {
            val d = phoneticToDigit[trimmed.lowercase()]!!
            if (d.first() in allowedDigits) return logNormalize("phoneticFull", d)
        }

        val latinTokens = tokens.filter { it.matches(Regex("[A-Za-z]+")) }.map { it.lowercase() }
        if (latinTokens.isNotEmpty()) {
            val keys = phoneticToDigit.keys
            for (tok in latinTokens) {
                var bestKey: String? = null
                var bestDist = Int.MAX_VALUE
                for (k in keys) {
                    val dist = editDistance(tok, k)
                    if (dist < bestDist) {
                        bestDist = dist
                        bestKey = k
                    }
                    if (bestDist == 0) break
                }
                if (bestKey != null && bestDist <= 2) {
                    val d = phoneticToDigit[bestKey]!!
                    if (d.first() in allowedDigits) return logNormalize("latinFuzzy", d)
                }
            }
        }

        tokens.firstOrNull { koreanToDigit.containsKey(it) }
            ?.let {
                val d = koreanToDigit[it]!!
                if (d.first() in allowedDigits) return logNormalize("koreanToken", d)
            }

        tokens.firstOrNull { token ->
            token.isNotEmpty() && koreanStrictSet.contains(token.first().toString())
        }?.let { token ->
            val firstChar = token.first().toString()
            if (koreanToDigit.containsKey(firstChar)) {
                val d = koreanToDigit[firstChar]!!
                if (d.first() in allowedDigits) return logNormalize("koreanFirstChar", d)
            }
        }

        if (trimmed.isNotEmpty() && trimmed.all { ch -> koreanStrictSet.contains(ch.toString()) }) {
            val firstChar = trimmed.first().toString()
            if (koreanToDigit.containsKey(firstChar)) {
                val d = koreanToDigit[firstChar]!!
                if (d.first() in allowedDigits) return logNormalize("koreanAllStrict", d)
            }
        }

        tokens.firstOrNull { it.length == 1 && it[0] in allowedDigits }
            ?.let { return logNormalize("tokenSingleDigit", it) }

        val firstDigit = trimmed.firstOrNull { it in allowedDigits }
        if (firstDigit != null) return logNormalize("inlineDigit", firstDigit.toString())

        val result = tokens.firstOrNull { token -> token.any { it in allowedDigits } }
            ?.first { it in allowedDigits }?.toString()
            ?: ""

        return logNormalize("fallback", result)
    }

    fun fallbackFrom(candidate: String?): String? {
        if (candidate.isNullOrBlank()) return null
        return normalizeCandidate(candidate).takeIf { it.isNotBlank() }
    }

    private fun logNormalize(stage: String, value: String): String {
        Log.d(traceTag, "normalizeCandidate[$stage]='$value'")
        return value
    }

    fun handleEmptyResults(
        sessionState: SessionState,
        matches: List<String>,
    ): String? {
        if (matches.isNotEmpty()) return null
        return sessionState.lastPartialCandidate?.let { fallbackFrom(it) }
    }

    private data class CandidateScore(
        val candidate: String,
        val normalized: String,
        val score: Float,
        val index: Int,
    )

    private fun findSingleSyllableCandidate(
        matches: List<String>,
        confidences: FloatArray?,
        koreanStrictSet: Set<String>,
        koreanToDigit: Map<String, String>,
        digitSet: Set<Char>,
    ): String? {
        var bestCandidate: String? = null
        var bestScore = -1f

        matches.forEachIndexed { idx, candidate ->
            val trimmed = candidate.trim()
            val isValid = (trimmed.length == 1 && trimmed[0] in digitSet) ||
                koreanStrictSet.contains(trimmed)

            if (isValid) {
                val conf = confidences?.getOrNull(idx) ?: 0.5f
                val targetKoreanDigits = setOf("이", "삼", "사", "오", "육", "칠")
                val score = conf + when {
                    trimmed.length == 1 && trimmed[0] in digitSet -> 1.5f
                    trimmed in targetKoreanDigits -> 1.3f
                    koreanStrictSet.contains(trimmed) -> 1.0f
                    else -> 0.2f
                }

                if (score > bestScore) {
                    bestScore = score
                    bestCandidate = candidate
                }
            }
        }

        return bestCandidate
    }

    private fun findBestCandidateByConfidence(
        matches: List<String>,
        confidences: FloatArray?,
        koreanStrictSet: Set<String>,
        koreanToDigit: Map<String, String>,
        digitRegex: Regex,
    ): CandidateScore? {
        val candidates = mutableListOf<CandidateScore>()

        matches.forEachIndexed { idx, candidate ->
            val trimmed = candidate.trim()
            val conf = confidences?.getOrNull(idx) ?: 0.5f

            val targetKoreanDigits = setOf("이", "삼", "사", "오", "육", "칠")
            when {
                trimmed.length == 1 && trimmed[0] in setOf('2', '3', '4', '5', '6', '7') -> {
                    var score = conf + 1.5f
                    if (phraseHints.contains(trimmed)) score += 0.8f
                    candidates.add(CandidateScore(candidate, trimmed, score, idx))
                }
                trimmed in targetKoreanDigits -> {
                    val normalized = koreanToDigit[trimmed] ?: ""
                    if (normalized.isNotBlank() && normalized[0] in setOf('2', '3', '4', '5', '6', '7')) {
                        var score = conf + 1.3f
                        if (phraseHints.contains(trimmed)) score += 0.8f
                        candidates.add(CandidateScore(candidate, normalized, score, idx))
                    }
                }
                koreanStrictSet.contains(trimmed) -> {
                    val normalized = koreanToDigit[trimmed] ?: ""
                    if (normalized.isNotBlank() && normalized[0] in setOf('2', '3', '4', '5', '6', '7')) {
                        var score = conf + 1.0f
                        if (phraseHints.contains(trimmed)) score += 0.7f
                        candidates.add(CandidateScore(candidate, normalized, score, idx))
                    }
                }
                else -> {
                    val tokens = trimmed.replace("[\\p{Punct}]".toRegex(), " ")
                        .split("\\s+".toRegex()).filter { it.isNotBlank() }
                    val firstKoreanDigit = tokens.firstOrNull { koreanStrictSet.contains(it) }

                    if (firstKoreanDigit != null) {
                        val normalized = koreanToDigit[firstKoreanDigit] ?: ""
                        if (normalized.isNotBlank() && normalized[0] in setOf('2', '3', '4', '5', '6', '7')) {
                            var score = conf + 0.8f
                            if (phraseHints.contains(firstKoreanDigit)) score += 0.7f
                            candidates.add(CandidateScore(candidate, normalized, score, idx))
                        }
                    } else if (candidate.contains(digitRegex)) {
                        val firstDigit = candidate.firstOrNull { it in setOf('2', '3', '4', '5', '6', '7') }
                        if (firstDigit != null) {
                            var score = conf + 0.7f
                            if (phraseHints.contains(firstDigit.toString())) score += 0.7f
                            candidates.add(CandidateScore(candidate, firstDigit.toString(), score, idx))
                        }
                    }
                }
            }
        }

        val bestCandidate = candidates.maxByOrNull { it.score }
        val minScore = if (bestCandidate != null && bestCandidate.normalized.length == 1) 0.1f else 0.5f

        return if (bestCandidate != null && bestCandidate.score >= minScore) {
            bestCandidate
        } else {
            null
        }
    }

    private fun findSpecialCandidate(matches: List<String>, dontKnowKeys: List<String>): String? {
        val dontKnowCandidate = matches.firstOrNull { candidate ->
            dontKnowKeys.any { key -> candidate.contains(key) }
        }

        val hintCandidate = if (phraseHints.isNotEmpty()) {
            matches.firstOrNull { candidate ->
                val normalized = normalizeCandidate(candidate)
                phraseHints.contains(normalized) || phraseHints.any { candidate.contains(it) }
            }
        } else {
            null
        }

        return dontKnowCandidate ?: hintCandidate
    }

    private fun editDistance(a: String, b: String): Int {
        val n = a.length
        val m = b.length
        if (n == 0) return m
        if (m == 0) return n

        val dp = Array(n + 1) { IntArray(m + 1) }
        for (i in 0..n) dp[i][0] = i
        for (j in 0..m) dp[0][j] = j

        for (i in 1..n) {
            for (j in 1..m) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }

        return dp[n][m]
    }
}

