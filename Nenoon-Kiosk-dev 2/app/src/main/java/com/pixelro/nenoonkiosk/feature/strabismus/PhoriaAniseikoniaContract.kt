package com.pixelro.nenoonkiosk.feature.strabismus

import com.harang.data.db.entity.AdImageEntity
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyGlass

data class PhoriaAniseikoniaUiState(
    val savedLanguage: String,
    val isSenior: Boolean,
    val isPhoriaDone: Boolean,
    val isAniseikoniaDone: Boolean,
    val surveyGlass: SurveyGlass,
    val showSurveyDialog: Boolean,
    val showFilterDialog: Boolean,
    val adImages: List<AdImageEntity> = emptyList()
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