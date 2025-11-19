package com.pixelro.nenoonkiosk.feature.survey.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 2개 선택지 질문 컴포넌트 (성별, 안경, 당뇨)
 *
 * @param questionText 질문 텍스트
 * @param option1Text 첫 번째 선택지 텍스트
 * @param option2Text 두 번째 선택지 텍스트
 * @param selectedOption 선택된 옵션 인덱스 (1 또는 2, 0이면 미선택)
 * @param onOptionSelected 선택지 클릭 시 호출되는 콜백 (1 또는 2)
 */
@Composable
fun SurveyTwoOptionsQuestion(
    questionText: String,
    option1Text: String,
    option2Text: String,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        //SurveyQuestionText(text = questionText)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SurveyOptionButton(
                text = option1Text,
                isSelected = selectedOption == 1,
                onClick = { onOptionSelected(1) },
                modifier =
                    Modifier
                        .weight(1f)
                        .height(300.dp),
            )

            SurveyOptionButton(
                text = option2Text,
                isSelected = selectedOption == 2,
                onClick = { onOptionSelected(2) },
                modifier =
                    Modifier
                        .weight(1f)
                        .height(300.dp),
            )
        }
    }
}

// ==================== Previews ====================

@Preview(
    name = "성별 선택 (선택 안됨)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyTwoOptionsQuestionUnselectedPreview() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
    ) {
        SurveyTwoOptionsQuestion(
            questionText = "성별을 선택해주세요",
            option1Text = "남성",
            option2Text = "여성",
            selectedOption = 0,
            onOptionSelected = {},
        )
    }
}

@Preview(
    name = "성별 선택 (남성 선택됨)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyTwoOptionsQuestionOption1SelectedPreview() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
    ) {
        SurveyTwoOptionsQuestion(
            questionText = "성별을 선택해주세요",
            option1Text = "남성",
            option2Text = "여성",
            selectedOption = 1,
            onOptionSelected = {},
        )
    }
}