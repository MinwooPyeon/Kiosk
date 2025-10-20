package com.pixelro.nenoonkiosk.feature.iotdevice.InGrip

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.manager.InGripManager
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.BatteryStatus
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

enum class DynamometerConnectionScreenState {
    Standby,
    DeviceSelection,
    Connecting,
    AwaitingStart,
    ConnectionError
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InGripManagmentScreen(navController: NavHostController, viewModel: InGripViewModel = hiltViewModel()) {

    var dynamometerConnectionScreenState by remember { mutableStateOf(
        DynamometerConnectionScreenState.Standby
    ) }

    val dynamometerData by InGripManager.dataReceived.collectAsState()
    var batteryLevel by remember { mutableStateOf<Double?>(null) }
    var isBatteryFetching by remember { mutableStateOf(false) }
    val buttonTimer: CountDownTimer? by remember { mutableStateOf(null) }
    val availableDevices by InGripManager.availableDevices.collectAsState()
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var connecting by remember { mutableStateOf(false) }
    val connectionState = InGripManager.connectionState.collectAsState()

    val context = LocalContext.current
    InGripManager.init(context)

    LaunchedEffect(Unit) {
        viewModel.resetTest()
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
                        dynamometerConnectionScreenState == DynamometerConnectionScreenState.DeviceSelection
                    ) {
                        dynamometerConnectionScreenState =
                            DynamometerConnectionScreenState.AwaitingStart
                        isBatteryFetching = true
                        delay(1000)
                        InGripManager.sendStatusCommand()
                        isBatteryFetching = false
                    }
                }
                is InGripManager.BluetoothConnectionState.DISCONNECTED -> {
                    if (dynamometerConnectionScreenState != DynamometerConnectionScreenState.Standby) {
                        dynamometerConnectionScreenState =
                            DynamometerConnectionScreenState.DeviceSelection
                    }
                    buttonTimer?.cancel()
                    selectedDevice = null
                }
                is InGripManager.BluetoothConnectionState.ERROR -> {
                    connecting = false
                    if (dynamometerConnectionScreenState != DynamometerConnectionScreenState.Standby) {
                        dynamometerConnectionScreenState =
                            DynamometerConnectionScreenState.ConnectionError
                        TTS.speechTTS(StringProvider.getString(R.string.dynamometer_connection_error_tts), TextToSpeech.QUEUE_ADD)
                    }
                }
                is InGripManager.BluetoothConnectionState.CONNECTING -> {
                    dynamometerConnectionScreenState = DynamometerConnectionScreenState.Connecting
                    connecting = true
                }
                else -> { }
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
        StyledText(StringProvider.getString(R.string.dynamometer_title), TextStyle.Title)

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
                            TTS.speechTTS(
                                StringProvider.getString(R.string.dynamometer_searching_tts),
                                TextToSpeech.QUEUE_ADD
                            )
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
                            InGripManager.disconnect()
                            dynamometerConnectionScreenState =
                                DynamometerConnectionScreenState.Standby
                        },
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                        text = StringProvider.getString(R.string.dynamometer_disconnect)
                    )
                }

                DynamometerConnectionScreenState.ConnectionError -> {
                    StyledText(
                        text = StringProvider.getString(R.string.dynamometer_connection_error),
                        style = TextStyle.Error,
                        modifier = Modifier.padding(bottom = 80.dp)
                    )
                    PrimaryButton(
                        onClick = {
                            InGripManager.startScan()
                            dynamometerConnectionScreenState =
                                DynamometerConnectionScreenState.DeviceSelection
                        },
                        text = StringProvider.getString(R.string.dynamometer_try_again)
                    )
                }
            }

            PrimaryButton(
                onClick = {
                    TTS.tts.stop()
                    navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false)
                },
                text = StringProvider.getString(R.string.back)
            )
        }
    }
}