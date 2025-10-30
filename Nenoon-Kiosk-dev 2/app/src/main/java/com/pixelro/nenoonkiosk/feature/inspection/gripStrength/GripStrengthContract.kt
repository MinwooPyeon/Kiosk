package com.pixelro.nenoonkiosk.feature.inspection.gripStrength

import android.bluetooth.BluetoothDevice
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.DynamometerConnectionScreenState

enum class GripStrengthInspectionState {
    RightHandReady, RightHand, RightHandCompleted,
    LeftHandReady, LeftHand, LeftHandCompleted,
}

data class DeviceUi(
    val name: String,
    val address: String,
)

data class GripStrengthUiState(
    val screenState: DynamometerConnectionScreenState = DynamometerConnectionScreenState.Standby,
    val batteryPercent: Int? = null,
    val isBatteryFetching: Boolean = false,
    val isConnecting: Boolean = false,
    val availableDevices: List<DeviceUi> = emptyList(),
)


sealed class GripAction {
    data object Back : GripAction()
    data object StartScan : GripAction()
    data class SelectDevice(val device: DeviceUi) : GripAction()
    data object RetryScan : GripAction()
    data object StartTest : GripAction()
}


enum class GripScreen { Start, Instructions, InProgress, Error }