package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.instructions

data class GripInstructionsUiState(
    val ttsSpeaking: Boolean = false,
)


sealed class GripInstructionsEvent {
    data object StartPressed : GripInstructionsEvent()
}