package com.pixelro.nenoonkiosk.feature.inspection.dementia

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun DementiaInspectionRoute(
    viewModel: DementiaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val savedLanguage = prefs.getString("language", "defaultLanguage")
    val questionTextSize = if (savedLanguage == "ru") 35.sp else 50.sp

    val state = viewModel.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    DementiaInspectionScreen(
        currentIndex = state.currentIndex,
        totalQuestions = QUESTIONS.size,
        questionResId = QUESTIONS[state.currentIndex],
        selectedAnswer = state.scores.getOrNull(state.currentIndex),
        questionTextSize = questionTextSize,
        onAnswer = { answer ->
            viewModel.updateDementiaScore(state.currentIndex, answer)
            viewModel.moveToNextQuestion()
        }
    )
}
