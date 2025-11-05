package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.BatteryStatus
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.DeviceUi
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripAction
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthUiState
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.components.AwaitingStartSection
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.components.ConnectingSection
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.components.ConnectionErrorSection
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.components.DeviceSelectionSection
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.components.StandbySection
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripConnectionUiState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GripStrengthStartScreen(
    state: GripStrengthUiState,
    onEvent: (GripAction) -> Unit,
) {
    Column(modifier =
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
        verticalArrangement = Arrangement.Bottom) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            BatteryStatus(
                state.batteryPercent?.toInt(),
                hidden = !state.isBatteryFetching && state.batteryPercent == null
            )

            Image(
                painter = painterResource(R.drawable.grip_strength_icon),
                contentDescription = stringResource(R.string.dynamometer_image_content_description),
                modifier = Modifier.size(300.dp),
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                when (state.screenState) {
                    InGripConnectionUiState.Standby -> StandbySection(onStart = {
                        onEvent(
                            GripAction.StartScan
                        )
                    })


                    InGripConnectionUiState.DeviceSelection -> DeviceSelectionSection(
                        devices = state.availableDevices,
                        isConnecting = state.isConnecting,
                        onSelect = { onEvent(GripAction.SelectDevice(it)) },
                        onRetry = { onEvent(GripAction.RetryScan) },
                    )


                    InGripConnectionUiState.Connecting -> ConnectingSection()


                    InGripConnectionUiState.AwaitingStart -> AwaitingStartSection(
                        onStartTest = { onEvent(GripAction.StartTest) },
                    )


                    InGripConnectionUiState.ConnectionError -> ConnectionErrorSection(
                        onRetry = { onEvent(GripAction.RetryScan) },
                    )
                }
                PrimaryButton(
                    onClick = { onEvent(GripAction.Back) },
                    text = stringResource(R.string.back),
                    modifier = Modifier.height(100.dp)
                )
            }
        }
    }
}

private fun previewDevices() = listOf(
    DeviceUi(name = "IN-GRIP 001", address = "AA:BB:CC:11:22:33"),
    DeviceUi(name = "IN-GRIP 002", address = "DD:EE:FF:44:55:66"),
)

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Scanning – Empty")
@Composable
private fun Preview_DeviceSelection_Scanning() {
    GripStrengthStartScreen(
        state = GripStrengthUiState(
            screenState = InGripConnectionUiState.DeviceSelection,
            isConnecting = true,
        ),
        onEvent = {},
    )
}


@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Devices Found")
@Composable
private fun Preview_DeviceSelection_List() {
    GripStrengthStartScreen(
        state = GripStrengthUiState(
            screenState = InGripConnectionUiState.DeviceSelection,
            availableDevices = previewDevices(),
            isConnecting = false,
        ),
        onEvent = {},
    )
}


@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Connecting")
@Composable
private fun Preview_Connecting() {
    GripStrengthStartScreen(
        state = GripStrengthUiState(
            screenState = InGripConnectionUiState.Connecting,
        ),
        onEvent = {},
    )
}


@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Awaiting Start")
@Composable
private fun Preview_AwaitingStart() {
    GripStrengthStartScreen(
        state = GripStrengthUiState(
            screenState = InGripConnectionUiState.AwaitingStart,
            batteryPercent = 87,
            isBatteryFetching = false,
        ),
        onEvent = {},
    )
}


@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Connection Error")
@Composable
private fun Preview_ConnectionError() {
    GripStrengthStartScreen(
        state = GripStrengthUiState(
            screenState = InGripConnectionUiState.ConnectionError,
        ),
        onEvent = {},
    )
}
