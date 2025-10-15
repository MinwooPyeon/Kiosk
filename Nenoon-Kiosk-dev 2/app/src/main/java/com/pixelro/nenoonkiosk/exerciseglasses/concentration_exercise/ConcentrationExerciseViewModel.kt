package com.pixelro.nenoonkiosk.exerciseglasses.concentration_exercise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pixelro.nenoonkiosk.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConcentrationExerciseViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _isMChartContentVisible = MutableStateFlow(false)
    val isMChartContentVisible: StateFlow<Boolean> = _isMChartContentVisible
    private val _currentLevel = MutableStateFlow(0)
    val currentLevel: StateFlow<Int> = _currentLevel
    private var _concentrationExerciseValue = 0
    private val _mChartImageId = MutableStateFlow(R.drawable.eyecontrol_02)
    val mChartImageId: StateFlow<Int> = _mChartImageId
    private val _isTesting = MutableStateFlow(false)
    val isTesting: StateFlow<Boolean> = _isTesting

    fun updateIsTesting(isTesting: Boolean) {
        _isTesting.update { isTesting }
    }

    fun getConcentrationExerciseResult(): ConcentrationExerciseResult {
        return ConcentrationExerciseResult(
            _concentrationExerciseValue
        )
    }

    fun updateConcentrationExerciseValue() {
        _concentrationExerciseValue = currentLevel.value
    }

    fun updateCurrentLevel(level: Int) {
        _currentLevel.update { level }
        _mChartImageId.update {
            when (currentLevel.value) {
                0 -> R.drawable.eyecontrol_ko
                else -> R.drawable.eyecontrol_en
            }
        }
    }

    fun init() {
        _currentLevel.update { 0 }
        _isMChartContentVisible.update { true }
        _mChartImageId.update { R.drawable.eyecontrol_ko }
    }

    init {
        init()
    }
}