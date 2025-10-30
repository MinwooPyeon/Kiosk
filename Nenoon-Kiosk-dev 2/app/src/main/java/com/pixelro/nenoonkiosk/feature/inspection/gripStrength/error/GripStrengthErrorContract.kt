package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.error

data class GripErrorUiState(
    val isSignedIn: Boolean = true,
)

sealed class GripErrorEvent {
    data object Retry : GripErrorEvent()
    data object Return : GripErrorEvent()
    data object Logout : GripErrorEvent()
}