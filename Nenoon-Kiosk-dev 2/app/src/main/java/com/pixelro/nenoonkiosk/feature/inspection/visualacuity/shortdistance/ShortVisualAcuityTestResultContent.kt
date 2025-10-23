package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.util.StringProvider

// 광주
@Composable
fun ShortDistanceVisualAcuityTestResultContent(
    testResult: ShortVisualAcuityTestResult,
    navController: NavHostController,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        Text(
            modifier =
                Modifier
                    .padding(start = 40.dp, top = 40.dp),
            text = StringProvider.getString(R.string.test_result_my_result),
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(
            modifier =
                Modifier
                    .height(20.dp),
        )
        Row(
            modifier =
                Modifier
                    .background(
                        color = Color(0xfff7f9f9),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(20.dp),
        ) {
            Spacer(
                modifier =
                    Modifier
                        .width(20.dp),
            )
            Text(
                text = StringProvider.getString(R.string.test_result_left) + " : ",
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier.width(180.dp),
            )

            /**
             * 왼쪽 시력 표기
             */
            Text(
                text =
                    when (SharedPreferencesManager.getString("language")) {
                        "ko" -> "${testResult.leftEye.toFloat() / 10}"
                        "en" -> "20/" + "${(200 / testResult.leftEye).toInt()}"
                        "zh" -> "${4 + (testResult.leftEye.toFloat() / 10)}"
                        "ja" -> "${testResult.leftEye.toFloat() / 10}"
                        else -> "${testResult.leftEye.toFloat() / 10}"
                    },
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

        Row(
            modifier =
                Modifier
                    .background(
                        color = Color(0xfff7f9f9),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(20.dp),
        ) {
            Spacer(
                modifier =
                    Modifier
                        .width(20.dp),
            )
            Text(
                text = StringProvider.getString(R.string.test_result_right) + " : ",
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                modifier = Modifier.width(180.dp),
            )

            /**
             * 오른쪽 시력 표기
             */

            Text(
                text =
                    when (SharedPreferencesManager.getString("language")) {
                        "ko" -> "${testResult.rightEye.toFloat() / 10}"
                        "en" -> "20/" + "${(200 / testResult.rightEye).toInt()}"
                        "zh" -> "${4 + (testResult.rightEye.toFloat() / 10)}"
                        "ja" -> "${testResult.rightEye.toFloat() / 10}"
                        else -> "${testResult.rightEye.toFloat() / 10}"
                    },
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}
