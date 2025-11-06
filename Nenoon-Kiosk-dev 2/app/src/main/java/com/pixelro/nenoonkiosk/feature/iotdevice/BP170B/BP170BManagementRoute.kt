package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
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
    val currentDevice by viewModel.currentDevice.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // 초기 상태를 연결 상태에 따라 동적으로 계산 (derivedStateOf로 동기화)
    val initialState by remember {
        derivedStateOf {
            when {
                connectionState == BP170BManager.BluetoothConnectionState.CONNECTED ->
                    BP170BConnectionUiState.AwaitingStart
                connectionState == BP170BManager.BluetoothConnectionState.CONNECTING ||
                    (connectionState == BP170BManager.BluetoothConnectionState.DISCONNECTED && currentDevice != null) ->
                    BP170BConnectionUiState.Connecting  // Manager가 device 유지 중이면 재연결 시도 중
                else ->
                    BP170BConnectionUiState.Standby
            }
        }
    }

    var screenState by remember { mutableStateOf(initialState) }
    var connecting by remember { mutableStateOf(false) }
    var previousState by remember { mutableStateOf<BP170BConnectionUiState?>(null) }
    var selectedDevice by remember { mutableStateOf(currentDevice) }  // Manager의 currentDevice로 초기화
    var isAutoReconnecting by remember { mutableStateOf(false) } // 자동 재연결 플래그
    var reconnectAttempts by remember { mutableStateOf(0) } // 재연결 시도 횟수
    val maxReconnectAttempts = 3 // 최대 재연결 시도 횟수

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
                    reconnectAttempts = 0  // 연결 성공 시 시도 횟수 초기화
                    if (isAutoReconnecting) {
                        // 자동 재연결 완료 - 화면 유지, TTS 안내만
                        Log.d("BP170BManagementRoute", "Auto-reconnected successfully, keeping screen state")
                        isAutoReconnecting = false
                    } else if (screenState == BP170BConnectionUiState.Connecting ||
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
                    Log.d("BP170BManagementRoute", "DISCONNECTED - screenState: $screenState, selectedDevice: ${selectedDevice?.name}, attempts: $reconnectAttempts")
                    if (screenState != BP170BConnectionUiState.Standby &&
                        screenState != BP170BConnectionUiState.AwaitingStart
                    ) {
                        // 재연결 시도 횟수 체크
                        if (reconnectAttempts < maxReconnectAttempts) {
                            // 3번까지 자동 재연결 시도
                            reconnectAttempts++
                            isAutoReconnecting = true
                            Log.d("BP170BManagementRoute", "Connection lost, auto-reconnect attempt $reconnectAttempts/$maxReconnectAttempts")

                            coroutineScope.launch {
                                delay(2000) // 2초 대기
                                selectedDevice?.let { device ->
                                    Log.d("BP170BManagementRoute", "Reconnecting to: ${device.name}")
                                    viewModel.connectToDevice(device)
                                } ?: run {
                                    // 선택된 기기 없으면 스캔 시작
                                    Log.d("BP170BManagementRoute", "No selected device, starting scan")
                                    isAutoReconnecting = false
                                    reconnectAttempts = 0
                                    screenState = BP170BConnectionUiState.DeviceSelection
                                }
                            }
                        } else {
                            // 3번 실패 → 에러 화면 표시
                            Log.e("BP170BManagementRoute", "Auto-reconnect failed after $maxReconnectAttempts attempts")
                            isAutoReconnecting = false
                            reconnectAttempts = 0
                            screenState = BP170BConnectionUiState.ConnectionError
                            TTS.stopTTS()
                            TTS.speechTTS(
                                StringProvider.getString(R.string.tts_bp170b_connection_failed),
                                TextToSpeech.QUEUE_ADD,
                            )
                        }
                    } else {
                        // Standby/AwaitingStart에서는 초기화만
                        Log.d("BP170BManagementRoute", "DISCONNECTED in Standby/AwaitingStart state, clearing selectedDevice")
                        selectedDevice = null
                        reconnectAttempts = 0
                    }
                }
                is BP170BManager.BluetoothConnectionState.CONNECTING -> {
                    // 자동 재연결 중이 아닐 때만 화면 상태 변경
                    if (!isAutoReconnecting) {
                        screenState = BP170BConnectionUiState.Connecting
                    }
                    connecting = true
                }
                is BP170BManager.BluetoothConnectionState.ERROR -> {
                    connecting = false
                    Log.e("BP170BManagementRoute", "Connection error: ${state.message}, isAutoReconnecting: $isAutoReconnecting, attempts: $reconnectAttempts")

                    if (isAutoReconnecting) {
                        // 자동 재연결 중 에러 발생 - 조용히 처리 (DISCONNECTED에서 다시 시도)
                        isAutoReconnecting = false
                    } else {
                        // 수동 연결 실패 - 화면 전환
                        reconnectAttempts = 0  // 수동 연결 실패 시 카운트 초기화
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
                    }
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
