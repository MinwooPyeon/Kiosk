package com.pixelro.nenoonkiosk.feature.inspection.dementia.process

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    val prefs = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = prefs.getString("language", "defaultLanguage")
    val questionTextSize = if (savedLanguage == "ru") 35.sp else 50.sp

    // 진행 상태 (UI 로컬 상태)
    var currentIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    // 점수 상태 (ViewModel Flow → 단순 스냅샷)
    val scores = viewModel.dementiaScores.collectAsState()

    // 초기화
    LaunchedEffect(Unit) {
        viewModel.init()
    }

    DementiaInspectionContent(
        currentIndex = currentIndex,
        totalQuestions = QUESTIONS.size,
        questionResId = QUESTIONS[currentIndex],
        selectedAnswer = scores.value.getOrNull(currentIndex),
        questionTextSize = questionTextSize,
        onAnswer = { answer ->
            scope.launch {
                viewModel.updateDementiaScore(currentIndex, answer)
                delay(500)
                if (currentIndex < QUESTIONS.lastIndex) {
                    currentIndex++
                } else {
                    toResultScreen(viewModel.getDementiaData())
                }
            }
        }
    )
}