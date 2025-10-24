package com.pixelro.nenoonkiosk.feature.survey

import com.pixelro.nenoonkiosk.feature.survey.model.SurveyAge
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyDiabetes
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyGlass
import com.pixelro.nenoonkiosk.feature.survey.model.SurveySex
import com.pixelro.nenoonkiosk.feature.survey.model.SurveySurgery

data class SurveyState(
    val currentQuestion: QuestionType = QuestionType.Age,
    val age: SurveyAge = SurveyAge.None,
    val sex: SurveySex = SurveySex.None,
    val glass: SurveyGlass = SurveyGlass.None,
    val surgery: SurveySurgery = SurveySurgery.None,
    val diabetes: SurveyDiabetes = SurveyDiabetes.None,
    val screenState: SurveyScreenState = SurveyScreenState.InProgress,
    val pastSurveyId: Long? = null,
    val isPastSurveyFetched: Boolean = false,
)

sealed class SurveyQuestion {
    data class EightOptions(
        val questionText: String,
        val leftOptions: List<String>,
        val rightOptions: List<String>,
        val selectedIndex: Int,
    ) : SurveyQuestion()

    data class FourOptions(
        val questionText: String,
        val topOptions: List<String>,
        val bottomOptions: List<String>,
        val selectedIndex: Int,
    ) : SurveyQuestion()

    data class TwoOptions(
        val questionText: String,
        val option1Text: String,
        val option2Text: String,
        val selectedIndex: Int,
    ) : SurveyQuestion()
}

enum class QuestionType {
    Age,
    Sex,
    Glass,
    Surgery,
    Diabetes,
}

enum class SurveyScreenState {
    Loading,
    InProgress,
    Error,
}