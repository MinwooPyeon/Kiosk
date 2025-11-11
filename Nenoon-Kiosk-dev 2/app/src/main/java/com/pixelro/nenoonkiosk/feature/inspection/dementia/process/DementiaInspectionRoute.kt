package com.pixelro.nenoonkiosk.feature.inspection.dementia.process

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DementiaInspectionRoute(
    toResultScreen: (DementiaInspectionResult) -> Unit,
    viewModel: DementiaViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(
            NavConstants.PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
    }

    val savedLanguage = prefs.getString("language", "defaultLanguage")
    val questionTextSize = if (savedLanguage == "ru") 35.sp else 50.sp

    var currentIndex by remember { mutableIntStateOf(0) }
    var currentAnswer by remember { mutableStateOf<DementiaViewModel.DementiaAnswer?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    DementiaInspectionContent(
        currentIndex = currentIndex,
        totalQuestions = QUESTIONS.size,
        questionResId = QUESTIONS[currentIndex],
        selectedAnswer = currentAnswer,
        questionTextSize = questionTextSize,
        onAnswer = { answer ->
            scope.launch {
                viewModel.updateDementiaScore(currentIndex, answer)
                currentAnswer = answer
                delay(300)
                if (currentIndex < QUESTIONS.lastIndex) {
                    currentIndex++
                    currentAnswer = null
                } else {
                    toResultScreen(viewModel.getDementiaData())
                }
            }
        }
    )
}
