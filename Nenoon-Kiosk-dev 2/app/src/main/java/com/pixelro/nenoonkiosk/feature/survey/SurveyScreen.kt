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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.harang.data.model.User
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveyAge
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveyDiabetes
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveyGlass
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveySex
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveySurgery
import com.pixelro.nenoonkiosk.feature.auth.login.SignInViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SurveyScreenState {
    Loading,
    InProgress,
    Error,
}

// 시력검사 전 설문조사 뷰
@Composable
fun SurveyScreen(
    isLoggedIn: Boolean,
    toCategoryListScreen: (Long) -> Unit,
    surveyViewModel: SurveyViewModel = hiltViewModel(),
    signInViewModel: SignInViewModel,
    userData: User?,
    onBack: () -> Unit,
    signOut: () -> Unit,
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val questionType = surveyViewModel.questionType.collectAsState().value
    val pastSurveyId = surveyViewModel.pastSurveyId.collectAsState().value
    val isPastSurveyFetched = surveyViewModel.isPastSurveyFetched.collectAsState().value

    var surveyScreenState by remember { mutableStateOf(SurveyScreenState.InProgress) }

    var isPressed by remember { mutableStateOf(false) }
    val buttonColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF1D71E1) else Color.White,
        animationSpec = tween(durationMillis = 500),
    )

    val textColor by animateColorAsState(
        targetValue = if (isPressed) Color.White else Color(0xFF1D71E1),
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

    LaunchedEffect(isPastSurveyFetched, pastSurveyId) {
//        if (!isLoggedIn) {
//            toCategoryListScreen(DebugConstants.SAMPLE_SURVEY_ID)
//        }

        if (isPastSurveyFetched) {
            if (pastSurveyId != null) {
                toCategoryListScreen(pastSurveyId)
            } else {
                surveyViewModel.initSurveyData()
                surveyScreenState = SurveyScreenState.InProgress
            }
        } else if (userData?.accessToken != null) {
            surveyViewModel.checkIsSurveyCompleted(userData.accessToken!!)
            surveyScreenState = SurveyScreenState.Loading
        } else if (pastSurveyId != null) {
            toCategoryListScreen(pastSurveyId)
        }
    }

    LaunchedEffect(questionType) {
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
                .background(Color(0xFFFFFFFF)),
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
                                    when (questionType) {
                                        SurveyViewModel.QuestionType.Age -> {
                                            onBack()
                                        }

                                        SurveyViewModel.QuestionType.Sex -> {
                                            surveyViewModel.updateQuestionType(SurveyViewModel.QuestionType.Age)
                                        }

                                        SurveyViewModel.QuestionType.Glass -> {
                                            surveyViewModel.updateQuestionType(SurveyViewModel.QuestionType.Sex)
                                        }

                                        SurveyViewModel.QuestionType.Surgery -> {
                                            surveyViewModel.updateQuestionType(SurveyViewModel.QuestionType.Glass)
                                        }

                                        SurveyViewModel.QuestionType.Diabetes -> {
                                            surveyViewModel.updateQuestionType(SurveyViewModel.QuestionType.Surgery)
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
                    .background(
                        color = Color(0xffdddddd),
                    ),
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
                    Box(
                        Modifier
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .height(20.dp)
                                    .fillMaxWidth(0.66f)
                                    .background(Color(0xffdddddd), RoundedCornerShape(8.dp)),
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth((questionType.ordinal + 1) / 5f)
                                        .background(Color(0xFF1D71E1), RoundedCornerShape(8.dp)),
                            )
                        }
                    }

                    Column(
                        modifier =
                            Modifier
                                .background(Color(0xFFFFFFFF))
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f)
                                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        when (questionType) {
                            /**
                             * 나이 질문
                             */
                            SurveyViewModel.QuestionType.Age -> {
                                Text(
                                    modifier =
                                        Modifier
                                            .padding(bottom = 20.dp),
                                    text =
                                        StringProvider.getString(
                                            R.string.survey_age,
                                        ),
                                    fontSize = 60.sp,
                                    fontWeight = FontWeight.Medium,
                                )

                                /**
                                 * 선택지
                                 */

                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth(),
                                ) {
                                    /**
                                     * 좌측 (0대, 20대, 40대, 60대)
                                     */
                                    Column(
                                        modifier =
                                            Modifier
                                                .weight(1f),
                                    ) {
                                        for (idx in 1..4) {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .weight(1f)
                                                        .clip(
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .border(
                                                            border =
                                                                BorderStroke(
                                                                    width = 4.dp,
                                                                    color =
                                                                        when (idx to surveyViewModel.surveyAge.collectAsState().value) {
                                                                            1 to SurveyAge.First,
                                                                            2 to SurveyAge.Third,
                                                                            3 to SurveyAge.Fifth,
                                                                            4 to SurveyAge.Seventh,
                                                                            ->
                                                                                Color(
                                                                                    0xff1d71e1,
                                                                                )

                                                                            else -> Color(0xff1d71e1)
                                                                        },
                                                                ),
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .background(
                                                            color =
                                                                when (idx to surveyViewModel.surveyAge.collectAsState().value) {
                                                                    1 to SurveyAge.First,
                                                                    2 to SurveyAge.Third,
                                                                    3 to SurveyAge.Fifth,
                                                                    4 to SurveyAge.Seventh,
                                                                    -> buttonColor

                                                                    else -> Color(0xFFFFFFFF)
                                                                },
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .clickable(
                                                            indication = null,
                                                            interactionSource = remember { MutableInteractionSource() },
                                                        ) {
                                                            surveyViewModel.updateSurveyAge(
                                                                when (idx) {
                                                                    1 -> SurveyAge.First
                                                                    2 -> SurveyAge.Third
                                                                    3 -> SurveyAge.Fifth
                                                                    else -> SurveyAge.Seventh
                                                                },
                                                            )
                                                            surveyViewModel.updateQuestionType(
                                                                SurveyViewModel.QuestionType.Sex,
                                                            )
                                                            coroutineScope.launch {
                                                                if (!isPressed) {
                                                                    isPressed = true
                                                                    delay(500)
                                                                    isPressed = false
                                                                }
                                                            }
                                                        },
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text =
                                                        when (idx) {
                                                            1 ->
                                                                StringProvider.getString(
                                                                    R.string.survey_under9,
                                                                )
                                                            2 ->
                                                                StringProvider.getString(
                                                                    R.string.survey_20s,
                                                                )
                                                            3 ->
                                                                StringProvider.getString(
                                                                    R.string.survey_40s,
                                                                )
                                                            else ->
                                                                StringProvider.getString(
                                                                    R.string.survey_60s,
                                                                )
                                                        },
                                                    fontSize = 60.sp,
                                                    color =
                                                        when (idx to surveyViewModel.surveyAge.collectAsState().value) {
                                                            1 to SurveyAge.First,
                                                            2 to SurveyAge.Third,
                                                            3 to SurveyAge.Fifth,
                                                            4 to SurveyAge.Seventh,
                                                            -> textColor

                                                            else -> Color(0xFF1D71E1)
                                                        },
                                                    fontWeight = FontWeight.ExtraBold,
                                                )
                                            }
                                            when (idx < 4) {
                                                true ->
                                                    Spacer(
                                                        modifier =
                                                            Modifier
                                                                .height(20.dp),
                                                    )

                                                false -> {}
                                            }
                                        }
                                    }
                                    Spacer(
                                        modifier =
                                            Modifier
                                                .width(20.dp),
                                    )
                                    /**
                                     * 우측 (10대, 30대, 50대, 70대 이상)
                                     */
                                    Column(
                                        modifier =
                                            Modifier
                                                .weight(1f),
                                    ) {
                                        for (idx in 5..8) {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .weight(1f)
                                                        .clip(
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .border(
                                                            border =
                                                                BorderStroke(
                                                                    width = 4.dp,
                                                                    color =
                                                                        when (idx to surveyViewModel.surveyAge.collectAsState().value) {
                                                                            5 to SurveyAge.Second,
                                                                            6 to SurveyAge.Fourth,
                                                                            7 to SurveyAge.Sixth,
                                                                            8 to SurveyAge.Eighth,
                                                                            ->
                                                                                Color(
                                                                                    0xff1d71e1,
                                                                                )

                                                                            else -> Color(0xff1d71e1)
                                                                        },
                                                                ),
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .background(
                                                            color =
                                                                when (idx to surveyViewModel.surveyAge.collectAsState().value) {
                                                                    5 to SurveyAge.Second,
                                                                    6 to SurveyAge.Fourth,
                                                                    7 to SurveyAge.Sixth,
                                                                    8 to SurveyAge.Eighth,
                                                                    -> buttonColor

                                                                    else -> Color(0xFFFFFFFF)
                                                                },
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .clickable(
                                                            indication = null,
                                                            interactionSource = remember { MutableInteractionSource() },
                                                        ) {
                                                            surveyViewModel.updateSurveyAge(
                                                                when (idx) {
                                                                    5 -> SurveyAge.Second
                                                                    6 -> SurveyAge.Fourth
                                                                    7 -> SurveyAge.Sixth
                                                                    else -> SurveyAge.Eighth
                                                                },
                                                            )
                                                            surveyViewModel.updateQuestionType(
                                                                SurveyViewModel.QuestionType.Sex,
                                                            )
                                                            coroutineScope.launch {
                                                                if (!isPressed) {
                                                                    isPressed = true
                                                                    delay(500)
                                                                    isPressed = false
                                                                }
                                                            }
                                                        },
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text =
                                                        when (idx) {
                                                            5 ->
                                                                StringProvider.getString(
                                                                    R.string.survey_10s,
                                                                )
                                                            6 ->
                                                                StringProvider.getString(
                                                                    R.string.survey_30s,
                                                                )
                                                            7 ->
                                                                StringProvider.getString(
                                                                    R.string.survey_50s,
                                                                )
                                                            else ->
                                                                StringProvider.getString(
                                                                    R.string.survey_above70,
                                                                )
                                                        },
                                                    fontSize = 60.sp,
                                                    color =
                                                        when (idx to surveyViewModel.surveyAge.collectAsState().value) {
                                                            5 to SurveyAge.Second,
                                                            6 to SurveyAge.Fourth,
                                                            7 to SurveyAge.Sixth,
                                                            8 to SurveyAge.Eighth,
                                                            -> textColor

                                                            else -> Color(0xFF1D71E1)
                                                        },
                                                    fontWeight = FontWeight.ExtraBold,
                                                )
                                            }
                                            when (idx < 8) {
                                                true ->
                                                    Spacer(
                                                        modifier =
                                                            Modifier
                                                                .height(20.dp),
                                                    )

                                                false -> {}
                                            }
                                        }
                                    }
                                }
                                //
                                Text(
                                    modifier =
                                        Modifier
                                            .padding(bottom = 20.dp),
                                    text =
                                        StringProvider.getString(
                                            R.string.survey_age,
                                        ),
                                    fontSize = 60.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }

                            SurveyViewModel.QuestionType.Sex -> {
                                /**
                                 * 성별 질문
                                 */
                                Text(
                                    modifier =
                                        Modifier
                                            .padding(bottom = 20.dp),
                                    text =
                                        StringProvider.getString(
                                            R.string.survey_sex,
                                        ),
                                    fontSize = 60.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    for (idx in 1..2) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight(0.7f)
                                                    .clip(
                                                        shape = RoundedCornerShape(8.dp),
                                                    )
                                                    .border(
                                                        border =
                                                            BorderStroke(
                                                                width = 4.dp,
                                                                color =
                                                                    when (idx to surveyViewModel.surveySex.collectAsState().value) {
                                                                        1 to SurveySex.Man,
                                                                        2 to SurveySex.Woman,
                                                                        -> Color(0xff1d71e1)

                                                                        else -> Color(0xff1d71e1)
                                                                    },
                                                            ),
                                                        shape = RoundedCornerShape(8.dp),
                                                    )
                                                    .background(
                                                        color =
                                                            when (idx to surveyViewModel.surveySex.collectAsState().value) {
                                                                1 to SurveySex.Man,
                                                                2 to SurveySex.Woman,
                                                                -> buttonColor

                                                                else -> Color(0xFFFFFFFF)
                                                            },
                                                        shape = RoundedCornerShape(8.dp),
                                                    )
                                                    .clickable(
                                                        indication = null,
                                                        interactionSource = remember { MutableInteractionSource() },
                                                    ) {
                                                        surveyViewModel.updateSurveySex(
                                                            when (idx) {
                                                                1 -> SurveySex.Man
                                                                else -> SurveySex.Woman
                                                            },
                                                        )
                                                        surveyViewModel.updateQuestionType(
                                                            SurveyViewModel.QuestionType.Glass,
                                                        )
                                                        coroutineScope.launch {
                                                            if (!isPressed) {
                                                                isPressed = true
                                                                delay(500)
                                                                isPressed = false
                                                            }
                                                        }
                                                    }
                                                    .padding(
                                                        start = 20.dp,
                                                        top = 16.dp,
                                                        end = 20.dp,
                                                        bottom = 16.dp,
                                                    ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text =
                                                    when (idx) {
                                                        1 ->
                                                            StringProvider.getString(
                                                                R.string.survey_male,
                                                            )
                                                        else ->
                                                            StringProvider.getString(
                                                                R.string.survey_female,
                                                            )
                                                    },
                                                fontSize = 60.sp,
                                                color =
                                                    when (idx to surveyViewModel.surveySex.collectAsState().value) {
                                                        1 to SurveySex.Man,
                                                        2 to SurveySex.Woman,
                                                        -> textColor

                                                        else -> Color(0xFF1D71E1)
                                                    },
                                                fontWeight = FontWeight.ExtraBold,
                                            )
                                        }
                                        when (idx < 2) {
                                            true ->
                                                Spacer(
                                                    modifier =
                                                        Modifier
                                                            .width(20.dp),
                                                )

                                            false -> {}
                                        }
                                    }
                                }
                            }

                            SurveyViewModel.QuestionType.Glass -> {
                                /**
                                 * 안경 질문
                                 */
                                Text(
                                    modifier =
                                        Modifier
                                            .padding(bottom = 20.dp),
                                    text =
                                        StringProvider.getString(
                                            R.string.survey_glasses,
                                        ),
                                    fontSize = 60.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    for (idx in 1..2) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight(0.7f)
                                                    .clip(
                                                        shape = RoundedCornerShape(8.dp),
                                                    )
                                                    .border(
                                                        border =
                                                            BorderStroke(
                                                                width = 4.dp,
                                                                color =
                                                                    when (idx to surveyViewModel.surveyGlass.collectAsState().value) {
                                                                        1 to SurveyGlass.Yes,
                                                                        2 to SurveyGlass.No,
                                                                        -> Color(0xFF1D71E1)

                                                                        else -> Color(0xFF1D71E1)
                                                                    },
                                                            ),
                                                        shape = RoundedCornerShape(8.dp),
                                                    )
                                                    .background(
                                                        color =
                                                            when (idx to surveyViewModel.surveyGlass.collectAsState().value) {
                                                                1 to SurveyGlass.Yes,
                                                                2 to SurveyGlass.No,
                                                                -> buttonColor

                                                                else -> Color(0xFFFFFFFF)
                                                            },
                                                        shape = RoundedCornerShape(8.dp),
                                                    )
                                                    .clickable(
                                                        indication = null,
                                                        interactionSource = remember { MutableInteractionSource() },
                                                    ) {
                                                        surveyViewModel.updateSurveyGlass(
                                                            when (idx) {
                                                                1 -> SurveyGlass.Yes
                                                                else -> SurveyGlass.No
                                                            },
                                                        )
                                                        surveyViewModel.updateQuestionType(
                                                            SurveyViewModel.QuestionType.Surgery,
                                                        )
                                                        coroutineScope.launch {
                                                            if (!isPressed) {
                                                                isPressed = true
                                                                delay(500)
                                                                isPressed = false
                                                            }
                                                        }
                                                    }
                                                    .padding(
                                                        start = 20.dp,
                                                        top = 16.dp,
                                                        end = 20.dp,
                                                        bottom = 16.dp,
                                                    ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text =
                                                    when (idx) {
                                                        1 ->
                                                            StringProvider.getString(
                                                                R.string.yes,
                                                            )
                                                        else ->
                                                            StringProvider.getString(
                                                                R.string.no,
                                                            )
                                                    },
                                                fontSize = 60.sp,
                                                color =
                                                    when (idx to surveyViewModel.surveyGlass.collectAsState().value) {
                                                        1 to SurveyGlass.Yes,
                                                        2 to SurveyGlass.No,
                                                        -> textColor

                                                        else -> Color(0xFF1D71E1)
                                                    },
                                                fontWeight = FontWeight.ExtraBold,
                                            )
                                        }
                                        when (idx < 2) {
                                            true ->
                                                Spacer(
                                                    modifier =
                                                        Modifier
                                                            .width(20.dp),
                                                )

                                            false -> {}
                                        }
                                    }
                                }
                            }

                            SurveyViewModel.QuestionType.Surgery -> {
                                /**
                                 * 수술 질문
                                 */
                                Text(
                                    modifier =
                                        Modifier
                                            .padding(bottom = 20.dp),
                                    text =
                                        StringProvider.getString(
                                            R.string.survey_surgery,
                                        ),
                                    fontSize = 56.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                )
                                Column {
                                    Row(
                                        modifier =
                                            Modifier
                                                .weight(1f),
                                    ) {
                                        for (idx in 1..2) {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                        .clip(
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .border(
                                                            border =
                                                                BorderStroke(
                                                                    width = 4.dp,
                                                                    color =
                                                                        when (idx to surveyViewModel.surveySurgery.collectAsState().value) {
                                                                            1 to SurveySurgery.Normal,
                                                                            2 to SurveySurgery.LASIK,
                                                                            -> Color(0xFF1D71E1)

                                                                            else -> Color(0xFF1D71E1)
                                                                        },
                                                                ),
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .background(
                                                            color =
                                                                when (idx to surveyViewModel.surveySurgery.collectAsState().value) {
                                                                    1 to SurveySurgery.Normal,
                                                                    2 to SurveySurgery.LASIK,
                                                                    -> buttonColor

                                                                    else -> Color(0xFFFFFFFF)
                                                                },
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .clickable(
                                                            indication = null,
                                                            interactionSource = remember { MutableInteractionSource() },
                                                        ) {
                                                            surveyViewModel.updateSurveySurgery(
                                                                when (idx) {
                                                                    1 -> SurveySurgery.Normal
                                                                    2 -> SurveySurgery.LASIK
                                                                    else -> SurveySurgery.None
                                                                },
                                                            )
                                                            surveyViewModel.updateQuestionType(
                                                                SurveyViewModel.QuestionType.Diabetes,
                                                            )
                                                            coroutineScope.launch {
                                                                if (!isPressed) {
                                                                    isPressed = true
                                                                    delay(500)
                                                                    isPressed = false
                                                                }
                                                            }
                                                        }
                                                        .padding(
                                                            start = 20.dp,
                                                            top = 16.dp,
                                                            end = 20.dp,
                                                            bottom = 16.dp,
                                                        ),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text =
                                                        when (idx) {
                                                            1 ->
                                                                StringProvider.getString(
                                                                    R.string.survey_none,
                                                                )
                                                            2 ->
                                                                StringProvider.getString(
                                                                    R.string.survey_lasik_lasek,
                                                                )
                                                            else ->
                                                                StringProvider.getString(
                                                                    R.string.survey_etc,
                                                                )
                                                        },
                                                    fontSize = 60.sp,
                                                    color =
                                                        when (idx to surveyViewModel.surveySurgery.collectAsState().value) {
                                                            1 to SurveySurgery.Normal,
                                                            2 to SurveySurgery.LASIK,
                                                            3 to SurveySurgery.Cataract,
                                                            4 to SurveySurgery.Etc,
                                                            -> textColor

                                                            else -> Color(0xFF1D71E1)
                                                        },
                                                    fontWeight = FontWeight.ExtraBold,
                                                )
                                            }
                                            when (idx < 2) {
                                                true ->
                                                    Spacer(
                                                        modifier =
                                                            Modifier
                                                                .width(20.dp),
                                                    )

                                                false -> {}
                                            }
                                        }
                                    }
                                    Spacer(
                                        modifier =
                                            Modifier
                                                .height(20.dp),
                                    )
                                    Row(
                                        modifier =
                                            Modifier
                                                .weight(1f),
                                    ) {
                                        for (idx in 1..2) {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                        .clip(
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .border(
                                                            border =
                                                                BorderStroke(
                                                                    width = 4.dp,
                                                                    color =
                                                                        when (idx to surveyViewModel.surveySurgery.collectAsState().value) {
                                                                            1 to SurveySurgery.Cataract,
                                                                            2 to SurveySurgery.Etc,
                                                                            ->
                                                                                Color(
                                                                                    0xff1d71e1,
                                                                                )

                                                                            else -> Color(0xFF1D71E1)
                                                                        },
                                                                ),
                                                            shape = RoundedCornerShape(8.dp),
                                                        )
                                                        .background(
                                                            color =
                                                                when (idx to surveyViewModel.surveySurgery.collectAsState().value) {
                                                                    1 to SurveySurgery.Cataract,
                                                                    2 to SurveySurgery.Etc,
                                                                    -> buttonColor

                                                                    else -> Color(0xFFFFFFFF)
                                                                },
                                                        )
                                                        .clickable(
                                                            indication = null,
                                                            interactionSource = remember { MutableInteractionSource() },
                                                        ) {
                                                            surveyViewModel.updateSurveySurgery(
                                                                when (idx) {
                                                                    1 -> SurveySurgery.Cataract
                                                                    2 -> SurveySurgery.Etc
                                                                    else -> SurveySurgery.None
                                                                },
                                                            )
                                                            surveyViewModel.updateQuestionType(
                                                                SurveyViewModel.QuestionType.Diabetes,
                                                            )
                                                            coroutineScope.launch {
                                                                if (!isPressed) {
                                                                    isPressed = true
                                                                    delay(500)
                                                                    isPressed = false
                                                                }
                                                            }
                                                        }
                                                        .padding(
                                                            start = 20.dp,
                                                            top = 16.dp,
                                                            end = 20.dp,
                                                            bottom = 16.dp,
                                                        ),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text =
                                                        when (idx) {
                                                            1 ->
                                                                StringProvider.getString(
                                                                    R.string.survey_cataract,
                                                                )
                                                            else ->
                                                                StringProvider.getString(
                                                                    R.string.survey_etc,
                                                                )
                                                        },
                                                    fontSize = 60.sp,
                                                    color =
                                                        when (idx to surveyViewModel.surveySurgery.collectAsState().value) {
                                                            1 to SurveySurgery.Cataract,
                                                            2 to SurveySurgery.Etc,
                                                            -> textColor

                                                            else -> Color(0xFF1D71E1)
                                                        },
                                                    fontWeight = FontWeight.ExtraBold,
                                                )
                                            }
                                            when (idx < 2) {
                                                true ->
                                                    Spacer(
                                                        modifier =
                                                            Modifier
                                                                .width(20.dp),
                                                    )

                                                false -> {}
                                            }
                                        }
                                    }
                                }
                            }

                            SurveyViewModel.QuestionType.Diabetes -> {
                                /**
                                 * 당뇨 질문
                                 */
                                Text(
                                    modifier =
                                        Modifier
                                            .padding(bottom = 20.dp),
                                    text =
                                        StringProvider.getString(
                                            R.string.survey_diabetes,
                                        ),
                                    fontSize = 60.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    for (idx in 1..2) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight(0.7f)
                                                    .clip(
                                                        shape = RoundedCornerShape(8.dp),
                                                    )
                                                    .border(
                                                        border =
                                                            BorderStroke(
                                                                width = 4.dp,
                                                                color =
                                                                    when (idx to surveyViewModel.surveyDiabetes.collectAsState().value) {
                                                                        1 to SurveyDiabetes.Yes,
                                                                        2 to SurveyDiabetes.No,
                                                                        ->
                                                                            Color(
                                                                                0xFF1D71E1,
                                                                            )

                                                                        else -> Color(0xFF1D71E1)
                                                                    },
                                                            ),
                                                        shape = RoundedCornerShape(8.dp),
                                                    )
                                                    .background(
                                                        color =
                                                            when (idx to surveyViewModel.surveyDiabetes.collectAsState().value) {
                                                                1 to SurveyDiabetes.Yes,
                                                                2 to SurveyDiabetes.No,
                                                                -> buttonColor

                                                                else -> Color(0xFFFFFFFF)
                                                            },
                                                    )
                                                    .clickable(
                                                        indication = null,
                                                        interactionSource = remember { MutableInteractionSource() },
                                                    ) {
                                                        surveyViewModel.updateSurveyDiabetes(
                                                            when (idx) {
                                                                1 -> SurveyDiabetes.Yes
                                                                else -> SurveyDiabetes.No
                                                            },
                                                        )
                                                        coroutineScope.launch {
                                                            if (!isPressed) {
                                                                isPressed = true
                                                                // 데이터 전송 후, 다른 페이지로 전환
                                                                surveyViewModel.getSurveyId(
                                                                    token = userData?.accessToken,
                                                                    toCategoryListScreen = toCategoryListScreen,
                                                                    isSignInSkipped = { signInViewModel.isUserSignInSkipped() },
                                                                ) {
                                                                    surveyScreenState =
                                                                        SurveyScreenState.Error
                                                                }
                                                                delay(2000)
                                                                isPressed = false
                                                            }
                                                        }
                                                    }
                                                    .padding(
                                                        start = 20.dp,
                                                        top = 16.dp,
                                                        end = 20.dp,
                                                        bottom = 16.dp,
                                                    ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text =
                                                    when (idx) {
                                                        1 ->
                                                            StringProvider.getString(
                                                                R.string.yes,
                                                            )
                                                        else ->
                                                            StringProvider.getString(
                                                                R.string.no,
                                                            )
                                                    },
                                                fontSize = 60.sp,
                                                color =
                                                    when (idx to surveyViewModel.surveyDiabetes.collectAsState().value) {
                                                        1 to SurveyDiabetes.Yes,
                                                        2 to SurveyDiabetes.No,
                                                        -> textColor

                                                        else -> Color(0xFF1D71E1)
                                                    },
                                                fontWeight = FontWeight.ExtraBold,
                                            )
                                        }
                                        when (idx < 2) {
                                            true ->
                                                Spacer(
                                                    modifier =
                                                        Modifier
                                                            .width(20.dp),
                                                )

                                            false -> {}
                                        }
                                    }
                                }
                            }
                        }
                        // /
                    }

                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(
                                    color = Color(0xffffffff),
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
                                        .background(color = Color(0xff1d71e1))
                                        .border(
                                            border =
                                                BorderStroke(
                                                    width = 4.dp,
                                                    color = Color(0xffffffff),
                                                ),
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = (questionType.ordinal + 1).toString(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 40.sp,
                                    color = Color(0xffffffff),
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }
                        }
                    }
                }
            }
        }
        // //////////
    }
}
