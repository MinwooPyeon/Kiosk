package com.pixelro.nenoonkiosk.feature.inspection.visualacuity

data class VisualAcuityUiState(
    val isMeasuringDistanceVisible: Boolean = true,
    val isVisualAcuityVisible: Boolean = false,
    val isLeftEye: Boolean = true,
    val sightLevel: Int = 1,
    val randomList: List<Int> = listOf(0),
    val ansNum: Int = 0,
    val wrongCount: Float = 0f,
    val sightHistory: Map<Int, Pair<Int, Int>> = mapOf(
        1 to Pair(0, 0),
        2 to Pair(0, 0),
        3 to Pair(0, 0),
        4 to Pair(0, 0),
        5 to Pair(0, 0),
        6 to Pair(0, 0),
        7 to Pair(0, 0),
        8 to Pair(0, 0),
        9 to Pair(0, 0),
        10 to Pair(0, 0)
    )
)
