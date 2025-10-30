package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.InGripManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.DeviceUi
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripAction
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthInspectionNavRoute
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.DynamometerConnectionScreenState
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine

@SuppressLint("MissingPermission")
@Composable
fun GripStrengthStartRoute(
    navController: NavHostController,
    viewModel: InGripViewModel,
    onBack: () -> Unit,
) {
    var screenState by remember { mutableStateOf(DynamometerConnectionScreenState.Standby) }
    var batteryLevel by remember { mutableStateOf<Int?>(null) }
    var isBatteryFetching by remember { mutableStateOf(false) }
    var connecting by remember { mutableStateOf(false) }

    val availableBtDevices = InGripManager.availableDevices.collectAsState()
    val connectionState = InGripManager.connectionState.collectAsState()
    val dynamometerData = InGripManager.dataReceived.collectAsState()

    val context = LocalContext.current
    InGripManager.init(context)

    LaunchedEffect(Unit) {
        viewModel.resetTest()
        TTS.stopTTS()
        TTS.speechTTS(
            StringProvider.getString(R.string.dynamometer_initial_instruction),
            TextToSpeech.QUEUE_ADD
        )
        isBatteryFetching = false
        if (connectionState.value == InGripManager.BluetoothConnectionState.CONNECTED) {
            screenState = DynamometerConnectionScreenState.AwaitingStart
        }
    }


// Auto-scan while in selection, fall back to error after timeout
    LaunchedEffect(screenState) {
        if (screenState == DynamometerConnectionScreenState.DeviceSelection) {
            InGripManager.startScan()
            connecting = true
            delay(10_000)
            if (screenState == DynamometerConnectionScreenState.DeviceSelection) {
                screenState = DynamometerConnectionScreenState.ConnectionError
                connecting = false
                TTS.speechTTS(
                    StringProvider.getString(R.string.dynamometer_connection_error_tts),
                    TextToSpeech.QUEUE_ADD
                )
            }
        }
    }

    LaunchedEffect(dynamometerData.value) {
        val v = dynamometerData.value
        batteryLevel = v?.toInt()
    }

    LaunchedEffect(Unit) {
        combine(
            InGripManager.connectionState,
            InGripManager.availableDevices,
        ) { state, _ -> state }.collect { state ->
            connecting = state is InGripManager.BluetoothConnectionState.CONNECTING
            when (state) {
                is InGripManager.BluetoothConnectionState.CONNECTED -> {
                    connecting = false
                    if (screenState == DynamometerConnectionScreenState.Connecting ||
                        screenState == DynamometerConnectionScreenState.DeviceSelection) {
                        screenState = DynamometerConnectionScreenState.AwaitingStart
                        isBatteryFetching = true
                        TTS.speechTTS(StringProvider.getString(R.string.dynamometer_connected_tts), TextToSpeech.QUEUE_ADD)
                        delay(1000)
                        InGripManager.sendStatusCommand()
                        isBatteryFetching = false
                    }
                }
                is InGripManager.BluetoothConnectionState.DISCONNECTED -> {
                    if (screenState != DynamometerConnectionScreenState.Standby) {
                        screenState = DynamometerConnectionScreenState.DeviceSelection
                        TTS.speechTTS(StringProvider.getString(R.string.dynamometer_disconnected_tts), TextToSpeech.QUEUE_ADD)
                        InGripManager.startScan()
                    }
                }
                is InGripManager.BluetoothConnectionState.ERROR -> {
                    connecting = false
                    if (screenState != DynamometerConnectionScreenState.Standby) {
                        screenState = DynamometerConnectionScreenState.ConnectionError
                        TTS.speechTTS(StringProvider.getString(R.string.dynamometer_connection_error_tts), TextToSpeech.QUEUE_ADD)
                    }
                }
                is InGripManager.BluetoothConnectionState.CONNECTING -> {
                    screenState = DynamometerConnectionScreenState.Connecting
                    connecting = true
                }
                else -> Unit
            }
        }
    }

    val uiState = GripStrengthUiState(
        screenState = screenState,
        batteryPercent = batteryLevel,
        isBatteryFetching = isBatteryFetching,
        isConnecting = connecting,
        availableDevices = availableBtDevices.value.map { d ->
            DeviceUi(
                name = d.name ?: StringProvider.getString(R.string.dynamometer_unknown_device_name),
                address = d.address,
            )
        },
    )
    GripStrengthStartScreen(
        state = uiState,
        onEvent = { ev ->
            when (ev) {
                GripAction.StartScan -> {
                    screenState = DynamometerConnectionScreenState.DeviceSelection
                    InGripManager.startScan()
                }

                is GripAction.SelectDevice -> {
// Find matching BT device by address and connect
                    val target =
                        availableBtDevices.value.firstOrNull { it.address == ev.device.address }
                    if (target != null) {
                        InGripManager.connect(target)
                        screenState = DynamometerConnectionScreenState.Connecting
                    }
                }

                GripAction.RetryScan -> {
                    InGripManager.startScan()
                }

                GripAction.StartTest -> {
                    navController.navigate(GripStrengthInspectionNavRoute.Instructions.name)
                }

                GripAction.Back -> {
                    TTS.tts.stop()
                    onBack()
                }
            }
        },
    )
}