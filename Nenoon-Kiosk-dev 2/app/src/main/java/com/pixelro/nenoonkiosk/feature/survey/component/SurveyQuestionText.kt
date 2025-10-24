package com.pixelro.nenoonkiosk.feature.survey.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.titleTextStyle

/**
 * 설문조사 질문 텍스트 컴포넌트
 */
@Composable
fun SurveyQuestionText(text: String) {
    Text(
        text = text,
        style = titleTextStyle,
    )
}

// ==================== Previews ====================

@Preview(
    name = "질문 텍스트",
    showBackground = true,
    widthDp = 600,
    heightDp = 200,
)
@Composable
private fun SurveyQuestionTextPreview() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
    ) {
        SurveyQuestionText("나이를 선택해주세요")
    }
}