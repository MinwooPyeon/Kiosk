package com.pixelro.nenoonkiosk.feature.strabismustest

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
import java.util.*

class FudoQuestionFragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = findNavController()
                var showDialog by remember { mutableStateOf(false) }

                StrabismusTestTheme {
                    FudoQuestionScreen(
                        onNextClicked = { answer ->
                            if (answer == 1) {
                                navController.navigate("fudo_adjustment")
                            } else {
                                navController.navigate("fudo_result/$answer/0.0")
                            }
                        },
                        onBackClicked = {
                            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        },
                        onShowHowToClicked = {
                            showDialog = true
                        }
                    )

                    if (showDialog) {
                        FudoHowToDialog(onDismissRequest = { showDialog = false })
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
            val result = tts.setLanguage(Locale.KOREAN)
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
