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
    var previousState by remember { mutableStateOf<BP170BConnectionScreenState?>(null) }
    var selectedDevice by remember { mutableStateOf<android.bluetooth.BluetoothDevice?>(null) }

    val connectionState by viewModel.connectionState.collectAsState()
    val availableDevices by viewModel.availableDevices.collectAsState()

    LaunchedEffect(Unit) {
        screenState = BP170BConnectionScreenState.DeviceCheck
        viewModel.startScan()
        connecting = true
    }

    LaunchedEffect(availableDevices, screenState) {
        if (screenState == BP170BConnectionScreenState.DeviceCheck && availableDevices.isNotEmpty()) {
            previousState = screenState
            screenState = BP170BConnectionScreenState.DeviceSelection
        }
    }

    LaunchedEffect(screenState) {
        if (screenState == BP170BConnectionScreenState.DeviceSelection) {
            if (!connecting && previousState == BP170BConnectionScreenState.Standby) {
                viewModel.startScan()
                connecting = true
            }
        }
    }
    
    // 이전 상태 업데이트 (DeviceSelection 제외)
    LaunchedEffect(screenState) {
        if (screenState != BP170BConnectionScreenState.DeviceSelection) {
            previousState = screenState
        }
    }

    LaunchedEffect(Unit) {
        viewModel.connectionState.collectLatest { state ->
            connecting = state is BP170BManager.BluetoothConnectionState.CONNECTING
            when (state) {
                is BP170BManager.BluetoothConnectionState.CONNECTED -> {
                    connecting = false
                    if (screenState == BP170BConnectionScreenState.Connecting ||
                        screenState == BP170BConnectionScreenState.DeviceSelection
                    ) {
                        screenState = BP170BConnectionScreenState.Standby
                        TTS.stopTTS()
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_bp170b_connected_message),
                            TextToSpeech.QUEUE_ADD,
                        )
                    } else if (screenState == BP170BConnectionScreenState.TurnOffDevice ||
                               screenState == BP170BConnectionScreenState.DeviceCheck
                    ) {
                        screenState = BP170BConnectionScreenState.TurnOffDevice
                    } else {
                        navController.navigate(BloodPressureInspectionNavRoute.InProgress.name)
                    }
                }
                is BP170BManager.BluetoothConnectionState.DISCONNECTED -> {
                    if (screenState == BP170BConnectionScreenState.TurnOffDevice) {
                        screenState = BP170BConnectionScreenState.Standby
                    }
                }
                is BP170BManager.BluetoothConnectionState.CONNECTING -> {
                    screenState = BP170BConnectionScreenState.Connecting
                    connecting = true
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
            BP170BConnectionScreenState.DeviceSelection ->
                TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_searching_device), TextToSpeech.QUEUE_ADD)
            else -> Unit
        }
    }

    val uiState = BP170BStartUiState(
        screenState = screenState,
        isConnecting = connecting,
        availableDevices = availableDevices,
    )

    BP170BStartScreen(
        state = uiState,
        onEvent = { ev ->
            when (ev) {
                BP170BStartEvent.RetryScan -> {
                    if (connectionState == BP170BManager.BluetoothConnectionState.CONNECTED) {
                        navController.navigate(BloodPressureInspectionNavRoute.InProgress.name)
                    } else {
                        selectedDevice?.let { device ->
                            previousState = screenState
                            screenState = BP170BConnectionScreenState.Connecting
                            viewModel.connectToDevice(device)
                        } ?: run {
                            previousState = screenState
                            screenState = BP170BConnectionScreenState.DeviceSelection
                        }
                    }
                }
                is BP170BStartEvent.DeviceSelected -> {
                    selectedDevice = ev.device
                    previousState = screenState
                    screenState = BP170BConnectionScreenState.Connecting
                    viewModel.connectToDevice(ev.device)
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