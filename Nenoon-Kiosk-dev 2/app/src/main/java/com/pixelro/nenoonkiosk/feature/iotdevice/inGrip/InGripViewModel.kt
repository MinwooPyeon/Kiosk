package com.pixelro.nenoonkiosk.feature.iotdevice.inGrip

import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.result.GripStrengthInspectionResultContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InGripViewModel
    @Inject
    constructor() : ViewModel() {
        private val _rightGrip = MutableStateFlow(0.0)
//    val rightGrip: StateFlow<Double> = _rightGrip.asStateFlow()

        private val _leftGrip = MutableStateFlow(0.0)
//    val leftGrip: StateFlow<Double> = _leftGrip.asStateFlow()

        private val _testFailed = MutableStateFlow(false)
        val testFailed: StateFlow<Boolean> = _testFailed.asStateFlow()

        fun setGripValues(
            right: Double,
            left: Double,
        ) {
            _rightGrip.value = right
            _leftGrip.value = left
            checkTestFailure(right, left)
        }

        private fun checkTestFailure(
            right: Double,
            left: Double,
        ) {
            if (right < 5 || left < 5) {
                _testFailed.value = true
            }
        }

        fun resetTest() {
            _rightGrip.value = 0.0
            _leftGrip.value = 0.0
            _testFailed.value = false
        }

        fun getGripStrengthData(): GripStrengthInspectionResultContract {
            return GripStrengthInspectionResultContract(
                rightGrip = _rightGrip.value,
                leftGrip = _leftGrip.value,
            )
        }
    }
