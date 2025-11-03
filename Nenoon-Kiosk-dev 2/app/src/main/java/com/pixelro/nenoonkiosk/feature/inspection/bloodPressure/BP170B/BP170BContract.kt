package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B

enum class BpMeasurementScreenState { Measuring, Completed }

data class BP170BInProgressUiState(
    val screenState: BpMeasurementScreenState = BpMeasurementScreenState.Measuring
)

sealed class BP170BInProgressEvent {
    data object CheckResultClicked : BP170BInProgressEvent()
}

enum class BP170BConnectionScreenState {
    DeviceCheck,
    Standby,
    DeviceSelection,
    Connecting,
    AwaitingStart,
    ConnectionError,
    TurnOffDevice,
}


data class BP170BStartUiState(
    val screenState: BP170BConnectionScreenState = BP170BConnectionScreenState.DeviceCheck,
    val isConnecting: Boolean = false,
    val availableDevices: List<android.bluetooth.BluetoothDevice> = emptyList(),
)

sealed class BP170BStartEvent {
    data object RetryScan : BP170BStartEvent()
    data object Back : BP170BStartEvent()
    data class DeviceSelected(val device: android.bluetooth.BluetoothDevice) : BP170BStartEvent()
}