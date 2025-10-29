package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.core.ui.*
import com.pixelro.nenoonkiosk.core.util.TTS
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@SuppressLint("MissingPermission")
@Composable
fun BP170BConnectionScreen(
    navController: NavHostController,
    viewModel: BP170BViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.collectAsState()

    // SideEffect 처리 (TTS나 네비게이션)
    viewModel.collectSideEffect { effect ->
        when (effect) {
            is BP170BContract.SideEffect.ShowMessage -> {
                TTS.speechTTS(effect.message, TextToSpeech.QUEUE_ADD)
            }
        }
    }

    // 초기 음성 안내
    LaunchedEffect(Unit) {
        TTS.speechTTS(
            context.getString(R.string.blood_pressure_monitor_initial_instruction),
            TextToSpeech.QUEUE_ADD
        )
    }

    // 데이터 수신 로그
    LaunchedEffect(state.dataReceived) {
        state.dataReceived?.let {
            Log.d("BP170BScreen", "Data Received: $it")
        }
    }

    // 연결 상태 변화 → 화면 상태 변경 + 음성 피드백
    LaunchedEffect(state.connectionState) {
        when (state.connectionState) {
            BP170BManager.BluetoothConnectionState.CONNECTED -> {
                TTS.speechTTS(
                    context.getString(R.string.blood_pressure_monitor_connected),
                    TextToSpeech.QUEUE_ADD
                )
            }

            BP170BManager.BluetoothConnectionState.DISCONNECTED -> {
                TTS.speechTTS(
                    context.getString(R.string.blood_pressure_monitor_disconnected),
                    TextToSpeech.QUEUE_ADD
                )
            }

            is BP170BManager.BluetoothConnectionState.ERROR -> {
                TTS.speechTTS(
                    context.getString(R.string.blood_pressure_monitor_connection_failed),
                    TextToSpeech.QUEUE_ADD
                )
            }

            else -> Unit
        }
    }

    BP170BConnectionContent(
        screenState = state.screenState,
        availableDevices = state.availableDevices.map {
            Pair(it.name ?: "Unknown", it.address ?: "")
        },
        onStartScan = { viewModel.onEvent(BP170BContract.Event.StartScan) },
        onRetry = { viewModel.onEvent(BP170BContract.Event.Retry) },
        onSelectDevice = { (_, address) ->
            val device = state.availableDevices.find { it.address == address }
            if (device != null)
                viewModel.onEvent(BP170BContract.Event.SelectDevice(device))
        },
        onDisconnect = { viewModel.onEvent(BP170BContract.Event.Disconnect) },
        onBack = {
            TTS.tts.stop()
            navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false)
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BP170BConnectionContent(
    screenState: BP170BContract.ScreenState,
    availableDevices: List<Pair<String, String>>,
    onStartScan: () -> Unit,
    onRetry: () -> Unit,
    onSelectDevice: (Pair<String, String>) -> Unit,
    onDisconnect: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        StyledText(
            text = stringResource(R.string.blood_pressure_monitor_title),
            style = TextStyle.Title,
        )

        Image(
            painter = painterResource(R.drawable.blood_pressure_icon),
            contentDescription = stringResource(R.string.blood_pressure_monitor_image_content_description),
            modifier = Modifier.weight(1f),
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            when (screenState) {
                BP170BContract.ScreenState.Standby -> {
                    AccentedText(
                        prefix = stringResource(R.string.blood_pressure_monitor_standby_instruction1),
                        accent = stringResource(R.string.blood_pressure_monitor_standby_instruction2),
                        suffix = stringResource(R.string.blood_pressure_monitor_standby_instruction3),
                    )
                    PrimaryButton(
                        onClick = onStartScan,
                        text = stringResource(R.string.blood_pressure_monitor_start_connection),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }

                BP170BContract.ScreenState.Scanning -> {
                    ProgressIndicator()
                    PrimaryButton(
                        onClick = onRetry,
                        text = stringResource(R.string.blood_pressure_monitor_retry_connection),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }

                BP170BContract.ScreenState.DeviceSelection -> {
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_select_device),
                        style = TextStyle.Message,
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .weight(1f),
                    ) {
                        items(availableDevices) { (name, address) ->
                            ListItem(
                                text = { StyledText(text = name, textAlign = TextAlign.Start) },
                                secondaryText = {
                                    StyledText(address, style = TextStyle.Hint, textAlign = TextAlign.Start)
                                },
                                modifier = Modifier
                                    .border(1.dp, colorResource(R.color.gray2), RoundedCornerShape(8.dp))
                                    .clickable { onSelectDevice(name to address) },
                            )
                        }
                    }
                    PrimaryButton(
                        onClick = onRetry,
                        text = stringResource(R.string.blood_pressure_monitor_retry_connection),
                        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                    )
                }

                BP170BContract.ScreenState.Connecting -> {
                    ProgressIndicator()
                }

                BP170BContract.ScreenState.Connected -> {
                    StyledText(text = stringResource(R.string.blood_pressure_monitor_device_connected))
                    PrimaryButton(
                        onClick = onDisconnect,
                        text = stringResource(R.string.blood_pressure_monitor_disconnect),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }

                BP170BContract.ScreenState.ConnectionError -> {
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_connection_failed),
                        style = TextStyle.Error,
                    )
                    PrimaryButton(
                        onClick = onRetry,
                        text = stringResource(R.string.blood_pressure_monitor_try_again),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }
            }

            PrimaryButton(
                onClick = onBack,
                text = stringResource(R.string.back),
            )
        }
    }
}

@Preview(showBackground = true, name = "BP170BConnection Preview", widthDp = 800, heightDp = 1280)
@Composable
fun BP170BConnectionPreview() {
    BP170BConnectionContent(
        screenState = BP170BContract.ScreenState.DeviceSelection,
        availableDevices = listOf(
            "BP170B_01" to "00:11:22:33:44:55",
            "BP170B_02" to "66:77:88:99:AA:BB"
        ),
        onStartScan = {},
        onRetry = {},
        onSelectDevice = {},
        onDisconnect = {},
        onBack = {},
    )
}
