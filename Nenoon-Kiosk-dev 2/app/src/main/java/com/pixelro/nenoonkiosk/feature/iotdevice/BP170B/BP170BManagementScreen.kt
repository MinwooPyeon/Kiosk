package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS

enum class BP170BConnectionScreenState {
    Standby,
    Scanning,
    DeviceSelection,
    Connecting,
    Connected,
    ConnectionError,
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BP170BConnectionScreen(
    navController: NavHostController,
    viewModel: BP170BViewModel = hiltViewModel(),
) {
    var bp170bConnectionScreenState by rememberSaveable { mutableStateOf(BP170BConnectionScreenState.Standby) }

    val connectionState by viewModel.connectionState.collectAsState()
    val dataReceived by viewModel.dataReceived.collectAsState()
    val availableDevices by viewModel.availableDevices.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_initial_instruction), TextToSpeech.QUEUE_ADD)
    }

    LaunchedEffect(connectionState) {
        Log.d("BP170BScreen", "connectionState: $connectionState, screenState: $bp170bConnectionScreenState")

        when (connectionState) {
            BP170BManager.BluetoothConnectionState.DISCONNECTED -> {
                if (bp170bConnectionScreenState != BP170BConnectionScreenState.Standby &&
                    bp170bConnectionScreenState != BP170BConnectionScreenState.ConnectionError
                ) {
                    bp170bConnectionScreenState = BP170BConnectionScreenState.ConnectionError
                    TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_disconnected), TextToSpeech.QUEUE_ADD)
                }
            }
            BP170BManager.BluetoothConnectionState.CONNECTING -> {
                bp170bConnectionScreenState = BP170BConnectionScreenState.Connecting
            }
            BP170BManager.BluetoothConnectionState.CONNECTED -> {
                bp170bConnectionScreenState = BP170BConnectionScreenState.Connected
                TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_connected), TextToSpeech.QUEUE_ADD)
            }
            is BP170BManager.BluetoothConnectionState.ERROR -> {
                bp170bConnectionScreenState = BP170BConnectionScreenState.ConnectionError
                TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_connection_failed), TextToSpeech.QUEUE_ADD)
                Log.e("BP170BScreen", "Connection error: ${(connectionState as BP170BManager.BluetoothConnectionState.ERROR).message}")
            }
            else -> { }
        }
    }

    LaunchedEffect(dataReceived) {
        dataReceived?.let {
            Log.d("BP170BScreen", "Data Received: $it")
        }
    }

    LaunchedEffect(availableDevices) {
        if (bp170bConnectionScreenState == BP170BConnectionScreenState.Scanning && availableDevices.isNotEmpty()) {
            bp170bConnectionScreenState = BP170BConnectionScreenState.DeviceSelection
            TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_select_device), TextToSpeech.QUEUE_ADD)
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        StyledText(
            StringProvider.getString(
                R.string.blood_pressure_monitor_title,
            ),
            TextStyle.Title,
        )

        Image(
            painter = painterResource(R.drawable.blood_pressure_icon),
            contentDescription =
                StringProvider.getString(
                    R.string.blood_pressure_monitor_image_content_description,
                ),
            modifier = Modifier.weight(1f),
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            when (bp170bConnectionScreenState) {
                BP170BConnectionScreenState.Standby -> {
                    AccentedText(
                        prefix =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_standby_instruction1,
                            ),
                        accent =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_standby_instruction2,
                            ),
                        suffix =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_standby_instruction3,
                            ),
                    )
                    PrimaryButton(
                        onClick = {
                            viewModel.startScan()
                            bp170bConnectionScreenState = BP170BConnectionScreenState.Scanning
                        },
                        text =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_start_connection,
                            ),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }

                BP170BConnectionScreenState.Scanning -> {
                    ProgressIndicator()
                    PrimaryButton(
                        onClick = {
                            viewModel.startScan()
                        },
                        text =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_retry_connection,
                            ),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }

                BP170BConnectionScreenState.DeviceSelection -> {
                    StyledText(
                        StringProvider.getString(
                            R.string.blood_pressure_monitor_select_device,
                        ),
                        TextStyle.Message,
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                                .weight(1f),
                    ) {
                        items(availableDevices) { device ->
                            ListItem(
                                text = {
                                    StyledText(
                                        text =
                                            device.name ?: StringProvider.getString(
                                                R.string.dynamometer_unknown_device_name,
                                            ),
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
                                modifier =
                                    Modifier
                                        .border(
                                            width = 1.dp,
                                            color = colorResource(R.color.gray2),
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                        .clickable {
                                            viewModel.connectToDevice(device)
                                            bp170bConnectionScreenState =
                                                BP170BConnectionScreenState.Connecting
                                        },
                            )
                        }
                    }
                    PrimaryButton(
                        onClick = {
                            viewModel.startScan()
                            bp170bConnectionScreenState = BP170BConnectionScreenState.Scanning
                        },
                        text =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_retry_connection,
                            ),
                        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                    )
                }

                BP170BConnectionScreenState.Connecting -> {
                    ProgressIndicator()
                }

                BP170BConnectionScreenState.Connected -> {
                    StyledText(
                        text =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_device_connected,
                            ),
                    )
                    PrimaryButton(
                        onClick = {
                            viewModel.disconnect()
                            bp170bConnectionScreenState = BP170BConnectionScreenState.Standby
                            TTS.speechTTS("장치 연결을 해제합니다.", TextToSpeech.QUEUE_ADD) // Hardcoded TTS
                        },
                        text =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_disconnect,
                            ),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }

                BP170BConnectionScreenState.ConnectionError -> {
                    StyledText(
                        text =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_connection_failed,
                            ),
                        style = TextStyle.Error,
                    )
                    PrimaryButton(
                        onClick = {
                            viewModel.startScan()
                            bp170bConnectionScreenState = BP170BConnectionScreenState.Scanning
                        },
                        text =
                            StringProvider.getString(
                                R.string.blood_pressure_monitor_try_again,
                            ),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }
            }

            PrimaryButton(
                onClick = {
                    TTS.tts.stop()
                    navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false)
                },
                text = StringProvider.getString(R.string.back),
            )
        }
    }
}
