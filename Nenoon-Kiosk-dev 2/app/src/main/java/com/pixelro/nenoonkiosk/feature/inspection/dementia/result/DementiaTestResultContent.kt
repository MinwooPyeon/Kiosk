package com.pixelro.nenoonkiosk.feature.inspection.dementia.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaViewModel.DementiaAnswer
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.GuideImageContainer
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.TheContent
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.WebContainer


@Composable
fun DementiaInspectionResultContent(
    testResult: DementiaInspectionResult,
    isWebViewShowing: Boolean,
    isGuideShowing: Boolean,
    onClickBackFromWeb: () -> Unit,
    onCloseGuide: () -> Unit,
    onShowWeb: () -> Unit,
    onShowGuide: () -> Unit,
) {
    when {
        isWebViewShowing -> {
            WebContainer(onBack = onClickBackFromWeb) {
                TheContent("https://m.nid.or.kr/main/main.aspx")
            }
        }
        isGuideShowing -> {
            GuideImageContainer(
                imageRes = R.drawable.dementia_2,
                onClose = onCloseGuide
            )
        }
        else -> {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 65.dp, vertical = 20.dp),
                ) {
                    Text(
                        text = StringProvider.getStringComposable(R.string.dementia_result_instruction),
                        color = Color(0xff1d71e1),
                        fontSize = 24.sp,
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth()
                        .background(color = Color(0xfff7f7f7), shape = RoundedCornerShape(8.dp))
                        .padding(40.dp),
                ) {
                    Text(
                        text = StringProvider.getStringComposable(R.string.dementia_result_wording1) + " " +
                                testResult.countActiveScore().toString() +
                                StringProvider.getStringComposable(R.string.dementia_result_wording2),
                        fontSize = 40.sp,
                    )
                }

                // 원래 UI엔 웹/가이드 진입 버튼이 없었음(디자인 유지).
                // 외부(상단바/메뉴 등)에서 onShowWeb(), onShowGuide() 호출하면 모드 전환됨.
            }
        }
    }
}


@Preview(name = "치매 결과 – 기본", showBackground = true, widthDp = 888, heightDp = 1422, apiLevel = 34)
@Composable
private fun Preview_DementiaResult_Default() {
    val result = DementiaInspectionResult(
        scores = listOf(
            DementiaAnswer.Yes, DementiaAnswer.No, DementiaAnswer.Yes,
            DementiaAnswer.No, DementiaAnswer.Yes, DementiaAnswer.Yes
        )
    )
    DementiaInspectionResultContent(
        testResult = result,
        isWebViewShowing = false,
        isGuideShowing = false,
        onClickBackFromWeb = {},
        onCloseGuide = {},
        onShowWeb = {},
        onShowGuide = {},
    )
}
@Preview(name = "치매 결과 – WebView", showBackground = true, widthDp = 888, heightDp = 1422, apiLevel = 34)
@Composable
private fun Preview_DementiaResult_Web() {
    val result = DementiaInspectionResult(
        scores = List(10) { DementiaAnswer.Yes }
    )
    DementiaInspectionResultContent(
        testResult = result,
        isWebViewShowing = true,
        isGuideShowing = false,
        onClickBackFromWeb = {},
        onCloseGuide = {},
        onShowWeb = {},
        onShowGuide = {},
    )
}

@Preview(name = "치매 결과 – 가이드", showBackground = true, widthDp = 888, heightDp = 1422, apiLevel = 34)
@Composable
private fun Preview_DementiaResult_Guide() {
    val result = DementiaInspectionResult(
        scores = List(14) { if (it % 2 == 0) DementiaAnswer.Yes else DementiaAnswer.No }
    )
    DementiaInspectionResultContent(
        testResult = result,
        isWebViewShowing = false,
        isGuideShowing = true,
        onClickBackFromWeb = {},
        onCloseGuide = {},
        onShowWeb = {},
        onShowGuide = {},
    )
}
