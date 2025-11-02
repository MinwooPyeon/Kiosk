package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid

import androidx.compose.ui.geometry.Offset

data class AmslerGridUiState(
    val isMeasuringDistanceVisible: Boolean = true,
    val isAmslerGridVisible: Boolean = false,
    val isLeftEye: Boolean = true,
    val currentSelectedPosition: Offset = Offset(0f, 0f),
    val currentSelectedArea: List<MacularDisorderType> = List(9) { MacularDisorderType.Normal },
    val leftSelectedArea: List<MacularDisorderType> = emptyList(),
    val rightSelectedArea: List<MacularDisorderType> = emptyList(),
    val isBlinkingDone: Boolean = false,
    val isDotShowing: Boolean = true,
    val isFaceCenter: Boolean = false,
    val isTestStarted: Boolean = false,
    val isLookAtTheDotTTSDone: Boolean = true,
    val isSelectTTSDone: Boolean = true
)

enum class MacularDisorderType {
    Normal,
    Distorted,
    Blacked,
    Whited
}

data class AmslerGridTestResult(
    val leftEyeDisorderType: List<MacularDisorderType> = emptyList(),
    val rightEyeDisorderType: List<MacularDisorderType> = emptyList()
)
