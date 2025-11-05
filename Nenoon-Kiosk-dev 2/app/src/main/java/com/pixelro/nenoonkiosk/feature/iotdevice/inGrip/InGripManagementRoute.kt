package com.pixelro.nenoonkiosk.feature.iotdevice.inGrip

import android.bluetooth.BluetoothDevice
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.InGripManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.iotdevice.common.DeviceManagementScreen
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.components.InGripConnectionStateContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun InGripManagementRoute(
    navController: NavHostController,
    viewModel: InGripViewModel = hiltViewModel(),
    showStartTest: Boolean = false,
    onStartTest: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    val dynamometerData by InGripManager.dataReceived.collectAsState()
    var batteryLevel by remember { mutableStateOf<Double?>(null) }
    var isBatteryFetching by remember { mutableStateOf(false) }
    val buttonTimer: CountDownTimer? by remember { mutableStateOf(null) }
    val availableDevices by InGripManager.availableDevices.collectAsState()
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var connecting by remember { mutableStateOf(false) }
    val connectionState = InGripManager.connectionState.collectAsState()

    val context = LocalContext.current

    // 초기 상태를 연결 상태에 따라 설정
    val initialState = if (connectionState.value == InGripManager.BluetoothConnectionState.CONNECTED) {
        InGripConnectionUiState.AwaitingStart
    } else {
        InGripConnectionUiState.Standby
    }

    var dynamometerConnectionScreenState by remember { mutableStateOf(initialState) }

    LaunchedEffect(Unit) {
        if (!InGripManager.isInitialized.value) {
            InGripManager.init(context)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetTest()
        val ttsMessage = if (connectionState.value == InGripManager.BluetoothConnectionState.CONNECTED) {
            StringProvider.getString(R.string.dynamometer_connected_tts)
        } else {
            StringProvider.getString(R.string.dynamometer_initial_instruction)
        }
        TTS.speechTTS(ttsMessage, TextToSpeech.QUEUE_ADD)
        isBatteryFetching = false
    }

    LaunchedEffect(dynamometerConnectionScreenState) {
        if (dynamometerConnectionScreenState == InGripConnectionUiState.DeviceSelection) {
            InGripManager.startScan()
            connecting = true
        }
    }

    LaunchedEffect(dynamometerData) {
        batteryLevel = dynamometerData
    }

    LaunchedEffect(Unit) {
        InGripManager.connectionState.collectLatest { state ->
            connecting = state is InGripManager.BluetoothConnectionState.CONNECTING
            when (state) {
                is InGripManager.BluetoothConnectionState.CONNECTED -> {
                    connecting = false
                    if (dynamometerConnectionScreenState == InGripConnectionUiState.Connecting ||
                        dynamometerConnectionScreenState == InGripConnectionUiState.DeviceSelection
                    ) {
                        dynamometerConnectionScreenState =
                            InGripConnectionUiState.AwaitingStart
                        isBatteryFetching = true
                        delay(1000)
                        InGripManager.sendStatusCommand()
                        isBatteryFetching = false
                    }
                }
                is InGripManager.BluetoothConnectionState.DISCONNECTED -> {
                    if (dynamometerConnectionScreenState != InGripConnectionUiState.Standby) {
                        dynamometerConnectionScreenState =
                            InGripConnectionUiState.DeviceSelection
                    }
                    buttonTimer?.cancel()
                    selectedDevice = null
                }
                is InGripManager.BluetoothConnectionState.ERROR -> {
                    connecting = false
                    if (dynamometerConnectionScreenState != InGripConnectionUiState.Standby) {
                        dynamometerConnectionScreenState =
                            InGripConnectionUiState.ConnectionError
                        TTS.speechTTS(StringProvider.getString(R.string.dynamometer_connection_error_tts), TextToSpeech.QUEUE_ADD)
                    }
                }
                is InGripManager.BluetoothConnectionState.CONNECTING -> {
                    dynamometerConnectionScreenState = InGripConnectionUiState.Connecting
                    connecting = true
                }
                else -> { }
            }
        }
    }

    val uiState = InGripManagementUiState(
        connectionState = dynamometerConnectionScreenState,
        batteryLevel = batteryLevel?.toInt(),
        isBatteryFetching = isBatteryFetching,
        availableDevices = availableDevices,
        connecting = connecting,
    )

    DeviceManagementScreen(
        titleRes = R.string.dynamometer_title,
        imageRes = R.drawable.grip_strength_icon,
        imageContentDescriptionRes = R.string.dynamometer_image_content_description,
        batteryLevel = uiState.batteryLevel,
        hideBattery = !uiState.isBatteryFetching && uiState.batteryLevel == null,
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
            InGripConnectionStateContent(
                state = uiState,
                onEvent = { event ->
                    when (event) {
                        is InGripManagementEvent.StartConnection -> {
                            dynamometerConnectionScreenState = InGripConnectionUiState.DeviceSelection
                            InGripManager.startScan()
                            TTS.speechTTS(
                                StringProvider.getString(R.string.dynamometer_searching_tts),
                                TextToSpeech.QUEUE_ADD,
                            )
                        }
                        is InGripManagementEvent.DeviceSelected -> {
                            selectedDevice = event.device
                            InGripManager.connect(event.device)
                            dynamometerConnectionScreenState = InGripConnectionUiState.Connecting
                        }
                        is InGripManagementEvent.Disconnect -> {
                            InGripManager.disconnect()
                            dynamometerConnectionScreenState = InGripConnectionUiState.Standby
                        }
                        is InGripManagementEvent.Retry -> {
                            InGripManager.startScan()
                            dynamometerConnectionScreenState = InGripConnectionUiState.DeviceSelection
                        }
                        is InGripManagementEvent.StartTest -> {
                            onStartTest?.invoke()
                        }
                        is InGripManagementEvent.Back -> {
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