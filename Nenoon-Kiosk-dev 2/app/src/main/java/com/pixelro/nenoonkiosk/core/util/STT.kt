package com.pixelro.nenoonkiosk.core.util

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.pixelro.nenoonkiosk.app.NenoonKioskApplication
import kotlinx.coroutines.delay

object STT {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var onResultCallback: ((String) -> Unit)? = null
    private var onErrorCallback: ((Int) -> Unit)? = null
    private var onReadyCallback: (() -> Unit)? = null
    private var isContinuous = false
    private val handler = Handler(Looper.getMainLooper())
    private var deliveredInSession = false
    private var retryDelayMs: Long = 300L
    private val retryDelayMaxMs: Long = 1000L
    private var lastSessionEndedAt: Long = 0L
    private val minRestartIntervalMs: Long = 300L
    private var hadSpeechInSession: Boolean = false
    private var suppressNextError: Boolean = false
    private var lastPartialCandidate: String? = null
    private var phraseHints: Set<String> = emptySet()
    private var phraseBoost: Int = 30
    private val phoneticToDigit: Map<String, String> = mapOf(
        "two" to "2", "three" to "3", "four" to "4", "five" to "5", "six" to "6", "seven" to "7", "eight" to "8", "nine" to "9",
        "ee" to "2", "eee" to "2", 
        "sam" to "3", 
        "sa" to "4", "sah" to "4", 
        "o" to "5", "oh" to "5",
        "yuk" to "6", "yook" to "6",
        "chil" to "7",
        "pal" to "8",
        "gu" to "9", "goo" to "9"
    )
    private var restartScheduled: Boolean = false

    fun initSTT() {
        if (speechRecognizer != null) {
            Log.d("STT", "initSTT: 이미 초기화됨, 재사용")
            return
        }
        val context = NenoonKioskApplication.applicationContext()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("STT", "onReadyForSpeech: 음성 입력 준비 완료")
                onReadyCallback?.invoke()
            }

            override fun onBeginningOfSpeech() {
                Log.d("STT", "onBeginningOfSpeech: 음성 입력 시작")
                hadSpeechInSession = true
            }

            override fun onRmsChanged(rmsdB: Float) {
                // 음성 레벨 변화는 너무 많이 찍히므로 로그 제외
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("STT", "onBufferReceived: 음성 버퍼 수신")
            }

            override fun onEndOfSpeech() {
                Log.d("STT", "onEndOfSpeech: 음성 입력 종료")
                lastSessionEndedAt = System.currentTimeMillis()
            }

