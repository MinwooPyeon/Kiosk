package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.longdistance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.components.VisualAcuityResultRow
import com.pixelro.nenoonkiosk.ui.theme.inputTextStyle

/**
 * 원거리 시력 검사 결과 표시 컴포넌트
 *
 * @param inspectionResult 시력 검사 결과 (leftEye, rightEye 값 포함, 1~10 범위)
 * @param navController 네비게이션 컨트롤러
 */
@Composable
fun LongVisualAcuityInspectionResultContent(
    inspectionResult: LongVisualAcuityInspectionResult,
    navController: NavHostController,
) {
    val myResultText = stringResource(R.string.test_result_my_result)
    val leftEyeText = stringResource(R.string.test_result_left)
    val rightEyeText = stringResource(R.string.test_result_right)

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(40.dp),
    ) {
        Text(
            text = myResultText,
            style = inputTextStyle,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(20.dp))

        VisualAcuityResultRow(
            label = leftEyeText,
            value = formatVisualAcuity(inspectionResult.leftEye),
        )

        Spacer(modifier = Modifier.height(10.dp))

        VisualAcuityResultRow(
            label = rightEyeText,
            value = formatVisualAcuity(inspectionResult.rightEye),
        )
    }
}

private fun formatVisualAcuity(value: Int): String {
    return try {
        when (SharedPreferencesManager.getString("language")) {
            "ko" -> "${value.toFloat() / 10}"
            "en" -> "20/${(200 / value).toInt()}"
            "zh" -> "${4 + (value.toFloat() / 10)}"
            "ja" -> "${value.toFloat() / 10}"
            else -> "${value.toFloat() / 10}"
        }
    } catch (_: Exception) {
        "${value.toFloat() / 10}"
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewLongDistanceVisualAcuityResult() {
    LongVisualAcuityInspectionResultContent(
        inspectionResult = LongVisualAcuityInspectionResult(leftEye = 5, rightEye = 7),
        navController = rememberNavController(),
    )
}

