package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.bluetooth.BluetoothDevice
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.components.BP170BConnectionStateContent
import com.pixelro.nenoonkiosk.feature.iotdevice.common.DeviceManagementScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BP170BManagementRoute(
    navController: NavHostController,
    viewModel: BP170BViewModel = hiltViewModel(),
    showStartTest: Boolean = false,
    onStartTest: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val availableDevices by viewModel.availableDevices.collectAsState()

    // 초기 상태를 연결 상태에 따라 설정
    val initialState = if (connectionState == BP170BManager.BluetoothConnectionState.CONNECTED) {
        BP170BConnectionUiState.AwaitingStart
    } else {
        BP170BConnectionUiState.Standby
    }

    var screenState by remember { mutableStateOf(initialState) }
    var connecting by remember { mutableStateOf(false) }
    var previousState by remember { mutableStateOf<BP170BConnectionUiState?>(null) }
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    // 초기 TTS
    LaunchedEffect(Unit) {
        val ttsMessage = if (connectionState == BP170BManager.BluetoothConnectionState.CONNECTED) {
            StringProvider.getString(R.string.tts_bp170b_connected_message)
        } else {
            StringProvider.getString(R.string.tts_bp170b_standby_instructions)
        }
        TTS.speechTTS(ttsMessage, TextToSpeech.QUEUE_ADD)
    }

    // DeviceSelection 진입 시 스캔 시작
    LaunchedEffect(screenState) {
        if (screenState == BP170BConnectionUiState.DeviceSelection) {
            viewModel.startScan()
            connecting = true
        }
    }

    // 이전 상태 업데이트 (DeviceSelection 제외)
    LaunchedEffect(screenState) {
        if (screenState != BP170BConnectionUiState.DeviceSelection) {
            previousState = screenState
        }
    }

    // 연결 상태 모니터링
    LaunchedEffect(Unit) {
        viewModel.connectionState.collectLatest { state ->
            connecting = state is BP170BManager.BluetoothConnectionState.CONNECTING
            when (state) {
                is BP170BManager.BluetoothConnectionState.CONNECTED -> {
                    connecting = false
                    if (screenState == BP170BConnectionUiState.Connecting ||
                        screenState == BP170BConnectionUiState.DeviceSelection
                    ) {
                        screenState = BP170BConnectionUiState.AwaitingStart
                        TTS.stopTTS()
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_bp170b_connected_message),
                            TextToSpeech.QUEUE_ADD,
                        )
                    } else if (screenState == BP170BConnectionUiState.TurnOffDevice) {
                        screenState = BP170BConnectionUiState.TurnOffDevice
                    }
                }
                is BP170BManager.BluetoothConnectionState.DISCONNECTED -> {
                    if (screenState != BP170BConnectionUiState.Standby) {
                        screenState = BP170BConnectionUiState.DeviceSelection
                    }
                    selectedDevice = null
                }
                is BP170BManager.BluetoothConnectionState.CONNECTING -> {
                    screenState = BP170BConnectionUiState.Connecting
                    connecting = true
                }
                is BP170BManager.BluetoothConnectionState.ERROR -> {
                    connecting = false
                    if (screenState == BP170BConnectionUiState.Connecting) {
                        screenState = BP170BConnectionUiState.DeviceSelection
                        TTS.stopTTS()
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_bp170b_connection_failed),
                            TextToSpeech.QUEUE_ADD,
                        )
                    } else {
                        screenState = BP170BConnectionUiState.ConnectionError
                        TTS.stopTTS()
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_bp170b_connection_failed),
                            TextToSpeech.QUEUE_ADD,
                        )
                    }
                    Log.e("BP170BManagementRoute", "Connection error: ${state.message}")
                }
                else -> Unit
            }
        }
    }

    // 상태 변화에 따른 TTS 안내 (Standby는 초기 진입 시만 TTS)
    LaunchedEffect(screenState) {
        when (screenState) {
            BP170BConnectionUiState.TurnOffDevice ->
                TTS.speechTTS(StringProvider.getString(R.string.tts_bp170b_turn_off_device_initialization), TextToSpeech.QUEUE_ADD)
            BP170BConnectionUiState.DeviceSelection ->
                TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_searching_device), TextToSpeech.QUEUE_ADD)
            else -> Unit
        }
    }

    val uiState = BP170BManagementUiState(
        connectionState = screenState,
        connecting = connecting,
        availableDevices = availableDevices,
    )

    DeviceManagementScreen(
        titleRes = R.string.blood_pressure_monitor_title,
        imageRes = R.drawable.blood_pressure_icon,
        imageContentDescriptionRes = R.string.blood_pressure_monitor_image_content_description,
        batteryLevel = null,
        hideBattery = true,
        showStartTest = showStartTest,
        onBack = {
            TTS.stopTTS()
            if (onBack != null) {
                onBack()
            } else {
                navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false)
            }
        },
        connectionContent = {
            BP170BConnectionStateContent(
                state = uiState,
                onEvent = { event ->
                    when (event) {
                        is BP170BManagementEvent.StartConnection -> {
                            previousState = screenState
                            screenState = BP170BConnectionUiState.DeviceSelection
                            viewModel.startScan()
                            TTS.speechTTS(
                                StringProvider.getString(R.string.blood_pressure_monitor_searching_device),
                                TextToSpeech.QUEUE_ADD,
                            )
                        }
                        is BP170BManagementEvent.DeviceSelected -> {
                            selectedDevice = event.device
                            previousState = screenState
                            screenState = BP170BConnectionUiState.Connecting
                            viewModel.connectToDevice(event.device)
                        }
                        is BP170BManagementEvent.Disconnect -> {
                            viewModel.disconnect()
                            screenState = BP170BConnectionUiState.Standby
                        }
                        is BP170BManagementEvent.Retry -> {
                            if (connectionState == BP170BManager.BluetoothConnectionState.CONNECTED) {
                                onStartTest?.invoke()
                            } else {
                                selectedDevice?.let { device ->
                                    previousState = screenState
                                    screenState = BP170BConnectionUiState.Connecting
                                    viewModel.connectToDevice(device)
                                } ?: run {
                                    previousState = screenState
                                    screenState = BP170BConnectionUiState.DeviceSelection
                                }
                            }
                        }
                        is BP170BManagementEvent.StartTest -> {
                            onStartTest?.invoke()
                        }
                        is BP170BManagementEvent.Back -> {
                            TTS.stopTTS()
                            if (onBack != null) {
                                onBack()
                            } else {
                                navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false)
                            }
                        }
                    }
                },
                showStartTest = showStartTest,
            )
        },
    )
}
