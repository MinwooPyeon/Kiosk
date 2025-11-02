package com.pixelro.nenoonkiosk.feature.inspection.dementia

data class DementiaUiState(
    val currentIndex: Int = 0,
    val scores: List<DementiaAnswer> = List(14) { DementiaAnswer.None }
)

enum class DementiaAnswer {
    Yes,
    No,
    None
}
