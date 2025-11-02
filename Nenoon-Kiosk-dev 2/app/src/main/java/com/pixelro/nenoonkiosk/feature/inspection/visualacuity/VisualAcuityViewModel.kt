package com.pixelro.nenoonkiosk.feature.inspection.visualacuity

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.navigation.InspectionRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.VisualAcuityInspectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class VisualAcuityViewModel @Inject constructor(
    private val navigator: Navigator,
    application: Application
) : AndroidViewModel(application), ContainerHost<VisualAcuityUiState, Nothing> {

    override val container = container<VisualAcuityUiState, Nothing>(
        VisualAcuityUiState()
    )

    private var leftEyeSightValue = 1
    private var rightEyeSightValue = 1

    fun init() = intent {
        leftEyeSightValue = 1
        rightEyeSightValue = 1
        reduce {
            VisualAcuityUiState(
                isMeasuringDistanceVisible = true,
                isVisualAcuityVisible = false,
                isLeftEye = true,
                sightLevel = 1,
                wrongCount = 0f
            )
        }
        updateRandomList()
    }

    fun updateIsMeasuringDistanceVisible(visible: Boolean) = intent {
        reduce { state.copy(isMeasuringDistanceVisible = visible) }
    }

    fun updateIsVisualAcuityVisible(visible: Boolean) = intent {
        reduce { state.copy(isVisualAcuityVisible = visible) }
    }

    private fun updateRandomList() = intent {
        val newRandomList = mutableListOf<Int>()
        var ranNum = (2..7).random()

        for (i in 1..3) {
            while (ranNum in newRandomList) {
                ranNum = (2..7).random()
            }
            newRandomList.add(ranNum)
        }

        val prevNum = state.ansNum
        var newNum = newRandomList[(0..2).random()]
        while (prevNum == newNum) {
            newNum = newRandomList[(0..2).random()]
        }

        reduce {
            state.copy(
                randomList = newRandomList,
                ansNum = newNum
            )
        }
    }

    fun processAnswerSelected(
        idx: Int,
        handleWrong: (Float) -> Unit
    ) = intent {
        var isEnd = false
        val currentState = state

        if (idx != 3) {
            if (currentState.ansNum == currentState.randomList[idx]) {
                val newHistory = currentState.sightHistory.toMutableMap()
                val current = newHistory[currentState.sightLevel]!!
                newHistory[currentState.sightLevel] = Pair(current.first + 1, current.second)

                reduce { state.copy(sightHistory = newHistory) }

                if (current.first == 0 && current.second == 0) {
                    if (currentState.sightLevel == 10) {
                        viewModelScope.launch {
                            handleWrong(1.2f)
                            delay(500)
                            isEnd = true
                            moveToNextStep(handleWrong)
                        }
                    } else {
                        reduce { state.copy(sightLevel = state.sightLevel + 1) }
                    }
                }
            } else {
                val newWrongCount = currentState.wrongCount + 1f
                handleWrong(newWrongCount / (newWrongCount + 1f))

                val newHistory = currentState.sightHistory.toMutableMap()
                val current = newHistory[currentState.sightLevel]!!
                newHistory[currentState.sightLevel] = Pair(current.first, current.second + 1)

                reduce {
                    state.copy(
                        wrongCount = newWrongCount,
                        sightHistory = newHistory
                    )
                }
            }
        } else {
            val newWrongCount = currentState.wrongCount + 1f
            handleWrong(newWrongCount / (newWrongCount + 1f))

            val newHistory = currentState.sightHistory.toMutableMap()
            val current = newHistory[currentState.sightLevel]!!
            newHistory[currentState.sightLevel] = Pair(current.first, current.second + 1)

            reduce {
                state.copy(
                    wrongCount = newWrongCount,
                    sightHistory = newHistory
                )
            }
        }

        val updatedHistory = state.sightHistory[state.sightLevel]!!
        if (updatedHistory.first + updatedHistory.second >= 3) {
            if (updatedHistory.first >= 2) {
                if (state.sightLevel == 10) {
                    viewModelScope.launch {
                        handleWrong(1.2f)
                        delay(500)
                        isEnd = true
                        moveToNextStep(handleWrong)
                    }
                } else {
                    reduce { state.copy(sightLevel = state.sightLevel + 1) }
                }
            } else {
                viewModelScope.launch {
                    handleWrong(1.2f)
                    delay(500)
                    isEnd = true
                    moveToNextStep(handleWrong)
                }
            }
        }

        if (!isEnd) updateRandomList()
    }

    private fun moveToNextStep(handleWrong: (Float) -> Unit) = intent {
        handleWrong(0.1f)

        if (state.isLeftEye) {
            leftEyeSightValue = state.sightLevel
        } else {
            rightEyeSightValue = state.sightLevel
        }

        viewModelScope.launch {
            delay(450)
            intent {
                reduce { state.copy(sightLevel = 1) }
            }
        }

        reduce {
            state.copy(
                isVisualAcuityVisible = false,
                wrongCount = 0f,
                sightHistory = mapOf(
                    1 to Pair(0, 0),
                    2 to Pair(0, 0),
                    3 to Pair(0, 0),
                    4 to Pair(0, 0),
                    5 to Pair(0, 0),
                    6 to Pair(0, 0),
                    7 to Pair(0, 0),
                    8 to Pair(0, 0),
                    9 to Pair(0, 0),
                    10 to Pair(0, 0)
                )
            )
        }

        if (!state.isLeftEye) {
            TTS.speechTTS(StringProvider.getString(R.string.tts_end), TextToSpeech.QUEUE_ADD)
            navigateToResult()
        } else {
            TTS.speechTTS(
                StringProvider.getString(R.string.tts_right_align),
                TextToSpeech.QUEUE_ADD
            )
            reduce {
                state.copy(
                    isMeasuringDistanceVisible = true,
                    isLeftEye = false
                )
            }
        }
    }

    private fun navigateToResult() = intent {
        val result = getVisualAcuityInspectionResult()
        // TODO: 결과 전달 방법 구현 필요
        navigator.navigate(InspectionRoute.InspectionResult)
    }

    fun getVisualAcuityInspectionResult(): VisualAcuityInspectionResult {
        return VisualAcuityInspectionResult(
            leftEyeSightValue,
            rightEyeSightValue
        )
    }
}
