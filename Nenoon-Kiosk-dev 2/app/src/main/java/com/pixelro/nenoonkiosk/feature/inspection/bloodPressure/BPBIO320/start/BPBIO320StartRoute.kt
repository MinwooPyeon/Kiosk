package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.start

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.BPBIO320StartEvent
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.BPBIO320StartUiState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureInspectionNavRoute
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ViewModel
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BloodPressureConnectionScreenState
import kotlinx.coroutines.delay


@SuppressLint("MissingPermission")
@Composable
fun BPBIO320StartRoute(
    navController: NavHostController,
    viewModel: BPBIO320ViewModel,
    onBack: () -> Unit,
) {
    var uiState by remember { mutableStateOf(BPBIO320StartUiState()) }
    var alreadyConnected by remember { mutableStateOf(false) }

    val connectionState by viewModel.connectionState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // 초기 진입: 연결 안내 + 자동 연결 시도
    LaunchedEffect(Unit) {
        if (connectionState != IB_SDKConst.CONNECTED) {
            TTS.speechTTS(
                StringProvider.getString(R.string.blood_pressure_monitor_initial_instruction),
                TextToSpeech.QUEUE_ADD,
            )
            viewModel.resetTest()
            viewModel.selectDevice()
            viewModel.connectDisconnect()
            uiState = uiState.copy(screenState = BloodPressureConnectionScreenState.Connecting)
        } else {
            alreadyConnected = true
            uiState = uiState.copy(screenState = BloodPressureConnectionScreenState.AwaitingStart)
        }
    }

    // Connecting 상태에서 15초 경과 시 장치 제거/리셋
    LaunchedEffect(uiState.screenState) {
        if (uiState.screenState == BloodPressureConnectionScreenState.Connecting) {
            delay(15_000)
            viewModel.removeDevice()
            viewModel.resetTest()
        }
    }

    // 연결 상태 변화 대응
    LaunchedEffect(connectionState) {
        when (connectionState) {
            IB_SDKConst.DISCONNECTED -> {
                if (uiState.screenState != BloodPressureConnectionScreenState.Standby) {
                    uiState = uiState.copy(screenState = BloodPressureConnectionScreenState.SearchingOrIdle)
                    TTS.speechTTS(
                        StringProvider.getString(R.string.blood_pressure_monitor_disconnected),
                        TextToSpeech.QUEUE_ADD,
                    )
                    delay(2_000)
                    viewModel.selectDevice()
                    viewModel.connectDisconnect()
                }
            }
            IB_SDKConst.CONNECTING -> {
                uiState = uiState.copy(screenState = BloodPressureConnectionScreenState.Connecting)
            }
            IB_SDKConst.CONNECTED -> {
                uiState = uiState.copy(screenState = BloodPressureConnectionScreenState.AwaitingStart)
                if (alreadyConnected) {
                    TTS.stopTTS()
                    TTS.speechTTS(
                        StringProvider.getString(R.string.blood_pressure_monitor_connected),
                        TextToSpeech.QUEUE_ADD,
                    )
                }
            }
            else -> Unit
        }
    }

    // 오류 수신 처리
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            if (msg.isNotBlank() && msg != "null") {
                Log.e("BPBIO320StartRoute", "Error: $msg")
                TTS.stopTTS()
                TTS.speechTTS(
                    StringProvider.getString(R.string.blood_pressure_monitor_connection_failed),
                    TextToSpeech.QUEUE_ADD,
                )
                if (connectionState != IB_SDKConst.CONNECTED &&
                    uiState.screenState != BloodPressureConnectionScreenState.Standby
                ) {
                    uiState = uiState.copy(screenState = BloodPressureConnectionScreenState.ConnectionError)
                }
            }
        }
    }

    BPBIO320StartScreen(
        state = uiState,
        onEvent = { ev ->
            when (ev) {
                BPBIO320StartEvent.StartConnect, BPBIO320StartEvent.RetryConnect -> {
                    viewModel.resetTest()
                    viewModel.removeDevice()
                    viewModel.selectDevice()
                    viewModel.connectDisconnect()
                    uiState = uiState.copy(screenState = BloodPressureConnectionScreenState.Connecting)
                }
                BPBIO320StartEvent.StartTest -> {
                    TTS.tts.stop()
                    navController.navigate(BloodPressureInspectionNavRoute.Instructions.name)
                }
                BPBIO320StartEvent.Back -> {
                    TTS.tts.stop()
                    onBack()
                }
            }
        },
    )
}


