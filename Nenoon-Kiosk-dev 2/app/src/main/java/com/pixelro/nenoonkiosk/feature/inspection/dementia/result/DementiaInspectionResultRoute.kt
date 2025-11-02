package com.pixelro.nenoonkiosk.feature.inspection.dementia.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaInspectionResult

@Composable
fun DementiaInspectionResultRoute(
    testResult: DementiaInspectionResult
) {
    var isWebViewShowing by rememberSaveable { mutableStateOf(false) }
    var isGuideShowing by rememberSaveable { mutableStateOf(false) }

    DementiaInspectionResultScreen(
        testResult = testResult,
        isWebViewShowing = isWebViewShowing,
        isGuideShowing = isGuideShowing,
        onClickBackFromWeb = { isWebViewShowing = false },
        onCloseGuide = { isGuideShowing = false },
        onShowWeb = { isWebViewShowing = true },
        onShowGuide = { isGuideShowing = true }
    )
}
