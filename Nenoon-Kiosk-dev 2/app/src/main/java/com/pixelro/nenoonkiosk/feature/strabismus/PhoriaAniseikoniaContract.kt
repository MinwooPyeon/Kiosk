package com.pixelro.nenoonkiosk.feature.strabismus

import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyGlass

data class PhoriaAniseikoniaUiState(
    val savedLanguage: String,
    val isSenior: Boolean,
    val isPhoriaDone: Boolean,
    val isAniseikoniaDone: Boolean,
    val surveyGlass: SurveyGlass,
    val showSurveyDialog: Boolean,
    val showFilterDialog: Boolean
)

sealed interface PhoriaAniseikoniaEvent {
    data object BackToIntro : PhoriaAniseikoniaEvent
    data object OpenSettings : PhoriaAniseikoniaEvent
    data class StartTest(val type: InspectionType) : PhoriaAniseikoniaEvent
    data object DismissSurveyDialog : PhoriaAniseikoniaEvent
    data object ShowSurveyDialog : PhoriaAniseikoniaEvent
    data object DismissFilterDialog : PhoriaAniseikoniaEvent
    data object ConfirmFilterDialog : PhoriaAniseikoniaEvent
}