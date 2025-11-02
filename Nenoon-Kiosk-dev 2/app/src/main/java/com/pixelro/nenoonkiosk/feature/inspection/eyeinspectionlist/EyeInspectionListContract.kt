package com.pixelro.nenoonkiosk.feature.inspection.eyeinspectionlist

import com.pixelro.nenoonkiosk.feature.inspection.InspectionType

data class EyeInspectionListUiState(
    val isDescriptionShowing: Boolean = true,
    val isDialogShowing: Boolean = false,
    val selectedTest: InspectionType = InspectionType.None,
    val isSenior: Boolean = false,
    val isPresbyopiaDone: Boolean = false,
    val isShortVisualAcuityDone: Boolean = false,
    val isAmslerGridDone: Boolean = false,
    val isMChartDone: Boolean = false
)
