package com.pixelro.nenoonkiosk

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.pixelro.nenoonkiosk.test.presbyopia.PresbyopiaViewModel
import java.util.Locale

object TTS {
    lateinit var tts: TextToSpeech
    fun speechTTS(string: String, queueType: Int) {
        tts.speak(string, queueType, null, "main")
    }
    fun stopTTS() {
        tts.stop()
    }
    fun initTTS(language: String) {
        this.tts = TextToSpeech(NenoonKioskApplication.applicationContext()) {
            if (it == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale(language))
            }
        }
    }
    fun destroyTTS() {
        tts.shutdown()
    }
    fun setOnDoneListener(onDone: () -> Unit) {
        tts.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onError(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onDone()
                }
            }
        )
    }

    fun clearOnDoneListener() {
        tts.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onError(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {}
            }
        )
    }
}