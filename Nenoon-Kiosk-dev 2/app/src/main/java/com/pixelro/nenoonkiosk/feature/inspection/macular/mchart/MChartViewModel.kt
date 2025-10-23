package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MChartViewModel
    @Inject
    constructor(
        application: Application,
    ) : AndroidViewModel(application) {
        private val _isMeasuringDistanceContentVisible = MutableStateFlow(true)
        val isMeasuringDistanceContentVisible: StateFlow<Boolean> = _isMeasuringDistanceContentVisible
        private val _isMChartContentVisible = MutableStateFlow(false)
        val isMChartContentVisible: StateFlow<Boolean> = _isMChartContentVisible
        private val _isLeftEye = MutableStateFlow(true)
        val isLeftEye: StateFlow<Boolean> = _isLeftEye
        private val _isVertical = MutableStateFlow(true)
        val isVertical: StateFlow<Boolean> = _isVertical
        private val _currentLevel = MutableStateFlow(0)
        val currentLevel: StateFlow<Int> = _currentLevel
        private var _leftVerticalValue = 0
        private var _leftHorizontalValue = 0
        private var _rightVerticalValue = 0
        private var _rightHorizontalValue = 0
        private val _mChartImageId = MutableStateFlow(R.drawable.mchart_0_0)
        val mChartImageId: StateFlow<Int> = _mChartImageId
        val exoPlayer: ExoPlayer = ExoPlayer.Builder(getApplication()).build()
        private val _isTTSSpeaking = MutableStateFlow(true)
        val isTTSSpeaking: StateFlow<Boolean> = _isTTSSpeaking
        private val _isTesting = MutableStateFlow(false)
        val isTesting: StateFlow<Boolean> = _isTesting

        fun updateIsTesting(isTesting: Boolean) {
            _isTesting.update { isTesting }
        }

        fun updateIsTTSSpeaking(isSpeaking: Boolean) {
            _isTTSSpeaking.update { isSpeaking }
        }

        fun updateIsMeasuringDistanceContentVisible(visible: Boolean) {
            _isMeasuringDistanceContentVisible.update { visible }
        }

        fun updateIsMChartContentVisible(visible: Boolean) {
            _isMChartContentVisible.update { visible }
        }

        fun getMChartTestResult(): MChartTestResult {
            return MChartTestResult(
                _leftVerticalValue,
                _leftHorizontalValue,
                _rightVerticalValue,
                _rightHorizontalValue,
            )
        }

        /**
         * 왼쪽 눈 마무리
         */
        fun toNextMChartTest() {
            _isLeftEye.update { false }
            viewModelScope.launch {
                _isTesting.update { false }
                exoPlayer.stop()
                _isTTSSpeaking.update { true }
                delay(450)
                updateCurrentLevel(0)
                updateIsVertical(true)
            }
        }

        fun updateLeftVerticalValue() {
            _leftVerticalValue = currentLevel.value
        }

        fun updateLeftHorizontalValue() {
            _leftHorizontalValue = currentLevel.value
        }

        fun updateRightVerticalValue() {
            _rightVerticalValue = currentLevel.value
        }

        fun updateRightHorizontalValue() {
            _rightHorizontalValue = currentLevel.value
        }

        fun updateIsVertical(isVertical: Boolean) {
            _isVertical.update { isVertical }
        }

        /**
         * 단계 상승 함수
         */
        fun updateCurrentLevel(level: Int) {
            _currentLevel.update { level }
            _mChartImageId.update {
                when (currentLevel.value) {
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
            }
        }

        fun init() {
            _isLeftEye.update { true }
            _isVertical.update { true }
            _currentLevel.update { 0 }
            _isMeasuringDistanceContentVisible.update { true }
            _isMChartContentVisible.update { false }
            _mChartImageId.update { R.drawable.mchart_0_0 }
        }

        init {
            exoPlayer.volume = 0f
        }
    }
