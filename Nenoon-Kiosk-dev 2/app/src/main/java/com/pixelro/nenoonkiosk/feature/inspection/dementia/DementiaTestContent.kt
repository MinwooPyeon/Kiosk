package com.pixelro.nenoonkiosk.feature.inspection.dementia

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DementiaTestContent(
//    toBackScreen: () -> Unit,
    toResultScreen: (DementiaTestResult) -> Unit,
    dementiaViewModel: DementiaViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val resultTextSize = if (savedLanguage == "ru") 35.sp else 50.sp
    val coroutineScope = rememberCoroutineScope()
//    BackHandler(enabled = true) {
//        Log.e("backhandler", "backhandler")
//        toBackScreen()
//    }

    val currentQuestion = remember { mutableStateOf(0) }
    LaunchedEffect(true) {
        dementiaViewModel.init()
    }

    @Composable
    fun questions(
        dementiaViewModel: DementiaViewModel = hiltViewModel(),
        questionIndex: Int,
        answer: DementiaViewModel.DementiaAnswer,
    ) {
        Box(
            modifier =
                Modifier
                    .padding(start = if (answer == DementiaViewModel.DementiaAnswer.Yes) 0.dp else 20.dp)
                    .clip(
                        shape = RoundedCornerShape(8.dp),
                    )
                    .width(355.dp)
                    .fillMaxHeight(0.8f)
                    .border(
                        border =
                            BorderStroke(
                                width = 4.dp,
                                color = Color(0xff1d71e1),
                            ),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .background(
                        color =
                            when (dementiaViewModel.dementiaScores.collectAsState().value[questionIndex]) {
                                answer -> Color(0xff1d71e1)
                                else -> Color(0xffffffff)
                            },
                    )
                    .clickable {
                        coroutineScope.launch {
                            dementiaViewModel.updateDementiaScore(
                                questionIndex,
                                answer,
                            )
                            delay(500)
                            if (currentQuestion.value < 13) {
                                currentQuestion.value++
                            } else {
                                toResultScreen(dementiaViewModel.getDementiaData())
                            }
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text =
                    StringProvider.getString(
                        if (answer == DementiaViewModel.DementiaAnswer.Yes) R.string.yes else R.string.no,
                    ),
                fontSize = 60.sp,
                color =
                    when (dementiaViewModel.dementiaScores.collectAsState().value[questionIndex]) {
                        answer -> Color(0xffffffff)
                        else -> Color(0xff1d71e1)
                    },
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }

    @Composable
    fun questionBox(
        dementiaViewModel: DementiaViewModel,
        i: Int,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally, // 가로 방향으로 가운데 정렬
        ) {
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
                                .fillMaxWidth((currentQuestion.value + 1) / 14f)
                                .background(Color(0xFF1D71E1), RoundedCornerShape(8.dp)),
                    )
                }
            }
            Text(
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .height(250.dp),
                text =
                    when (i) {
                        0 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question0,
                            )
                        1 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question1,
                            )
                        2 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question2,
                            )
                        3 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question3,
                            )
                        4 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question4,
                            )
                        5 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question5,
                            )
                        6 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question6,
                            )
                        7 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question7,
                            )
                        8 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question8,
                            )
                        9 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question9,
                            )
                        10 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question10,
                            )
                        11 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question11,
                            )
                        12 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question12,
                            )
                        13 ->
                            StringProvider.getString(
                                R.string.dementia_survey_question13,
                            )

                        else -> ""
                    },
                fontSize = resultTextSize,
                fontWeight = FontWeight.Medium,
                // contentAlignment = Alignment.Center
            )
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
        ) {
            Spacer(Modifier.weight(1f))
            questions(
                dementiaViewModel,
                questionIndex = i,
                answer = DementiaViewModel.DementiaAnswer.Yes,
            )
            questions(
                dementiaViewModel,
                questionIndex = i,
                answer = DementiaViewModel.DementiaAnswer.No,
            )
        }
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
        questionBox(dementiaViewModel, i = currentQuestion.value)

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
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
                    text = (currentQuestion.value + 1).toString(),
                    textAlign = TextAlign.Center,
                    fontSize = 40.sp,
                    color = Color(0xffffffff),
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}
