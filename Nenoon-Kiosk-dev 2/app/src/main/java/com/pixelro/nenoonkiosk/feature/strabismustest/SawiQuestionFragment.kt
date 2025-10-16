package com.pixelro.nenoonkiosk.feature.strabismustest

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class SawiQuestionFragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var isTtsReady = false
    private val instructionText = "눈의 정렬에 약간의 어긋남이 있습니다. 추가 검사가 필요합니다. 화면에 보이는 그림을 선택한 후 다음 버튼을 눌러주세요."
    private val TAG = "SawiQuestionFragment_TTS"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = findNavController()
                var showHowToDialog by remember { mutableStateOf(false) }

                StrabismusTestTheme {
                    SawiQuestionScreen(
                        onNextClicked = { answer ->
                            if (answer == 2) {
                                navController.navigate("sawi_adjustment")
                            }
                            else if(answer == 1) {
                                navController.navigate("sawi_result/1/true/0/0/0/0")
                            }
                            else {
                                navController.navigate("sawi_result/$answer/false/0/0/0/0")
                            }
                        },
                        onBackClicked = {
                            parentFragmentManager.popBackStack(
                                null,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                        },
                        testType = FilterInstructionFragment.TEST_TYPE_SAWI,
                    )

                    if (showHowToDialog) {
                        SawiHowToDialog(onDismissRequest = { showHowToDialog = false })
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: TTS 초기화를 시작합니다.")
        tts = TextToSpeech(requireContext(), this)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Fragment가 활성화되었습니다. 음성 출력을 시도합니다.")
        speakInstructions()
    }

    override fun onInit(status: Int) {
        Log.d(TAG, "onInit: TTS 초기화 완료. Status: $status")

        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "onInit: TTS 초기화 성공.")
            val result = tts.setLanguage(Locale.KOREAN)
            Log.d(TAG, "onInit: 한국어 설정 결과: $result")

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "onInit: 한국어 데이터가 없거나 지원되지 않는 언어입니다.")
                Toast.makeText(requireContext(), "음성 안내를 위해 한국어 음성 데이터를 설치해주세요.", Toast.LENGTH_LONG).show()
                // TTS 데이터 설치 화면으로 이동
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent)
            } else {
                isTtsReady = true
                speakInstructions()
            }
        } else {
            Log.e(TAG, "onInit: TTS 초기화 실패! Status: $status")
            Toast.makeText(requireContext(), "음성 안내 기능 초기화에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakInstructions() {
        Log.d(TAG, "speakInstructions: 호출됨. isTtsReady = $isTtsReady, view != null = ${view != null}")
        if (isTtsReady && view != null) {
            Log.d(TAG, "speakInstructions: TTS.speak()를 실행합니다.")
            tts.speak(instructionText, TextToSpeech.QUEUE_FLUSH, null, "fudo_question_instruction")
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