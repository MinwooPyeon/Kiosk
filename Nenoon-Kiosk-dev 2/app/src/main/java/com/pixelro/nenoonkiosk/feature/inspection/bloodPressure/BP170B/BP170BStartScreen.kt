package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.ui.InstructionItem
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class BP170BConnectionScreenState {
    DeviceCheck,
    Standby,
    Connecting,
    AwaitingStart,
    ConnectionError,
    TurnOffDevice,
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BP170BStartScreen(
    navController: NavHostController,
    viewModel: BP170BViewModel,
    onBack: () -> Unit,
) {

    var bp170bConnectionScreenState by remember { mutableStateOf(BP170BConnectionScreenState.DeviceCheck) }

    val connectionState by viewModel.connectionState.collectAsState()
    val availableDevices by viewModel.availableDevices.collectAsState()
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var connecting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Initialize the BP170BManager
    LaunchedEffect(Unit) {

        bp170bConnectionScreenState = BP170BConnectionScreenState.DeviceCheck
        viewModel.startScan()

        coroutineScope.launch {

            delay(5000)

            if (bp170bConnectionScreenState == BP170BConnectionScreenState.TurnOffDevice) {
                bp170bConnectionScreenState = BP170BConnectionScreenState.Standby
            }

            bp170bConnectionScreenState = if (connectionState == BP170BManager.BluetoothConnectionState.CONNECTED) {
                BP170BConnectionScreenState.TurnOffDevice
            } else {
                BP170BConnectionScreenState.Standby
            }
        }

        coroutineScope.launch {
            delay(BP170BManager.SCAN_DURATION.toLong())
            viewModel.disconnect()
            onBack()
        }
    }

    // Effect to start scan when entering DeviceSelection state
    LaunchedEffect(connectionState) {
        if (connectionState == BP170BManager.BluetoothConnectionState.CONNECTED) {

            if (bp170bConnectionScreenState != BP170BConnectionScreenState.TurnOffDevice &&
                bp170bConnectionScreenState != BP170BConnectionScreenState.DeviceCheck) {
                navController.navigate(BloodPressureTestScreen.InProgress.name)

            } else {
                bp170bConnectionScreenState = BP170BConnectionScreenState.TurnOffDevice
            }

        } else if (connectionState == BP170BManager.BluetoothConnectionState.DISCONNECTED &&
            bp170bConnectionScreenState == BP170BConnectionScreenState.TurnOffDevice) {
            bp170bConnectionScreenState = BP170BConnectionScreenState.Standby
            viewModel.startScan()
        }
    }

    LaunchedEffect(availableDevices, connectionState) {
        if (availableDevices.isNotEmpty() && connectionState == BP170BManager.BluetoothConnectionState.DISCONNECTED) {
            selectedDevice = availableDevices[0] // automatically choose first device
            viewModel.connectToDevice(availableDevices[0])
        }
    }

    // Observe connection state changes from BP170BManager
    LaunchedEffect(Unit) {
        viewModel.connectionState.collectLatest { state ->
            connecting = state is BP170BManager.BluetoothConnectionState.CONNECTING
            when (state) {
                is BP170BManager.BluetoothConnectionState.CONNECTED -> {
                    if (bp170bConnectionScreenState == BP170BConnectionScreenState.Connecting ||
                        bp170bConnectionScreenState == BP170BConnectionScreenState.Standby
                    ) {
                        bp170bConnectionScreenState = BP170BConnectionScreenState.AwaitingStart
                        TTS.stopTTS()
                        TTS.speechTTS(StringProvider.getString(R.string.tts_bp170b_connected_message), TextToSpeech.QUEUE_ADD)
                    }
                }
                is BP170BManager.BluetoothConnectionState.DISCONNECTED -> {
                    if (bp170bConnectionScreenState != BP170BConnectionScreenState.Standby &&
                        bp170bConnectionScreenState != BP170BConnectionScreenState.ConnectionError &&
                        bp170bConnectionScreenState != BP170BConnectionScreenState.DeviceCheck
                    ) {
                        bp170bConnectionScreenState = BP170BConnectionScreenState.Standby
                        viewModel.startScan() // Restart scan after disconnection
                    }
                    selectedDevice = null
                }
                is BP170BManager.BluetoothConnectionState.ERROR -> {
                    bp170bConnectionScreenState = BP170BConnectionScreenState.ConnectionError
                    TTS.stopTTS()
                    TTS.speechTTS(StringProvider.getString(R.string.tts_bp170b_connection_failed), TextToSpeech.QUEUE_ADD)
                    Log.e("BP170BStartScreen", "Connection error: ${state.message}")
                }
                else -> {
                }
            }
        }
    }

    LaunchedEffect(bp170bConnectionScreenState) {
        when (bp170bConnectionScreenState) {
            BP170BConnectionScreenState.DeviceCheck -> {
                TTS.speechTTS(StringProvider.getString(R.string.tts_bp170b_device_check), TextToSpeech.QUEUE_ADD)
            }
            BP170BConnectionScreenState.TurnOffDevice -> {
                TTS.speechTTS(StringProvider.getString(R.string.tts_bp170b_turn_off_device_initialization), TextToSpeech.QUEUE_ADD)
            }
            BP170BConnectionScreenState.Standby -> {
                TTS.speechTTS(StringProvider.getString(R.string.tts_bp170b_standby_instructions), TextToSpeech.QUEUE_ADD)
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            when (bp170bConnectionScreenState) {
                BP170BConnectionScreenState.Standby, BP170BConnectionScreenState.Connecting, BP170BConnectionScreenState.AwaitingStart -> {
                    InstructionItem(
                        titleText = StringProvider.getString(R.string.bp170b_step_1_title),
                        prefix = StringProvider.getString(R.string.bp170b_step_1_prefix),
                        accent = StringProvider.getString(R.string.bp170b_step_1_accent),
                        suffix = StringProvider.getString(R.string.bp170b_step_1_suffix),
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    InstructionItem(
                        titleText = StringProvider.getString(R.string.bp170b_step_2_title),
                        prefix = StringProvider.getString(R.string.bp170b_step_2_prefix),
                        accent = StringProvider.getString(R.string.bp170b_step_2_accent),
                        suffix = StringProvider.getString(R.string.bp170b_step_2_suffix),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                BP170BConnectionScreenState.ConnectionError -> {
                    Spacer(modifier = Modifier.weight(1f))
                    StyledText(
                        text = StringProvider.getString(R.string.blood_pressure_monitor_connection_error),
                        style = TextStyle.Error,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrimaryButton(
                        onClick = {
                            bp170bConnectionScreenState =
                                BP170BConnectionScreenState.Standby
                            viewModel.startScan()
                        },
                        text = StringProvider.getString(R.string.blood_pressure_monitor_try_again),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp)
                    )
                }

                BP170BConnectionScreenState.TurnOffDevice -> {
                    Spacer(modifier = Modifier.weight(1f))
                    StyledText(
                        text = StringProvider.getString(R.string.bp170b_initialization_required),
                        style = TextStyle.Error,
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    StyledText(
                        text = StringProvider.getString(R.string.bp170b_turn_off_bp_monitor),
                        style = TextStyle.Error,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                BP170BConnectionScreenState.DeviceCheck -> {
                    Spacer(modifier = Modifier.weight(1f))
                    StyledText(
                        text = StringProvider.getString(R.string.blood_pressure_monitor_searching_device),
                        style = TextStyle.Message,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ProgressIndicator()
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

                PrimaryButton(
                onClick = {
                    TTS.tts.stop()
                    viewModel.disconnect()
                    onBack()
                },
                text = StringProvider.getString(R.string.back)
            )
        }
    }
}