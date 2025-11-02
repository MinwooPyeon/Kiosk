package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.shortdistance

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.components.VisualAcuityResultRow
import com.pixelro.nenoonkiosk.ui.theme.inputTextStyle

/**
 * 근거리 시력 검사 결과 표시 컴포넌트
 *
 * ## 표기 방식
 *  - 좌안/우안 각각 표시
 *  - 언어 설정(SharedPreferences)에 따라 자동 변환
 *  - value는 1~10 범위 (10이 가장 좋은 시력)
 *
 * @param inspectionResult 시력 검사 결과 (leftEye, rightEye 값 포함, 1~10 범위)
 * @param navController 네비게이션 컨트롤러
 */
@Composable
fun ShortDistanceVisualAcuityInspectionResultScreen(
    inspectionResult: ShortVisualAcuityInspectionResult,
    navController: NavHostController,
) {
    // 리소스
    val myResultText = stringResource(R.string.test_result_my_result)
    val leftEyeText = stringResource(R.string.test_result_left)
    val rightEyeText = stringResource(R.string.test_result_right)

    Column(
        modifier = Modifier
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

        //왼쪽 시력
        VisualAcuityResultRow(
            label = leftEyeText,
            value = formatVisualAcuity(inspectionResult.leftEye),
        )

        Spacer(modifier = Modifier.height(10.dp))

        //오른쪽 시력
        VisualAcuityResultRow(
            label = rightEyeText,
            value = formatVisualAcuity(inspectionResult.rightEye),
        )
    }
}

/**
 * 시력 값을 언어별로 변환
 */
private fun formatVisualAcuity(value: Int): String {
    return try {
        when (SharedPreferencesManager.getString("language")) {
            "ko" -> "${value.toFloat() / 10}"
            "en" -> "20/${(200 / value).toInt()}"
            "zh" -> "${4 + (value.toFloat() / 10)}"
            "ja" -> "${value.toFloat() / 10}"
            else -> "${value.toFloat() / 10}"
        }
    } catch (e: Exception) {
        "${value.toFloat() / 10}"
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewShortDistanceVisualAcuityResult() {
    ShortDistanceVisualAcuityInspectionResultScreen(
        inspectionResult = ShortVisualAcuityInspectionResult(leftEye = 3, rightEye = 5),
        navController = rememberNavController()
    )
}
