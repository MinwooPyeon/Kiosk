package com.pixelro.nenoonkiosk.core.util.stt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.content.ContextCompat

class SttCoordinator(
    private val contextProvider: () -> Context,
    private val handler: Handler = Handler(Looper.getMainLooper()),
    private val recognizerLifecycle: RecognizerLifecycle = RecognizerLifecycle(contextProvider()),
    private val sessionState: SessionState = SessionState(),
    private val resultProcessor: ResultProcessor = ResultProcessor(),
) {
    private companion object {
        const val TAG = "SttCoordinator"
        const val TRACE_TAG = "STT_TRACE"
        const val MAX_CONSECUTIVE_NO_MATCH = 3
    }

    private data class ContinuousConfig(
        val language: String,
        val restartDelayOnResultMs: Long,
        val restartDelayOnErrorMs: Long,
        val shortUtterance: Boolean,
        val autoStopTimeoutMs: Long?,
    )

    private var recognitionListener: RecognitionListener? = null
    private var continuousConfig: ContinuousConfig? = null
    private var pendingResultTimeout: Runnable? = null

    private var onResultCallback: ((String) -> Unit)? = null
    private var onErrorCallback: ((Int) -> Unit)? = null
    private var onReadyCallback: (() -> Unit)? = null

    private var isListening = false
    private var isContinuous = false
    private var restartScheduled = false

    private var stateObserver: ((Boolean, Boolean) -> Unit)? = null

    fun setStateObserver(observer: ((Boolean, Boolean) -> Unit)?) {
        stateObserver = observer
    }

    private fun notifyState(active: Boolean, hadSpeech: Boolean) {
        stateObserver?.invoke(active, hadSpeech)
    }

    private var autoStopRunnable: Runnable? = null

    fun init() {
        if (recognizerLifecycle.getRecognizer() != null) {
            recognitionListener?.let { recognizerLifecycle.setRecognitionListener(it) }
            return
        }
        val listener = buildRecognitionListener()
        recognitionListener = listener
        recognizerLifecycle.ensureInitialized(listener)
    }

    fun configurePhraseHints(hints: Set<String>, boost: Int) {
        resultProcessor.phraseHints = hints
        resultProcessor.phraseBoost = boost
    }

    fun enableVisualAcuityNumberHints(boost: Int) {
        val hints = buildSet {
            addAll(listOf("이", "삼", "사", "오", "육", "칠"))
            addAll(listOf("2", "3", "4", "5", "6", "7"))
            addAll(listOf("two", "three", "four", "five", "six", "seven"))
            addAll(listOf("ee", "eee", "sam", "sa", "sah", "o", "oh", "yuk", "yook", "chil"))
        }
        configurePhraseHints(hints, boost)
    }

    fun startListening(
        language: String,
        onResult: (String) -> Unit,
        onError: ((Int) -> Unit)? = null,
        onReady: (() -> Unit)? = null,
        shortUtterance: Boolean,
        autoStopTimeoutMs: Long? = null,
    ) {
        isContinuous = false
        continuousConfig = null
        startListeningInternal(language, onResult, onError, onReady, shortUtterance, autoStopTimeoutMs)
    }

    fun startContinuousListening(
        language: String,
        onResult: (String) -> Unit,
        onError: ((Int) -> Unit)? = null,
        onReady: (() -> Unit)? = null,
        restartDelayOnResultMs: Long,
        restartDelayOnErrorMs: Long,
        shortUtterance: Boolean,
        autoStopTimeoutMs: Long? = null,
    ) {
        isContinuous = true
        continuousConfig = ContinuousConfig(
            language = language,
            restartDelayOnResultMs = restartDelayOnResultMs,
            restartDelayOnErrorMs = restartDelayOnErrorMs,
            shortUtterance = shortUtterance,
            autoStopTimeoutMs = autoStopTimeoutMs,
        )
        startListeningInternal(language, onResult, onError, onReady, shortUtterance, autoStopTimeoutMs)
    }

    private fun startListeningInternal(
        language: String,
        onResult: (String) -> Unit,
        onError: ((Int) -> Unit)?,
        onReady: (() -> Unit)?,
        shortUtterance: Boolean,
        autoStopTimeoutMs: Long?,
    ) {
        val ctx = contextProvider()
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "startListening: RECORD_AUDIO permission missing")
            onError?.invoke(SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS)
            return
        }

        if (!recognizerLifecycle.isAvailable()) {
            Log.e(TAG, "startListening: Speech recognition unavailable")
            onError?.invoke(SpeechRecognizer.ERROR_CLIENT)
            return
        }

        onResultCallback = onResult
        onErrorCallback = onError
        onReadyCallback = onReady

        if (isListening) {
            stopListening()
            if (!restartScheduled) {
                restartScheduled = true
                handler.postDelayed({
                    restartScheduled = false
                    if (!isListening) {
                        startListeningInternal(language, onResult, onError, onReady, shortUtterance, autoStopTimeoutMs)
                    }
                }, 200L)
            }
            return
        }

        cancelPendingTimeout()
        cancelAutoStop()
        sessionState.beginListening()
        sessionState.resetNoMatch()
        notifyState(true, false)

        val listener = buildRecognitionListener()
        recognitionListener = listener
        recognizerLifecycle.ensureInitialized(listener)

        val intent = recognizerLifecycle.buildIntent(
            language = language,
            shortUtterance = shortUtterance,
            hasHints = resultProcessor.phraseHints.isNotEmpty(),
        )

        isListening = true
        recognizerLifecycle.startListening(intent)
        Log.d(TAG, "startListening: language=$language short=$shortUtterance")
        Log.d(TRACE_TAG, "startListening(language=$language, continuous=$isContinuous, short=$shortUtterance)")

        autoStopTimeoutMs?.takeIf { it > 0 }?.let(::scheduleAutoStop)
    }

    fun stopListening() {
        cancelPendingTimeout()
        cancelAutoStop()
        recognizerLifecycle.stopListening()
        isListening = false
        notifyState(false, sessionState.hadSpeechInSession)
    }

    fun cancelListening() {
        cancelPendingTimeout()
        cancelAutoStop()
        recognizerLifecycle.cancelListening()
        isListening = false
        notifyState(false, sessionState.hadSpeechInSession)
    }

    fun stopContinuousListening() {
        isContinuous = false
        continuousConfig = null
        restartScheduled = false
        handler.removeCallbacksAndMessages(null)
        cancelAutoStop()
        cancelListening()
        sessionState.beginListening()
        notifyState(false, false)
    }

    fun destroy() {
        stopContinuousListening()
        onResultCallback = null
        onErrorCallback = null
        onReadyCallback = null
        cancelAutoStop()
        recognitionListener = null
        recognizerLifecycle.destroy()
    }

    fun isListening(): Boolean = isListening

    fun isAvailable(): Boolean = recognizerLifecycle.isAvailable()

    fun getErrorMessage(error: Int): String = when (error) {
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

    private fun buildRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech")
                onReadyCallback?.invoke()
            }

            override fun onBeginningOfSpeech() {
                sessionState.markSpeechDetected()
                notifyState(true, true)
                cancelAutoStop()
            }

            override fun onRmsChanged(rmsdB: Float) {
                if (rmsdB > -10f && !sessionState.hadSpeechInSession) {
                    sessionState.markSpeechDetected()
                }
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // no-op
            }

            override fun onEndOfSpeech() {
                if (!sessionState.hadSpeechInSession) {
                    sessionState.resetSpeechStart()
                    Log.d(TAG, "onEndOfSpeech: no speech")
                    return
                }
                sessionState.waitingForResults = true
                sessionState.markSpeechEnd()
                scheduleResultTimeout()
            }

            override fun onError(error: Int) {
                Log.e(TAG, "onError: ${getErrorMessage(error)} ($error)")
                isListening = false
                cancelPendingTimeout()
                cancelAutoStop()
                sessionState.waitingForResults = false
                notifyState(false, sessionState.hadSpeechInSession)

                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> handleNoMatchError()
                    SpeechRecognizer.ERROR_CLIENT -> handleClientError()
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> handleRecognizerBusyError()
                    else -> handleGenericError(error)
                }

                sessionState.updateLastSessionEnded()
            }

            override fun onResults(results: Bundle?) {
                Log.d(TAG, "onResults")
                Log.d(TRACE_TAG, "onResults raw=${results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}")
                if (sessionState.deliveredInSession) {
                    Log.d(TRACE_TAG, "onResults ignored - already delivered in session")
                    return
                }

                isListening = false
                cancelPendingTimeout()
                cancelAutoStop()

                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: emptyList()
                val confidences = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                if (matches.isEmpty()) {
                    val fallback = resultProcessor.handleEmptyResults(sessionState, matches)
                    if (fallback != null) {
                        Log.d(TRACE_TAG, "onResults fallback=$fallback (empty matches)")
                        deliverResult(fallback)
                    } else {
                        onErrorCallback?.invoke(SpeechRecognizer.ERROR_NO_MATCH)
                    }
                    return
                }

                sessionState.resetNoMatch()
                sessionState.waitingForResults = false

                val bestResult = resultProcessor.selectBestResult(matches, confidences)
                val normalized = resultProcessor.normalizeCandidate(bestResult)

                if (normalized.isNotBlank()) {
                    Log.d(TRACE_TAG, "onResults best='$bestResult' normalized='$normalized'")
                    deliverResult(normalized)
                } else {
                    onErrorCallback?.invoke(SpeechRecognizer.ERROR_NO_MATCH)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                sessionState.markPartialResultsReceived()
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidences = partialResults?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
                Log.d(TRACE_TAG, "onPartialResults matches=$matches confidences=${confidences?.contentToString()}")

                val action = resultProcessor.processPartialResults(partialResults, sessionState)
                when (action) {
                    is ResultProcessor.Action.Deliver -> deliverResult(action.result)
                    is ResultProcessor.Action.Remember -> sessionState.rememberPartialCandidate(action.candidate)
                    ResultProcessor.Action.None -> {}
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d(TAG, "onEvent: $eventType")
            }
        }
    }

    private fun handleNoMatchError() {
        val fallback = resultProcessor.fallbackFrom(sessionState.consumeLastPartialCandidate())
        if (fallback != null) {
            deliverResult(fallback)
            return
        }

        sessionState.waitingForResults = false
        sessionState.incrementNoMatch()

        if (sessionState.consecutiveNoMatchCount >= MAX_CONSECUTIVE_NO_MATCH) {
            Log.w(TAG, "handleNoMatchError: recreating recognizer")
            sessionState.resetNoMatch()
            val listener = buildRecognitionListener()
            recognitionListener = listener
            recognizerLifecycle.recreate(listener)
        }

        onErrorCallback?.invoke(SpeechRecognizer.ERROR_NO_MATCH)
        scheduleContinuousRestart(baseDelayMs = 500L)
    }

    private fun handleClientError() {
        sessionState.resetNoMatch()
        onErrorCallback?.invoke(SpeechRecognizer.ERROR_CLIENT)
        val listener = buildRecognitionListener()
        recognitionListener = listener
        recognizerLifecycle.recreate(listener)
        scheduleContinuousRestart(SttConfig.Recognition.CLIENT_ERROR_RESTART_DELAY_MS)
    }

    private fun handleRecognizerBusyError() {
        sessionState.resetNoMatch()
        onErrorCallback?.invoke(SpeechRecognizer.ERROR_RECOGNIZER_BUSY)
        scheduleContinuousRestart(SttConfig.Recognition.RECOGNIZER_BUSY_RETRY_DELAY_MS)
    }

    private fun handleGenericError(error: Int) {
        sessionState.resetNoMatch()
        onErrorCallback?.invoke(error)
        scheduleContinuousRestart(continuousConfig?.restartDelayOnErrorMs ?: 400L)
    }

    private fun deliverResult(result: String) {
        if (result.isBlank()) return

        sessionState.markResultDelivered()
        sessionState.waitingForResults = false
        sessionState.clearLastPartialCandidate()
        sessionState.setSuppressNextError()

        onResultCallback?.invoke(result)
        Log.d(TRACE_TAG, "deliverResult result='$result' continuous=$isContinuous")
        sessionState.updateLastSessionEnded()
        isListening = false
        notifyState(false, sessionState.hadSpeechInSession)
        cancelAutoStop()

        if (isContinuous) {
            recognizerLifecycle.stopListening()
            val baseDelay = continuousConfig?.restartDelayOnResultMs ?: 100L
            scheduleContinuousRestart(baseDelay)
        } else {
            stopListening()
        }
    }

    private fun scheduleResultTimeout() {
        cancelPendingTimeout()
        val runnable = Runnable {
            if (sessionState.waitingForResults && !sessionState.deliveredInSession) {
                val fallback = resultProcessor.fallbackFrom(sessionState.consumeLastPartialCandidate())
                if (fallback != null) {
                    deliverResult(fallback)
                } else {
                    handleNoMatchError()
                }
            }
        }
        pendingResultTimeout = runnable
        handler.postDelayed(runnable, SttConfig.Recognition.END_OF_SPEECH_WAIT_MS)
    }

    private fun cancelPendingTimeout() {
        pendingResultTimeout?.let { handler.removeCallbacks(it) }
        pendingResultTimeout = null
    }

    private fun scheduleContinuousRestart(baseDelayMs: Long) {
        if (!isContinuous) return
        val config = continuousConfig ?: return
        if (restartScheduled) return

        val sinceEnd = (System.currentTimeMillis() - sessionState.lastSessionEndedAt).coerceAtLeast(0)
        val enforcedDelay = maxOf(baseDelayMs, SttConfig.MIN_RESTART_INTERVAL_MS - sinceEnd, 0L)

        restartScheduled = true
        handler.postDelayed({
            restartScheduled = false
            if (!isContinuous) return@postDelayed
            startListeningInternal(
                language = config.language,
                onResult = onResultCallback ?: return@postDelayed,
                onError = onErrorCallback,
                onReady = onReadyCallback,
                shortUtterance = config.shortUtterance,
                autoStopTimeoutMs = config.autoStopTimeoutMs,
            )
        }, enforcedDelay)
    }

    private fun scheduleAutoStop(timeoutMs: Long) {
        cancelAutoStop()
        val runnable = Runnable {
            if (!isListening) return@Runnable
            Log.w(TAG, "autoStop triggered after ${timeoutMs}ms")
            cancelPendingTimeout()
            sessionState.waitingForResults = false
            performAutoStop()
        }
        autoStopRunnable = runnable
        handler.postDelayed(runnable, timeoutMs)
    }

    private fun cancelAutoStop() {
        autoStopRunnable?.let { handler.removeCallbacks(it) }
        autoStopRunnable = null
    }

    private fun performAutoStop() {
        autoStopRunnable = null
        isListening = false
        recognizerLifecycle.cancelListening()
        notifyState(false, sessionState.hadSpeechInSession)
        sessionState.clearLastPartialCandidate()
        onErrorCallback?.invoke(SpeechRecognizer.ERROR_SPEECH_TIMEOUT)
        sessionState.updateLastSessionEnded()
    }
}

