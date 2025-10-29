package com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.navigation.compose.rememberNavController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.*

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
    var uiState by remember { mutableStateOf(BloodPressureConnectionScreenState.Standby) }

    val connectionState by viewModel.connectionState.collectAsState()
    val deviceName by viewModel.deviceName.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // connectionState에 따라 상태 전환
    LaunchedEffect(connectionState, deviceName) {
        when (connectionState) {
            IB_SDKConst.IDLE, IB_SDKConst.DISCONNECTED -> {
                if (uiState != BloodPressureConnectionScreenState.Standby) {
                    uiState = BloodPressureConnectionScreenState.SearchingOrIdle
                    viewModel.selectDevice()
                    viewModel.connectDisconnect()
                }
            }
            IB_SDKConst.CONNECTING -> uiState = BloodPressureConnectionScreenState.Connecting
            IB_SDKConst.CONNECTED -> uiState = BloodPressureConnectionScreenState.AwaitingStart
        }
    }

    // 에러 감지
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            if (it.isNotBlank() && it != "null") {
                Log.e("BPBIO320", "Error: $it")
                if (connectionState != IB_SDKConst.CONNECTED &&
                    uiState != BloodPressureConnectionScreenState.Standby
                ) {
                    uiState = BloodPressureConnectionScreenState.ConnectionError
                }
            }
        }
    }

    // 실제 UI Content 호출
    BPBIO320ManagementContent(
        state = uiState,
        onStartConnection = {
            viewModel.removeDevice()
            viewModel.selectDevice()
            viewModel.connectDisconnect()
            uiState = BloodPressureConnectionScreenState.Connecting
        },
        onDisconnect = {
            viewModel.connectDisconnect()
            uiState = BloodPressureConnectionScreenState.Standby
        },
        onRetry = {
            viewModel.removeDevice()
            viewModel.selectDevice()
            viewModel.connectDisconnect()
            uiState = BloodPressureConnectionScreenState.Connecting
        },
        onBack = { navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BPBIO320ManagementContent(
    state: BloodPressureConnectionScreenState,
    onStartConnection: () -> Unit,
    onDisconnect: () -> Unit,
    onRetry: () -> Unit,
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
            when (state) {
                BloodPressureConnectionScreenState.Standby -> {
                    AccentedText(
                        prefix = stringResource(R.string.blood_pressure_monitor_standby_instruction1),
                        accent = stringResource(R.string.blood_pressure_monitor_standby_instruction2),
                        suffix = stringResource(R.string.blood_pressure_monitor_standby_instruction3),
                    )
                    PrimaryButton(
                        onClick = onStartConnection,
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

@Preview(showBackground = true, name = "Standby", widthDp = 800, heightDp = 1280)
@Composable
fun Preview_BPBIO320_Standby() {
    BPBIO320ManagementContent(
        state = BloodPressureConnectionScreenState.Standby,
        onStartConnection = {},
        onDisconnect = {},
        onRetry = {},
        onBack = {},
    )
}

@Preview(showBackground = true, name = "Searching/Idle", widthDp = 800, heightDp = 1280)
@Composable
fun Preview_BPBIO320_Searching() {
    BPBIO320ManagementContent(
        state = BloodPressureConnectionScreenState.SearchingOrIdle,
        onStartConnection = {},
        onDisconnect = {},
        onRetry = {},
        onBack = {},
    )
}

@Preview(showBackground = true, name = "Connecting", widthDp = 800, heightDp = 1280)
@Composable
fun Preview_BPBIO320_Connecting() {
    BPBIO320ManagementContent(
        state = BloodPressureConnectionScreenState.Connecting,
        onStartConnection = {},
        onDisconnect = {},
        onRetry = {},
        onBack = {},
    )
}

@Preview(showBackground = true, name = "Awaiting Start", widthDp = 800, heightDp = 1280)
@Composable
fun Preview_BPBIO320_AwaitingStart() {
    BPBIO320ManagementContent(
        state = BloodPressureConnectionScreenState.AwaitingStart,
        onStartConnection = {},
        onDisconnect = {},
        onRetry = {},
        onBack = {},
    )
}

@Preview(showBackground = true, name = "Connection Error", widthDp = 800, heightDp = 1280)
@Composable
fun Preview_BPBIO320_ConnectionError() {
    BPBIO320ManagementContent(
        state = BloodPressureConnectionScreenState.ConnectionError,
        onStartConnection = {},
        onDisconnect = {},
        onRetry = {},
        onBack = {},
    )
}
