package com.pixelro.nenoonkiosk.core.util.stt

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class RecognizerLifecycle(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var originalSystemVolume: Int = -1
    private var originalNotificationVolume: Int = -1
    private var cachedIntent: Intent? = null
    private var lastIntentConfig: IntentConfig? = null

    private data class IntentConfig(
        val language: String,
        val shortUtterance: Boolean,
        val hasHints: Boolean,
    )

    fun ensureInitialized(listener: RecognitionListener) {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).also {
                it.setRecognitionListener(listener)
            }
        } else {
            speechRecognizer?.setRecognitionListener(listener)
        }
    }

    fun recreate(listener: RecognitionListener) {
        destroy()
        ensureInitialized(listener)
    }

    fun startListening(intent: Intent) {
        muteSystemSounds()
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        restoreSystemSounds()
    }

    fun cancelListening() {
        speechRecognizer?.cancel()
        restoreSystemSounds()
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        restoreSystemSounds()
        cachedIntent = null
        lastIntentConfig = null
    }

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun setRecognitionListener(listener: RecognitionListener) {
        speechRecognizer?.setRecognitionListener(listener)
    }

    fun getRecognizer(): SpeechRecognizer? = speechRecognizer

    fun buildIntent(
        language: String,
        shortUtterance: Boolean,
        hasHints: Boolean,
    ): Intent {
        val config = IntentConfig(language, shortUtterance, hasHints)
        if (cachedIntent == null || lastIntentConfig != config) {
            cachedIntent = createBaseIntent(language, shortUtterance, hasHints)
            lastIntentConfig = config
        }
        return Intent(cachedIntent)
    }

    private fun createBaseIntent(
        language: String,
        shortUtterance: Boolean,
        hasHints: Boolean,
    ): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)

            if (shortUtterance) {
                if (hasHints) {
                    putExtra(
                        RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                        SttConfig.Recognition.SHORT_WITH_HINTS_COMPLETE_SILENCE_MS
                    )
                    putExtra(
                        RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                        SttConfig.Recognition.SHORT_WITH_HINTS_POSSIBLY_COMPLETE_SILENCE_MS
                    )
                } else {
                    putExtra(
                        RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                        SttConfig.Recognition.SHORT_COMPLETE_SILENCE_MS
                    )
                    putExtra(
                        RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                        SttConfig.Recognition.SHORT_POSSIBLY_COMPLETE_SILENCE_MS
                    )
                }
                putExtra(
                    RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                    SttConfig.Recognition.SHORT_MINIMUM_LENGTH_MS
                )
            } else {
                putExtra(
                    RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                    SttConfig.Recognition.LONG_COMPLETE_SILENCE_MS
                )
                putExtra(
                    RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                    SttConfig.Recognition.LONG_POSSIBLY_COMPLETE_SILENCE_MS
                )
                putExtra(
                    RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                    SttConfig.Recognition.LONG_MINIMUM_LENGTH_MS
                )
            }
        }
    }

    private fun muteSystemSounds() {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.let { am ->
                if (originalSystemVolume == -1) {
                    originalSystemVolume = am.getStreamVolume(AudioManager.STREAM_SYSTEM)
                }
                if (originalNotificationVolume == -1) {
                    originalNotificationVolume = am.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
                }
                am.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0)
                am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0)
            }
        } catch (e: Exception) {
            Log.e("RecognizerLifecycle", "muteSystemSounds 실패: ${e.message}")
        }
    }

    private fun restoreSystemSounds() {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.let { am ->
                if (originalSystemVolume >= 0) {
                    am.setStreamVolume(AudioManager.STREAM_SYSTEM, originalSystemVolume, 0)
                }
                if (originalNotificationVolume >= 0) {
                    am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, originalNotificationVolume, 0)
                }
                originalSystemVolume = -1
                originalNotificationVolume = -1
            }
        } catch (e: Exception) {
            Log.e("RecognizerLifecycle", "restoreSystemSounds 실패: ${e.message}")
        }
    }
}