            override fun onError(error: Int) {
                isListening = false
                val errorMsg = getErrorMessage(error)
                Log.e("STT", "onError: $errorMsg (code: $error)")
                if (suppressNextError) {
                    Log.d("STT", "onError: suppressNextError=true → 재시작 스케줄 안 함")
                    suppressNextError = false
                } else {
                    if (error == SpeechRecognizer.ERROR_NO_MATCH && !lastPartialCandidate.isNullOrBlank()) {
                        val fallbackRaw = lastPartialCandidate!!
                        val fallback = normalizeCandidate(fallbackRaw)
                        Log.d("STT", "onError: NO_MATCH → lastPartialCandidate로 대체: '$fallback'")
                        onResultCallback?.invoke(fallback)
                        lastPartialCandidate = null
                        deliveredInSession = true
                        suppressNextError = true
                    } else {
                        onErrorCallback?.invoke(error)
                    }
                }
                lastSessionEndedAt = System.currentTimeMillis()
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
                val confidences = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                val digitSet = setOf('2','3','4','5','6','7')
                val koreanSinglesStrict = setOf("일","이","삼","사","오","육","칠","팔","구")
                val koreanSingles = setOf("일","이","삼","사","오","육","칠","팔","구","둘","셋","넷","다섯","여섯","일곱")
                fun score(candidate: String, idx: Int): Int {
                    val trimmed = candidate.trim()
                    var s = 10
                    // 숫자 우선 가중치
                    if (trimmed.length == 1 && trimmed[0] in digitSet) s = 130
                    else if (trimmed.any { it in digitSet }) s = 110
                    else if (koreanSinglesStrict.contains(trimmed)) s = 90
                    else if (koreanSingles.contains(trimmed)) s = 80
                    else if (koreanSingles.any { trimmed.contains(it) }) s = 70

                    val conf = confidences?.getOrNull(idx) ?: 0f
                    var score = s + (conf * 20f).toInt()
                    if (phraseHints.isNotEmpty()) {
                        val tokens = trimmed.replace("[\\p{Punct}]".toRegex(), " ")
                            .split("\\s+".toRegex()).filter { it.isNotBlank() }
                        if (tokens.any { phraseHints.contains(it) } || phraseHints.contains(trimmed)) {
                            score += phraseBoost
                        } else if (phraseHints.any { trimmed.contains(it) }) {
                            score += (phraseBoost / 2)
                        }
                    }
                    return score
                }
                val bestIdx = matches.indices.maxByOrNull { i -> score(matches[i], i) }
                val best = bestIdx?.let { matches[it] }
                val textRaw = best ?: ""
                val text = normalizeCandidate(textRaw)
                Log.d("STT", "onResults: 인식 결과 = '$text'")
                Log.d("STT", "onResults: 전체 매칭 목록 = $matches")
                if (!deliveredInSession) {
                    deliveredInSession = true
                    retryDelayMs = 300L  
                    onResultCallback?.invoke(text)
                } else {
                    Log.d("STT", "onResults: 이미 전달된 세션 결과, 무시")
                }
                suppressNextError = true
                lastSessionEndedAt = System.currentTimeMillis()
                hadSpeechInSession = false
                lastPartialCandidate = null
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
                if (matches.isEmpty()) return
                
                Log.d("STT", "onPartialResults: 부분 인식 목록 = $matches")

                if (!deliveredInSession) {
                    val digitRegex = Regex("[2-7]")
                    val koreanStrictSet = setOf("일","이","삼","사","오","육","칠","팔","구")
                    val koreanMapKeys = listOf("일","이","삼","사","오","육","칠","팔","구","둘","셋","넷","다섯","여섯","일곱")
                    val dontKnowKeys = listOf(
                        "모르겠", "모름", "몰라", "안보여", "안 보여", "안보임", "안 보임", "안보입니다", "안 보입니다", "안보인다", "안 보인다"
                    )

                    val singleSyllableCandidate = run {
                        matches.firstOrNull { cand ->
                            val trimmed = cand.trim()
                            (trimmed.length == 1 && trimmed[0] in setOf('2','3','4','5','6','7')) ||
                            koreanStrictSet.contains(trimmed)
                        }
                    }
                    
                    val candidateStrictHangul = run {
                        matches.forEach { cand ->
                            val tokens = cand.replace("[\\p{Punct}]".toRegex(), " ")
                                .split("\\s+".toRegex()).filter { it.isNotBlank() }
                            val token = tokens.firstOrNull { koreanStrictSet.contains(it) }
                            if (token != null) return@run cand
                        }
                        null
                    }
                    
                    val digitCandidate = matches.firstOrNull { it.contains(digitRegex) }
                    
                    val koreanCandidate = matches.firstOrNull { cand -> 
                        koreanMapKeys.any { key -> cand.contains(key) }
                    }
                    
                    val dontKnowCandidate = matches.firstOrNull { cand -> 
                        dontKnowKeys.any { key -> cand.contains(key) }
                    }
                    
                    val hintCandidate = if (phraseHints.isNotEmpty()) {
                        matches.firstOrNull { cand ->
                            val norm = normalizeCandidate(cand)
                            phraseHints.contains(norm) || phraseHints.any { cand.contains(it) }
                        }
                    } else null

                    val candidate = singleSyllableCandidate 
                        ?: digitCandidate
                        ?: candidateStrictHangul
                        ?: koreanCandidate
                        ?: dontKnowCandidate
                        ?: hintCandidate

                    if (candidate != null) {
                        lastPartialCandidate = candidate
                        val normalized = normalizeCandidate(candidate)
                        
                        if (normalized.isNotBlank()) {
                            Log.d("STT", "onPartialResults: 조기 확정 = '$normalized' (원본: '$candidate')")
                            deliveredInSession = true
                            retryDelayMs = 300L
                            onResultCallback?.invoke(normalized)
                            suppressNextError = true

                            stopListening()
                            lastSessionEndedAt = System.currentTimeMillis()
                            hadSpeechInSession = false
                        } else {
                            Log.d("STT", "onPartialResults: 후보는 있으나 정규화 실패: '$candidate'")
                        }
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("STT", "onEvent: eventType = $eventType")
            }
        })
    }

    private fun normalizeCandidate(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return trimmed
        
        val dontKnowKeys = listOf(
            "모르겠", "모름", "몰라", "안보여", "안 보여", "안보임", "안 보임", 
            "안보입니다", "안 보입니다", "안보인다", "안 보인다"
        )
        
        dontKnowKeys.forEach { key ->
            if (trimmed.contains(key)) {
                return "안 보임"
            }
        }
        
        val koreanStrictSet = setOf("일","이","삼","사","오","육","칠","팔","구")
        val koreanToDigit = mapOf(
            "일" to "1", "이" to "2", "삼" to "3", "사" to "4", "오" to "5", "육" to "6", "칠" to "7", "팔" to "8", "구" to "9",
            "둘" to "2", "셋" to "3", "넷" to "4", "다섯" to "5", "여섯" to "6", "일곱" to "7"
        )
        val tokens = trimmed.replace("[\\p{Punct}]".toRegex(), " ")
            .split("\\s+".toRegex()).filter { it.isNotBlank() }
        val allowedDigits = setOf('2','3','4','5','6','7')
        tokens.firstOrNull { tok -> phoneticToDigit.containsKey(tok.lowercase()) }
            ?.let {
                val d = phoneticToDigit[it.lowercase()]!!
                if (d.first() in allowedDigits) return d
            }
        if (phoneticToDigit.containsKey(trimmed.lowercase())) {
            val d = phoneticToDigit[trimmed.lowercase()]!!
            if (d.first() in allowedDigits) return d
        }
        run {
            val latinTokens = tokens.filter { it.matches(Regex("[A-Za-z]+")) }.map { it.lowercase() }
            if (latinTokens.isNotEmpty()) {
                val keys = phoneticToDigit.keys
                for (tok in latinTokens) {
                    var bestKey: String? = null
                    var bestDist = Int.MAX_VALUE
                    for (k in keys) {
                        val d = editDistance(tok, k)
                        if (d < bestDist) { bestDist = d; bestKey = k }
                        if (bestDist == 0) break
                    }
                    if (bestKey != null && bestDist <= 1) {
                        val d = phoneticToDigit[bestKey]!!
                        if (d.first() in allowedDigits) return d
                    }
                }
            }
        }
        tokens.firstOrNull { koreanToDigit.containsKey(it) }
            ?.let {
                val d = koreanToDigit[it]!!
                if (d.first() in allowedDigits) return d
            }
        tokens.firstOrNull { it.length == 1 && it[0] in allowedDigits }
            ?.let { return it }
        val firstDigit = trimmed.firstOrNull { it in allowedDigits }
        if (firstDigit != null) return firstDigit.toString()
        return tokens.firstOrNull { it.any { ch -> ch in allowedDigits } }?.first { ch -> ch in allowedDigits }?.toString()
            ?: ""
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

    fun configurePhraseHints(hints: Set<String>, boost: Int = 30) {
        phraseHints = hints
        phraseBoost = boost
        Log.d("STT", "configurePhraseHints: hints=${hints.joinToString()}, boost=$boost")
    }

    fun enableVisualAcuityNumberHints(boost: Int = 30) {
        val hints = buildSet {
            addAll(listOf("이","삼","사","오","육","칠"))
            addAll(listOf("2","3","4","5","6","7"))
            addAll(listOf("two","three","four","five","six","seven"))
            addAll(listOf("ee","eee","sam","sa","sah","o","oh","yuk","yook","chil"))
        }
        configurePhraseHints(hints, boost)
    }

    // consonant hints removed (reverted)
    
    // 음성 인식
    fun startListening(
        language: String = "ko-KR",
        onResult: (String) -> Unit,
        onError: ((Int) -> Unit)? = null,
        onReady: (() -> Unit)? = null,
        shortUtterance: Boolean = true,
    ) {
        Log.d("STT", "startListening: 시작 - language=$language")

        val ctx = NenoonKioskApplication.applicationContext()
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e("STT", "startListening: RECORD_AUDIO 권한 없음")
            onError?.invoke(SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS)
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(ctx)) {
            Log.e("STT", "startListening: 음성 인식 불가능")
            onError?.invoke(SpeechRecognizer.ERROR_CLIENT)
            return
        }
        
        if (isListening) {
            stopListening()
            if (!restartScheduled) {
                restartScheduled = true
                handler.postDelayed({
                    restartScheduled = false
                    startListening(language, onResult, onError, onReady, shortUtterance)
                }, 150L)
            }
            return
        }
        
        onResultCallback = onResult
        onErrorCallback = onError
        onReadyCallback = onReady
        
        if (speechRecognizer == null) {
            Log.d("STT", "startListening: SpeechRecognizer 초기화")
            initSTT()
        } else {
            Log.d("STT", "startListening: 기존 SpeechRecognizer 재사용")
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20)
            
            if (shortUtterance) {
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 800L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 600L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 200L)
            } else {
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 700L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 300L)
            }
            
            // 추가 힌트 설정을 통한 한 음절 우선 인식
            if (phraseHints.isNotEmpty()) {
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 500L)
            }
        }
        
        deliveredInSession = false
        suppressNextError = false
        hadSpeechInSession = false
        isListening = true
        speechRecognizer?.startListening(intent)
        Log.d("STT", "startListening: 음성 인식 시작 완료")
        restartScheduled = false
    }
    
    // 음성 인식 종료
    fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
    }
    
    // 음성 인식 취소
    fun cancelListening() {
        speechRecognizer?.cancel()
        isListening = false
    }

    fun startContinuousListening(
        language: String = "ko-KR",
        onResult: (String) -> Unit,
        onError: ((Int) -> Unit)? = null,
        onReady: (() -> Unit)? = null,
        restartDelayOnResultMs: Long = 300L,
        restartDelayOnErrorMs: Long = 600L,
        shortUtterance: Boolean = true,
    ) {
        isContinuous = true
        onResultCallback = { text ->
            onResult(text)
            if (isContinuous) {
                val sinceEnd = (System.currentTimeMillis() - lastSessionEndedAt).coerceAtLeast(0)
                val delayMs = maxOf(restartDelayOnResultMs, minRestartIntervalMs - sinceEnd)
                if (!restartScheduled) {
                    restartScheduled = true
                    handler.postDelayed({
                        restartScheduled = false
                        startListening(
                            language = language,
                            onResult = onResultCallback ?: {},
                            onError = onErrorCallback,
                            onReady = onReadyCallback,
                            shortUtterance = shortUtterance,
                        )
                    }, delayMs)
                }
            }
        }
        onErrorCallback = { code ->
            if (isContinuous && code != SpeechRecognizer.ERROR_NO_MATCH) onError?.invoke(code) 
            if (isContinuous) {
                val sinceEnd = (System.currentTimeMillis() - lastSessionEndedAt).coerceAtLeast(0)
                
                var delayMs = when (code) {
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        maxOf(300L, minRestartIntervalMs - sinceEnd)
                    }
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        maxOf(500L, minRestartIntervalMs - sinceEnd)
                    }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        maxOf(800L, minRestartIntervalMs - sinceEnd)
                    }
                    else -> {
                        maxOf(restartDelayOnErrorMs, minRestartIntervalMs - sinceEnd)
                    }
                }
                
                if (!hadSpeechInSession) delayMs += 200L
                
                delayMs = delayMs.coerceAtMost(1000L)
                
                if (!restartScheduled) {
                    restartScheduled = true
                    handler.postDelayed({
                        restartScheduled = false
                        startListening(
                            language = language,
                            onResult = onResultCallback ?: {},
                            onError = onErrorCallback,
                            onReady = onReadyCallback,
                            shortUtterance = shortUtterance,
                        )
                    }, delayMs)
                }
            }
        }
        onReadyCallback = onReady

        startListening(
            language = language,
            onResult = onResultCallback ?: {},
            onError = onErrorCallback,
            onReady = onReadyCallback,
            shortUtterance = shortUtterance,
        )
    }

    fun stopContinuousListening() {
        isContinuous = false
        handler.removeCallbacksAndMessages(null)
        cancelListening()
        deliveredInSession = false
        retryDelayMs = 300L  // 초기값도 최소화
    }
    
    // STT 종료
    fun destroySTT() {
        handler.removeCallbacksAndMessages(null)
        cancelListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        onResultCallback = null
        onErrorCallback = null
        onReadyCallback = null
        deliveredInSession = false
        retryDelayMs = 300L  
    }
    
    // 현재 음성 인식 중인지
    fun isListening(): Boolean {
        return isListening
    }
    
    // 음성 인식 가능한지
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(NenoonKioskApplication.applicationContext())
    }
    
    // 에러 메세지
    fun getErrorMessage(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "오디오 오류"
            SpeechRecognizer.ERROR_CLIENT -> "클라이언트 오류"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한 없음"
            SpeechRecognizer.ERROR_NETWORK -> "네트워크 오류"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 타임아웃"
            SpeechRecognizer.ERROR_NO_MATCH -> "인식된 결과 없음"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "인식기 사용 중"
            SpeechRecognizer.ERROR_SERVER -> "서버 오류"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 입력 타임아웃"
            else -> "알 수 없는 오류"
        }
    }
}

