package com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320

enum class BPBIO320ConnectionUiState {
    Standby,
    SearchingOrIdle,
    Connecting,
    AwaitingStart,
    ConnectionError,
}

data class BPBIO320ManagementUiState(
    val connectionState: BPBIO320ConnectionUiState = BPBIO320ConnectionUiState.Standby,
    val batteryLevel: Int? = null,
    val deviceName: String? = null,
)

sealed class BPBIO320ManagementEvent {
    data object StartConnection : BPBIO320ManagementEvent()
    data object Disconnect : BPBIO320ManagementEvent()
    data object Retry : BPBIO320ManagementEvent()
    data object Back : BPBIO320ManagementEvent()
    data object StartTest : BPBIO320ManagementEvent()
}