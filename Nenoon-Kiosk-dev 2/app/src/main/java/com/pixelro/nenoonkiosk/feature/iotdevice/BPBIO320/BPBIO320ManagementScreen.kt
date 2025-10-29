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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.*
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BPBIO320ManagementScreen(
    navController: NavHostController,
    viewModel: BPBIO320ViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    // ---- SideEffect 처리 ----
    viewModel.collectSideEffect { effect ->
        when (effect) {
            is BPBIO320Contract.SideEffect.ShowMessage -> {
                Log.d("BPBIO320", "SideEffect: ${effect.message}")
            }
        }
    }

    BPBIO320ManagementContent(
        screenState = state.screenState,
        onStart = { viewModel.onEvent(BPBIO320Contract.Event.Start) },
        onRetry = { viewModel.onEvent(BPBIO320Contract.Event.Retry) },
        onDisconnect = { viewModel.onEvent(BPBIO320Contract.Event.Disconnect) },
        onBack = {
            navController.popBackStack(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT, false)
        },
    )
}

/* ----------------------------- PREVIEW & UI ----------------------------- */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BPBIO320ManagementContent(
    screenState: BPBIO320Contract.ScreenState,
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
                BPBIO320Contract.ScreenState.Standby -> {
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

                BPBIO320Contract.ScreenState.SearchingOrIdle -> {
                    ProgressIndicator()
                    PrimaryButton(
                        onClick = onRetry,
                        text = stringResource(R.string.blood_pressure_monitor_retry_connection),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                    )
                }

                BPBIO320Contract.ScreenState.Connecting -> {
                    ProgressIndicator()
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_connecting),
                        modifier = Modifier.padding(top = 40.dp, bottom = 180.dp),
                    )
                }

                BPBIO320Contract.ScreenState.AwaitingStart -> {
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_device_connected),
                    )
                    PrimaryButton(
                        onClick = onDisconnect,
                        text = stringResource(R.string.blood_pressure_monitor_disconnect),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                    )
                }

                BPBIO320Contract.ScreenState.ConnectionError -> {
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

@Preview(showBackground = true, name = "BPBIO320Management Preview", apiLevel = 34, widthDp = 800, heightDp = 1280)
@Composable
fun BPBIO320ManagementPreview() {
    BPBIO320ManagementContent(
        screenState = BPBIO320Contract.ScreenState.AwaitingStart,
        onStart = {},
        onRetry = {},
        onDisconnect = {},
        onBack = {},
    )
}
