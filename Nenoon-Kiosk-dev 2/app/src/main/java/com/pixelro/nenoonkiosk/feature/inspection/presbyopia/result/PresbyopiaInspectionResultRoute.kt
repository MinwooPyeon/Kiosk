package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.result

import androidx.compose.runtime.Composable
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaInspectionResult

@Composable
fun PresbyopiaInspectionResultRoute(
    testResult: PresbyopiaInspectionResult
) {
    PresbyopiaInspectionResultScreen(testResult = testResult)
}
