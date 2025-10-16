package com.pixelro.nenoonkiosk.test.gripStrength

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.manager.InGripManager
import com.pixelro.nenoonkiosk.feature.screen.iotdevice.InGrip.InGripViewModel
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.feature.components.AccentStyle
import com.pixelro.nenoonkiosk.feature.components.AccentedText
import com.pixelro.nenoonkiosk.feature.components.PrimaryButton
import com.pixelro.nenoonkiosk.feature.components.StyledText
import com.pixelro.nenoonkiosk.feature.components.TextStyle
import kotlinx.coroutines.delay
import kotlin.math.round

enum class TestState {
    RightHandReady,
    RightHand,
    RightHandCompleted,
    LeftHandReady,
    LeftHand,
    LeftHandCompleted,
}

@SuppressLint("CoroutineCreationDuringComposition", "MissingPermission")
@Composable
fun GripStrengthInProgressScreen(
    navController: NavHostController,
    viewModel: InGripViewModel,
    toResultScreen: (GripStrengthTestResult) -> Unit,
) {
    var testState by remember { mutableStateOf(TestState.RightHandReady) }
    var rightGripValue by remember { mutableStateOf(0.0) }
    var leftGripValue by remember { mutableStateOf(0.0) }
    val testFailed by viewModel.testFailed.collectAsState()

    var countdownValue by remember { mutableStateOf(5) }
    var gripMeasurementTimer: CountDownTimer? by remember { mutableStateOf(null) }

    val dynamometerData by InGripManager.dataReceived.collectAsState()

    val animatedRightGrip by animateFloatAsState(targetValue = rightGripValue.toFloat(), animationSpec = tween(1000))
    val animatedLeftGrip by animateFloatAsState(targetValue = leftGripValue.toFloat(), animationSpec = tween(1000))

    fun startGripMeasurementTimer() {
        countdownValue = 10
        gripMeasurementTimer?.cancel()
        gripMeasurementTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownValue = (millisUntilFinished / 1000).toInt() + 1
            }

            override fun onFinish() {
                InGripManager.sendResultCommand()
                testState = if (testState == TestState.RightHand) {
                    TestState.RightHandCompleted
                } else {
                    TestState.LeftHandCompleted
                }
            }
        }.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            gripMeasurementTimer?.cancel()
        }
    }

    LaunchedEffect(Unit) {
        TTS.stopTTS()
    }

    LaunchedEffect(dynamometerData) {
        if (dynamometerData != null) {
            dynamometerData?.let {
                if (testState == TestState.RightHandCompleted) {
                    rightGripValue = it
                } else if (testState == TestState.LeftHandCompleted) {
                    leftGripValue = it
                }
            }
        }
    }

    LaunchedEffect(testState) {
        when (testState) {
            TestState.RightHandReady -> {
                TTS.speechTTS(StringProvider.getString(R.string.grip_strength_ready_instruction_button_tts_right), TextToSpeech.QUEUE_ADD)
            }
            TestState.RightHandCompleted -> {
                TTS.speechTTS(StringProvider.getString(R.string.grip_strength_right_hand_completed_tts), TextToSpeech.QUEUE_ADD)
                delay(5000)
                testState = TestState.LeftHandReady
            }
            TestState.LeftHandReady -> {
                TTS.speechTTS(StringProvider.getString(R.string.grip_strength_ready_instruction_button_tts_left), TextToSpeech.QUEUE_ADD)
            }
            TestState.LeftHandCompleted -> {
                TTS.speechTTS(
                    StringProvider.getString(R.string.grip_strength_left_hand_completed_tts),
                    TextToSpeech.QUEUE_ADD
                )
                delay(5000)
                viewModel.setGripValues(rightGripValue, leftGripValue)
                delay(2000)
                if (testFailed) {
                    navController.navigate(GripStrengthTestScreen.Error.name)
                } else {
                    toResultScreen(viewModel.getGripStrengthData())
                }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .padding(40.dp)
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(1f))

        if (testState == TestState.LeftHandReady) {
            AccentedText(
                prefix = StringProvider.getString(R.string.grip_strength_left_hand_ready_text1),
                accent = StringProvider.getString(R.string.grip_strength_left_hand_ready_text2),
                suffix = StringProvider.getString(R.string.grip_strength_left_hand_ready_text3),
            )
        } else if (testState == TestState.RightHandReady) {
            AccentedText(
                prefix = StringProvider.getString(R.string.grip_strength_right_hand_ready_text1),
                accent = StringProvider.getString(R.string.grip_strength_right_hand_ready_text2),
                suffix = StringProvider.getString(R.string.grip_strength_right_hand_ready_text3),
            )
        } else {
            StyledText(
                text = when (testState) {
                    TestState.RightHandCompleted -> StringProvider.getString(R.string.grip_strength_right_hand_completed_text)
                    TestState.LeftHandCompleted -> StringProvider.getString(R.string.grip_strength_left_hand_completed_text)
                    TestState.RightHand -> StringProvider.getString(R.string.grip_strength_right_hand_instruction_tts)
                    TestState.LeftHand -> StringProvider.getString(R.string.grip_strength_left_hand_instruction_tts)
                    else -> ""
                },
            )
        }
        Spacer(modifier = Modifier.height(64.dp))

        when (testState) {
            TestState.RightHandReady, TestState.LeftHandReady -> {
                StyledText(
                    text = StringProvider.getString(R.string.grip_strength_press_button_and_squeeze_text),
                )
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    text = StringProvider.getString(R.string.grip_strength_start_button),
                    onClick = {
                        TTS.stopTTS()
                        InGripManager.sendInitializeCommand()
                        if (testState == TestState.RightHandReady) {
                            TTS.speechTTS(StringProvider.getString(R.string.grip_strength_right_hand_instruction_tts), TextToSpeech.QUEUE_ADD)
                            testState = TestState.RightHand
                        } else {
                            TTS.speechTTS(StringProvider.getString(R.string.grip_strength_left_hand_instruction_tts), TextToSpeech.QUEUE_ADD)
                            testState = TestState.LeftHand
                        }
                        startGripMeasurementTimer()
                    }
                )
            }
            TestState.RightHand, TestState.LeftHand -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    StyledText(
                        text = "$countdownValue",
                        style = TextStyle.BigNumber
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            TestState.RightHandCompleted -> {
                AccentedText(
                    prefix = StringProvider.getString(R.string.grip_strength_right_hand_value),
                    accent = " ${round(animatedRightGrip * 10.0f) / 10.0f}kg",
                    suffix = "",
                    accentStyle = AccentStyle.Blue
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            TestState.LeftHandCompleted -> {
                AccentedText(
                    prefix = StringProvider.getString(R.string.grip_strength_left_hand_value),
                    accent = " ${round(animatedLeftGrip * 10.0f) / 10.0f}kg",
                    suffix = "",
                    accentStyle = AccentStyle.Blue
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}