@Composable
fun AutoStartSTT(
    onResult: (String) -> Unit,
    language: String = "ko-KR",
    onError: ((Int) -> Unit)? = null,
    enabled: Boolean = true,
    delay: Long = 0
) {
    val latestOnResult = rememberUpdatedState(onResult)
    val latestOnError = rememberUpdatedState(onError)
    
    LaunchedEffect(enabled) {
        if (enabled) {
            Log.d("AutoStartSTT", "LaunchedEffect 실행 - enabled=$enabled")
            STT.initSTT()
            STT.enableVisualAcuityNumberHints(boost = 30)
            if (delay > 0) {
                Log.d("AutoStartSTT", "delay 대기 중: ${delay}ms")
                delay(delay)
            }
            STT.startContinuousListening(
                language = language,
                onResult = {
                    Log.d("AutoStartSTT", "onResult 콜백 호출: result='$it'")
                    latestOnResult.value(it)
                },
                onError = { error ->
                    Log.d("AutoStartSTT", "onError 콜백 호출: error=$error")
                    latestOnError.value?.invoke(error)
                },
                onReady = null,
            )
        } else {
            Log.d("AutoStartSTT", "enabled=false: 연속 인식 중지")
            STT.stopContinuousListening()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("AutoStartSTT", "DisposableEffect onDispose: 연속 인식 중지")
            STT.stopContinuousListening()
        }
    }
}