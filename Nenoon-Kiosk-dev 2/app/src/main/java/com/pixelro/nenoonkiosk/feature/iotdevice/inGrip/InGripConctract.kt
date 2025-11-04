package com.pixelro.nenoonkiosk.feature.iotdevice.inGrip

import android.bluetooth.BluetoothDevice

enum class InGripConnectionUiState {
    Standby,
    DeviceSelection,
    Connecting,
    AwaitingStart,
    ConnectionError,
}

data class InGripManagementUiState(
    val connectionState: InGripConnectionUiState = InGripConnectionUiState.Standby,
    val batteryLevel: Int? = null,
    val isBatteryFetching: Boolean = false,
    val availableDevices: List<BluetoothDevice> = emptyList(),
    val connecting: Boolean = false,
)

sealed class InGripManagementEvent {
    data object StartConnection : InGripManagementEvent()
    data class DeviceSelected(val device: BluetoothDevice) : InGripManagementEvent()
    data object Disconnect : InGripManagementEvent()
    data object Retry : InGripManagementEvent()
    data object Back : InGripManagementEvent()
}