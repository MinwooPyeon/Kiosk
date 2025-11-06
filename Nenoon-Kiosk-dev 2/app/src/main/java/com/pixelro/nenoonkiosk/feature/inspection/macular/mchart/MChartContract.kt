package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart

import com.pixelro.nenoonkiosk.R

data class MChartInspectionUiState(
    val isLeftEye: Boolean = true,
    val isVertical: Boolean = true,
    val currentLevel: Int = 0,
    val imageId: Int = R.drawable.eyecontrol_02,
    val isTTSSpeaking: Boolean = false,
    val isTesting: Boolean = false,
)


