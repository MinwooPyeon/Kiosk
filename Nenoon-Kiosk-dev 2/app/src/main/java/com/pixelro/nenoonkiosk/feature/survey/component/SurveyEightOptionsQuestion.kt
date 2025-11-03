package com.pixelro.nenoonkiosk.feature.survey.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
 * 8개 선택지 질문 컴포넌트 (나이)
 *
 * @param questionText 질문 텍스트
 * @param leftOptions 왼쪽 열 선택지 목록 (4개)
 * @param rightOptions 오른쪽 열 선택지 목록 (4개)
 * @param selectedOption 선택된 옵션 인덱스 (1~8, 0이면 미선택)
 * @param onOptionSelected 선택지 클릭 시 호출되는 콜백 (1~8)
 */
@Composable
fun SurveyEightOptionsQuestion(
    questionText: String,
    leftOptions: List<String>,
    rightOptions: List<String>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        //SurveyQuestionText(text = questionText)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // 왼쪽 열 (1~4)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                leftOptions.forEachIndexed { index, text ->
                    val optionIndex = index + 1
                    SurveyOptionButton(
                        text = text,
                        isSelected = selectedOption == optionIndex,
                        onClick = { onOptionSelected(optionIndex) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                    )
                }
            }

            // 오른쪽 열 (5~8)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                rightOptions.forEachIndexed { index, text ->
                    val optionIndex = index + 5
                    SurveyOptionButton(
                        text = text,
                        isSelected = selectedOption == optionIndex,
                        onClick = { onOptionSelected(optionIndex) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                    )
                }
            }
        }
    }
}

// ==================== Previews ====================

@Preview(
    name = "나이 선택 (선택 안됨)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyEightOptionsQuestionUnselectedPreview() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
    ) {
        SurveyEightOptionsQuestion(
            questionText = "나이를 선택해주세요",
            leftOptions = listOf("0~9세", "20대", "40대", "60대"),
            rightOptions = listOf("10대", "30대", "50대", "70대 이상"),
            selectedOption = 0,
            onOptionSelected = {},
        )
    }
}

@Preview(
    name = "나이 선택 (0~9세 선택됨)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyEightOptionsQuestionOption1SelectedPreview() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
    ) {
        SurveyEightOptionsQuestion(
            questionText = "나이를 선택해주세요",
            leftOptions = listOf("0~9세", "20대", "40대", "60대"),
            rightOptions = listOf("10대", "30대", "50대", "70대 이상"),
            selectedOption = 1,
            onOptionSelected = {},
        )
    }
}
