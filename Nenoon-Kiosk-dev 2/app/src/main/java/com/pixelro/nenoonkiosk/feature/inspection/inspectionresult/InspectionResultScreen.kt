package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.components.InspectionDivider
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components.DarkBottomTripleButtons
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components.LightBottomArea
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components.LoadingView
import com.pixelro.nenoonkiosk.feature.screen.TypewriterText
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
        modifier = if (isDarkBackground) {
            Modifier.fillMaxSize().background(color = Color(0xff000000))
        } else {
            Modifier.fillMaxSize()
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NenoonTopBar(
            title = titleText,
            showBackButton = false,
            containerColor = if (isDarkBackground) Color(0xff000000) else White,
            contentColor = if (isDarkBackground) White else Color(0xff000000)
        )
        InspectionDivider()

        // 본문
        if (showLoading) {
            LoadingView(savedLanguage = savedLanguage)
        } else {
            resultContent()

            if (aiCommentText != null) {
                TypewriterText(
                    text = aiCommentText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isDarkBackground) {
                // 안경 운동 2종 하단 버튼 (디자인 그대로)
                DarkBottomTripleButtons(
                    onRetest = onBack,
                    onToBeginning = onBack,
                    onLogout = onLogout,
                )
            } else {
                // 경고문 + 인쇄/뒤로/로그아웃 (디자인 그대로)
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
        InspectionType.PulmonaryFunction -> StringProvider.getString(R.string.pulmonary_function_test_result)
        InspectionType.GripStrength -> StringProvider.getString(R.string.grip_strength_result)
        InspectionType.BloodPressure -> StringProvider.getString(R.string.blood_pressure_result)
        else -> "None TestResultScreen"
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Result – Loading Light", apiLevel = 34)
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

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Result – Loaded Light", apiLevel = 34)
@Composable
private fun Preview_TestResult_Loaded_Light() {
    InspectionResultScreen(
        titleText = "검사 결과",
        isDarkBackground = false,
        showLoading = false,
        savedLanguage = "ko",
        resultContent = {
            Text("결과 콘텐츠 자리 (프리뷰용)", modifier = Modifier.padding(40.dp))
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
        isDarkBackground = true,
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
