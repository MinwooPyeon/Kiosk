package com.pixelro.nenoonkiosk.feature.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.survey.component.SurveyEightOptionsQuestion
import com.pixelro.nenoonkiosk.feature.survey.component.SurveyFourOptionsQuestion
import com.pixelro.nenoonkiosk.feature.survey.component.SurveyProgressBar
import com.pixelro.nenoonkiosk.feature.survey.component.SurveyTwoOptionsQuestion
import com.pixelro.nenoonkiosk.ui.theme.White

/**
 * Survey Screen
 *
 * @param screenState 현재 화면 상태 (Loading, InProgress, Error)
 * @param currentQuestion 현재 질문 타입
 * @param question 현재 질문 데이터
 * @param onBack 뒤로가기 콜백
 * @param onAnswerSelected 답변 선택 콜백
 * @param onSignOut 로그아웃 콜백
 */
@Composable
fun SurveyScreen(
    screenState: SurveyScreenState,
    currentQuestion: QuestionType,
    question: SurveyQuestion,
    onBack: () -> Unit,
    onAnswerSelected: (Int) -> Unit,
    onSignOut: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White),
    ) {
        /**
         * 상단 바
         */
        NenoonTopBar(
            title = if (screenState == SurveyScreenState.InProgress) {
                when (question) {
                    is SurveyQuestion.EightOptions -> question.questionText
                    is SurveyQuestion.FourOptions -> question.questionText
                    is SurveyQuestion.TwoOptions -> question.questionText
                }
            } else {
                stringResource(R.string.survey_title)
            },
            orientation = if (isLandscape()) TopBarOrientation.Horizontal else TopBarOrientation.Vertical,
            showBackButton = true,
            onBackClicked = {
                if (screenState == SurveyScreenState.InProgress) {
                    onBack()
                }
            },
        )

        Spacer(
            modifier =
                Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
                    .height(1.dp),
        )

        when (screenState) {
            SurveyScreenState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProgressIndicator()
                    Spacer(modifier = Modifier.height(20.dp))
                    StyledText(stringResource(R.string.survey_loading_message))
                }
            }

            SurveyScreenState.Error -> {
                Column(
                    modifier =
                        Modifier
                            .padding(40.dp)
                            .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    StyledText(stringResource(R.string.survey_error_message), TextStyle.Error)
                    Spacer(modifier = Modifier.weight(1f))
                    PrimaryButton(
                        text = stringResource(R.string.settings_signout),
                        onClick = onSignOut,
                    )
                }
            }

            SurveyScreenState.InProgress -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    /**
                     * 질문 진행 상황
                     */
                    SurveyProgressBar(
                        currentStep = currentQuestion.ordinal + 1,
                        totalSteps = 5,
                    )

                    Column(
                        modifier =
                            Modifier
                                .background(White)
                                .fillMaxWidth()
                                .fillMaxHeight(1f)
                                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        when (question) {
                            is SurveyQuestion.EightOptions -> {
                                SurveyEightOptionsQuestion(
                                    questionText = question.questionText,
                                    leftOptions = question.leftOptions,
                                    rightOptions = question.rightOptions,
                                    selectedOption = question.selectedIndex,
                                    onOptionSelected = onAnswerSelected,
                                )
                            }

                            is SurveyQuestion.FourOptions -> {
                                SurveyFourOptionsQuestion(
                                    questionText = question.questionText,
                                    topOptions = question.topOptions,
                                    bottomOptions = question.bottomOptions,
                                    selectedOption = question.selectedIndex,
                                    onOptionSelected = onAnswerSelected,
                                )
                            }

                            is SurveyQuestion.TwoOptions -> {
                                SurveyTwoOptionsQuestion(
                                    questionText = question.questionText,
                                    option1Text = question.option1Text,
                                    option2Text = question.option2Text,
                                    selectedOption = question.selectedIndex,
                                    onOptionSelected = onAnswerSelected,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== Previews ====================
@Preview(
    name = "나이 질문 (8개 옵션)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyAgeQuestionPreview() {
    SurveyScreen(
        screenState = SurveyScreenState.InProgress,
        currentQuestion = QuestionType.Age,
        question =
            SurveyQuestion.EightOptions(
                questionText = "나이를 선택해주세요",
                leftOptions =
                    listOf(
                        "9세 이하",
                        "20대",
                        "40대",
                        "60대",
                    ),
                rightOptions =
                    listOf(
                        "10대",
                        "30대",
                        "50대",
                        "70세 이상",
                    ),
                selectedIndex = 2,
            ),
        onBack = {},
        onAnswerSelected = {},
        onSignOut = {},
    )
}

@Preview(
    name = "성별 질문 (2개 옵션)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveySexQuestionPreview() {
    SurveyScreen(
        screenState = SurveyScreenState.InProgress,
        currentQuestion = QuestionType.Sex,
        question =
            SurveyQuestion.TwoOptions(
                questionText = "성별을 선택해주세요",
                option1Text = "남성",
                option2Text = "여성",
                selectedIndex = 1,
            ),
        onBack = {},
        onAnswerSelected = {},
        onSignOut = {},
    )
}

@Preview(
    name = "안경 착용 질문 (2개 옵션)",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
)
@Composable
private fun SurveyGlassQuestionPreview() {
    SurveyScreen(
        screenState = SurveyScreenState.InProgress,
        currentQuestion = QuestionType.Glass,
        question =
            SurveyQuestion.TwoOptions(
                questionText = "안경을 착용하시나요?",
                option1Text = "예",
                option2Text = "아니오",
                selectedIndex = 0,
            ),
        onBack = {},
        onAnswerSelected = {},
        onSignOut = {},
    )
}

@Preview(
    name = "수술 이력 질문 (4개 옵션)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveySurgeryQuestionPreview() {
    SurveyScreen(
        screenState = SurveyScreenState.InProgress,
        currentQuestion = QuestionType.Surgery,
        question =
            SurveyQuestion.FourOptions(
                questionText = "눈 관련 수술 이력이 있으신가요?",
                topOptions =
                    listOf(
                        "없음",
                        "라식/라섹",
                    ),
                bottomOptions =
                    listOf(
                        "백내장",
                        "기타",
                    ),
                selectedIndex = 3,
            ),
        onBack = {},
        onAnswerSelected = {},
        onSignOut = {},
    )
}

@Preview(
    name = "당뇨 질문 (2개 옵션)",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
)
@Composable
private fun SurveyDiabetesQuestionPreview() {
    SurveyScreen(
        screenState = SurveyScreenState.InProgress,
        currentQuestion = QuestionType.Diabetes,
        question =
            SurveyQuestion.TwoOptions(
                questionText = "당뇨가 있으신가요?",
                option1Text = "예",
                option2Text = "아니오",
                selectedIndex = 2,
            ),
        onBack = {},
        onAnswerSelected = {},
        onSignOut = {},
    )
}

@Preview(
    name = "로딩 상태",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
)
@Composable
private fun SurveyLoadingStatePreview() {
    SurveyScreen(
        screenState = SurveyScreenState.Loading,
        currentQuestion = QuestionType.Age,
        question =
            SurveyQuestion.EightOptions(
                questionText = "",
                leftOptions = emptyList(),
                rightOptions = emptyList(),
                selectedIndex = 0,
            ),
        onBack = {},
        onAnswerSelected = {},
        onSignOut = {},
    )
}

@Preview(
    name = "에러 상태",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
)
@Composable
private fun SurveyErrorStatePreview() {
    SurveyScreen(
        screenState = SurveyScreenState.Error,
        currentQuestion = QuestionType.Age,
        question =
            SurveyQuestion.EightOptions(
                questionText = "",
                leftOptions = emptyList(),
                rightOptions = emptyList(),
                selectedIndex = 0,
            ),
        onBack = {},
        onAnswerSelected = {},
        onSignOut = {},
    )
}