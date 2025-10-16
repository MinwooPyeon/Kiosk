package com.pixelro.nenoonkiosk.test.bloodPressure.BPBIO320

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.feature.screen.iotdevice.BPBIO320.BPBIO320ViewModel
import com.pixelro.nenoonkiosk.feature.screen.iotdevice.BPBIO320.BloodPressureConnectionScreenState
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.test.bloodPressure.BloodPressureTestScreen
import com.pixelro.nenoonkiosk.feature.components.AccentedText
import com.pixelro.nenoonkiosk.feature.components.PrimaryButton
import com.pixelro.nenoonkiosk.feature.components.ProgressIndicator
import com.pixelro.nenoonkiosk.feature.components.StyledText
import com.pixelro.nenoonkiosk.feature.components.TextStyle
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BPBIO320StartScreen(
    navController: NavHostController,
    viewModel: BPBIO320ViewModel,
    onBack: () -> Unit
) {

    var bloodPressureConnectionScreenState by remember { mutableStateOf(BloodPressureConnectionScreenState.Standby) }

    val connectionState by viewModel.connectionState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var alreadyConnected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (connectionState != IB_SDKConst.CONNECTED) {
            TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_initial_instruction), TextToSpeech.QUEUE_ADD)
            viewModel.resetTest()
            viewModel.selectDevice()
            viewModel.connectDisconnect()
            bloodPressureConnectionScreenState = BloodPressureConnectionScreenState.Connecting
        } else {
            alreadyConnected = true
        }
    }

    LaunchedEffect(bloodPressureConnectionScreenState) {
        if (bloodPressureConnectionScreenState == BloodPressureConnectionScreenState.Connecting) {
            delay(15000)
            viewModel.removeDevice()
            viewModel.resetTest()
        }
    }

    LaunchedEffect(connectionState) {
        when (connectionState) {
            IB_SDKConst.DISCONNECTED -> {
                if (bloodPressureConnectionScreenState != BloodPressureConnectionScreenState.Standby) {
                    bloodPressureConnectionScreenState = BloodPressureConnectionScreenState.SearchingOrIdle
                    TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_disconnected), TextToSpeech.QUEUE_ADD)
                    delay(2000)
                    viewModel.selectDevice()
                    viewModel.connectDisconnect()
                }
            }
            IB_SDKConst.CONNECTING -> {
                bloodPressureConnectionScreenState = BloodPressureConnectionScreenState.Connecting
            }
            IB_SDKConst.CONNECTED -> {
                bloodPressureConnectionScreenState = BloodPressureConnectionScreenState.AwaitingStart
                if (alreadyConnected) {
                    TTS.stopTTS()
                    TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_connected), TextToSpeech.QUEUE_ADD)
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            if (it.isNotBlank() && it != "null") {
                Log.e("BPStartScreen", "Error: $it")
                TTS.stopTTS()
                TTS.speechTTS(StringProvider.getString(R.string.blood_pressure_monitor_connection_failed), TextToSpeech.QUEUE_ADD)
                if (connectionState != IB_SDKConst.CONNECTED && bloodPressureConnectionScreenState != BloodPressureConnectionScreenState.Standby) {
                    bloodPressureConnectionScreenState = BloodPressureConnectionScreenState.ConnectionError
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

        Image(
            painter = painterResource(R.drawable.blood_pressure_icon),
            contentDescription = StringProvider.getString(R.string.blood_pressure_monitor_image_content_description),
            modifier = Modifier.weight(1f).width(400.dp).padding(top = 100.dp)
        )

        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            when (bloodPressureConnectionScreenState) {
                BloodPressureConnectionScreenState.Standby -> {
                    AccentedText(
                        prefix = StringProvider.getString(R.string.blood_pressure_monitor_standby_instruction1),
                        accent = StringProvider.getString(R.string.blood_pressure_monitor_standby_instruction2),
                        suffix = StringProvider.getString(R.string.blood_pressure_monitor_standby_instruction3),
                    )
                    PrimaryButton(
                        onClick = {
                            viewModel.resetTest()
                            viewModel.removeDevice()
                            viewModel.selectDevice()
                            viewModel.connectDisconnect()
                            bloodPressureConnectionScreenState =
                                BloodPressureConnectionScreenState.Connecting
                        },
                        text = StringProvider.getString(R.string.blood_pressure_monitor_start_connection),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp)
                    )
                }

                BloodPressureConnectionScreenState.SearchingOrIdle -> {
                    ProgressIndicator()
                    PrimaryButton(
                        onClick = {
                            viewModel.resetTest()
                            viewModel.removeDevice()
                            viewModel.selectDevice()
                            viewModel.connectDisconnect()
                            bloodPressureConnectionScreenState =
                                BloodPressureConnectionScreenState.Connecting
                        },
                        text = StringProvider.getString(R.string.blood_pressure_monitor_retry_connection),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp)
                    )
                }

                BloodPressureConnectionScreenState.Connecting -> {
                    ProgressIndicator()
                    StyledText(
                        text = StringProvider.getString(R.string.blood_pressure_monitor_connecting),
                        modifier = Modifier.padding(top = 40.dp, bottom = 180.dp)
                    )
                }

                BloodPressureConnectionScreenState.AwaitingStart -> {
                    StyledText(
                        text = StringProvider.getString(R.string.blood_pressure_monitor_device_connected),
                    )
                    PrimaryButton(
                        onClick = {
                            TTS.tts.stop()
                            navController.navigate(BloodPressureTestScreen.Instructions.name)
                        },
                        text = StringProvider.getString(R.string.start),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp)
                    )
                }

                BloodPressureConnectionScreenState.ConnectionError -> {
                    StyledText(
                        text = StringProvider.getString(R.string.blood_pressure_monitor_connection_error),
                        style = TextStyle.Error,
                    )
                    PrimaryButton(
                        onClick = {
                            viewModel.removeDevice()
                            viewModel.selectDevice()
                            viewModel.connectDisconnect()
                            bloodPressureConnectionScreenState = BloodPressureConnectionScreenState.Connecting
                        },
                        text = StringProvider.getString(R.string.blood_pressure_monitor_try_again),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp)
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