package com.pixelro.nenoonkiosk.core.util.stt

object SttConfig {
    object Recognition {
        const val SHORT_COMPLETE_SILENCE_MS = 320L
        const val SHORT_POSSIBLY_COMPLETE_SILENCE_MS = 220L
        const val SHORT_MINIMUM_LENGTH_MS = 240L

        const val SHORT_WITH_HINTS_COMPLETE_SILENCE_MS = 300L
        const val SHORT_WITH_HINTS_POSSIBLY_COMPLETE_SILENCE_MS = 200L

        const val LONG_COMPLETE_SILENCE_MS = 1500L
        const val LONG_POSSIBLY_COMPLETE_SILENCE_MS = 1200L
        const val LONG_MINIMUM_LENGTH_MS = 300L

        const val END_OF_SPEECH_WAIT_MS = 650L

        const val NO_MATCH_RETRY_DELAY_MS = 120L
        const val NO_MATCH_WAIT_MS = 650L

        const val CLIENT_ERROR_RECREATE_DELAY_MS = 100L
        const val CLIENT_ERROR_RESTART_DELAY_MS = 200L

        const val RECREATE_DELAY_MS = 300L

        const val RECOGNIZER_BUSY_RETRY_DELAY_MS = 800L

        const val DEFAULT_AUTO_STOP_TIMEOUT_MS = 3_000L
        const val AUTO_RESTART_DELAY_MS = 400L
    }

    const val INITIAL_RETRY_DELAY_MS = 100L
    const val MAX_RETRY_DELAY_MS = 1000L
    const val MIN_RESTART_INTERVAL_MS = 100L
    const val MIN_SPEECH_DURATION_MS = 10L

    const val DEFAULT_PHRASE_BOOST = 150
}

