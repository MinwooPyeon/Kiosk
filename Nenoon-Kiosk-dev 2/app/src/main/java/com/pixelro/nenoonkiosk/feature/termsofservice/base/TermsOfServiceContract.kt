package com.pixelro.nenoonkiosk.feature.termsofservice.base

import androidx.compose.ui.unit.TextUnit

data class TermsOfServiceUiState(
    val acceptedPersonal: Boolean?,
    val acceptedSensitive: Boolean?,
    val textSize: TextUnit
)

data class TermsTableData(
    val label: String,
    val column1: String,
    val column2: String,
    val column3: String,
    val evenly: Boolean = false
)