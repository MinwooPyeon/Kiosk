package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320

/**
 * BPBIO320 혈압 측정 진행 화면 관련 Contract
 * (연결 관련 Contract는 feature/iotdevice/BPBIO320/BPBIO320Contract.kt 참조)
 */

import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BpMeasurementScreenState

data class BPBIO320InProgressUiState(
    val screenState: BpMeasurementScreenState = BpMeasurementScreenState.Measuring,
)

sealed class BPBIO320InProgressEvent {
    data object StopPressed : BPBIO320InProgressEvent()
    data object CheckResultPressed : BPBIO320InProgressEvent()
}