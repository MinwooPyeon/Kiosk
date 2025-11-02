package com.pixelro.nenoonkiosk.feature.inspection.externaldevice

import com.pixelro.nenoonkiosk.feature.inspection.InspectionType

/**
 * 외부 장비 검사 목록 화면의 UI 상태
 */
data class ExternalDeviceInspectionListUiState(
    val isBloodPressureDone: Boolean = false,
    val isGripStrengthDone: Boolean = false,
    val isSenior: Boolean = false,
    val isDialogShowing: Boolean = false,
    val selectedInspection: InspectionType = InspectionType.None
)

/**
 * 외부 장비 검사 목록 화면의 사용자 이벤트
 */
sealed interface ExternalDeviceInspectionListSideEffect {
    data class OnInspectionSelected(val inspectionType: InspectionType) : ExternalDeviceInspectionListSideEffect
    object OnDialogDismissed : ExternalDeviceInspectionListSideEffect
}
