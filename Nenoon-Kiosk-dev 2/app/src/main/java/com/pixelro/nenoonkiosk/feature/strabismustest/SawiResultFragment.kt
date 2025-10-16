package com.pixelro.nenoonkiosk.feature.strabismustest

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.strabismustest.ui.theme.StrabismusTestTheme

class SawiResultFragment : Fragment(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                StrabismusTestTheme {
                    val answer = arguments?.getInt("answer")
                    val isAdjusted = arguments?.getBoolean("isAdjusted") ?: false
                    val crossX = arguments?.getFloat("crossX")
                    val crossY = arguments?.getFloat("crossY")
                    val circleX = arguments?.getFloat("circleX")
                    val circleY = arguments?.getFloat("circleY")

                    SawiResultScreen(
                        answer = answer,
                        isAdjusted = isAdjusted,
                        crossX = crossX,
                        crossY = crossY,
                        circleX = circleX,
                        circleY = circleY,
                        onPrintClicked = {
                            Toast.makeText(requireContext(), "프린트 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        },
                        onBackToMainClicked = {
                            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tts = TextToSpeech(requireContext(), this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(java.util.Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle language not supported error
            } else {
                val text = "검사가 완료되었습니다 검사 결과를 확인해주세요"
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "fudo_question_instruction")
            }
        } else {
            // Handle TTS initialization error
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}