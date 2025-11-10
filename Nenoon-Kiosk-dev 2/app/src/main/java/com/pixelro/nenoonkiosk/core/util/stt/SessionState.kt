package com.pixelro.nenoonkiosk.core.util.stt

class SessionState {
    var deliveredInSession: Boolean = false
        private set
    var suppressNextError: Boolean = false
        private set
    var hadSpeechInSession: Boolean = false
        private set
    var waitingForResults: Boolean = false
        set(value) {
            field = value
            if (!value) partialResultsReceived = false
        }
    var partialResultsReceived: Boolean = false
        private set

    var speechStartTime: Long = 0L
        private set
    var lastSessionEndedAt: Long = 0L
        private set

    var lastPartialCandidate: String? = null
        private set

    var consecutiveNoMatchCount: Int = 0
        private set

    var retryDelayMs: Long = SttConfig.INITIAL_RETRY_DELAY_MS
        private set

    fun beginListening() {
        deliveredInSession = false
        suppressNextError = false
        hadSpeechInSession = false
        waitingForResults = false
        partialResultsReceived = false
        speechStartTime = 0L
        lastPartialCandidate = null
    }

    fun markSpeechDetected() {
        hadSpeechInSession = true
        if (speechStartTime == 0L) {
            speechStartTime = System.currentTimeMillis()
        }
    }

    fun markSpeechEnd() {
        lastSessionEndedAt = System.currentTimeMillis()
    }

    fun resetSpeechStart() {
        hadSpeechInSession = false
        speechStartTime = 0L
    }

    fun markResultDelivered() {
        deliveredInSession = true
        retryDelayMs = SttConfig.INITIAL_RETRY_DELAY_MS
    }

    fun setSuppressNextError() {
        suppressNextError = true
    }

    fun clearSuppressNextError() {
        suppressNextError = false
    }

    fun isSuppressingNextError(): Boolean = suppressNextError

    fun consumeSuppressNextError(): Boolean {
        val shouldSuppress = suppressNextError
        suppressNextError = false
        return shouldSuppress
    }

    fun markPartialResultsReceived() {
        partialResultsReceived = true
    }

    fun rememberPartialCandidate(candidate: String?) {
        lastPartialCandidate = candidate
    }

    fun consumeLastPartialCandidate(): String? {
        val candidate = lastPartialCandidate
        lastPartialCandidate = null
        return candidate
    }

    fun clearLastPartialCandidate() {
        lastPartialCandidate = null
    }

    fun incrementNoMatch() {
        consecutiveNoMatchCount++
    }

    fun resetNoMatch() {
        consecutiveNoMatchCount = 0
    }

    fun updateRetryDelay(onBackoff: (Long) -> Long) {
        retryDelayMs = onBackoff(retryDelayMs).coerceAtMost(SttConfig.MAX_RETRY_DELAY_MS)
    }

    fun updateLastSessionEnded(time: Long = System.currentTimeMillis()) {
        lastSessionEndedAt = time
    }
}

