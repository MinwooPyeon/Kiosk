package com.pixelro.nenoonkiosk.feature.inspection.dementia.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaInspectionResult

@Composable
fun DementiaInspectionResultRoute(
    testResult: DementiaInspectionResult,
    savedLanguage: String?,
) {
    var isWebViewShowing by rememberSaveable { mutableStateOf(false) }
    var isGuideShowing by rememberSaveable { mutableStateOf(false) }

    DementiaInspectionResultContent(
        testResult = testResult,
        isWebViewShowing = isWebViewShowing,
        isGuideShowing = isGuideShowing,
        savedLanguage = savedLanguage,
        onClickBackFromWeb = { isWebViewShowing = false },
        onCloseGuide = { isGuideShowing = false },
        // 트리거(외부 버튼/메뉴에서 호출하도록 준비)
        onShowWeb = { isWebViewShowing = true },
        onShowGuide = { isGuideShowing = true },
    )
}
