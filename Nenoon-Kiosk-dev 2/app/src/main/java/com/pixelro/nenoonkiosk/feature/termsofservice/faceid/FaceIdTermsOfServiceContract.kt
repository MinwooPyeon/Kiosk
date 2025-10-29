package com.pixelro.nenoonkiosk.feature.termsofservice.faceid

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.feature.termsofservice.base.TermsTableData

data class FaceIdTermsOfServiceState(
    val acceptedPersonal: Boolean? = null,
    val textSize: TextUnit = 20.sp,
    val smallTextSize: TextUnit = 18.sp,
    val tableData: TermsTableData? = null
)
