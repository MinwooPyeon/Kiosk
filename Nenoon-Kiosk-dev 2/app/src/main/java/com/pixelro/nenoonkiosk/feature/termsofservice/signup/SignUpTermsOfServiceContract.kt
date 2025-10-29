package com.pixelro.nenoonkiosk.feature.termsofservice.signup

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsTableData

data class SignUpTermsOfServiceState(
    val acceptedPersonal: Boolean? = null,
    val acceptedSensitive: Boolean? = null,
    val textSize: TextUnit = 20.sp,
    val personalTable: TermsTableData? = null,
    val sensitiveTable: TermsTableData? = null
)

// SideEffect 완전 제거 - Navigator가 처리
