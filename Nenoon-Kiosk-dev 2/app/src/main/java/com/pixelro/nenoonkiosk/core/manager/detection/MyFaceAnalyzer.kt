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
        // 얼굴 인식 범위 상수
        private const val EYE_LEFT_MIN_X = 360f
        private const val EYE_CENTER_X = 644f
        private const val EYE_RIGHT_MAX_X = 904f
        private const val EYE_MIN_Y = 400f
        private const val EYE_DISTANCE_MIN = 100f

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
            updateInputImageSize?.invoke(inputImage.width.toFloat(), inputImage.height.toFloat())
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

    // 얼굴 인식 범위 확인
    private fun isFaceInValidRange(leftEye: PointF, rightEye: PointF): Boolean {
        return leftEye.x > EYE_LEFT_MIN_X &&
                leftEye.x < EYE_CENTER_X &&
                rightEye.x > EYE_CENTER_X &&
                rightEye.x < EYE_RIGHT_MAX_X &&
                leftEye.y > EYE_MIN_Y &&
                rightEye.y > EYE_MIN_Y &&
                (rightEye.x - leftEye.x) > EYE_DISTANCE_MIN
    }
}