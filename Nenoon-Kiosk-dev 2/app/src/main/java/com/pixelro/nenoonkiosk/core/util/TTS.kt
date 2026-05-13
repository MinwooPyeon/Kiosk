package com.pixelro.nenoonkiosk.core.util

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.pixelro.nenoonkiosk.app.NenoonKioskApplication
import java.util.Locale

object TTS {
    private const val TAG = "TTS"
    private const val UTTERANCE_ID = "main"

    private var tts: TextToSpeech? = null

    val isInitialized: Boolean get() = tts != null
    val isSpeaking: Boolean get() = tts?.isSpeaking ?: false

    fun initTTS(language: String) {
        tts?.shutdown()
        tts = TextToSpeech(NenoonKioskApplication.applicationContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                applyLanguage(language)
            } else {
                Log.e(TAG, "TextToSpeech initialization failed")
            }
        }
    }

    fun setLanguage(language: String) {
        if (tts == null) return
        applyLanguage(language)
    }

    fun speechTTS(string: String, queueType: Int) {
        tts?.speak(string, queueType, null, UTTERANCE_ID)
    }

    fun stopTTS() {
        tts?.stop()
    }

    fun destroyTTS() {
        tts?.shutdown()
        tts = null
    }

    fun setOnDoneListener(onDone: () -> Unit) {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onError(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { onDone() }
        })
    }

    fun clearOnDoneListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onError(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {}
        })
    }

    fun forceKoreanLanguage() {
        tts?.setLanguage(Locale.KOREAN)
    }

    fun isKoreanLanguageAvailable(): Boolean {
        val result = tts?.isLanguageAvailable(Locale.KOREAN) ?: return false
        return result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE
    }

    fun openTTSSettings() {
        val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        NenoonKioskApplication.applicationContext().startActivity(intent)
    }

    private fun applyLanguage(language: String) {
        val engine = tts ?: return
        val locale = Locale.forLanguageTag(language)
        Log.d(TAG, "Applying language: $language")

        val availability = engine.isLanguageAvailable(locale)
        if (availability >= TextToSpeech.LANG_AVAILABLE) {
            engine.setLanguage(locale)
            Log.d(TAG, "Language set: $language")
        } else {
            Log.w(TAG, "Language unavailable ($availability), falling back to Korean")
            fallbackToKorean()
        }
    }

    private fun fallbackToKorean() {
        val engine = tts ?: return
        val candidates = listOf(Locale.KOREAN, Locale.forLanguageTag("ko-KR"), Locale.forLanguageTag("ko"))

        for (locale in candidates) {
            val result = engine.setLanguage(locale)
            if (result >= TextToSpeech.LANG_AVAILABLE) {
                Log.d(TAG, "Fell back to Korean: $locale")
                return
            }
        }

        Log.w(TAG, "Korean not available, using default locale")
        engine.setLanguage(Locale.getDefault())
    }
}
