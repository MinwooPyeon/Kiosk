package com.pixelro.nenoonkiosk.survey.datatype

data class SurveyData(
    val surveyAge: SurveyAge = SurveyAge.None,
    val surveySex: SurveySex = SurveySex.None,
    val surveyGlass: SurveyGlass = SurveyGlass.None,
    val surveySurgery: SurveySurgery = SurveySurgery.None,
    val surveyDiabetes: SurveyDiabetes = SurveyDiabetes.None
)
