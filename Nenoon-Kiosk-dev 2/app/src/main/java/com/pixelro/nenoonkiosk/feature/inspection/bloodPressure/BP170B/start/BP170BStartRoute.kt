package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.start

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
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BConnectionScreenState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BStartEvent
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BStartUiState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureInspectionNavRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun BP170BStartRoute(
    navController: NavHostController,
    viewModel: BP170BViewModel,
    onBack: () -> Unit,
) {
    var screenState by remember { mutableStateOf(BP170BConnectionScreenState.DeviceCheck) }
    var connecting by remember { mutableStateOf(false) }

    val connectionState by viewModel.connectionState.collectAsState()
    val availableDevices by viewModel.availableDevices.collectAsState()

    // 초기 진입: 스캔 시작 + 5초 후 상태 전환 + 스캔 타임아웃 처리
    LaunchedEffect(Unit) {
        screenState = BP170BConnectionScreenState.DeviceCheck
        viewModel.startScan()

        // 5초 후 초기 안내 완료 → 연결 여부에 따라 상태 분기
        launch {
            delay(5_000)
            if (screenState == BP170BConnectionScreenState.TurnOffDevice) {
                screenState = BP170BConnectionScreenState.Standby
            }
            screenState =
                if (connectionState == BP170BManager.BluetoothConnectionState.CONNECTED) {
                    BP170BConnectionScreenState.TurnOffDevice
                } else {
                    BP170BConnectionScreenState.Standby
                }
        }

        // 스캔 종료 타임아웃: SCAN_DURATION 경과 시 뒤로
        launch {
            delay(BP170BManager.SCAN_DURATION.toLong())
            viewModel.disconnect()
            onBack()
        }
    }

    // 연결 상태에 따른 네비/상태 관리
    LaunchedEffect(connectionState) {
        if (connectionState == BP170BManager.BluetoothConnectionState.CONNECTED) {
            if (
                screenState != BP170BConnectionScreenState.TurnOffDevice &&
                screenState != BP170BConnectionScreenState.DeviceCheck
            ) {
                navController.navigate(BloodPressureInspectionNavRoute.InProgress.name)
            } else {
                screenState = BP170BConnectionScreenState.TurnOffDevice
            }
        } else if (
            connectionState == BP170BManager.BluetoothConnectionState.DISCONNECTED &&
            screenState == BP170BConnectionScreenState.TurnOffDevice
        ) {
            screenState = BP170BConnectionScreenState.Standby
            viewModel.startScan()
        }
    }

    // 디바이스 자동 선택/연결
    LaunchedEffect(availableDevices, connectionState) {
        if (availableDevices.isNotEmpty() &&
            connectionState == BP170BManager.BluetoothConnectionState.DISCONNECTED
        ) {
            viewModel.connectToDevice(availableDevices.first())
        }
    }

    // 연결 상태 상세 흐름 (TTS 포함)
    LaunchedEffect(Unit) {
        viewModel.connectionState.collectLatest { state ->
            connecting = state is BP170BManager.BluetoothConnectionState.CONNECTING
            when (state) {
                is BP170BManager.BluetoothConnectionState.CONNECTED -> {
                    if (screenState == BP170BConnectionScreenState.Connecting ||
                        screenState == BP170BConnectionScreenState.Standby
                    ) {
                        screenState = BP170BConnectionScreenState.AwaitingStart
                        TTS.stopTTS()
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_bp170b_connected_message),
                            TextToSpeech.QUEUE_ADD,
                        )
                    }
                }
                is BP170BManager.BluetoothConnectionState.DISCONNECTED -> {
                    if (screenState != BP170BConnectionScreenState.Standby &&
                        screenState != BP170BConnectionScreenState.ConnectionError &&
                        screenState != BP170BConnectionScreenState.DeviceCheck
                    ) {
                        screenState = BP170BConnectionScreenState.Standby
                        viewModel.startScan()
                    }
                }
                is BP170BManager.BluetoothConnectionState.ERROR -> {
                    screenState = BP170BConnectionScreenState.ConnectionError
                    TTS.stopTTS()
                    TTS.speechTTS(
                        StringProvider.getString(R.string.tts_bp170b_connection_failed),
                        TextToSpeech.QUEUE_ADD,
                    )
                    Log.e("BP170BStartRoute", "Connection error: ${state.message}")
                }
                else -> Unit
            }
        }
    }

    // 상태 변화에 따른 TTS 안내
    LaunchedEffect(screenState) {
        when (screenState) {
            BP170BConnectionScreenState.DeviceCheck ->
                TTS.speechTTS(StringProvider.getString(R.string.tts_bp170b_device_check), TextToSpeech.QUEUE_ADD)
            BP170BConnectionScreenState.TurnOffDevice ->
                TTS.speechTTS(StringProvider.getString(R.string.tts_bp170b_turn_off_device_initialization), TextToSpeech.QUEUE_ADD)
            BP170BConnectionScreenState.Standby ->
                TTS.speechTTS(StringProvider.getString(R.string.tts_bp170b_standby_instructions), TextToSpeech.QUEUE_ADD)
            else -> Unit
        }
    }

    val uiState = BP170BStartUiState(
        screenState = screenState,
        isConnecting = connecting,
    )

    BP170BStartScreen(
        state = uiState,
        onEvent = { ev ->
            when (ev) {
                BP170BStartEvent.RetryScan -> {
                    screenState = BP170BConnectionScreenState.Standby
                    viewModel.startScan()
                }
                BP170BStartEvent.Back -> {
                    TTS.tts.stop()
                    viewModel.disconnect()
                    onBack()
                }
            }
        },
    )
}