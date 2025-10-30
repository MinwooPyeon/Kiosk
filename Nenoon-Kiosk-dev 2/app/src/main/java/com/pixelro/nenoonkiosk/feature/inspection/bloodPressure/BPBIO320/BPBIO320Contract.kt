package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320

import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BpMeasurementScreenState
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BloodPressureConnectionScreenState


data class BPBIO320InProgressUiState(
    val screenState: BpMeasurementScreenState = BpMeasurementScreenState.Measuring,
)

sealed class BPBIO320InProgressEvent {
    data object StopPressed : BPBIO320InProgressEvent()
    data object CheckResultPressed : BPBIO320InProgressEvent()
}

data class BPBIO320StartUiState(
    val screenState: BloodPressureConnectionScreenState = BloodPressureConnectionScreenState.Standby,
)

sealed class BPBIO320StartEvent {
    data object StartConnect : BPBIO320StartEvent()
    data object RetryConnect : BPBIO320StartEvent()
    data object StartTest : BPBIO320StartEvent()
    data object Back : BPBIO320StartEvent()
}