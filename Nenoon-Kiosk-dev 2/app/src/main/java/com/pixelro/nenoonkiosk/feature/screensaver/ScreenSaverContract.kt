package com.pixelro.nenoonkiosk.feature.screensaver

data class ScreenSaverState(
    val isVideoReady: Boolean = false,
    val videoUri: String = "",
    val language: String = "ko"
)

sealed interface ScreenSaverSideEffect {
    data class ShowToast(val message: String) : ScreenSaverSideEffect
}
