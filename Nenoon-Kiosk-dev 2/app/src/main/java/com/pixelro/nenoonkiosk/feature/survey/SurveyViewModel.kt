package com.pixelro.nenoonkiosk.feature.survey

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harang.data.model.SendSurveyDataRequest
import com.harang.data.repository.SignInRepository
import com.harang.data.repository.SurveyRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyAge
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyDiabetes
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyGlass
import com.pixelro.nenoonkiosk.feature.survey.model.SurveySex
import com.pixelro.nenoonkiosk.feature.survey.model.SurveySurgery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class SurveyViewModel
    @Inject
    constructor(
        application: Application,
        private val surveyRepository: SurveyRepository,
        private val signInRepository: SignInRepository,
    ) : AndroidViewModel(application),
    ContainerHost<SurveyUiState, SurveySideEffect> {
        override val container: Container<SurveyUiState, SurveySideEffect> =
            container(SurveyUiState())

        val state: StateFlow<SurveyUiState> = container.stateFlow

        val currentQuestion: StateFlow<SurveyQuestion> =
            container.stateFlow.map { state ->
                when (state.currentQuestion) {
                    QuestionType.Age -> {
                        val selectedIndex =
                            when (state.age) {
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
                            leftOptions =
                                listOf(
                                    StringProvider.getString(R.string.survey_under9),
                                    StringProvider.getString(R.string.survey_20s),
                                    StringProvider.getString(R.string.survey_40s),
                                    StringProvider.getString(R.string.survey_60s),
                                ),
                            rightOptions =
                                listOf(
                                    StringProvider.getString(R.string.survey_10s),
                                    StringProvider.getString(R.string.survey_30s),
                                    StringProvider.getString(R.string.survey_50s),
                                    StringProvider.getString(R.string.survey_above70),
                                ),
                            selectedIndex = selectedIndex,
                        )
                    }
                    QuestionType.Sex -> {
                        val selectedIndex =
                            when (state.sex) {
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
                        val selectedIndex =
                            when (state.glass) {
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
                        val selectedIndex =
                            when (state.surgery) {
                                SurveySurgery.Normal -> 1
                                SurveySurgery.LASIK -> 2
                                SurveySurgery.Cataract -> 3
                                SurveySurgery.Etc -> 4
                                else -> 0
                            }
                        SurveyQuestion.FourOptions(
                            questionText = StringProvider.getString(R.string.survey_surgery),
                            topOptions =
                                listOf(
                                    StringProvider.getString(R.string.survey_none),
                                    StringProvider.getString(R.string.survey_lasik_lasek),
                                ),
                            bottomOptions =
                                listOf(
                                    StringProvider.getString(R.string.survey_cataract),
                                    StringProvider.getString(R.string.survey_etc),
                                ),
                            selectedIndex = selectedIndex,
                        )
                    }
                    QuestionType.Diabetes -> {
                        val selectedIndex =
                            when (state.diabetes) {
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
                initialValue =
                    SurveyQuestion.EightOptions(
                        questionText = "",
                        leftOptions = emptyList(),
                        rightOptions = emptyList(),
                        selectedIndex = 0,
                    ),
            )

        fun updateQuestionType(type: QuestionType) =
            intent {
                delay(1000)
                reduce {
                    state.copy(currentQuestion = type)
                }
            }

        fun updateSurveyAge(type: SurveyAge) =
            intent {
                reduce {
                    state.copy(age = type)
                }
            }

        fun updateSurveySex(type: SurveySex) =
            intent {
                reduce {
                    state.copy(sex = type)
                }
            }

        fun updateSurveyGlass(type: SurveyGlass) =
            intent {
                reduce {
                    state.copy(glass = type)
                }
            }

        fun updateSurveySurgery(type: SurveySurgery) =
            intent {
                reduce {
                    state.copy(surgery = type)
                }
            }

        fun updateSurveyDiabetes(type: SurveyDiabetes) =
            intent {
                reduce {
                    state.copy(diabetes = type)
                }
            }

        fun initSurveyData() =
            intent {
                reduce {
                    SurveyUiState()
                }
            }

        fun handleSelection(
            index: Int,
            onComplete: () -> Unit = {},
        ) {
            when (state.value.currentQuestion) {
                QuestionType.Age -> {
                    val age =
                        when (index) {
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
                    val sex =
                        when (index) {
                            1 -> SurveySex.Man
                            2 -> SurveySex.Woman
                            else -> SurveySex.None
                        }
                    updateSurveySex(sex)
                    updateQuestionType(QuestionType.Glass)
                }

                QuestionType.Glass -> {
                    val glass =
                        when (index) {
                            1 -> SurveyGlass.Yes
                            2 -> SurveyGlass.No
                            else -> SurveyGlass.None
                        }
                    updateSurveyGlass(glass)
                    updateQuestionType(QuestionType.Surgery)
                }

                QuestionType.Surgery -> {
                    val surgery =
                        when (index) {
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
                    val diabetes =
                        when (index) {
                            1 -> SurveyDiabetes.Yes
                            2 -> SurveyDiabetes.No
                            else -> SurveyDiabetes.None
                        }
                    updateSurveyDiabetes(diabetes)
                    onComplete()
                }
            }
        }

        fun checkIsSurveyCompleted(token: String) =
            intent {
                withContext(Dispatchers.IO) {
                    surveyRepository.getPastSurveyId(token).also {
                        try {
                            if (it?.data?.get("hasSurvey") as Boolean) {
                                surveyRepository.generateResultsChart(token).also { generateResultsChartResponse ->
                                    try {
                                        val tid = floor(generateResultsChartResponse?.data?.get("tid") as Double).toLong()
                                        reduce {
                                            state.copy(pastSurveyId = tid)
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "SurveyViewModel",
                                            "Error when reading generateResultsChart survey ID as Long - ${generateResultsChartResponse?.data}",
                                        )
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SurveyViewModel", "Error when reading past survey ID as Long - ${it?.data?.get("surveyId")}")
                        }
                        reduce {
                            state.copy(isPastSurveyFetched = true)
                        }
                    }
                }
            }

        fun setIsPastSurveyFetched(value: Boolean) =
            intent {
                reduce {
                    state.copy(isPastSurveyFetched = value)
                }
            }

        fun getSurveyId(
            token: String?,
            toCategoryListScreen: (Long) -> Unit,
            isSignInSkipped: () -> Boolean,
            onError: () -> Unit,
        ) =
            intent {
                if (AppConstants.MANAGE_USERS_INTERNALLY) {
                    toCategoryListScreen(DebugConstants.SAMPLE_SURVEY_ID)
                } else if (AppConstants.ALLOW_OFFLINE_BYPASS_FOR_SIGN_IN_SKIP && isSignInSkipped()) {
                    reduce {
                        state.copy(pastSurveyId = DebugConstants.SAMPLE_SURVEY_ID)
                    }
                    toCategoryListScreen(DebugConstants.SAMPLE_SURVEY_ID)
                } else {
                    withContext(Dispatchers.IO) {
                        // 서버에 보낼 data 정보
                        val currentState = state
                        val response =
                            surveyRepository.sendSurveyData(
                                token = token,
                                SendSurveyDataRequest(
                                    age =
                                        when (currentState.age) {
                                            SurveyAge.First -> 1
                                            SurveyAge.Second -> 2
                                            SurveyAge.Third -> 4
                                            SurveyAge.Fourth -> 5
                                            SurveyAge.Fifth -> 6
                                            SurveyAge.Sixth -> 7
                                            SurveyAge.Seventh -> 8
                                            else -> 9
                                        },
                                    gender =
                                        when (currentState.sex) {
                                            SurveySex.Man -> "M"
                                            else -> "W"
                                        },
                                    glasses =
                                        when (currentState.glass) {
                                            SurveyGlass.Yes -> true
                                            else -> false
                                        },
                                    surgery =
                                        when (currentState.surgery) {
                                            SurveySurgery.Normal -> "normal"
                                            SurveySurgery.LASIK -> "correction"
                                            SurveySurgery.Cataract -> "cataract"
                                            else -> "etc"
                                        },
                                    diabetes =
                                        when (currentState.diabetes) {
                                            SurveyDiabetes.Yes -> true
                                            else -> false
                                        },
                                    pid = surveyRepository.getLocationId(),
                                ),
                            )
                        // 로그인 tid 가져오기
                        if (response != null) {
                            withContext(Dispatchers.IO) {
                                if (token != null) {
                                    surveyRepository.getPastSurveyId(token)
                                        .also { getPastSurveyIdResponse ->
                                            try {
                                                if (getPastSurveyIdResponse?.data?.get("hasSurvey") as Boolean) {
                                                    surveyRepository.generateResultsChart(token)
                                                        .also { generateResultsChartResponse ->
                                                            try {
                                                                val tid =
                                                                    floor(
                                                                        generateResultsChartResponse?.data?.get(
                                                                            "tid",
                                                                        ) as Double,
                                                                    ).toLong()
                                                                reduce {
                                                                    state.copy(pastSurveyId = tid)
                                                                }
                                                                withContext(Dispatchers.Main) {
                                                                    toCategoryListScreen(tid)
                                                                }
                                                            } catch (e: Exception) {
                                                                Log.e(
                                                                    "SurveyViewModel",
                                                                    "Error when reading generateResultsChart survey ID as Long - ${generateResultsChartResponse?.data}",
                                                                )
                                                                onError()
                                                            }
                                                        }
                                                }
                                            } catch (e: Exception) {
                                                Log.e(
                                                    "SurveyViewModel",
                                                    "Token does not exist - ${getPastSurveyIdResponse?.data}",
                                                )
                                                onError()
                                            }
                                        }
                                } else {
                                    try {
                                        val tid = floor(response?.data?.get("tid") as Double).toLong()
                                        reduce {
                                            state.copy(pastSurveyId = tid)
                                        }
                                        withContext(Dispatchers.Main) {
                                            toCategoryListScreen(tid)
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "SurveyViewModel",
                                            "Error when reading sendSurveyData survey ID as Long - ${response?.data}",
                                        )
                                        onError()
                                    }
                                }
                            }
                        } else {
                            onError()
                        }
                    }
                }
            }

        init {
            initSurveyData()
        }
    }
