package com.pixelro.nenoonkiosk.feature.termsofservice.base

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class TermsOfServiceUiState(
    val acceptedPersonal: Boolean? = null,
    val acceptedSensitive: Boolean? = null,
    val textSize: TextUnit = 20.sp,
)

data class TermsTableData(
    val label: String,
    val column1: String,
    val column2: String,
    val column3: String,
    val evenly: Boolean = false,
)
