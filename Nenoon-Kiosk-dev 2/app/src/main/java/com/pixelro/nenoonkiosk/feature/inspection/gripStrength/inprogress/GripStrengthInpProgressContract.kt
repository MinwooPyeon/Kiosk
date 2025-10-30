package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.inprogress

import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthInspectionState

data class GripInProgressUiState(
    val testState: GripStrengthInspectionState = GripStrengthInspectionState.RightHandReady,
    val rightGripValue: Double = 0.0,
    val leftGripValue: Double = 0.0,
    val countdown: Int = 10,
)


sealed class GripInProgressEvent {
    data object StartPressed : GripInProgressEvent()
}