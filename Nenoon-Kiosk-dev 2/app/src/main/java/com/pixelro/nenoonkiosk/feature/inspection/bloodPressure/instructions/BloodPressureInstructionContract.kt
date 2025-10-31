package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.instructions

data class BloodPressureInstructionsUiState(
    val ttsSpeaking: Boolean = false,
)


sealed class BloodPressureInstructionsEvent {
    data object StartPressed : BloodPressureInstructionsEvent()
}