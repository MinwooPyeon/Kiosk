package com.pixelro.nenoonkiosk.core.util

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.pixelro.nenoonkiosk.app.NenoonKioskApplication
import java.util.Locale

object TTS {
    lateinit var tts: TextToSpeech

    fun speechTTS(
        string: String,
        queueType: Int,
    ) {
        if (::tts.isInitialized) {
            tts.speak(string, queueType, null, "main")
        }
    }

    fun stopTTS() {
        if (::tts.isInitialized) {
            tts.stop()
        }
    }
    
    fun isInitialized(): Boolean {
        return ::tts.isInitialized
    }

    fun initTTS(language: String) {
        this.tts = TextToSpeech(NenoonKioskApplication.applicationContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val targetLocale = Locale.forLanguageTag(language)
                var result = tts.setLanguage(targetLocale)
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    val koreanLocale = Locale.KOREAN
                    val koKRLocale = Locale.forLanguageTag("ko-KR")
                    val koLocale = Locale.forLanguageTag("ko")
                    
                    result = tts.setLanguage(koreanLocale)
                    
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        result = tts.setLanguage(koKRLocale)
                    }
                    
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        result = tts.setLanguage(koLocale)
                    }
                    
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.setLanguage(Locale.getDefault())
                    }
                }
            }
        }
    }

    fun destroyTTS() {
        tts.shutdown()
    }

    fun setOnDoneListener(onDone: () -> Unit) {
        if (::tts.isInitialized) {
            clearOnDoneListener() 
            tts.setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onError(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        onDone()
                    }
                },
            )
        }
    }

    fun clearOnDoneListener() {
        if (::tts.isInitialized) {
            tts.setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onError(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {}
                },
            )
        }
    }
    
    fun setLanguage(language: String) {
        if (::tts.isInitialized) {
            val targetLocale = Locale.forLanguageTag(language)
            var result = tts.setLanguage(targetLocale)
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                val koreanLocale = Locale.KOREAN
                val koKRLocale = Locale.forLanguageTag("ko-KR")
                val koLocale = Locale.forLanguageTag("ko")
                
                result = tts.setLanguage(koreanLocale)
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    result = tts.setLanguage(koKRLocale)
                }
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    result = tts.setLanguage(koLocale)
                     }
                
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.getDefault())
                }
            }
        }
    }
    
    fun forceKoreanLanguage() {
        if (::tts.isInitialized) {
            tts.setLanguage(Locale.KOREAN)
        }
    }
    
    fun isKoreanLanguageAvailable(): Boolean {
        return if (::tts.isInitialized) {
            val result = tts.isLanguageAvailable(Locale.KOREAN)
            result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE
        } else {
            false
        }
    }
    
    fun checkAndInstallKoreanTTS(): Boolean {
        return if (::tts.isInitialized) {
            val result = tts.isLanguageAvailable(Locale.KOREAN)
            when (result) {
                TextToSpeech.LANG_AVAILABLE, TextToSpeech.LANG_COUNTRY_AVAILABLE -> true
                TextToSpeech.LANG_MISSING_DATA -> {
                    false
                }
                TextToSpeech.LANG_NOT_SUPPORTED -> {
                    false
                }
                else -> false
            }
        } else {
            false
        }
    }
    
    fun openTTSSettings() {
        val intent = Intent()
        intent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        NenoonKioskApplication.applicationContext().startActivity(intent)
    }
}
