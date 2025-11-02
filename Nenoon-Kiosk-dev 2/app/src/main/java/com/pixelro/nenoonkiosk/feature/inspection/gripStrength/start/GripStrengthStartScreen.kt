package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripScreen
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthEvent
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthUiState
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components.AwaitingStartSection
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components.ConnectingSection
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components.DeviceSelectionSection
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components.StandbySection
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.DynamometerConnectionScreenState

@Composable
fun GripStrengthStartScreen(
    state: GripStrengthUiState,
    onEvent: (GripStrengthEvent) -> Unit
) {
    when (state.screenState) {
        DynamometerConnectionScreenState.Standby -> {
            StandbySection(
                onStart = { onEvent(GripStrengthEvent.StartScan) },
                onBack = { onEvent(GripStrengthEvent.Back) }
            )
        }

        DynamometerConnectionScreenState.DeviceSelection -> {
            DeviceSelectionSection(
                devices = state.availableDevices,
                isConnecting = state.isConnecting,
                onSelect = { device -> onEvent(GripStrengthEvent.SelectDevice(device)) },
                onRetry = { onEvent(GripStrengthEvent.Retry) }
            )
        }

        DynamometerConnectionScreenState.Connecting -> {
            ConnectingSection()
        }

        DynamometerConnectionScreenState.AwaitingStart -> {
            AwaitingStartSection(
                batteryPercent = state.batteryPercent,
                isBatteryFetching = state.isBatteryFetching,
                onStartTest = { onEvent(GripStrengthEvent.StartTest) }
            )
        }

        DynamometerConnectionScreenState.ConnectionError -> {
            TODO()
        }
    }
}


@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun Preview_GripStrengthStart() {
    GripStrengthStartScreen(
        state = GripStrengthUiState(
            currentScreen = GripScreen.START,
            screenState = DynamometerConnectionScreenState.AwaitingStart,
            batteryPercent = 85,
            isBatteryFetching = false
        ),
        onEvent = {}
    )
}
