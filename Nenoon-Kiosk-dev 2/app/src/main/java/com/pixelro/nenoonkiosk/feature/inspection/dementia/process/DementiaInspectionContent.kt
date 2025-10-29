package com.pixelro.nenoonkiosk.feature.inspection.dementia.process

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaViewModel
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.AnswerRow
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.BottomCounter
import com.pixelro.nenoonkiosk.feature.inspection.dementia.components.QuestionBox


@Composable
fun DementiaInspectionContent(
    currentIndex: Int,
    totalQuestions: Int,
    @StringRes questionResId: Int,
    selectedAnswer: DementiaViewModel.DementiaAnswer?,
    questionTextSize: TextUnit,
    onAnswer: (DementiaViewModel.DementiaAnswer) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(40.dp),
        verticalArrangement = Arrangement.Center
    ) {
        QuestionBox(
            currentIndex = currentIndex,
            totalQuestions = totalQuestions,
            questionText = StringProvider.getStringComposable(questionResId),
            questionTextSize = questionTextSize
        )
        Spacer(Modifier.height(52.dp))
        AnswerRow(
            selected = selectedAnswer,
            onClickYes = { onAnswer(DementiaViewModel.DementiaAnswer.Yes) },
            onClickNo = { onAnswer(DementiaViewModel.DementiaAnswer.No) }
        )
        Spacer(Modifier.height(24.dp))
        BottomCounter(
            index1Based = currentIndex + 1
        )
    }
}


@StringRes
val QUESTIONS = listOf(
    R.string.dementia_survey_question0,
    R.string.dementia_survey_question1,
    R.string.dementia_survey_question2,
    R.string.dementia_survey_question3,
    R.string.dementia_survey_question4,
    R.string.dementia_survey_question5,
    R.string.dementia_survey_question6,
    R.string.dementia_survey_question7,
    R.string.dementia_survey_question8,
    R.string.dementia_survey_question9,
    R.string.dementia_survey_question10,
    R.string.dementia_survey_question11,
    R.string.dementia_survey_question12,
    R.string.dementia_survey_question13
)

@Preview(showBackground = true, widthDp = 900, heightDp = 1400, name = "Dementia - Start", apiLevel = 34)
@Composable
private fun Preview_DementiaTestScreen_Start() {
    DementiaInspectionContent(
        currentIndex = 0,
        totalQuestions = 14,
        questionResId = R.string.dementia_survey_question0,
        selectedAnswer = null,
        questionTextSize = 50.sp,
        onAnswer = {}
    )
}

@Preview(showBackground = true, widthDp = 900, heightDp = 1400, name = "Dementia - Mid Selected", apiLevel = 34)
@Composable
private fun Preview_DementiaTestScreen_Mid() {
    DementiaInspectionContent(
        currentIndex = 6,
        totalQuestions = 14,
        questionResId = R.string.dementia_survey_question6,
        selectedAnswer = DementiaViewModel.DementiaAnswer.Yes,
        questionTextSize = 50.sp,
        onAnswer = {}
    )
}
