package com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.components.BPBIO320ConnectionStateContent
import com.pixelro.nenoonkiosk.feature.iotdevice.common.DeviceManagementScreen

@Composable
fun BPBIO320ManagementRoute(
    navController: NavHostController,
    viewModel: BPBIO320ViewModel = hiltViewModel(),
    showStartTest: Boolean = false,
    onStartTest: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val deviceName by viewModel.deviceName.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val batteryLevelFromManager by viewModel.batteryLevel.collectAsState()

    // 초기 상태를 연결 상태에 따라 설정
    val initialState = if (connectionState == IB_SDKConst.CONNECTED) {
        BPBIO320ConnectionUiState.AwaitingStart
    } else {
        BPBIO320ConnectionUiState.Standby
    }

    var uiState by remember { mutableStateOf(initialState) }
    var batteryLevel by remember { mutableStateOf<Int?>(null) }

    // connectionState에 따라 상태 전환
    LaunchedEffect(connectionState, deviceName) {
        when (connectionState) {
            IB_SDKConst.IDLE, IB_SDKConst.DISCONNECTED -> {
                if (uiState != BPBIO320ConnectionUiState.Standby) {
                    uiState = BPBIO320ConnectionUiState.SearchingOrIdle
                    viewModel.selectDevice()
                    viewModel.connectDisconnect()
                }
            }
            IB_SDKConst.CONNECTING -> uiState = BPBIO320ConnectionUiState.Connecting
            IB_SDKConst.CONNECTED -> {
                uiState = BPBIO320ConnectionUiState.AwaitingStart
                TTS.speechTTS(
                    StringProvider.getString(R.string.blood_pressure_monitor_connected),
                    TextToSpeech.QUEUE_ADD
                )
            }
        }
    }

    // 에러 감지
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            if (it.isNotBlank() && it != "null") {
                Log.e("BPBIO320", "Error: $it")
                if (connectionState != IB_SDKConst.CONNECTED &&
                    uiState != BPBIO320ConnectionUiState.Standby
                ) {
                    uiState = BPBIO320ConnectionUiState.ConnectionError
                    TTS.speechTTS(
                        StringProvider.getString(R.string.blood_pressure_monitor_connection_failed),
                        TextToSpeech.QUEUE_ADD
                    )
                }
            }
        }
    }

    // 배터리 레벨 업데이트
    LaunchedEffect(batteryLevelFromManager) {
        batteryLevel = batteryLevelFromManager
    }

    // 초기 TTS
    LaunchedEffect(Unit) {
        val ttsMessage = if (connectionState == IB_SDKConst.CONNECTED) {
            StringProvider.getString(R.string.blood_pressure_monitor_connected)
        } else {
            StringProvider.getString(R.string.blood_pressure_monitor_initial_instruction)
        }
        TTS.speechTTS(ttsMessage, TextToSpeech.QUEUE_ADD)
    }

    val state = BPBIO320ManagementUiState(
        connectionState = uiState,
        batteryLevel = batteryLevel,
        deviceName = deviceName,
    )

    DeviceManagementScreen(
        titleRes = R.string.blood_pressure_monitor_title,
        imageRes = R.drawable.blood_pressure_icon,
        imageContentDescriptionRes = R.string.blood_pressure_monitor_image_content_description,
        batteryLevel = state.batteryLevel,
        hideBattery = state.batteryLevel == null,
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
            BPBIO320ConnectionStateContent(
                state = state,
                onEvent = { event ->
                    when (event) {
                        is BPBIO320ManagementEvent.StartConnection -> {
                            viewModel.removeDevice()
                            viewModel.selectDevice()
                            viewModel.connectDisconnect()
                            uiState = BPBIO320ConnectionUiState.Connecting
                            TTS.speechTTS(
                                StringProvider.getString(R.string.blood_pressure_monitor_searching_device),
                                TextToSpeech.QUEUE_ADD
                            )
                        }
                        is BPBIO320ManagementEvent.Disconnect -> {
                            viewModel.connectDisconnect()
                            uiState = BPBIO320ConnectionUiState.Standby
                        }
                        is BPBIO320ManagementEvent.Retry -> {
                            viewModel.removeDevice()
                            viewModel.selectDevice()
                            viewModel.connectDisconnect()
                            uiState = BPBIO320ConnectionUiState.Connecting
                        }
                        is BPBIO320ManagementEvent.StartTest -> {
                            onStartTest?.invoke()
                        }
                        is BPBIO320ManagementEvent.Back -> {
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