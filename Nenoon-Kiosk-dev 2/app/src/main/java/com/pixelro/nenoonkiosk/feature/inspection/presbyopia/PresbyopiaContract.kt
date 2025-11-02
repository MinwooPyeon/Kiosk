package com.pixelro.nenoonkiosk.feature.inspection.presbyopia

data class PresbyopiaUiState(
    val testState: TestState = TestState.Started,
    val tryCount: Int = 0,
    val isTextShowing: Boolean = true,
    val isComingCloserTTSDone: Boolean = false
)

enum class TestState {
    Started,
    AdjustingDistance,
    TextBlinking,
    ComingCloser,
    NoPresbyopia
}
