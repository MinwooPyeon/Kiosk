package com.pixelro.nenoonkiosk.feature.survey

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.harang.data.model.dto.User
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel

/**
 * SurveyScreen(순수 UI)을 호출하고 ViewModel 로직을 처리
 */
@Composable
fun SurveyRoute(
    isLoggedIn: Boolean,
    toCategoryListScreen: (Long) -> Unit,
    surveyViewModel: SurveyViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel,
    userData: User?,
    onBack: () -> Unit,
    signOut: () -> Unit,
) {
    // ViewModel 상태 구독
    val state = surveyViewModel.state.collectAsState().value
    val question = surveyViewModel.currentQuestion.collectAsState().value

    var surveyScreenState by remember { mutableStateOf(SurveyScreenState.InProgress) }

    // 과거 설문 체크
    LaunchedEffect(state.isPastSurveyFetched, state.pastSurveyId) {
        if (state.isPastSurveyFetched) {
            if (state.pastSurveyId != null) {
                toCategoryListScreen(state.pastSurveyId!!)
            } else {
                surveyViewModel.initSurveyData()
                surveyScreenState = SurveyScreenState.InProgress
            }
        } else if (userData?.accessToken != null) {
            surveyViewModel.checkIsSurveyCompleted(userData.accessToken!!)
            surveyScreenState = SurveyScreenState.Loading
        } else if (state.pastSurveyId != null) {
            toCategoryListScreen(state.pastSurveyId!!)
        }
    }

    LaunchedEffect(state.currentQuestion) {
        if (surveyScreenState != SurveyScreenState.Loading) {
            surveyScreenState = SurveyScreenState.InProgress
        }
    }

    // Debug: Survey 건너뛰기
    LaunchedEffect(Unit) {
        if (DebugConstants.SKIP_SURVEY) {
            toCategoryListScreen(DebugConstants.SAMPLE_SURVEY_ID)
        }
    }

    // 순수 UI 컴포넌트 호출
    SurveyScreen(
        screenState = surveyScreenState,
        currentQuestion = state.currentQuestion,
        question = question,
        onBack = {
            when (state.currentQuestion) {
                QuestionType.Age -> onBack()
                QuestionType.Sex -> surveyViewModel.updateQuestionType(QuestionType.Age)
                QuestionType.Glass -> surveyViewModel.updateQuestionType(QuestionType.Sex)
                QuestionType.Surgery -> surveyViewModel.updateQuestionType(QuestionType.Glass)
                QuestionType.Diabetes -> surveyViewModel.updateQuestionType(QuestionType.Surgery)
            }
        },
        onAnswerSelected = { index ->
            surveyViewModel.handleSelection(index) {
                surveyViewModel.getSurveyId(
                    token = userData?.accessToken,
                    toCategoryListScreen = toCategoryListScreen,
                    isSignInSkipped = { loginViewModel.isUserSignInSkipped() },
                ) {
                    surveyScreenState = SurveyScreenState.Error
                }
            }
        },
        onSignOut = signOut,
    )
}