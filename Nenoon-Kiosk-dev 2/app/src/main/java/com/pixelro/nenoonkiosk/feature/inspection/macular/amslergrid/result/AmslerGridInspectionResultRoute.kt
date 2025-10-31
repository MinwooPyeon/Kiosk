package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.result

import androidx.compose.runtime.Composable
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult

@Composable
fun AmslerGridTestResultRoute(
    testResult: AmslerGridTestResult,
) {
    val leftSelectedArea = testResult.leftEyeDisorderType
    val rightSelectedArea = testResult.rightEyeDisorderType

    AmslerGridInspectionResultContent(
        leftSelectedArea,
        rightSelectedArea
    )
}