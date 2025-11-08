package com.pixelro.nenoonkiosk.feature.survey.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.LightGray100
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * 설문 진행 표시기 컴포넌트
 *
 * @param currentStep 현재 단계 (1부터 시작)
 * @param totalSteps 전체 단계 수
 */
@Composable
fun SurveyProgressBar(
    currentStep: Int,
    totalSteps: Int,
) {
    Box(
        Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.66f)
                    .background(LightGray100, RoundedCornerShape(8.dp)),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(currentStep / totalSteps.toFloat())
                        .background(neNoon_blue, RoundedCornerShape(8.dp)),
            )
        }
    }
}

// ==================== Previews ====================

@Preview(
    name = "진행 표시기 (1/5)",
    showBackground = true,
    widthDp = 800,
    heightDp = 200,
)
@Composable
private fun SurveyProgressBar1of5Preview() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        SurveyProgressBar(
            currentStep = 1,
            totalSteps = 5,
        )
    }
}