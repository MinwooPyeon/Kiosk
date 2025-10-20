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
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
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
    viewModel: BPBIO320ViewModel
) {

    var bloodPressureConnectionScreenState by remember { mutableStateOf(BloodPressureConnectionScreenState.Standby) }

    val connectionState by viewModel.connectionState.collectAsState()
    val deviceName by viewModel.deviceName.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(connectionState, deviceName) {
        when (connectionState) {
            IB_SDKConst.IDLE, IB_SDKConst.DISCONNECTED -> {
                if (bloodPressureConnectionScreenState != BloodPressureConnectionScreenState.Standby) {
                    bloodPressureConnectionScreenState = BloodPressureConnectionScreenState.SearchingOrIdle
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
            }
            else -> {}
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            if (it.isNotBlank() && it != "null") {
                Log.e("BPStartScreen", "Error: $it")
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

        StyledText(StringProvider.getString(R.string.blood_pressure_monitor_title), TextStyle.Title)

        Image(
            painter = painterResource(R.drawable.blood_pressure_icon),
            contentDescription = StringProvider.getString(R.string.blood_pressure_monitor_image_content_description),
            modifier = Modifier.weight(1f).width(500.dp)
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
                            viewModel.removeDevice()
                            viewModel.selectDevice()
                            viewModel.connectDisconnect()
                            bloodPressureConnectionScreenState = BloodPressureConnectionScreenState.Connecting
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
                            viewModel.connectDisconnect()
                            bloodPressureConnectionScreenState =
                                BloodPressureConnectionScreenState.Standby
                        },
                        text = StringProvider.getString(R.string.blood_pressure_monitor_disconnect),
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
                    navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false)
                },
                text = StringProvider.getString(R.string.back)
            )
        }
    }
}