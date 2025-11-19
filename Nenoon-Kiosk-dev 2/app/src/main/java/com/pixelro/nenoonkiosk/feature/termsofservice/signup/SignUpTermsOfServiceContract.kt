package com.pixelro.nenoonkiosk.feature.termsofservice.signup

import androidx.compose.ui.unit.TextUnit

data class SignUpTosUiState(
    val acceptedPersonal: Boolean?,
    val acceptedSensitive: Boolean?,
    val textSize: TextUnit
)