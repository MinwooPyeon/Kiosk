package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.navigation.InspectionRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AmslerGridViewModel @Inject constructor(
    private val navigator: Navigator,
    application: Application
) : AndroidViewModel(application), ContainerHost<AmslerGridUiState, Nothing> {

    override val container = container<AmslerGridUiState, Nothing>(
        AmslerGridUiState()
    )

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(getApplication()).build().apply {
        volume = 0f
    }

    fun init() = intent {
        reduce {
            AmslerGridUiState(
                isMeasuringDistanceVisible = true,
                isAmslerGridVisible = false,
                isLeftEye = true,
                currentSelectedArea = List(9) { MacularDisorderType.Normal },
                isBlinkingDone = false,
                isDotShowing = true,
                isFaceCenter = false,
                isTestStarted = false
            )
        }
    }

    fun updateIsTestStarted(isStarted: Boolean) = intent {
        reduce { state.copy(isTestStarted = isStarted) }
    }

    fun updateIsLookAtTheDotTTSDone(isDone: Boolean) = intent {
        reduce { state.copy(isLookAtTheDotTTSDone = isDone) }
    }

    fun updateIsSelectTTSDone(isDone: Boolean) = intent {
        reduce { state.copy(isSelectTTSDone = isDone) }
    }

    fun updateIsBlinkingDone(isDone: Boolean) = intent {
        reduce { state.copy(isBlinkingDone = isDone) }
    }

    fun updateIsDotShowing(isShowing: Boolean) = intent {
        reduce { state.copy(isDotShowing = isShowing) }
    }

    fun updateIsFaceCenter(isCenter: Boolean) = intent {
        reduce { state.copy(isFaceCenter = isCenter) }
    }

    fun updateIsMeasuringDistanceVisible(visible: Boolean) = intent {
        reduce { state.copy(isMeasuringDistanceVisible = visible) }
    }

    fun updateIsAmslerGridVisible(visible: Boolean) = intent {
        reduce { state.copy(isAmslerGridVisible = visible) }
    }

    fun updateIsLeftEye(isLeft: Boolean) = intent {
        reduce { state.copy(isLeftEye = isLeft) }
    }

    fun updateLeftSelectedArea() = intent {
        TTS.speechTTS(StringProvider.getString(R.string.tts_right_eye), TextToSpeech.QUEUE_ADD)
        viewModelScope.launch {
            delay(450)
            reduce {
                state.copy(
                    isTestStarted = false,
                    isBlinkingDone = false,
                    isDotShowing = true,
                    isFaceCenter = false,
                    leftSelectedArea = state.currentSelectedArea,
                    currentSelectedArea = List(9) { MacularDisorderType.Normal }
                )
            }
        }
    }

    fun updateRightSelectedArea() = intent {
        reduce { state.copy(rightSelectedArea = state.currentSelectedArea) }
    }

    fun getAmslerGridTestResult(): AmslerGridTestResult {
        val currentState = container.stateFlow.value
        return AmslerGridTestResult(currentState.leftSelectedArea, currentState.rightSelectedArea)
    }

    fun updateCurrentSelectedPosition(position: Offset) = intent {
        reduce { state.copy(currentSelectedPosition = position) }
        for (i in 0..8) {
            if (position.x in ((i % 3) * 300f)..((i % 3) * 300f + 299f) &&
                position.y in ((i / 3) * 300f)..((i / 3) * 300f + 299f)
            ) {
                if (state.currentSelectedArea[i] != MacularDisorderType.Normal) {
                    reduce {
                        val tmpList = state.currentSelectedArea.toMutableList()
                        tmpList[i] = MacularDisorderType.Normal
                        state.copy(currentSelectedArea = tmpList.toList())
                    }
                } else {
                    updateCurrentSelectedArea(i)
                }
                return@intent
            }
        }
    }

    private fun updateCurrentSelectedArea(index: Int) = intent {
        reduce {
            val tmpList = state.currentSelectedArea.toMutableList()
            tmpList[index] = MacularDisorderType.Distorted
            state.copy(currentSelectedArea = tmpList.toList())
        }
    }

    fun startBlinking() = intent {
        viewModelScope.launch {
            var count = 24
            while (count > 0) {
                reduce { state.copy(isDotShowing = !state.isDotShowing) }
                delay(250)
                count--
            }
            reduce { state.copy(isBlinkingDone = true) }
        }
    }

    fun navigateToResult() = intent {
        val result = getAmslerGridTestResult()
        // TODO: 결과 전달 방법 구현 필요
        navigator.navigate(InspectionRoute.InspectionResult)
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
