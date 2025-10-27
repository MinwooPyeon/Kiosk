package com.pixelro.nenoonkiosk.feature.survey

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harang.data.model.dto.request.SendSurveyDataRequest
import com.harang.data.repository.SignInRepository
import com.harang.data.repository.SurveyRepository
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveyAge
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveyDiabetes
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveyGlass
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveySex
import com.pixelro.nenoonkiosk.feature.survey.surveytype.SurveySurgery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class SurveyViewModel
    @Inject
    constructor(
        application: Application,
        private val surveyRepository: SurveyRepository,
        private val signInRepository: SignInRepository,
    ) : AndroidViewModel(application) {
        private val _surveyAge = MutableStateFlow(SurveyAge.None)
        val surveyAge: StateFlow<SurveyAge> = _surveyAge
        private val _surveySex = MutableStateFlow(SurveySex.None)
        val surveySex: StateFlow<SurveySex> = _surveySex
        private val _surveyGlass = MutableStateFlow(SurveyGlass.None)
        val surveyGlass: StateFlow<SurveyGlass> = _surveyGlass
        private val _surveySurgery = MutableStateFlow(SurveySurgery.None)
        val surveySurgery: StateFlow<SurveySurgery> = _surveySurgery
        private val _surveyDiabetes = MutableStateFlow(SurveyDiabetes.None)
        val surveyDiabetes: StateFlow<SurveyDiabetes> = _surveyDiabetes
        private val _questionType = MutableStateFlow(QuestionType.Age)
        val questionType: StateFlow<QuestionType> = _questionType
        private val _pid = MutableStateFlow(0)
        val pid: StateFlow<Int> = _pid
        private val _pastSurveyId = MutableStateFlow<Long?>(null)
        val pastSurveyId: StateFlow<Long?> = _pastSurveyId
        private val _isPastSurveyFetched = MutableStateFlow(false)
        val isPastSurveyFetched: StateFlow<Boolean> = _isPastSurveyFetched

        fun updateQuestionType(type: QuestionType) {
            viewModelScope.launch {
                delay(1000)
                _questionType.update { type }
            }
        }

        fun updateSurveyAge(type: SurveyAge) {
            _surveyAge.update { type }
        }

        fun updateSurveySex(type: SurveySex) {
            _surveySex.update { type }
        }

        fun updateSurveyGlass(type: SurveyGlass) {
            _surveyGlass.update { type }
        }

        fun updateSurveySurgery(type: SurveySurgery) {
            _surveySurgery.update { type }
        }

        fun updateSurveyDiabetes(type: SurveyDiabetes) {
            _surveyDiabetes.update { type }
        }

        fun initSurveyData() {
            _questionType.update { QuestionType.Age }
            _surveyAge.update { SurveyAge.None }
            _surveySex.update { SurveySex.None }
            _surveyGlass.update { SurveyGlass.None }
            _surveySurgery.update { SurveySurgery.None }
            _surveyDiabetes.update { SurveyDiabetes.None }
        }

        fun checkIsSurveyCompleted(token: String) {
            viewModelScope.launch(Dispatchers.IO) {
                surveyRepository.getPastSurveyId(token).also {
                    try {
                        if (it?.data?.get("hasSurvey") as Boolean) {
                            surveyRepository.generateResultsChart(token).also { generateResultsChartResponse ->
                                try {
                                    val tid = floor(generateResultsChartResponse?.data?.get("tid") as Double).toLong()
                                    _pastSurveyId.value = tid
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
                    _isPastSurveyFetched.value = true
                }
            }
        }

        fun setIsPastSurveyFetched(value: Boolean) {
            _isPastSurveyFetched.value = value
        }

        fun getSurveyId(
            token: String?,
            toCategoryListScreen: (Long) -> Unit,
            isSignInSkipped: () -> Boolean,
            onError: () -> Unit,
        ) {
            if (AppConstants.MANAGE_USERS_INTERNALLY) {
                toCategoryListScreen(DebugConstants.SAMPLE_SURVEY_ID)
            } else if (AppConstants.ALLOW_OFFLINE_BYPASS_FOR_SIGN_IN_SKIP && isSignInSkipped()) {
                _pastSurveyId.value = DebugConstants.SAMPLE_SURVEY_ID
                toCategoryListScreen(DebugConstants.SAMPLE_SURVEY_ID)
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    // 서버에 보낼 data 정보
                    val response =
                        surveyRepository.sendSurveyData(
                            token = token,
                            SendSurveyDataRequest(
                                age =
                                    when (_surveyAge.value) {
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
                                    when (_surveySex.value) {
                                        SurveySex.Man -> "M"
                                        else -> "W"
                                    },
                                glasses =
                                    when (_surveyGlass.value) {
                                        SurveyGlass.Yes -> true
                                        else -> false
                                    },
                                surgery =
                                    when (_surveySurgery.value) {
                                        SurveySurgery.Normal -> "normal"
                                        SurveySurgery.LASIK -> "correction"
                                        SurveySurgery.Cataract -> "cataract"
                                        else -> "etc"
                                    },
                                diabetes =
                                    when (_surveyDiabetes.value) {
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
                                                            _pastSurveyId.value = tid
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
                                    _pastSurveyId.value = tid
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

        enum class QuestionType {
            Age,
            Sex,
            Glass,
            Surgery,
            Diabetes,
        }

        init {
            initSurveyData()
        }
    }
