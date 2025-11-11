package com.pixelro.nenoonkiosk.feature.inspection.dementia.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaViewModel.DementiaAnswer
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.GuideImageContainer
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.TheContent
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.WebContainer
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.InspectionResultScreen
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.Red
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun DementiaInspectionResultContent(
    testResult: DementiaInspectionResult,
    isWebViewShowing: Boolean,
    isGuideShowing: Boolean,
    savedLanguage: String?,
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
            val guideImageRes = if (savedLanguage == "ko") {
                R.drawable.dementia_2
            } else {
                R.drawable.dementia_eng
            }
            GuideImageContainer(
                imageRes = guideImageRes,
                onClose = onCloseGuide
            )
        }
        else -> {
            val score = testResult.countActiveScore()
            val isNeedCaution = score >= 6
            val borderColor = if (isNeedCaution) Red else neNoon_blue
            val statusText = if (isNeedCaution) {
                stringResource(R.string.dementia_result_caution_needed)
            } else {
                stringResource(R.string.sawi_result_normal_range)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 결과 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(4.dp, borderColor), RoundedCornerShape(8.dp))
                        .background(White, RoundedCornerShape(10.dp))
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if (isNeedCaution) R.drawable.ic_sad else R.drawable.ic_happy
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = statusText,
                                fontSize = 40.sp,
                                color = borderColor,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "$score ${stringResource(R.string.dementia_result_wording2)}",
                            fontSize = 60.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = borderColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 안내 문구
                Text(
                    text = stringResource(R.string.dementia_result_instruction),
                    fontSize = 24.sp,
                    color = Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

//                Text(
//                    text = stringResource(R.string.dementia_result_wording3),
//                    fontSize = 24.sp,
//                    color = Gray,
//                    modifier = Modifier.fillMaxWidth()
//                )

                Spacer(modifier = Modifier.height(20.dp))

                // 링크
                Text(
                    text = "> ${stringResource(R.string.dementia_result_selection1)}",
                    fontSize = 24.sp,
                    color = neNoon_blue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShowGuide() }
                )
            }
        }
    }
}

@Preview(name = "치매 결과 – 기본", showBackground = true, widthDp = 1280, heightDp = 800, apiLevel = 34)
@Composable
private fun Preview_DementiaResult_Default() {
    val result = DementiaInspectionResult(
        scores = listOf(
            DementiaAnswer.Yes, DementiaAnswer.No, DementiaAnswer.Yes,
            DementiaAnswer.No, DementiaAnswer.Yes, DementiaAnswer.Yes
        )
    )
    InspectionResultScreen(
        titleText = stringResource(R.string.dementia_result_title),
        isDarkBackground = false,
        showLoading = false,
        savedLanguage = "ko",
        resultContent = {
            DementiaInspectionResultContent(
                testResult = result,
                isWebViewShowing = false,
                isGuideShowing = false,
                savedLanguage = "ko",
                onClickBackFromWeb = {},
                onCloseGuide = {},
                onShowWeb = {},
                onShowGuide = {},
            )
        },
        aiCommentText = null,
        printEnabled = true,
        onPrint = {},
        onBack = {},
        onLogout = {},
    )
}

@Preview(name = "치매 결과 – 기본", showBackground = true, widthDp = 1280, heightDp = 800, apiLevel = 34)
@Composable
private fun Preview_DementiaResult_Warn() {
    val result = DementiaInspectionResult(
        scores = listOf(
            DementiaAnswer.Yes, DementiaAnswer.Yes, DementiaAnswer.Yes,
            DementiaAnswer.Yes, DementiaAnswer.Yes, DementiaAnswer.Yes
        )
    )
    InspectionResultScreen(
        titleText = stringResource(R.string.dementia_result_title),
        isDarkBackground = false,
        showLoading = false,
        savedLanguage = "ko",
        resultContent = {
            DementiaInspectionResultContent(
                testResult = result,
                isWebViewShowing = false,
                isGuideShowing = false,
                savedLanguage = "ko",
                onClickBackFromWeb = {},
                onCloseGuide = {},
                onShowWeb = {},
                onShowGuide = {},
            )
        },
        aiCommentText = null,
        printEnabled = true,
        onPrint = {},
        onBack = {},
        onLogout = {},
    )
}

@Preview(name = "치매 결과 – WebView", showBackground = true, widthDp = 800, heightDp = 1280, apiLevel = 34)
@Composable
private fun Preview_DementiaResult_Web() {
    val result = DementiaInspectionResult(
        scores = List(10) { DementiaAnswer.Yes }
    )
    DementiaInspectionResultContent(
        testResult = result,
        isWebViewShowing = true,
        isGuideShowing = false,
        savedLanguage = "ko",
        onClickBackFromWeb = {},
        onCloseGuide = {},
        onShowWeb = {},
        onShowGuide = {},
    )
}

@Preview(name = "치매 결과 – 가이드", showBackground = true, widthDp = 800, heightDp = 1280, apiLevel = 34)
@Composable
private fun Preview_DementiaResult_Guide() {
    val result = DementiaInspectionResult(
        scores = List(14) { if (it % 2 == 0) DementiaAnswer.Yes else DementiaAnswer.No }
    )
    DementiaInspectionResultContent(
        testResult = result,
        isWebViewShowing = false,
        isGuideShowing = true,
        savedLanguage = "ko",
        onClickBackFromWeb = {},
        onCloseGuide = {},
        onShowWeb = {},
        onShowGuide = {},
    )
}