package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResultContent
import com.pixelro.nenoonkiosk.feature.inspection.components.InspectionDivider
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components.DarkBottomTripleButtons
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components.LightBottomArea
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components.LoadingView
import com.pixelro.nenoonkiosk.core.ui.TypewriterText
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.Red
import com.pixelro.nenoonkiosk.ui.theme.White

@Composable
fun InspectionResultScreen(
    titleText: String,
    isDarkBackground: Boolean,
    showLoading: Boolean,
    savedLanguage: String?,
    resultContent: @Composable () -> Unit,
    aiCommentText: AnnotatedString?,
    printEnabled: Boolean,
    onPrint: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = if (isDarkBackground) Black else White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NenoonTopBar(
            title = titleText,
            showBackButton = false,
            containerColor = if (isDarkBackground) Black else White,
            contentColor = if (isDarkBackground) White else Black,
        )
        InspectionDivider()

        // 본문 영역 (weight로 남은 공간 차지)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (showLoading) {
                LoadingView()
            } else {
                if (isLandscape()) {
                    // 가로 모드: 결과 콘텐츠 왼쪽, 버튼 영역 오른쪽
                    Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // 왼쪽: 결과 콘텐츠 + AI 코멘트
                    Column(
                        modifier =
                            Modifier
                                .weight(2.5f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        resultContent()

                        if (aiCommentText != null) {
                            TypewriterText(
                                text = aiCommentText,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(40.dp))

                    // 오른쪽: 하단 버튼 영역
                    Column(
                        modifier =
                            Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                                .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        if (isDarkBackground) {
                            DarkBottomTripleButtons(
                                onRetest = onBack,
                                onToBeginning = onBack,
                                onLogout = onLogout,
                            )
                        } else {
                            LightBottomArea(
                                printEnabled = printEnabled,
                                onPrint = onPrint,
                                onBack = onBack,
                                onLogout = onLogout,
                            )
                        }
                    }
                    }
                } else {
                    // 세로 모드
                    Column(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        resultContent()

                        if (aiCommentText != null) {
                            TypewriterText(
                                text = aiCommentText,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    if (isDarkBackground) {
                        DarkBottomTripleButtons(
                            onRetest = onBack,
                            onToBeginning = onBack,
                            onLogout = onLogout,
                        )
                    } else {
                        LightBottomArea(
                            printEnabled = printEnabled,
                            onPrint = onPrint,
                            onBack = onBack,
                            onLogout = onLogout,
                        )
                    }
                }
            }
        }

        // 경고문 (하단 고정, 로딩 중이거나 다크 배경일 때는 숨김)
        if (!showLoading && !isDarkBackground) {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 40.dp, bottom = 20.dp),
                text =
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Gray, fontSize = 16.sp)) {
                            append(stringResource(R.string.test_list_screen_warning1))
                        }
                        withStyle(
                            style =
                                SpanStyle(
                                    color = Red,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                        ) {
                            append(stringResource(R.string.test_list_screen_warning2))
                        }
                        withStyle(style = SpanStyle(color = Gray, fontSize = 16.sp)) {
                            append(stringResource(R.string.test_list_screen_warning3))
                        }
                    },
            )
        }
    }
}

internal fun titleFor(testType: InspectionType): String {
    return when (testType) {
        InspectionType.Presbyopia -> StringProvider.getString(R.string.presbyopia_result_title)
        InspectionType.ShortDistanceVisualAcuity -> StringProvider.getString(R.string.short_visual_acuity_result_title)
        InspectionType.LongDistanceVisualAcuity -> StringProvider.getString(R.string.long_visual_acuity_result_title)
        InspectionType.ChildrenVisualAcuity -> StringProvider.getString(R.string.children_visual_acuity_result_title)
        InspectionType.AmslerGrid -> StringProvider.getString(R.string.amsler_grid_result_title)
        InspectionType.MChart -> StringProvider.getString(R.string.mchart_result_title)
        InspectionType.Dementia -> StringProvider.getString(R.string.dementia_result_title)
        InspectionType.Presbyopia_Glasses -> StringProvider.getString(R.string.presbyopia_glasses_result_title)
        InspectionType.Concentration_Glasses -> StringProvider.getString(R.string.concentration_glasses_result_title)
        InspectionType.GripStrength -> StringProvider.getString(R.string.grip_strength_result)
        InspectionType.BloodPressure -> StringProvider.getString(R.string.blood_pressure_result)
        else -> "None TestResultScreen"
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280, name = "Result – Loading Light", apiLevel = 34)
@Composable
private fun Preview_TestResult_Loading_Light() {
    InspectionResultScreen(
        titleText = "검사 결과",
        isDarkBackground = false,
        showLoading = true,
        savedLanguage = "ko",
        resultContent = { /* no-op */ },
        aiCommentText = null,
        printEnabled = true,
        onPrint = {},
        onBack = {},
        onLogout = {},
    )
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800, name = "Result – Loaded Landscape", apiLevel = 34)
@Composable
private fun Preview_TestResult_Loaded_Landscape() {
    InspectionResultScreen(
        titleText = "검사 결과",
        isDarkBackground = false,
        showLoading = false,
        savedLanguage = "ko",
        resultContent = {
            BloodPressureInspectionResultContent(
                testResult = BloodPressureInspectionResult(
                    systolic = 118,
                    diastolic = 76,
                    pulseRate = 68,
                )
            )
        },
        aiCommentText = AnnotatedString("AI 코멘트가 여기에 출력됩니다."),
        printEnabled = true,
        onPrint = {},
        onBack = {},
        onLogout = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Result – Loaded Portrait", apiLevel = 34)
@Composable
private fun Preview_TestResult_Loaded_Portrait() {
    InspectionResultScreen(
        titleText = "검사 결과",
        isDarkBackground = false,
        showLoading = false,
        savedLanguage = "ko",
        resultContent = {
            BloodPressureInspectionResultContent(
                testResult = BloodPressureInspectionResult(
                    systolic = 118,
                    diastolic = 76,
                    pulseRate = 68,
                )
            )
        },
        aiCommentText = AnnotatedString("AI 코멘트가 여기에 출력됩니다."),
        printEnabled = true,
        onPrint = {},
        onBack = {},
        onLogout = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Result – Dark (Glasses)", apiLevel = 34)
@Composable
private fun Preview_TestResult_Dark() {
    InspectionResultScreen(
        titleText = "안경 운동 결과",
        isDarkBackground = false,
        showLoading = false,
        savedLanguage = "ko",
        resultContent = {
            Text(
                "운동 결과 콘텐츠 (프리뷰)",
                color = Color.White,
                modifier = Modifier.padding(40.dp)
            )
        },
        aiCommentText = null,
        printEnabled = false,
        onPrint = {},
        onBack = {},
        onLogout = {},
    )
}
