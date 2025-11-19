package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.inprogress

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.InGripManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthInspectionNavRoute
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthInspectionState
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.result.GripStrengthInspectionResultContract
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripViewModel

@SuppressLint("MissingPermission")
@Composable
fun GripStrengthInProgressRoute(
    navController: NavHostController,
    viewModel: InGripViewModel,
    toResultScreen: (GripStrengthInspectionResultContract) -> Unit,
) {
    var testState by remember { mutableStateOf(GripStrengthInspectionState.RightHandReady) }
    var rightGripValue by remember { mutableStateOf(0.0) }
    var leftGripValue by remember { mutableStateOf(0.0) }
    val testFailed = viewModel.testFailed.collectAsState()


    var countdownValue by remember { mutableStateOf(10) }
    var gripMeasurementTimer: CountDownTimer? by remember { mutableStateOf(null) }


    val dynamometerData = InGripManager.dataReceived.collectAsState()


    fun startGripMeasurementTimer() {
        countdownValue = 10
        gripMeasurementTimer?.cancel()
        gripMeasurementTimer = object : CountDownTimer(10_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownValue = (millisUntilFinished / 1_000).toInt() + 1
            }

            override fun onFinish() {
                InGripManager.sendResultCommand()
                testState = if (testState == GripStrengthInspectionState.RightHand) {
                    GripStrengthInspectionState.RightHandCompleted
                } else {
                    GripStrengthInspectionState.LeftHandCompleted
                }
            }
        }.start()
    }


    DisposableEffect(Unit) {
        onDispose { gripMeasurementTimer?.cancel() }
    }


    LaunchedEffect(Unit) { TTS.stopTTS() }


    LaunchedEffect(dynamometerData.value) {
        val value = dynamometerData.value ?: return@LaunchedEffect
        when (testState) {
            GripStrengthInspectionState.RightHandCompleted -> rightGripValue = value
            GripStrengthInspectionState.LeftHandCompleted -> leftGripValue = value
            else -> Unit
        }
    }
    LaunchedEffect(testState) {
        when (testState) {
            GripStrengthInspectionState.RightHandReady -> {
                TTS.speechTTS(StringProvider.getString(R.string.grip_strength_ready_instruction_button_tts_right), TextToSpeech.QUEUE_ADD)
            }
            GripStrengthInspectionState.RightHandCompleted -> {
                TTS.speechTTS(StringProvider.getString(R.string.grip_strength_right_hand_completed_tts), TextToSpeech.QUEUE_ADD)
                kotlinx.coroutines.delay(5_000)
                testState = GripStrengthInspectionState.LeftHandReady
            }
            GripStrengthInspectionState.LeftHandReady -> {
                TTS.speechTTS(StringProvider.getString(R.string.grip_strength_ready_instruction_button_tts_left), TextToSpeech.QUEUE_ADD)
            }
            GripStrengthInspectionState.LeftHandCompleted -> {
                TTS.speechTTS(StringProvider.getString(R.string.grip_strength_left_hand_completed_tts), TextToSpeech.QUEUE_ADD)
                kotlinx.coroutines.delay(5_000)
                viewModel.setGripValues(rightGripValue, leftGripValue)
                kotlinx.coroutines.delay(2_000)
                if (testFailed.value) {
                    navController.navigate(GripStrengthInspectionNavRoute.Error.name)
                } else {
                    toResultScreen(viewModel.getGripStrengthData())
                }
            }
            else -> Unit
        }
    }

    val uiState = GripInProgressUiState(
        testState = testState,
        rightGripValue = rightGripValue,
        leftGripValue = leftGripValue,
        countdown = countdownValue,
    )

    GripStrengthInProgressScreen(
        state = uiState,
        onEvent = { ev ->
            when (ev) {
                GripInProgressEvent.StartPressed -> {
                    TTS.stopTTS()
                    InGripManager.sendInitializeCommand()
                    if (testState == GripStrengthInspectionState.RightHandReady) {
                        TTS.speechTTS(StringProvider.getString(R.string.grip_strength_right_hand_instruction_tts), TextToSpeech.QUEUE_ADD)
                        testState = GripStrengthInspectionState.RightHand
                    } else {
                        TTS.speechTTS(StringProvider.getString(R.string.grip_strength_left_hand_instruction_tts), TextToSpeech.QUEUE_ADD)
                        testState = GripStrengthInspectionState.LeftHand
                    }
                    startGripMeasurementTimer()
                }
            }
        },
    )
}