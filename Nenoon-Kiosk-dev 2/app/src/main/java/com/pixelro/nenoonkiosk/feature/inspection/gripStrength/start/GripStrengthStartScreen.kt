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

// 변경: 파라미터를 state와 onEvent로 통합
@Composable
fun GripStrengthStartScreen(
    state: GripStrengthUiState,
    onEvent: (GripStrengthEvent) -> Unit
) {
    // 기존 UI 코드 그대로 사용
    when (state.screenState) {
        DynamometerConnectionScreenState.Standby -> {
            StandbySection(
                onStartScan = { onEvent(GripStrengthEvent.StartScan) },
                onBack = { onEvent(GripStrengthEvent.Back) }
            )
        }
        DynamometerConnectionScreenState.DeviceSelection -> {
            DeviceSelectionSection(
                availableDevices = state.availableDevices,
                onDeviceSelected = { device ->
                    onEvent(GripStrengthEvent.SelectDevice(device))
                },
                isConnecting = state.isConnecting
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
            // 기존 Error Section UI 그대로 사용
            // onRetry만 변경
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
