package com.pixelro.nenoonkiosk.feature.termsofservice.faceid

import androidx.compose.ui.unit.TextUnit

data class FaceIdTosUiState(
    val acceptedPersonal: Boolean?,
    val textSize: TextUnit,
    val smallTextSize: TextUnit
)