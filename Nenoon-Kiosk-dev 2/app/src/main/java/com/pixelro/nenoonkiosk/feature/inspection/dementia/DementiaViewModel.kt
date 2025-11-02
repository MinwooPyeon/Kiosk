package com.pixelro.nenoonkiosk.feature.inspection.dementia

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.navigation.InspectionRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class DementiaViewModel @Inject constructor(
    private val navigator: Navigator,
    application: Application
) : AndroidViewModel(application), ContainerHost<DementiaUiState, Nothing> {

    override val container = container<DementiaUiState, Nothing>(
        DementiaUiState()
    )

    fun init() = intent {
        reduce {
            DementiaUiState(
                currentIndex = 0,
                scores = List(14) { DementiaAnswer.None }
            )
        }
    }

    fun updateDementiaScore(idx: Int, selected: DementiaAnswer) = intent {
        reduce {
            val updatedScores = state.scores.toMutableList()
            updatedScores[idx] = selected
            state.copy(scores = updatedScores.toList())
        }
    }

    fun moveToNextQuestion() = intent {
        viewModelScope.launch {
            delay(500)
            if (state.currentIndex < 13) {
                reduce { state.copy(currentIndex = state.currentIndex + 1) }
            } else {
                navigateToResult()
            }
        }
    }

    private fun navigateToResult() = intent {
        val result = DementiaInspectionResult(state.scores)
        // TODO: 결과 데이터 전달 방법 구현 필요
        navigator.navigate(InspectionRoute.InspectionResult)
    }

    fun checkDementiaIsDone(): Boolean {
        val currentState = container.stateFlow.value
        for (i in 0..13) {
            if (currentState.scores[i] == DementiaAnswer.None) {
                Toast.makeText(
                    getApplication(),
                    StringProvider.getString(R.string.ans_question),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }

    fun getDementiaData(): DementiaInspectionResult {
        return DementiaInspectionResult(container.stateFlow.value.scores)
    }
}
