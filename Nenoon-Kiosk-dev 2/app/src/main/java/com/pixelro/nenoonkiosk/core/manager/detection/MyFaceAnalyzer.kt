package com.pixelro.nenoonkiosk.core.manager.detection

import android.annotation.SuppressLint
import android.graphics.PointF
import android.graphics.Rect
import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.facedetection.IrisResult
import java.util.concurrent.Executor

class MyFaceAnalyzer(
    private val updateFaceDetectionData: (Rect, PointF?, PointF?, Float, Float, Float, Float?, Float?) -> Unit,
    private val updateTextRecognitionData: (Rect?) -> Unit,
    private val updateIsFaceDetected: (Boolean) -> Unit,
    private val updateIsNenoonTextDetected: (Boolean) -> Unit,
    private val onGazeDetectionResult: (IrisResult) -> Unit,
    private val executor: Executor,
    private val updateInputImageSize: ((Float, Float) -> Unit)? = null,
) : ImageAnalysis.Analyzer {

    companion object {
        // 얼굴 인식 범위 비율 (카메라 이미지 중앙 기준)
        private const val CENTER_RATIO = 0.5f        // 이미지 정중앙
        private const val EYE_MIN_Y_RATIO = 0.1f     // 상단 10% 이하는 제외 (세로모드 대응)
        private const val EYE_DISTANCE_MIN_RATIO = 0.1f  // 최소 눈 간격 10%

        // 얼굴 미감지 카운트 임계값
        private const val NO_FACE_COUNT_THRESHOLD = 6

        // NENOON 텍스트 인식 키워드
        private const val NENOON_TEXT = "NENOON"
        private const val NENOON_TEXT_ALT = "NE NOON"

        // Face Detector 옵션
        private const val MIN_FACE_SIZE = 0.3f
    }

    private var lastAnalysisTime = -1L
    private var noFaceCount = 0
    private var isImageSizeUpdated = false

    // 카메라 이미지 크기
    private var _inputImageSizeX = 1088f
    private var _inputImageSizeY = 1088f

    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(MIN_FACE_SIZE)
        .enableTracking()
        .build()

    private val faceDetector = FaceDetection.getClient(faceDetectorOptions)
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        lastAnalysisTime = SystemClock.uptimeMillis()

        val mediaImage = imageProxy.image ?: return
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        // 이미지 크기 업데이트 (최초 1회만)
        if (!isImageSizeUpdated) {
            _inputImageSizeX = inputImage.width.toFloat()
            _inputImageSizeY = inputImage.height.toFloat()
            updateInputImageSize?.invoke(_inputImageSizeX, _inputImageSizeY)
            isImageSizeUpdated = true
        }

        // 텍스트 인식
        val faceTask = processTextRecognition(inputImage)

        // 얼굴 감지
        val textTask = processFaceDetection(inputImage, imageProxy)

        Tasks.whenAllComplete(faceTask, textTask)
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    // 텍스트 인식 처리
    private fun processTextRecognition(image: InputImage): Task<Text?> {
        return textRecognizer.process(image)
            .addOnSuccessListener(executor) { result ->
                val nenoonTextDetected = result.textBlocks.any { block ->
                    block.lines.any { line ->
                        if (isNenoonText(line.text)) {
                            updateTextRecognitionData(line.boundingBox)
                            true
                        } else {
                            false
                        }
                    }
                }

                if (nenoonTextDetected) {
                    updateIsNenoonTextDetected(true)
                }
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    // 얼굴 감지 처리
    private fun processFaceDetection(image: InputImage, imageProxy: ImageProxy): Task<List<Face?>?> {
        return faceDetector.process(image)
            .addOnSuccessListener(executor) { faces ->
                val centerFace = findCenterFace(faces)

                if (centerFace != null) {
                    handleFaceDetected(centerFace)
                } else {
                    handleFaceNotDetected()
                }
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    // 중앙 얼굴 찾기
    private fun findCenterFace(faces: List<Face>): Face? {
        return faces.firstOrNull { face ->
            val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
            val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position

            if (leftEye != null && rightEye != null) {
                isFaceInValidRange(leftEye, rightEye)
            } else {
                false
            }
        }
    }

    // 얼굴 감지 성공 처리
    private fun handleFaceDetected(face: Face) {
        noFaceCount = 0
        updateIsFaceDetected(true)

        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position

        if (leftEye != null && rightEye != null) {
            // Face Detection 데이터 업데이트
            updateFaceDetectionData(
                face.boundingBox,
                leftEye,
                rightEye,
                face.headEulerAngleX,
                face.headEulerAngleY,
                face.headEulerAngleZ,
                face.leftEyeOpenProbability,
                face.rightEyeOpenProbability
            )

            // 시선 추적 분석
            val gazeResult = GazeDetector.detectGazeDirection(face)
            onGazeDetectionResult(gazeResult)
        } else {
            updateIsFaceDetected(false)
        }
    }

    // 얼굴 미감지 처리
    private fun handleFaceNotDetected() {
        noFaceCount++
        if (noFaceCount > NO_FACE_COUNT_THRESHOLD) {
            updateIsFaceDetected(false)
        }
    }

    // NENOON 텍스트 확인
    private fun isNenoonText(text: String): Boolean {
        return text == NENOON_TEXT || text == NENOON_TEXT_ALT
    }

    // 얼굴 인식 범위 확인 (카메라 이미지 중앙 기준)
    private fun isFaceInValidRange(leftEye: PointF, rightEye: PointF): Boolean {
        // 이미지 크기 업데이트되지 않았으면 기본값 사용
        if (!isImageSizeUpdated) return false

        // 카메라 이미지 중앙 계산
        val imageCenterX = if (!GlobalValue.isLandscape) {
            _inputImageSizeX / 2f * 0.8f  // 세로모드: 중앙을 5% 왼쪽으로
        } else {
            _inputImageSizeX / 2f
        }
        val imageCenterY = _inputImageSizeY / 2f

        // 비율 계산
        val leftEyeYRatio = leftEye.y / _inputImageSizeY
        val rightEyeYRatio = rightEye.y / _inputImageSizeY
        val eyeDistanceRatio = (rightEye.x - leftEye.x) / _inputImageSizeX

        // 왼쪽 눈은 중앙보다 왼쪽, 오른쪽 눈은 중앙보다 오른쪽
        return leftEye.x < imageCenterX &&
                rightEye.x > imageCenterX &&
                leftEyeYRatio > EYE_MIN_Y_RATIO &&
                rightEyeYRatio > EYE_MIN_Y_RATIO &&
                eyeDistanceRatio > EYE_DISTANCE_MIN_RATIO
    }
}