package com.pixelro.nenoonkiosk.ui.strabismustest

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.strabismustest.ui.theme.StrabismusTestTheme

class SawiAdjustmentFragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var showDialog by remember { mutableStateOf(false) }

                StrabismusTestTheme {
                    SawiAdjustmentScreen(
                        onConfirmClicked = { crosshairPosition, circlePosition ->
                            val navController = findNavController()
                            val isAdjusted = arguments?.getBoolean("isAdjusted", false) ?: false

                            val resultFragment = SawiResultFragment().apply {
                                arguments = Bundle().apply {
                                    putInt("answer", 2) // Indicates adjustment was made
                                    putBoolean("isAdjusted", isAdjusted)
                                    putFloat("crossX", crosshairPosition.x)
                                    putFloat("crossY", crosshairPosition.y)
                                    putFloat("circleX", circlePosition.x)
                                    putFloat("circleY", circlePosition.y)
                                }
                            }
                            navController.navigate("sawi_result/2/true/${crosshairPosition.x}/${crosshairPosition.y}/${circlePosition.x}/${circlePosition.y}")

                        },
                        onBackClicked = {
                            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        },
                        onShowHowToClicked = {
                            showDialog = true
                        }
                    )

                    if (showDialog) {
                        SawiHowToDialog(onDismissRequest = { showDialog = false })
                    }
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
                val text = "화면에 보이는 그림을 선택한 후 다음 버튼을 눌러주세요."
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