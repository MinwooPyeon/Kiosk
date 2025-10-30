package com.pixelro.nenoonkiosk.feature.survey

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harang.data.model.dto.request.SendSurveyDataRequest
import com.harang.data.repository.SurveyRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.TestRoute
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyAge
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyDiabetes
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyGlass
import com.pixelro.nenoonkiosk.feature.survey.model.SurveySex
import com.pixelro.nenoonkiosk.feature.survey.model.SurveySurgery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class SurveyViewModel @Inject constructor(
    application: Application,
    private val surveyRepository: SurveyRepository,
    private val navigator: Navigator,
) : AndroidViewModel(application),
    ContainerHost<SurveyUiState, SurveySideEffect> {

    override val container: Container<SurveyUiState, SurveySideEffect> =
        container(SurveyUiState())

    val state: StateFlow<SurveyUiState> = container.stateFlow

    val currentQuestion: StateFlow<SurveyQuestion> =
        container.stateFlow.map { state ->
            when (state.currentQuestion) {
                QuestionType.Age -> {
                    val selectedIndex = when (state.age) {
                        SurveyAge.First -> 1
                        SurveyAge.Third -> 2
                        SurveyAge.Fifth -> 3
                        SurveyAge.Seventh -> 4
                        SurveyAge.Second -> 5
                        SurveyAge.Fourth -> 6
                        SurveyAge.Sixth -> 7
                        SurveyAge.Eighth -> 8
                        else -> 0
                    }
                    SurveyQuestion.EightOptions(
                        questionText = StringProvider.getString(R.string.survey_age),
                        leftOptions = listOf(
                            StringProvider.getString(R.string.survey_under9),
                            StringProvider.getString(R.string.survey_20s),
                            StringProvider.getString(R.string.survey_40s),
                            StringProvider.getString(R.string.survey_60s),
                        ),
                        rightOptions = listOf(
                            StringProvider.getString(R.string.survey_10s),
                            StringProvider.getString(R.string.survey_30s),
                            StringProvider.getString(R.string.survey_50s),
                            StringProvider.getString(R.string.survey_above70),
                        ),
                        selectedIndex = selectedIndex,
                    )
                }

                QuestionType.Sex -> {
                    val selectedIndex = when (state.sex) {
                        SurveySex.Man -> 1
                        SurveySex.Woman -> 2
                        else -> 0
                    }
                    SurveyQuestion.TwoOptions(
                        questionText = StringProvider.getString(R.string.survey_sex),
                        option1Text = StringProvider.getString(R.string.survey_male),
                        option2Text = StringProvider.getString(R.string.survey_female),
                        selectedIndex = selectedIndex,
                    )
                }

                QuestionType.Glass -> {
                    val selectedIndex = when (state.glass) {
                        SurveyGlass.Yes -> 1
                        SurveyGlass.No -> 2
                        else -> 0
                    }
                    SurveyQuestion.TwoOptions(
                        questionText = StringProvider.getString(R.string.survey_glasses),
                        option1Text = StringProvider.getString(R.string.yes),
                        option2Text = StringProvider.getString(R.string.no),
                        selectedIndex = selectedIndex,
                    )
                }

                QuestionType.Surgery -> {
                    val selectedIndex = when (state.surgery) {
                        SurveySurgery.Normal -> 1
                        SurveySurgery.LASIK -> 2
                        SurveySurgery.Cataract -> 3
                        SurveySurgery.Etc -> 4
                        else -> 0
                    }
                    SurveyQuestion.FourOptions(
                        questionText = StringProvider.getString(R.string.survey_surgery),
                        topOptions = listOf(
                            StringProvider.getString(R.string.survey_none),
                            StringProvider.getString(R.string.survey_lasik_lasek),
                        ),
                        bottomOptions = listOf(
                            StringProvider.getString(R.string.survey_cataract),
                            StringProvider.getString(R.string.survey_etc),
                        ),
                        selectedIndex = selectedIndex,
                    )
                }

                QuestionType.Diabetes -> {
                    val selectedIndex = when (state.diabetes) {
                        SurveyDiabetes.Yes -> 1
                        SurveyDiabetes.No -> 2
                        else -> 0
                    }
                    SurveyQuestion.TwoOptions(
                        questionText = StringProvider.getString(R.string.survey_diabetes),
                        option1Text = StringProvider.getString(R.string.yes),
                        option2Text = StringProvider.getString(R.string.no),
                        selectedIndex = selectedIndex,
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SurveyQuestion.EightOptions(
                questionText = "",
                leftOptions = emptyList(),
                rightOptions = emptyList(),
                selectedIndex = 0,
            ),
        )

    init {
        initSurveyData()
    }

    fun initSurveyData() = intent {
        reduce {
            SurveyUiState()
        }
    }

    fun checkIsSurveyCompleted(token: String) = intent {
        surveyRepository.getPastSurveyId(token).also {
            try {
                if (it?.data?.get("hasSurvey") as Boolean) {
                    surveyRepository.generateResultsChart(token)
                        .also { generateResultsChartResponse ->
                            try {
                                val tid = floor(
                                    generateResultsChartResponse?.data?.get("tid") as Double
                                ).toLong()
                                reduce {
                                    state.copy(pastSurveyId = tid)
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "SurveyViewModel",
                                    "Error reading generateResultsChart ID - ${generateResultsChartResponse?.data}",
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                Log.e(
                    "SurveyViewModel",
                    "Error reading past survey ID - ${it?.data?.get("surveyId")}"
                )
            }
            reduce {
                state.copy(isPastSurveyFetched = true)
            }
        }
    }

    fun navigateToPreviousQuestion() = intent {
        when (state.currentQuestion) {
            QuestionType.Sex -> updateQuestionType(QuestionType.Age)
            QuestionType.Glass -> updateQuestionType(QuestionType.Sex)
            QuestionType.Surgery -> updateQuestionType(QuestionType.Glass)
            QuestionType.Diabetes -> updateQuestionType(QuestionType.Surgery)
            else -> navigator.navigateBack()
        }
    }

    fun navigateBack() = intent {
        navigator.navigateBack()
    }

    fun navigateToCategoryList(pid: Long) = intent {
//        navigator.navigate(TestRoute.CategoryList(pid))
    }

    private fun updateQuestionType(type: QuestionType) = intent {
        delay(1000)
        reduce {
            state.copy(currentQuestion = type)
        }
    }

    fun handleSelection(
        index: Int,
        token: String?,
    ) {
        when (state.value.currentQuestion) {
            QuestionType.Age -> {
                val age = when (index) {
                    1 -> SurveyAge.First
                    2 -> SurveyAge.Third
                    3 -> SurveyAge.Fifth
                    4 -> SurveyAge.Seventh
                    5 -> SurveyAge.Second
                    6 -> SurveyAge.Fourth
                    7 -> SurveyAge.Sixth
                    8 -> SurveyAge.Eighth
                    else -> SurveyAge.None
                }
                updateSurveyAge(age)
                updateQuestionType(QuestionType.Sex)
            }

            QuestionType.Sex -> {
                val sex = when (index) {
                    1 -> SurveySex.Man
                    2 -> SurveySex.Woman
                    else -> SurveySex.None
                }
                updateSurveySex(sex)
                updateQuestionType(QuestionType.Glass)
            }

            QuestionType.Glass -> {
                val glass = when (index) {
                    1 -> SurveyGlass.Yes
                    2 -> SurveyGlass.No
                    else -> SurveyGlass.None
                }
                updateSurveyGlass(glass)
                updateQuestionType(QuestionType.Surgery)
            }

            QuestionType.Surgery -> {
                val surgery = when (index) {
                    1 -> SurveySurgery.Normal
                    2 -> SurveySurgery.LASIK
                    3 -> SurveySurgery.Cataract
                    4 -> SurveySurgery.Etc
                    else -> SurveySurgery.None
                }
                updateSurveySurgery(surgery)
                updateQuestionType(QuestionType.Diabetes)
            }

            QuestionType.Diabetes -> {
                val diabetes = when (index) {
                    1 -> SurveyDiabetes.Yes
                    2 -> SurveyDiabetes.No
                    else -> SurveyDiabetes.None
                }
                updateSurveyDiabetes(diabetes)
                getSurveyId(token)
            }
        }
    }

    private fun updateSurveyAge(type: SurveyAge) = intent {
        reduce { state.copy(age = type) }
    }

    private fun updateSurveySex(type: SurveySex) = intent {
        reduce { state.copy(sex = type) }
    }

    private fun updateSurveyGlass(type: SurveyGlass) = intent {
        reduce { state.copy(glass = type) }
    }

    private fun updateSurveySurgery(type: SurveySurgery) = intent {
        reduce { state.copy(surgery = type) }
    }

    private fun updateSurveyDiabetes(type: SurveyDiabetes) = intent {
        reduce { state.copy(diabetes = type) }
    }

    private fun getSurveyId(token: String?) = intent {
        if (AppConstants.MANAGE_USERS_INTERNALLY) {
            navigateToCategoryList(DebugConstants.SAMPLE_SURVEY_ID)
        } else if (AppConstants.ALLOW_OFFLINE_BYPASS_FOR_SIGN_IN_SKIP && token == null) {
            navigateToCategoryList(DebugConstants.SAMPLE_SURVEY_ID)
        } else {
            val currentState = state
            val response = surveyRepository.sendSurveyData(
                token = token,
                SendSurveyDataRequest(
                    age = when (currentState.age) {
                        SurveyAge.First -> 1
                        SurveyAge.Second -> 2
                        SurveyAge.Third -> 4
                        SurveyAge.Fourth -> 5
                        SurveyAge.Fifth -> 6
                        SurveyAge.Sixth -> 7
                        SurveyAge.Seventh -> 8
                        else -> 9
                    },
                    gender = when (currentState.sex) {
                        SurveySex.Man -> "M"
                        else -> "W"
                    },
                    glasses = when (currentState.glass) {
                        SurveyGlass.Yes -> true
                        else -> false
                    },
                    surgery = when (currentState.surgery) {
                        SurveySurgery.Normal -> "normal"
                        SurveySurgery.LASIK -> "correction"
                        SurveySurgery.Cataract -> "cataract"
                        else -> "etc"
                    },
                    diabetes = when (currentState.diabetes) {
                        SurveyDiabetes.Yes -> true
                        else -> false
                    },
                    pid = surveyRepository.getLocationId(),
                ),
            )

            if (response != null) {
                if (token != null) {
                    surveyRepository.getPastSurveyId(token)
                        .also { getPastSurveyIdResponse ->
                            try {
                                if (getPastSurveyIdResponse?.data?.get("hasSurvey") as Boolean) {
                                    surveyRepository.generateResultsChart(token)
                                        .also { generateResultsChartResponse ->
                                            try {
                                                val tid = floor(
                                                    generateResultsChartResponse?.data?.get("tid") as Double
                                                ).toLong()
                                                reduce { state.copy(pastSurveyId = tid) }
                                                navigateToCategoryList(tid)
                                            } catch (_: Exception) {
                                                Log.e(
                                                    "SurveyViewModel",
                                                    "Error reading chart ID"
                                                )
                                                postSideEffect(SurveySideEffect.ShowError)
                                            }
                                        }
                                }
                            } catch (_: Exception) {
                                Log.e("SurveyViewModel", "Token error")
                                postSideEffect(SurveySideEffect.ShowError)
                            }
                        }
                } else {
                    try {
                        val tid = floor(response.data["tid"] as Double).toLong()
                        reduce { state.copy(pastSurveyId = tid) }
                        navigateToCategoryList(tid)
                    } catch (_: Exception) {
                        Log.e("SurveyViewModel", "Error reading survey ID")
                        postSideEffect(SurveySideEffect.ShowError)
                    }
                }
            } else {
                postSideEffect(SurveySideEffect.ShowError)
            }
        }
    }
}
