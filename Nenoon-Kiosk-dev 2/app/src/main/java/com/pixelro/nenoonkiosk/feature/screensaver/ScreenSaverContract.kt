package com.pixelro.nenoonkiosk.feature.screensaver

sealed interface ScreenSaverUiState {
    data object Initializing : ScreenSaverUiState
    data class Ready(val isVideoPlaying: Boolean = false) : ScreenSaverUiState
}

sealed interface ScreenSaverSideEffect {
    data object NavigateToIntro : ScreenSaverSideEffect
    data object NavigateToCategoryList : ScreenSaverSideEffect
}
