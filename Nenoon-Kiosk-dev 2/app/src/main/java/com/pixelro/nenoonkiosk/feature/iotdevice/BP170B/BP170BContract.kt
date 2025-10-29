package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.bluetooth.BluetoothDevice
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult

// ---- UI 상태 ----
data class State(
    val connectionState: BP170BManager.BluetoothConnectionState =
        BP170BManager.BluetoothConnectionState.DISCONNECTED,
    val availableDevices: List<BluetoothDevice> = emptyList(),
    val dataReceived: String? = null,
    val bloodPressureResult: BloodPressureTestResult? = null,
    val isInitialized: Boolean = false,
    val screenState: ScreenState = ScreenState.Standby
)

// ---- 화면 단계 (UI 상태머신) ----
enum class ScreenState {
    Standby, Scanning, DeviceSelection, Connecting, Connected, ConnectionError
}

// ---- 사용자 이벤트 (Intent/Event) ----
sealed interface Event {
    data object StartScan : Event
    data class SelectDevice(val device: BluetoothDevice) : Event
    data object Retry : Event
    data object Disconnect : Event
    data object SendDeviceStatusCheck : Event
    data object SendErrorCodeCheck : Event
    data class SendTimeSetup(
        val year: Byte,
        val month: Byte,
        val day: Byte,
        val hour: Byte,
        val minute: Byte,
        val second: Byte
    ) : Event

    data object SendSerialNumberRequest : Event
}

// ---- 일회성 효과 (SideEffect: Toast, TTS 등) ----
sealed interface SideEffect {
    data class ShowMessage(val message: String) : SideEffect
}
