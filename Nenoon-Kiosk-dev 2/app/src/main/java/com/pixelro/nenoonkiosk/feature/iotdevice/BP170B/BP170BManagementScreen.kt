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
    val uiState by viewModel.uiState.collectAsState() // ✅ 한 줄로 통합
    var screenState by remember { mutableStateOf(BP170BConnectionScreenState.Standby) }

    val context = LocalContext.current

    // 초기 안내 음성
    LaunchedEffect(Unit) {
        TTS.speechTTS(
            context.getString(R.string.blood_pressure_monitor_initial_instruction),
            TextToSpeech.QUEUE_ADD
        )
    }

    // 연결 상태 변화 감지 → 화면 상태 반영 + TTS
    LaunchedEffect(uiState.connectionState) {
        when (uiState.connectionState) {
            BP170BManager.BluetoothConnectionState.DISCONNECTED -> {
                if (screenState != BP170BConnectionScreenState.Standby &&
                    screenState != BP170BConnectionScreenState.ConnectionError
                ) {
                    screenState = BP170BConnectionScreenState.ConnectionError
                    TTS.speechTTS(
                        context.getString(R.string.blood_pressure_monitor_disconnected),
                        TextToSpeech.QUEUE_ADD
                    )
                }
            }

            BP170BManager.BluetoothConnectionState.CONNECTING -> {
                screenState = BP170BConnectionScreenState.Connecting
            }

            BP170BManager.BluetoothConnectionState.CONNECTED -> {
                screenState = BP170BConnectionScreenState.Connected
                TTS.speechTTS(
                    context.getString(R.string.blood_pressure_monitor_connected),
                    TextToSpeech.QUEUE_ADD
                )
            }

            is BP170BManager.BluetoothConnectionState.ERROR -> {
                screenState = BP170BConnectionScreenState.ConnectionError
                TTS.speechTTS(
                    context.getString(R.string.blood_pressure_monitor_connection_failed),
                    TextToSpeech.QUEUE_ADD
                )
                Log.e(
                    "BP170BScreen",
                    "Connection error: ${(uiState.connectionState as BP170BManager.BluetoothConnectionState.ERROR).message}"
                )
            }

            else -> {}
        }
    }

    // 데이터 수신 로그
    LaunchedEffect(uiState.dataReceived) {
        uiState.dataReceived?.let {
            Log.d("BP170BScreen", "Data Received: $it")
        }
    }

    // 스캔 중 기기 발견 → 선택 화면으로 전환
    LaunchedEffect(uiState.availableDevices) {
        if (screenState == BP170BConnectionScreenState.Scanning &&
            uiState.availableDevices.isNotEmpty()
        ) {
            screenState = BP170BConnectionScreenState.DeviceSelection
            TTS.speechTTS(
                context.getString(R.string.blood_pressure_monitor_select_device),
                TextToSpeech.QUEUE_ADD
            )
        }
    }

    BP170BConnectionContent(
        screenState = screenState,
        availableDevices = uiState.availableDevices.map {
            Pair(it.name ?: "Unknown", it.address ?: "")
        },
        onStartScan = {
            viewModel.startScan()
            screenState = BP170BConnectionScreenState.Scanning
        },
        onRetry = {
            viewModel.startScan()
            screenState = BP170BConnectionScreenState.Scanning
        },
        onSelectDevice = { (_, address) ->
            val device = uiState.availableDevices.find { it.address == address }
            if (device != null) {
                viewModel.connectToDevice(device)
                screenState = BP170BConnectionScreenState.Connecting
            }
        },
        onDisconnect = {
            viewModel.disconnect()
            screenState = BP170BConnectionScreenState.Standby
            TTS.speechTTS("장치 연결을 해제합니다.", TextToSpeech.QUEUE_ADD)
        },
        onBack = {
            TTS.tts.stop()
            navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false)
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BP170BConnectionContent(
    screenState: BP170BConnectionScreenState,
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
                BP170BConnectionScreenState.Standby -> {
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

                BP170BConnectionScreenState.Scanning -> {
                    ProgressIndicator()
                    PrimaryButton(
                        onClick = onRetry,
                        text = stringResource(R.string.blood_pressure_monitor_retry_connection),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }

                BP170BConnectionScreenState.DeviceSelection -> {
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

                BP170BConnectionScreenState.Connecting -> {
                    ProgressIndicator()
                }

                BP170BConnectionScreenState.Connected -> {
                    StyledText(text = stringResource(R.string.blood_pressure_monitor_device_connected))
                    PrimaryButton(
                        onClick = onDisconnect,
                        text = stringResource(R.string.blood_pressure_monitor_disconnect),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }

                BP170BConnectionScreenState.ConnectionError -> {
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
        screenState = BP170BConnectionScreenState.DeviceSelection,
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
