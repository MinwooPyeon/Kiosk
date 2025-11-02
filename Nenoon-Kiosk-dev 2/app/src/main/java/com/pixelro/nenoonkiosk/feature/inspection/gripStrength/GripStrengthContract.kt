package com.pixelro.nenoonkiosk.feature.inspection.gripStrength

import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.DynamometerConnectionScreenState

// 통합 UiState
data class GripStrengthUiState(
    // 현재 화면
    val currentScreen: GripScreen = GripScreen.START,

    // Start 화면 상태
    val screenState: DynamometerConnectionScreenState = DynamometerConnectionScreenState.Standby,
    val batteryPercent: Int? = null,
    val isBatteryFetching: Boolean = false,
    val isConnecting: Boolean = false,
    val availableDevices: List<DeviceUi> = emptyList(),

    // Instructions 화면 상태
    val ttsSpeaking: Boolean = false,

    // InProgress 화면 상태
    val testState: GripStrengthInspectionState = GripStrengthInspectionState.RightHandReady,
    val rightGripValue: Double = 0.0,
    val leftGripValue: Double = 0.0,
    val countdown: Int = 10,

    // Error 화면 상태
    val isSignedIn: Boolean = true
)

// 화면 enum
enum class GripScreen {
    START,
    INSTRUCTIONS,
    IN_PROGRESS,
    ERROR
}

// Device UI (기존 유지)
data class DeviceUi(
    val name: String,
    val address: String
)

// 통합 Event
sealed class GripStrengthEvent {
    // Start 화면
    object Back : GripStrengthEvent()
    object StartScan : GripStrengthEvent()
    data class SelectDevice(val device: DeviceUi) : GripStrengthEvent()
    object RetryScan : GripStrengthEvent()
    object StartTest : GripStrengthEvent()

    // Instructions 화면
    object ProceedToTest : GripStrengthEvent()

    // InProgress 화면
    object StartPressed : GripStrengthEvent()

    // Error 화면
    object Retry : GripStrengthEvent()
    object Return : GripStrengthEvent()
    object Logout : GripStrengthEvent()
}

// 기존 유지
enum class GripStrengthInspectionState {
    RightHandReady,
    RightHand,
    RightHandCompleted,
    LeftHandReady,
    LeftHand,
    LeftHandCompleted
}
