package com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.*
import kotlinx.coroutines.delay

enum class BloodPressureConnectionScreenState {
    Standby,
    SearchingOrIdle,
    Connecting,
    AwaitingStart,
    ConnectionError,
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BPBIO320ManagementScreen(
    navController: NavHostController,
    viewModel: BPBIO320ViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    var screenState by remember { mutableStateOf(BloodPressureConnectionScreenState.Standby) }

    // 연결 상태 감시
    LaunchedEffect(uiState.connectionState, uiState.deviceName) {
        when (uiState.connectionState) {
            IB_SDKConst.IDLE, IB_SDKConst.DISCONNECTED -> {
                if (screenState != BloodPressureConnectionScreenState.Standby) {
                    screenState = BloodPressureConnectionScreenState.SearchingOrIdle
                    delay(2000)
                    viewModel.selectDevice()
                    viewModel.connectDisconnect()
                }
            }

            IB_SDKConst.CONNECTING -> {
                screenState = BloodPressureConnectionScreenState.Connecting
            }

            IB_SDKConst.CONNECTED -> {
                screenState = BloodPressureConnectionScreenState.AwaitingStart
            }

            else -> {}
        }
    }

    // 에러 감시
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            if (it.isNotBlank() && it != "null") {
                Log.e("BPBIO320Screen", "Error: $it")
                if (uiState.connectionState != IB_SDKConst.CONNECTED &&
                    screenState != BloodPressureConnectionScreenState.Standby
                ) {
                    screenState = BloodPressureConnectionScreenState.ConnectionError
                }
            }
        }
    }

    BPBIO320ManagementContent(
        screenState = screenState,
        onStart = {
            viewModel.removeDevice()
            viewModel.selectDevice()
            viewModel.connectDisconnect()
            screenState = BloodPressureConnectionScreenState.Connecting
        },
        onRetry = {
            viewModel.removeDevice()
            viewModel.selectDevice()
            viewModel.connectDisconnect()
            screenState = BloodPressureConnectionScreenState.Connecting
        },
        onDisconnect = {
            viewModel.connectDisconnect()
            screenState = BloodPressureConnectionScreenState.Standby
        },
        onBack = {
            navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false)
        },
    )
}

/* ----------------------------- PREVIEW & UI ----------------------------- */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BPBIO320ManagementContent(
    screenState: BloodPressureConnectionScreenState,
    onStart: () -> Unit,
    onRetry: () -> Unit,
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
        StyledText(stringResource(R.string.blood_pressure_monitor_title), TextStyle.Title)

        Image(
            painter = painterResource(R.drawable.blood_pressure_icon),
            contentDescription = stringResource(R.string.blood_pressure_monitor_image_content_description),
            modifier = Modifier
                .weight(1f)
                .width(500.dp),
        )

        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            when (screenState) {
                BloodPressureConnectionScreenState.Standby -> {
                    AccentedText(
                        prefix = stringResource(R.string.blood_pressure_monitor_standby_instruction1),
                        accent = stringResource(R.string.blood_pressure_monitor_standby_instruction2),
                        suffix = stringResource(R.string.blood_pressure_monitor_standby_instruction3),
                    )
                    PrimaryButton(
                        onClick = onStart,
                        text = stringResource(R.string.blood_pressure_monitor_start_connection),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                    )
                }

                BloodPressureConnectionScreenState.SearchingOrIdle -> {
                    ProgressIndicator()
                    PrimaryButton(
                        onClick = onRetry,
                        text = stringResource(R.string.blood_pressure_monitor_retry_connection),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                    )
                }

                BloodPressureConnectionScreenState.Connecting -> {
                    ProgressIndicator()
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_connecting),
                        modifier = Modifier.padding(top = 40.dp, bottom = 180.dp),
                    )
                }

                BloodPressureConnectionScreenState.AwaitingStart -> {
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_device_connected),
                    )
                    PrimaryButton(
                        onClick = onDisconnect,
                        text = stringResource(R.string.blood_pressure_monitor_disconnect),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                    )
                }

                BloodPressureConnectionScreenState.ConnectionError -> {
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_connection_error),
                        style = TextStyle.Error,
                    )
                    PrimaryButton(
                        onClick = onRetry,
                        text = stringResource(R.string.blood_pressure_monitor_try_again),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
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

@Preview(showBackground = true, name = "BPBIO320Management Preview - Connected", apiLevel = 34, widthDp = 800, heightDp = 1280)
@Composable
fun BPBIO320ManagementPreview() {
    BPBIO320ManagementContent(
        screenState = BloodPressureConnectionScreenState.AwaitingStart,
        onStart = {},
        onRetry = {},
        onDisconnect = {},
        onBack = {},
    )
}
