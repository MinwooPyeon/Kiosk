package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.result

import androidx.compose.runtime.Composable
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult

@Composable
fun AmslerGridInspectionResultRoute(
    testResult: AmslerGridTestResult
) {
    AmslerGridInspectionResultScreen(
        leftSelectedArea = testResult.leftEyeDisorderType,
        rightSelectedArea = testResult.rightEyeDisorderType
    )
}
