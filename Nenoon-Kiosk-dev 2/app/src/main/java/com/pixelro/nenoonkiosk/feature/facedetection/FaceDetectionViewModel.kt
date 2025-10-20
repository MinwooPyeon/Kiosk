package com.pixelro.nenoonkiosk.feature.facedetection

import android.app.Application
import android.graphics.PointF
import android.graphics.Rect
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceDetectionViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val _screenToFaceDistance = MutableStateFlow(0f)
    val screenToFaceDistance: StateFlow<Float> = _screenToFaceDistance
    private val _inputImageSizeX = MutableStateFlow(1088f)
    private val _inputImageSizeY = MutableStateFlow(1088f)
    private val _rightEyePosition = MutableStateFlow(PointF(0f, 0f))
    val rightEyePosition: StateFlow<PointF> = _rightEyePosition
    private val _leftEyePosition = MutableStateFlow(PointF(0f, 0f))
    val leftEyePosition: StateFlow<PointF> = _leftEyePosition
    private val _rotX = MutableStateFlow(0f)
    val rotX: StateFlow<Float> = _rotX
    private val _rotY = MutableStateFlow(0f)
    val rotY: StateFlow<Float> = _rotY
    private val _rotZ = MutableStateFlow(0f)
    val rotZ: StateFlow<Float> = _rotZ
    private val _leftEyeContour = MutableStateFlow(listOf(PointF(0f, 0f)))
    val leftEyeContour: StateFlow<List<PointF>> = _leftEyeContour
    private val _rightEyeContour = MutableStateFlow(listOf(PointF(0f, 0f)))
    val rightEyeContour: StateFlow<List<PointF>> = _rightEyeContour
    private val _upperLipTopContour = MutableStateFlow(listOf(PointF(0f, 0f)))
    val upperLipTopContour: StateFlow<List<PointF>> = _upperLipTopContour
    private val _upperLipBottomContour = MutableStateFlow(listOf(PointF(0f, 0f)))
    val upperLipBottomContour: StateFlow<List<PointF>> = _upperLipBottomContour
    private val _lowerLipTopContour = MutableStateFlow(listOf(PointF(0f, 0f)))
    val lowerLipTopContour: StateFlow<List<PointF>> = _lowerLipTopContour
    private val _lowerLipBottomContour = MutableStateFlow(listOf(PointF(0f, 0f)))
    val lowerLipBottomContour: StateFlow<List<PointF>> = _lowerLipBottomContour
    private val _faceContour = MutableStateFlow(listOf(PointF(0f, 0f)))
    val faceContour: StateFlow<List<PointF>> = _faceContour
    private val _leftEyeOpenProbability = MutableStateFlow(0f)
    val leftEyeOpenProbability: StateFlow<Float> = _leftEyeOpenProbability
    private val _rightEyeOpenProbability = MutableStateFlow(0f)
    val rightEyeOpenProbability: StateFlow<Float> = _rightEyeOpenProbability
    private val _isFaceDetected = MutableStateFlow(false)
    val isFaceDetected: StateFlow<Boolean> = _isFaceDetected
    private val _isDistanceOK = MutableStateFlow(0)
    val isDistanceOK: StateFlow<Int> = _isDistanceOK
    private val _isLeftEyeCovered = MutableStateFlow(false)
    val isLeftEyeCovered: StateFlow<Boolean> = _isLeftEyeCovered
    private val _isRightEyeCovered = MutableStateFlow(false)
    val isRightEyeCovered: StateFlow<Boolean> = _isRightEyeCovered
    private val _isNenoonTextDetected = MutableStateFlow(false)
    val isNenoonTextDetected: StateFlow<Boolean> = _isNenoonTextDetected
    private var count = 0

    // TTS 전용 변수
    private val _isOccluderPickedTTSDone = MutableStateFlow(true)
    val isOccluderPickedTTSDone: StateFlow<Boolean> = _isOccluderPickedTTSDone
    private val _isFaceDetectedTTSDone = MutableStateFlow(true)
    val isFaceDetectedTTSDone: StateFlow<Boolean> = _isFaceDetectedTTSDone
    private val _isEyeCoveredTTSDone = MutableStateFlow(true)
    val isEyeCoveredTTSDone: StateFlow<Boolean> = _isEyeCoveredTTSDone
    private val _isDistanceMeasuredTTSDone = MutableStateFlow(true)
    val isDistanceMeasuredTTSDone: StateFlow<Boolean> = _isDistanceMeasuredTTSDone
    private val _isPressStartButtonTTSDone = MutableStateFlow(true)
    val isPressStartButtonTTSDone: StateFlow<Boolean> = _isPressStartButtonTTSDone

    fun updateIsOccluderPickedTTSDone(isDone: Boolean) {
        _isOccluderPickedTTSDone.update { isDone }
    }

    fun updateIsFaceDetectedTTSDone(isDone: Boolean) {
        _isFaceDetectedTTSDone.update { isDone }
    }

    fun updateIsEyeCoveredTTSDone(isDone: Boolean) {
        _isEyeCoveredTTSDone.update { isDone }
    }

    fun updateIsDistanceMeasuredTTSDone(isDone: Boolean) {
        _isDistanceMeasuredTTSDone.update { isDone }
    }

    fun updateIsPressStartButtonTTSDone(isDone: Boolean) {
        _isPressStartButtonTTSDone.update { isDone }
    }

    fun updateFaceDetectionData(
        boundingBox: Rect,
        leftEyePosition: PointF?,
        rightEyePosition: PointF?,
        rotX: Float,
        rotY: Float,
        rotZ: Float,
        leftEyeOpenProbability: Float?,
        rightEyeOpenProbability: Float?,
    ) {
        _rightEyePosition.update { PointF(rightEyePosition?.x ?: it.x, rightEyePosition?.y ?: it.y) }
        _leftEyePosition.update { PointF(leftEyePosition?.x ?: it.x, leftEyePosition?.y ?: it.y) }
        _rotX.update { rotX }
        _rotY.update { rotY }
        _rotZ.update { rotZ }
        _leftEyeOpenProbability.update { leftEyeOpenProbability ?: 100f }
        _rightEyeOpenProbability.update { rightEyeOpenProbability ?: 100f }
        updateScreenToFaceDistance()
    }

    fun updateIsFaceDetected(isFaceDetected: Boolean) {
        _isFaceDetected.update { isFaceDetected }
    }

    fun updateIsDistanceOK(isDistanceOK: Int) {
        _isDistanceOK.value =  isDistanceOK
    }

    fun updateInputImageSize(x: Float, y: Float) {
        _inputImageSizeX.update { x }
        _inputImageSizeY.update { y }
    }

    fun updateIsNenoonTextDetected(isNenoonTextDetected: Boolean) {
        count = 10
        _isNenoonTextDetected.update { isNenoonTextDetected }
    }

    /**
     * 얼굴 인식 거리 조절 함수
     */
    private fun updateScreenToFaceDistance() {
        if (!_isNenoonTextDetected.value) {
            if ((rightEyePosition.value.x - leftEyePosition.value.y) != 0f && GlobalValue.lensSize.width != 0f) {
                _screenToFaceDistance.update {
                    val prev = _screenToFaceDistance.value
                    val dist = 1.33f * (GlobalValue.focalLength * 63) * _inputImageSizeX.value / ((rightEyePosition.value.x - leftEyePosition.value.x) * (GlobalValue.lensSize.width))
                    if (dist > 600f || dist < 1f) prev
                    else dist
                }
            } else {
                return
            }
        } else {
            _screenToFaceDistance.update { _distance.value * 10f }
        }
    }

    // Text Recognition
    private val _textBox: MutableStateFlow<Rect?> = MutableStateFlow(Rect(0, 0, 0, 0))
    val textBox: StateFlow<Rect?> = _textBox

    private val _distance = MutableStateFlow(0f)

    /**
     * 텍스트 거리 조절 함수
     */
    fun updateTextRecognitionData(
        textBox: Rect?
    ) {

        _textBox.update { textBox }
        if (((_textBox.value?.right?.toFloat() ?: 0f) + (_textBox.value?.left?.toFloat() ?: 0f)) / 2 > 544) {
            _isLeftEyeCovered.update { true }
            _isRightEyeCovered.update { false }
        } else {
            _isLeftEyeCovered.update { false }
            _isRightEyeCovered.update { true }
        }
        _distance.update { 3200 / ((_textBox.value?.right?.toFloat() ?: 0f) - (_textBox.value?.left?.toFloat() ?: 0f)) }
        _screenToFaceDistance.update { _distance.value * 10f }
    }

    /**
     * 몇 초 마다 측정할 것인지
     */
    private fun startTimer() {
        viewModelScope.launch {
            while(true) {
                delay(100)
                count--
                if (count < 0) {
                    _isNenoonTextDetected.update { false }
                    count = 0
                } else {
                    _isNenoonTextDetected.update { true }
                }
            }
        }
    }

    init {
        startTimer()
    }
}