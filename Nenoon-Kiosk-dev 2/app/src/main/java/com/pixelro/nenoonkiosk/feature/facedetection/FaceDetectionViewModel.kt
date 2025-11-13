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
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FaceDetectionViewModel
@Inject
constructor(
    application: Application,
) : AndroidViewModel(application),
    ContainerHost<FaceDetectionUiState, FaceDetectionSideEffect> {

    companion object {
        // 기본 이미지 크기 상수
        private const val DEFAULT_IMAGE_SIZE = 1088f
        private const val ZERO_FLOAT = 0f
        // 텍스트 인식 중앙값 (얼굴 인식 범위와 동일하게 설정)
        private const val TEXT_CENTER_X = 644f

        // 거리 측정 상수
        private const val FOCAL_LENGTH_MULTIPLIER = 1.33f
        private const val INTERPUPILLARY_DISTANCE_MM = 63
        private const val MAX_VALID_DISTANCE = 600f
        private const val MIN_VALID_DISTANCE = 1f

        // 텍스트 기반 거리 계산 상수
        private const val TEXT_WIDTH_CONSTANT = 3200
        private const val TEXT_DISTANCE_MULTIPLIER = 10f

        // 타이머 상수
        private const val TIMER_DELAY_MS = 100L
        private const val TEXT_DETECTION_COUNT_INIT = 10
    }

    override val container: Container<FaceDetectionUiState, FaceDetectionSideEffect> =
        container(FaceDetectionUiState())

    // 얼굴 거리 및 위치 관련
    private val _screenToFaceDistance = MutableStateFlow(ZERO_FLOAT)
    val screenToFaceDistance: StateFlow<Float> = _screenToFaceDistance

    private val _inputImageSizeX = MutableStateFlow(DEFAULT_IMAGE_SIZE)
    private val _inputImageSizeY = MutableStateFlow(DEFAULT_IMAGE_SIZE)

    private val _rightEyePosition = MutableStateFlow(PointF(ZERO_FLOAT, ZERO_FLOAT))
    val rightEyePosition: StateFlow<PointF> = _rightEyePosition

    private val _leftEyePosition = MutableStateFlow(PointF(ZERO_FLOAT, ZERO_FLOAT))
    val leftEyePosition: StateFlow<PointF> = _leftEyePosition

    // 얼굴 회전 각도
    private val _rotX = MutableStateFlow(ZERO_FLOAT)
    val rotX: StateFlow<Float> = _rotX

    private val _rotY = MutableStateFlow(ZERO_FLOAT)
    val rotY: StateFlow<Float> = _rotY

    private val _rotZ = MutableStateFlow(ZERO_FLOAT)
    val rotZ: StateFlow<Float> = _rotZ

    // 얼굴 윤곽 데이터
    private val _leftEyeContour = MutableStateFlow(listOf(PointF(ZERO_FLOAT, ZERO_FLOAT)))
    val leftEyeContour: StateFlow<List<PointF>> = _leftEyeContour

    private val _rightEyeContour = MutableStateFlow(listOf(PointF(ZERO_FLOAT, ZERO_FLOAT)))
    val rightEyeContour: StateFlow<List<PointF>> = _rightEyeContour

    private val _upperLipTopContour = MutableStateFlow(listOf(PointF(ZERO_FLOAT, ZERO_FLOAT)))
    val upperLipTopContour: StateFlow<List<PointF>> = _upperLipTopContour

    private val _upperLipBottomContour = MutableStateFlow(listOf(PointF(ZERO_FLOAT, ZERO_FLOAT)))
    val upperLipBottomContour: StateFlow<List<PointF>> = _upperLipBottomContour

    private val _lowerLipTopContour = MutableStateFlow(listOf(PointF(ZERO_FLOAT, ZERO_FLOAT)))
    val lowerLipTopContour: StateFlow<List<PointF>> = _lowerLipTopContour

    private val _lowerLipBottomContour = MutableStateFlow(listOf(PointF(ZERO_FLOAT, ZERO_FLOAT)))
    val lowerLipBottomContour: StateFlow<List<PointF>> = _lowerLipBottomContour

    private val _faceContour = MutableStateFlow(listOf(PointF(ZERO_FLOAT, ZERO_FLOAT)))
    val faceContour: StateFlow<List<PointF>> = _faceContour

    // 눈 열림 확률
    private val _leftEyeOpenProbability = MutableStateFlow(ZERO_FLOAT)
    val leftEyeOpenProbability: StateFlow<Float> = _leftEyeOpenProbability

    private val _rightEyeOpenProbability = MutableStateFlow(ZERO_FLOAT)
    val rightEyeOpenProbability: StateFlow<Float> = _rightEyeOpenProbability

    // 감지 상태
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

    private var textDetectionCount = 0

    // TTS 완료 상태
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

    // TTS 상태 업데이트 함수
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

    // 얼굴 감지 데이터 업데이트
    fun onFaceDataDetected(
        boundingBox: Rect,
        leftEyePosition: PointF?,
        rightEyePosition: PointF?,
        rotX: Float,
        rotY: Float,
        rotZ: Float,
        leftEyeOpenProbability: Float?,
        rightEyeOpenProbability: Float?
    ) = intent {
        reduce {
            state.copy(
                leftEyePosition = leftEyePosition,
                rightEyePosition = rightEyePosition,
                rotX = rotX,
                rotY = rotY,
                rotZ = rotZ,
                leftEyeOpenProbability = leftEyeOpenProbability,
                rightEyeOpenProbability = rightEyeOpenProbability,
                faceBoundingBox = boundingBox
            )
        }

        // Legacy 함수 호출
        updateFaceDetectionData(
            boundingBox, leftEyePosition, rightEyePosition,
            rotX, rotY, rotZ, leftEyeOpenProbability, rightEyeOpenProbability
        )
    }

    // 텍스트 인식 데이터 업데이트
    fun onTextRecognized(textBoundingBox: Rect?) = intent {
        reduce {
            state.copy(textBoundingBox = textBoundingBox)
        }
        updateTextRecognitionData(textBoundingBox)
    }

    // 얼굴 감지 여부 업데이트
    fun onFaceDetectionChanged(isDetected: Boolean) = intent {
        reduce {
            state.copy(isFaceDetected = isDetected)
        }
        updateIsFaceDetected(isDetected)
    }

    // NENOON 텍스트 감지 여부 업데이트
    fun onNenoonTextDetected(isDetected: Boolean) = intent {
        reduce {
            state.copy(isNenoonTextDetected = isDetected)
        }
        updateIsNenoonTextDetected(isDetected)
    }

    // 시선 추적 결과 업데이트
    fun onGazeResultDetected(irisResult: IrisResult) = intent {
        reduce {
            state.copy(
                isFacingForward = irisResult.isFacingForward,
                leftEyePosition = irisResult.leftEyePosition,
                rightEyePosition = irisResult.rightEyePosition,
                rotX = irisResult.rotX,
                rotY = irisResult.rotY,
                rotZ = irisResult.rotZ,
                leftEyeOpenProbability = irisResult.leftEyeOpenProbability,
                rightEyeOpenProbability = irisResult.rightEyeOpenProbability,
                gazeScore = irisResult.gazeScore
            )
        }
    }

    // Legacy 함수 
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
        _rightEyePosition.update {
            PointF(
                rightEyePosition?.x ?: it.x,
                rightEyePosition?.y ?: it.y
            )
        }
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

        // 얼굴이 감지되지 않으면 거리 값도 리셋
        if (!isFaceDetected) {
            _screenToFaceDistance.update { ZERO_FLOAT }
        }
    }

    fun updateIsDistanceOK(isDistanceOK: Int) {
        _isDistanceOK.value = isDistanceOK
    }

    fun updateInputImageSize(x: Float, y: Float) {
        _inputImageSizeX.update { x }
        _inputImageSizeY.update { y }
    }

    fun updateIsNenoonTextDetected(isNenoonTextDetected: Boolean) {
        textDetectionCount = TEXT_DETECTION_COUNT_INIT
        _isNenoonTextDetected.update { isNenoonTextDetected }
    }

    // 얼굴 인식 거리 계산
    private fun updateScreenToFaceDistance() {
        if (!_isNenoonTextDetected.value) {
            val eyeDistance = rightEyePosition.value.x - leftEyePosition.value.x
            val lensWidth = GlobalValue.lensSize.width

            if (eyeDistance != ZERO_FLOAT && lensWidth != ZERO_FLOAT) {
                _screenToFaceDistance.update { prev ->
                    val calculatedDistance = FOCAL_LENGTH_MULTIPLIER *
                            (GlobalValue.focalLength * INTERPUPILLARY_DISTANCE_MM) *
                            _inputImageSizeX.value / (eyeDistance * lensWidth)

                    if (calculatedDistance > MAX_VALID_DISTANCE || calculatedDistance < MIN_VALID_DISTANCE) {
                        prev
                    } else {
                        calculatedDistance
                    }
                }
            }
        } else {
            _screenToFaceDistance.update { _distance.value * TEXT_DISTANCE_MULTIPLIER }
        }
    }

    // 텍스트 인식 데이터
    private val _textBox: MutableStateFlow<Rect?> = MutableStateFlow(Rect(0, 0, 0, 0))
    val textBox: StateFlow<Rect?> = _textBox

    private val _distance = MutableStateFlow(ZERO_FLOAT)

    // 텍스트 거리 계산
    fun updateTextRecognitionData(textBox: Rect?) {
        _textBox.update { textBox }

        val textCenterX = ((_textBox.value?.right?.toFloat() ?: ZERO_FLOAT) +
                (_textBox.value?.left?.toFloat() ?: ZERO_FLOAT)) / 2

        // 얼굴 인식 범위와 동일한 중앙값 사용
        if (textCenterX > TEXT_CENTER_X) {
            _isLeftEyeCovered.update { true }
            _isRightEyeCovered.update { false }
        } else {
            _isLeftEyeCovered.update { false }
            _isRightEyeCovered.update { true }
        }

        val textWidth = (_textBox.value?.right?.toFloat() ?: ZERO_FLOAT) -
                (_textBox.value?.left?.toFloat() ?: ZERO_FLOAT)

        _distance.update { TEXT_WIDTH_CONSTANT / textWidth }
        _screenToFaceDistance.update { _distance.value * TEXT_DISTANCE_MULTIPLIER }
    }

    // 텍스트 감지 타이머
    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                delay(TIMER_DELAY_MS)
                textDetectionCount--

                if (textDetectionCount < 0) {
                    _isNenoonTextDetected.update { false }
                    textDetectionCount = 0
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
