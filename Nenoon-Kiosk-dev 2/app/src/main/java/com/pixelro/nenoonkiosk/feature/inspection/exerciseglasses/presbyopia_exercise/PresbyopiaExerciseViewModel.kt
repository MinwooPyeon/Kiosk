package com.pixelro.nenoonkiosk.feature.inspection.exerciseglasses.presbyopia_exercise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PresbyopiaExerciseViewModel
    @Inject
    constructor(
        application: Application,
    ) : AndroidViewModel(application) {
        private val _isMChartContentVisible = MutableStateFlow(false)
        val isMChartContentVisible: StateFlow<Boolean> = _isMChartContentVisible
        private val _currentLevel = MutableStateFlow(0)
        val currentLevel: StateFlow<Int> = _currentLevel
        private var _presbyopiaExerciseValue = 0
        private val _mChartImageId = MutableStateFlow(R.drawable.eyecontrol_02)
        val mChartImageId: StateFlow<Int> = _mChartImageId
        private val _isTesting = MutableStateFlow(false)
        val isTesting: StateFlow<Boolean> = _isTesting

        fun updateIsTesting(isTesting: Boolean) {
            _isTesting.update { isTesting }
        }

        fun getPresbyopiaExerciseResult(): PresbyopiaExerciseResult {
            return PresbyopiaExerciseResult(
                _presbyopiaExerciseValue,
            )
        }

        fun updatePresbyopiaExerciseValue() {
            _presbyopiaExerciseValue = currentLevel.value
        }

        fun updateCurrentLevel(level: Int) {
            _currentLevel.update { level }
            _mChartImageId.update {
                if (SharedPreferencesManager.getString("language") == "ko") {
                    when (currentLevel.value) {
                        0 -> R.drawable.eyecontrol_02
                        1 -> R.drawable.eyecontrol_03
                        2 -> R.drawable.eyecontrol_04
                        3 -> R.drawable.eyecontrol_05
                        4 -> R.drawable.eyecontrol_06
                        5 -> R.drawable.eyecontrol_07
                        6 -> R.drawable.eyecontrol_08
                        7 -> R.drawable.eyecontrol_09
                        else -> R.drawable.eyecontrol_10
                    }
                } else {
                    when (currentLevel.value) {
                        0 -> R.drawable.presbyopia_glasses_test_chart_2
                        1 -> R.drawable.presbyopia_glasses_test_chart_3
                        2 -> R.drawable.presbyopia_glasses_test_chart_4
                        3 -> R.drawable.presbyopia_glasses_test_chart_5
                        4 -> R.drawable.presbyopia_glasses_test_chart_6
                        5 -> R.drawable.presbyopia_glasses_test_chart_7
                        6 -> R.drawable.presbyopia_glasses_test_chart_8
                        7 -> R.drawable.presbyopia_glasses_test_chart_9
                        else -> R.drawable.presbyopia_glasses_test_chart_10
                    }
                }
            }
        }

        fun init() {
            _currentLevel.update { 0 }
            _isMChartContentVisible.update { true }
            if (SharedPreferencesManager.getString("language") == "ko") {
                _mChartImageId.update { R.drawable.eyecontrol_02 }
            } else {
                _mChartImageId.update { R.drawable.presbyopia_glasses_test_chart_2 }
            }
        }

        init {
            init()
        }
    }
