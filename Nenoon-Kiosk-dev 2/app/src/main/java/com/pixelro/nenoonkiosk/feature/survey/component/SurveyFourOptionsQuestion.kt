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
 * 4개 선택지 질문 컴포넌트 (수술)
 *
 * @param questionText 질문 텍스트
 * @param topOptions 상단 행 선택지 목록 (2개)
 * @param bottomOptions 하단 행 선택지 목록 (2개)
 * @param selectedOption 선택된 옵션 인덱스 (1~4, 0이면 미선택)
 * @param onOptionSelected 선택지 클릭 시 호출되는 콜백 (1~4)
 */
@Composable
fun SurveyFourOptionsQuestion(
    questionText: String,
    topOptions: List<String>,
    bottomOptions: List<String>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SurveyQuestionText(text = questionText)

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(0.95f),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // 상단 행 (1~2)
            Row(
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                topOptions.forEachIndexed { index, text ->
                    val optionIndex = index + 1
                    SurveyOptionButton(
                        text = text,
                        isSelected = selectedOption == optionIndex,
                        onClick = { onOptionSelected(optionIndex) },
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                    )
                }
            }

            // 하단 행 (3~4)
            Row(
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                bottomOptions.forEachIndexed { index, text ->
                    val optionIndex = index + 3
                    SurveyOptionButton(
                        text = text,
                        isSelected = selectedOption == optionIndex,
                        onClick = { onOptionSelected(optionIndex) },
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                    )
                }
            }
        }
    }
}

// ==================== Previews ====================

@Preview(
    name = "수술 이력 (선택 안됨)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyFourOptionsQuestionUnselectedPreview() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
    ) {
        SurveyFourOptionsQuestion(
            questionText = "눈 수술을 받으신 적이 있나요?",
            topOptions = listOf("없음", "라식/라섹"),
            bottomOptions = listOf("백내장", "기타"),
            selectedOption = 0,
            onOptionSelected = {},
        )
    }
}

@Preview(
    name = "수술 이력 (없음 선택됨)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyFourOptionsQuestionOption1SelectedPreview() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
    ) {
        SurveyFourOptionsQuestion(
            questionText = "눈 수술을 받으신 적이 있나요?",
            topOptions = listOf("없음", "라식/라섹"),
            bottomOptions = listOf("백내장", "기타"),
            selectedOption = 1,
            onOptionSelected = {},
        )
    }
}