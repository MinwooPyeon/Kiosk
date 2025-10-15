package com.pixelro.nenoonkiosk.test.gripStrength

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.bTManager.InGrip.DynamometerConnectionScreenState
import com.pixelro.nenoonkiosk.bTManager.InGrip.InGripManager
import com.pixelro.nenoonkiosk.bTManager.InGrip.InGripViewModel
import com.pixelro.nenoonkiosk.data.StringProvider
import com.pixelro.nenoonkiosk.ui.components.AccentedText
import com.pixelro.nenoonkiosk.ui.components.BatteryStatus
import com.pixelro.nenoonkiosk.ui.components.PrimaryButton
import com.pixelro.nenoonkiosk.ui.components.ProgressIndicator
import com.pixelro.nenoonkiosk.ui.components.StyledText
import com.pixelro.nenoonkiosk.ui.components.TextStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GripStrengthStartScreen(
    navController: NavHostController,
    viewModel: InGripViewModel,
    onBack: () -> Unit
) {

    var dynamometerConnectionScreenState by remember { mutableStateOf(
        DynamometerConnectionScreenState.Standby) }

    val dynamometerData by InGripManager.dataReceived.collectAsState()
    var batteryLevel by remember { mutableStateOf<Double?>(null) }
    var isBatteryFetching by remember { mutableStateOf(false) }
    val buttonTimer: CountDownTimer? by remember { mutableStateOf(null) }
    val availableDevices by InGripManager.availableDevices.collectAsState()
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    val connectionState = InGripManager.connectionState.collectAsState()
    var connecting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    InGripManager.init(context)

    LaunchedEffect(Unit) {
        viewModel.resetTest()
        TTS.stopTTS()
        TTS.speechTTS(StringProvider.getString(R.string.dynamometer_initial_instruction), TextToSpeech.QUEUE_ADD)
        isBatteryFetching = false
        if (connectionState.value == InGripManager.BluetoothConnectionState.CONNECTED) {
            dynamometerConnectionScreenState = DynamometerConnectionScreenState.AwaitingStart
        }
    }

    LaunchedEffect(dynamometerConnectionScreenState) {
        if (dynamometerConnectionScreenState == DynamometerConnectionScreenState.DeviceSelection) {
            InGripManager.startScan()
            connecting = true

            delay(10000)
            if (dynamometerConnectionScreenState == DynamometerConnectionScreenState.DeviceSelection) {
                dynamometerConnectionScreenState = DynamometerConnectionScreenState.ConnectionError
                connecting = false
                TTS.speechTTS(StringProvider.getString(R.string.dynamometer_connection_error_tts), TextToSpeech.QUEUE_ADD)
            }
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
                    if (dynamometerConnectionScreenState == DynamometerConnectionScreenState.Connecting ||
                        dynamometerConnectionScreenState == DynamometerConnectionScreenState.DeviceSelection) {
                        dynamometerConnectionScreenState = DynamometerConnectionScreenState.AwaitingStart
                        isBatteryFetching = true
                        TTS.speechTTS(StringProvider.getString(R.string.dynamometer_connected_tts), TextToSpeech.QUEUE_ADD)
                        delay(1000)
                        InGripManager.sendStatusCommand()
                        isBatteryFetching = false
                    }
                }
                is InGripManager.BluetoothConnectionState.DISCONNECTED -> {
                    if (dynamometerConnectionScreenState != DynamometerConnectionScreenState.Standby) {
                        dynamometerConnectionScreenState = DynamometerConnectionScreenState.DeviceSelection
                        TTS.speechTTS(StringProvider.getString(R.string.dynamometer_disconnected_tts), TextToSpeech.QUEUE_ADD)
                        InGripManager.startScan()
                    }
                    buttonTimer?.cancel()
                    selectedDevice = null
                }
                is InGripManager.BluetoothConnectionState.ERROR -> {
                    connecting = false
                    if (dynamometerConnectionScreenState != DynamometerConnectionScreenState.Standby) {
                        dynamometerConnectionScreenState = DynamometerConnectionScreenState.ConnectionError
                        TTS.speechTTS(StringProvider.getString(R.string.dynamometer_connection_error_tts), TextToSpeech.QUEUE_ADD)
                    }
                }
                is InGripManager.BluetoothConnectionState.CONNECTING -> {
                    dynamometerConnectionScreenState = DynamometerConnectionScreenState.Connecting
                    connecting = true
                }
                else -> {
                }
            }
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

        BatteryStatus(batteryLevel?.toInt(), hidden = !isBatteryFetching && batteryLevel == null)

        Image(
            painter = painterResource(R.drawable.grip_strength_icon),
            contentDescription = StringProvider.getString(R.string.dynamometer_image_content_description),
            modifier = Modifier.weight(1f).width(500.dp)
        )

        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            when (dynamometerConnectionScreenState) {
                DynamometerConnectionScreenState.Standby -> {
                    AccentedText(
                        prefix = StringProvider.getString(R.string.dynamometer_standby_instruction1),
                        accent = StringProvider.getString(R.string.dynamometer_standby_instruction2),
                        suffix = StringProvider.getString(R.string.dynamometer_standby_instruction3),
                    )
                    PrimaryButton(
                        onClick = {
                            dynamometerConnectionScreenState =
                                DynamometerConnectionScreenState.DeviceSelection
                            InGripManager.startScan()
                        },
                        text = StringProvider.getString(R.string.dynamometer_start_connection),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp)
                    )
                }

                DynamometerConnectionScreenState.DeviceSelection -> {
                    if (availableDevices.isNotEmpty()) {
                        StyledText(
                            StringProvider.getString(R.string.dynamometer_select_device),
                            TextStyle.Message,
                            modifier = Modifier.padding(top = 60.dp)
                        )
                        LazyColumn(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        ) {
                            items(availableDevices) { device ->
                                ListItem(
                                    text = {
                                        StyledText(
                                            text = device.name ?: StringProvider.getString(R.string.dynamometer_unknown_device_name),
                                            textAlign = TextAlign.Start,
                                        )
                                    },
                                    secondaryText = {
                                        StyledText(
                                            device.address,
                                            style = TextStyle.Hint,
                                            textAlign = TextAlign.Start,
                                        )
                                    },
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = colorResource(R.color.gray2),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedDevice = device
                                            InGripManager.connect(device)
                                            dynamometerConnectionScreenState =
                                                DynamometerConnectionScreenState.Connecting
                                        }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        if (connecting) {
                            ProgressIndicator()
                            StyledText(
                                text = StringProvider.getString(R.string.dynamometer_searching),
                                modifier = Modifier.padding(bottom = 180.dp)
                            )
                        } else {
                            StyledText(
                                StringProvider.getString(R.string.dynamometer_not_connected_instruction),
                                style = TextStyle.Error,
                            )
                            PrimaryButton(
                                onClick = { InGripManager.startScan() },
                                text = StringProvider.getString(R.string.dynamometer_retry_connection),
                                modifier = Modifier.padding(top = 180.dp, bottom = 20.dp)
                            )
                        }
                    }
                }

                DynamometerConnectionScreenState.Connecting -> {
                    ProgressIndicator()
                    StyledText(
                        text = StringProvider.getString(R.string.dynamometer_connecting),
                        modifier = Modifier.padding(bottom = 180.dp)
                    )
                }

                DynamometerConnectionScreenState.AwaitingStart -> {
                    AccentedText(
                        prefix = StringProvider.getString(R.string.dynamometer_device_connected1),
                        accent = StringProvider.getString(R.string.dynamometer_device_connected2),
                        suffix = StringProvider.getString(R.string.dynamometer_device_connected3),
                    )
                    PrimaryButton(
                        onClick = {
                            navController.navigate(GripStrengthTestScreen.Instructions.name)
                        },
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                        text = StringProvider.getString(R.string.grip_strength_start_test)
                    )
                }

                DynamometerConnectionScreenState.ConnectionError -> {
                    StyledText(
                        text = StringProvider.getString(R.string.dynamometer_connection_error),
                        style = TextStyle.Error,
                        modifier = Modifier.padding(bottom = 180.dp)
                    )
                    PrimaryButton(
                        onClick = {
                            InGripManager.startScan()
                            dynamometerConnectionScreenState =
                                DynamometerConnectionScreenState.DeviceSelection
                        },
                        text = StringProvider.getString(R.string.dynamometer_try_again),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }
            }

            PrimaryButton(
                onClick = {
                    TTS.tts.stop()
                    onBack()
                },
                text = StringProvider.getString(R.string.back)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422)
@Composable
fun GripStrengthStartScreenPreview() {
    val navController = NavHostController(LocalContext.current)
    val viewModel = InGripViewModel()
    GripStrengthStartScreen(navController, viewModel, {})
}