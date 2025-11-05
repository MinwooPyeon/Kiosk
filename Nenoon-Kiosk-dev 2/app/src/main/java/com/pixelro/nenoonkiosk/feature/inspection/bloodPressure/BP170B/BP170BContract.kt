package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B

/**
 * BP170B 혈압 측정 진행 화면 관련 Contract
 * (연결 관련 Contract는 feature/iotdevice/BP170B/BP170BContract.kt 참조)
 */

enum class BpMeasurementScreenState { Measuring, Completed }

data class BP170BInProgressUiState(
    val screenState: BpMeasurementScreenState = BpMeasurementScreenState.Measuring
)

sealed class BP170BInProgressEvent {
    data object CheckResultClicked : BP170BInProgressEvent()
}