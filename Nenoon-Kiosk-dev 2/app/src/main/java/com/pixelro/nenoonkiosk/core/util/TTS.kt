package com.pixelro.nenoonkiosk.core.util

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
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
        // 기존 TTS가 있으면 먼저 shutdown
        if (::tts.isInitialized) {
            Log.d("TTS", "[initTTS] Shutting down existing TTS engine before re-initialization")
            tts.shutdown()
        }

        this.tts = TextToSpeech(NenoonKioskApplication.applicationContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val targetLocale = Locale.forLanguageTag(language)

                // 먼저 해당 언어의 음성 데이터가 실제로 있는지 확인
                val availabilityResult = tts.isLanguageAvailable(targetLocale)

                Log.d("TTS", "[initTTS] Attempting to set language: $language (Locale: $targetLocale)")
                Log.d("TTS", "[initTTS] Language availability check result: $availabilityResult")

                when (availabilityResult) {
                    TextToSpeech.LANG_AVAILABLE, TextToSpeech.LANG_COUNTRY_AVAILABLE, TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> {
                        // 음성 데이터가 있음 - 언어 설정 진행
                        val setResult = tts.setLanguage(targetLocale)
                        if (setResult >= TextToSpeech.LANG_AVAILABLE) {
                            Log.d("TTS", "[initTTS] Successfully set language to: $language")
                        } else {
                            Log.w("TTS", "[initTTS] setLanguage returned unexpected result: $setResult")
                        }
                    }
                    TextToSpeech.LANG_MISSING_DATA -> {
                        Log.w("TTS", "[initTTS] Language $language voice data not installed (LANG_MISSING_DATA)")
                        fallbackToKorean()
                    }
                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        Log.w("TTS", "[initTTS] Language $language not supported (LANG_NOT_SUPPORTED)")
                        fallbackToKorean()
                    }
                    else -> {
                        Log.w("TTS", "[initTTS] Unknown language availability result: $availabilityResult")
                        fallbackToKorean()
                    }
                }
            } else {
                Log.e("TTS", "[initTTS] TextToSpeech initialization failed!")
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

            // 먼저 해당 언어의 음성 데이터가 실제로 있는지 확인
            val availabilityResult = tts.isLanguageAvailable(targetLocale)

            Log.d("TTS", "Attempting to set language: $language (Locale: $targetLocale)")
            Log.d("TTS", "Language availability check result: $availabilityResult")

            when (availabilityResult) {
                TextToSpeech.LANG_AVAILABLE, TextToSpeech.LANG_COUNTRY_AVAILABLE, TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> {
                    // 음성 데이터가 있음 - 언어 설정 진행
                    val setResult = tts.setLanguage(targetLocale)
                    if (setResult >= TextToSpeech.LANG_AVAILABLE) {
                        Log.d("TTS", "Successfully set language to: $language")
                    } else {
                        Log.w("TTS", "setLanguage returned unexpected result: $setResult")
                    }
                }
                TextToSpeech.LANG_MISSING_DATA -> {
                    Log.w("TTS", "Language $language voice data not installed (LANG_MISSING_DATA)")
                    fallbackToKorean()
                }
                TextToSpeech.LANG_NOT_SUPPORTED -> {
                    Log.w("TTS", "Language $language not supported (LANG_NOT_SUPPORTED)")
                    fallbackToKorean()
                }
                else -> {
                    Log.w("TTS", "Unknown language availability result: $availabilityResult")
                    fallbackToKorean()
                }
            }
        }
    }

    private fun fallbackToKorean() {
        Log.d("TTS", "Falling back to Korean...")
        val koreanLocale = Locale.KOREAN
        val koKRLocale = Locale.forLanguageTag("ko-KR")
        val koLocale = Locale.forLanguageTag("ko")

        var result = tts.setLanguage(koreanLocale)

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            result = tts.setLanguage(koKRLocale)
        }

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            result = tts.setLanguage(koLocale)
        }

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w("TTS", "Even Korean is not available, using default locale")
            tts.setLanguage(Locale.getDefault())
        } else {
            Log.d("TTS", "Successfully fell back to Korean")
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
