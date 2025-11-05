package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.bluetooth.BluetoothDevice

enum class BP170BConnectionUiState {
    Standby,
    DeviceSelection,
    Connecting,
    AwaitingStart,
    ConnectionError,
    TurnOffDevice,
}

data class BP170BManagementUiState(
    val connectionState: BP170BConnectionUiState = BP170BConnectionUiState.Standby,
    val connecting: Boolean = false,
    val availableDevices: List<BluetoothDevice> = emptyList(),
)

sealed class BP170BManagementEvent {
    data object StartConnection : BP170BManagementEvent()
    data class DeviceSelected(val device: BluetoothDevice) : BP170BManagementEvent()
    data object Disconnect : BP170BManagementEvent()
    data object Retry : BP170BManagementEvent()
    data object StartTest : BP170BManagementEvent()
    data object Back : BP170BManagementEvent()
}
