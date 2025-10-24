package com.pixelro.nenoonkiosk.feature.survey

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.harang.data.model.User
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.survey.component.SurveyEightOptionsQuestion
import com.pixelro.nenoonkiosk.feature.survey.component.SurveyFourOptionsQuestion
import com.pixelro.nenoonkiosk.feature.survey.component.SurveyProgressBar
import com.pixelro.nenoonkiosk.feature.survey.component.SurveyTwoOptionsQuestion
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 시력검사 전 설문조사 뷰
@Composable
fun SurveyScreen(
    isLoggedIn: Boolean,
    toCategoryListScreen: (Long) -> Unit,
    surveyViewModel: SurveyViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel,
    userData: User?,
    onBack: () -> Unit,
    signOut: () -> Unit,
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val state = surveyViewModel.state.collectAsState().value
    val question = surveyViewModel.currentQuestion.collectAsState().value

    var surveyScreenState by remember { mutableStateOf(SurveyScreenState.InProgress) }

    var isPressed by remember { mutableStateOf(false) }
    val buttonColor by animateColorAsState(
        targetValue = if (isPressed) neNoon_blue else White,
        animationSpec = tween(durationMillis = 500),
    )

    val textColor by animateColorAsState(
        targetValue = if (isPressed) White else neNoon_blue,
        animationSpec = tween(durationMillis = 500),
    )
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = 1000
                    },
                repeatMode = RepeatMode.Restart,
            ),
    )
    val size by transition.animateFloat(
        initialValue = 60f,
        targetValue = 100f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = 1000
                    },
                repeatMode = RepeatMode.Restart,
            ),
    )

    LaunchedEffect(state.isPastSurveyFetched, state.pastSurveyId) {
//        if (!isLoggedIn) {
//            toCategoryListScreen(DebugConstants.SAMPLE_SURVEY_ID)
//        }

        if (state.isPastSurveyFetched) {
            if (state.pastSurveyId != null) {
                toCategoryListScreen(state.pastSurveyId!!)
            } else {
                surveyViewModel.initSurveyData()
                surveyScreenState = SurveyScreenState.InProgress
            }
        } else if (userData?.accessToken != null) {
            surveyViewModel.checkIsSurveyCompleted(userData.accessToken!!)
            surveyScreenState = SurveyScreenState.Loading
        } else if (state.pastSurveyId != null) {
            toCategoryListScreen(state.pastSurveyId!!)
        }
    }

    LaunchedEffect(state.currentQuestion) {
        if (surveyScreenState != SurveyScreenState.Loading) {
            surveyScreenState = SurveyScreenState.InProgress
        }
    }

    LaunchedEffect(Unit) {
        if (DebugConstants.SKIP_SURVEY) {
            toCategoryListScreen(DebugConstants.SAMPLE_SURVEY_ID)
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White),
    ) {
        /**
         * 상단 바
         */
        Box(
            modifier =
                Modifier
                    .padding(
                        top = (GlobalValue.statusBarPadding + 20).dp,
                        bottom = 20.dp,
                    )
                    .fillMaxWidth()
                    .height(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(start = 20.dp)
                        .fillMaxSize(),
                contentAlignment = Alignment.CenterStart,
            ) {
                /**
                 * 뒤로 가기 버튼
                 * 설문 한칸 뒤로 가기
                 */
                Row(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) {
                                if (surveyScreenState == SurveyScreenState.InProgress) {
                                    when (state.currentQuestion) {
                                        QuestionType.Age -> {
                                            onBack()
                                        }

                                        QuestionType.Sex -> {
                                            surveyViewModel.updateQuestionType(QuestionType.Age)
                                        }

                                        QuestionType.Glass -> {
                                            surveyViewModel.updateQuestionType(QuestionType.Sex)
                                        }

                                        QuestionType.Surgery -> {
                                            surveyViewModel.updateQuestionType(QuestionType.Glass)
                                        }

                                        QuestionType.Diabetes -> {
                                            surveyViewModel.updateQuestionType(QuestionType.Surgery)
                                        }
                                    }
                                }
                            },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier =
                            Modifier
                                .padding(top = 4.dp)
                                .width(28.dp),
                        painter = painterResource(id = R.drawable.icon_back_black),
                        contentDescription = "",
                    )
                    Text(
                        text = StringProvider.getString(R.string.back),
                        fontSize = 24.sp,
                    )
                }
            }
            Text(
                textAlign = TextAlign.Center,
                text = StringProvider.getString(R.string.survey_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(
            modifier =
                Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    ,
        )

        when (surveyScreenState) {
            SurveyScreenState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProgressIndicator()
                    Spacer(modifier = Modifier.height(20.dp))
                    StyledText(StringProvider.getString(R.string.survey_loading_message))
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
                    StyledText(StringProvider.getString(R.string.survey_error_message), TextStyle.Error)
                    Spacer(modifier = Modifier.weight(1f))
                    PrimaryButton(
                        text = StringProvider.getString(R.string.settings_signout),
                        onClick = signOut,
                    )
                }
            }

            SurveyScreenState.InProgress -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    /**
                     * 질문 진행 상황
                     */
                    SurveyProgressBar(
                        currentStep = state.currentQuestion.ordinal + 1,
                        totalSteps = 5,
                    )

                    Column(
                        modifier =
                            Modifier
                                .background(White)
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f)
                                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        when (question) {
                            is SurveyQuestion.EightOptions -> {
                                SurveyEightOptionsQuestion(
                                    questionText = question.questionText,
                                    leftOptions = question.leftOptions,
                                    rightOptions = question.rightOptions,
                                    selectedOption = question.selectedIndex,
                                    onOptionSelected = { index ->
                                        surveyViewModel.handleSelection(index)
                                    },
                                )
                            }

                            is SurveyQuestion.FourOptions -> {
                                SurveyFourOptionsQuestion(
                                    questionText = question.questionText,
                                    topOptions = question.topOptions,
                                    bottomOptions = question.bottomOptions,
                                    selectedOption = question.selectedIndex,
                                    onOptionSelected = { index ->
                                        surveyViewModel.handleSelection(index)
                                    },
                                )
                            }

                            is SurveyQuestion.TwoOptions -> {
                                SurveyTwoOptionsQuestion(
                                    questionText = question.questionText,
                                    option1Text = question.option1Text,
                                    option2Text = question.option2Text,
                                    selectedOption = question.selectedIndex,
                                    onOptionSelected = { index ->
                                        surveyViewModel.handleSelection(index) {
                                            surveyViewModel.getSurveyId(
                                                token = userData?.accessToken,
                                                toCategoryListScreen = toCategoryListScreen,
                                                isSignInSkipped = { loginViewModel.isUserSignInSkipped() },
                                            ) {
                                                surveyScreenState = SurveyScreenState.Error
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }


                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(
                                    color = White,
                                )
                                .padding(40.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .width(70.dp)
                                        .height(70.dp)
                                        .background(color = neNoon_blue)
                                        .border(
                                            border =
                                                BorderStroke(
                                                    width = 4.dp,
                                                    color = White,
                                                ),
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = (state.currentQuestion.ordinal + 1).toString(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 40.sp,
                                    color = White,
                                    fontWeight = FontWeight.ExtraBold,
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

@Composable
private fun SurveyQuestionPreview(question: SurveyQuestion) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White),
    ) {
        Box(
            modifier =
                Modifier
                    .padding(top = 40.dp, bottom = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "설문조사",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        SurveyProgressBar(
            currentStep = 1,
            totalSteps = 5,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (question) {
                is SurveyQuestion.EightOptions -> {
                    SurveyEightOptionsQuestion(
                        questionText = question.questionText,
                        leftOptions = question.leftOptions,
                        rightOptions = question.rightOptions,
                        selectedOption = question.selectedIndex,
                        onOptionSelected = {},
                    )
                }

                is SurveyQuestion.FourOptions -> {
                    SurveyFourOptionsQuestion(
                        questionText = question.questionText,
                        topOptions = question.topOptions,
                        bottomOptions = question.bottomOptions,
                        selectedOption = question.selectedIndex,
                        onOptionSelected = {},
                    )
                }

                is SurveyQuestion.TwoOptions -> {
                    SurveyTwoOptionsQuestion(
                        questionText = question.questionText,
                        option1Text = question.option1Text,
                        option2Text = question.option2Text,
                        selectedOption = question.selectedIndex,
                        onOptionSelected = {},
                    )
                }
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White)
                    .padding(40.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Box(
                    modifier =
                        Modifier
                            .width(70.dp)
                            .height(70.dp)
                            .background(color = neNoon_blue)
                            .border(
                                border =
                                    BorderStroke(
                                        width = 4.dp,
                                        color = White,
                                    ),
                                shape = RoundedCornerShape(8.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "1",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        color = White,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "나이 질문 (8개 옵션)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyAgeQuestionPreview() {
    SurveyQuestionPreview(
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
    SurveyQuestionPreview(
        question =
            SurveyQuestion.TwoOptions(
                questionText = "성별을 선택해주세요",
                option1Text = "남성",
                option2Text = "여성",
                selectedIndex = 1,
            ),
    )
}

@Preview(
    name = "안경 착용 질문 (2개 옵션)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyGlassQuestionPreview() {
    SurveyQuestionPreview(
        question =
            SurveyQuestion.TwoOptions(
                questionText = "안경을 착용하시나요?",
                option1Text = "예",
                option2Text = "아니오",
                selectedIndex = 0,
            ),
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
    SurveyQuestionPreview(
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
    )
}

@Preview(
    name = "당뇨 질문 (2개 옵션)",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
)
@Composable
private fun SurveyDiabetesQuestionPreview() {
    SurveyQuestionPreview(
        question =
            SurveyQuestion.TwoOptions(
                questionText = "당뇨가 있으신가요?",
                option1Text = "예",
                option2Text = "아니오",
                selectedIndex = 2,
            ),
    )
}
