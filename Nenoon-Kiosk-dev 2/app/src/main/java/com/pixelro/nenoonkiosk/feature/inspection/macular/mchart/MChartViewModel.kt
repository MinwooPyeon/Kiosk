package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.navigation.InspectionRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.result.MChartInspectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MChartViewModel @Inject constructor(
    private val navigator: Navigator,
    application: Application
) : AndroidViewModel(application), ContainerHost<MChartUiState, Nothing> {

    override val container = container<MChartUiState, Nothing>(
        MChartUiState()
    )

    private var leftVerticalValue = 0
    private var leftHorizontalValue = 0
    private var rightVerticalValue = 0
    private var rightHorizontalValue = 0

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(getApplication()).build().apply {
        volume = 0f
    }

    fun init() = intent {
        reduce {
            MChartUiState(
                isMeasuringDistanceVisible = true,
                isMChartVisible = false,
                isLeftEye = true,
                isVertical = true,
                currentLevel = 0,
                mChartImageId = R.drawable.mchart_0_0,
                isTTSSpeaking = true,
                isTesting = false
            )
        }
        leftVerticalValue = 0
        leftHorizontalValue = 0
        rightVerticalValue = 0
        rightHorizontalValue = 0
    }

    fun updateIsTesting(isTesting: Boolean) = intent {
        reduce { state.copy(isTesting = isTesting) }
    }

    fun updateIsTTSSpeaking(isSpeaking: Boolean) = intent {
        reduce { state.copy(isTTSSpeaking = isSpeaking) }
    }

    fun updateIsMeasuringDistanceVisible(visible: Boolean) = intent {
        reduce { state.copy(isMeasuringDistanceVisible = visible) }
    }

    fun updateIsMChartVisible(visible: Boolean) = intent {
        reduce { state.copy(isMChartVisible = visible) }
    }

    fun getMChartTestResult(): MChartInspectionResult {
        return MChartInspectionResult(
            leftVerticalValue,
            leftHorizontalValue,
            rightVerticalValue,
            rightHorizontalValue
        )
    }

    fun toNextMChartTest() = intent {
        reduce { state.copy(isLeftEye = false) }
        viewModelScope.launch {
            reduce { state.copy(isTesting = false) }
            exoPlayer.stop()
            reduce { state.copy(isTTSSpeaking = true) }
            delay(450)
            updateCurrentLevel(0)
            updateIsVertical(true)
        }
    }

    fun updateLeftVerticalValue() {
        leftVerticalValue = container.stateFlow.value.currentLevel
    }

    fun updateLeftHorizontalValue() {
        leftHorizontalValue = container.stateFlow.value.currentLevel
    }

    fun updateRightVerticalValue() {
        rightVerticalValue = container.stateFlow.value.currentLevel
    }

    fun updateRightHorizontalValue() {
        rightHorizontalValue = container.stateFlow.value.currentLevel
    }

    fun updateIsVertical(isVertical: Boolean) = intent {
        reduce { state.copy(isVertical = isVertical) }
    }

    fun updateCurrentLevel(level: Int) = intent {
        val imageId = when (level) {
            0 -> R.drawable.mchart_0_0
            1 -> R.drawable.mchart_0_2
            2 -> R.drawable.mchart_0_3
            3 -> R.drawable.mchart_0_4
            4 -> R.drawable.mchart_0_5
            5 -> R.drawable.mchart_0_6
            6 -> R.drawable.mchart_0_7
            7 -> R.drawable.mchart_0_8
            8 -> R.drawable.mchart_0_9
            9 -> R.drawable.mchart_1_0
            10 -> R.drawable.mchart_1_1
            11 -> R.drawable.mchart_1_2
            12 -> R.drawable.mchart_1_3
            13 -> R.drawable.mchart_1_4
            14 -> R.drawable.mchart_1_5
            15 -> R.drawable.mchart_1_6
            16 -> R.drawable.mchart_1_7
            17 -> R.drawable.mchart_1_8
            18 -> R.drawable.mchart_1_9
            else -> R.drawable.mchart_2_0
        }
        reduce {
            state.copy(
                currentLevel = level,
                mChartImageId = imageId
            )
        }
    }

    fun navigateToResult() = intent {
        val result = getMChartTestResult()
        // TODO: 결과 전달 방법 구현 필요
        navigator.navigate(InspectionRoute.InspectionResult)
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
