package com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320

import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult

object BPBIO320Contract {

    // ---- UI 상태 ----
    data class State(
        val connectionState: Int = IB_SDKConst.IDLE,
        val deviceName: String = "",
        val bloodPressureResult: BloodPressureTestResult? = null,
        val errorMessage: String? = null,
        val testInProgress: Boolean = false,
        val isLastResultComplete: Boolean = false,
        val batteryLevel: Int? = null,
        val screenState: ScreenState = ScreenState.Standby
    )

    // ---- 화면 상태 머신 ----
    enum class ScreenState {
        Standby,
        SearchingOrIdle,
        Connecting,
        AwaitingStart,
        ConnectionError
    }

    // ---- 이벤트 (Intent) ----
    sealed interface Event {
        data object InitializeBluetooth : Event
        data object Start : Event
        data object Retry : Event
        data object Disconnect : Event
        data object RemoveDevice : Event
        data object SelectDevice : Event
        data object ConnectOrDisconnect : Event
    }

    // ---- 부수효과 (SideEffect: 일회성 이벤트) ----
    sealed interface SideEffect {
        data class ShowMessage(val message: String) : SideEffect
    }
}
