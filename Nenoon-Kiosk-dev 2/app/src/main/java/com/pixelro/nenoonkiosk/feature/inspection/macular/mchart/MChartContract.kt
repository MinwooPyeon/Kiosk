package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart

import com.pixelro.nenoonkiosk.R

data class MChartUiState(
    val isMeasuringDistanceVisible: Boolean = true,
    val isMChartVisible: Boolean = false,
    val isLeftEye: Boolean = true,
    val isVertical: Boolean = true,
    val currentLevel: Int = 0,
    val mChartImageId: Int = R.drawable.mchart_0_0,
    val isTTSSpeaking: Boolean = true,
    val isTesting: Boolean = false
)
