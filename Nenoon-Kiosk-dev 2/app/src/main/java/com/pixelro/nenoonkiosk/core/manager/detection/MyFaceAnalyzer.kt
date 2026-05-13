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
        private const val EYE_MIN_Y_RATIO = 0.1f
        private const val EYE_DISTANCE_MIN_RATIO = 0.1f
        private const val NO_FACE_COUNT_THRESHOLD = 6
        private const val NENOON_TEXT = "NENOON"
        private const val NENOON_TEXT_ALT = "NE NOON"
        private const val MIN_FACE_SIZE = 0.3f
    }

    private var lastAnalysisTime = -1L
    private var noFaceCount = 0
    private var isImageSizeUpdated = false
    private var inputImageWidth = 1088f
    private var inputImageHeight = 1088f

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

        if (!isImageSizeUpdated) {
            inputImageWidth = inputImage.width.toFloat()
            inputImageHeight = inputImage.height.toFloat()
            updateInputImageSize?.invoke(inputImageWidth, inputImageHeight)
            isImageSizeUpdated = true
        }

        val textTask = processTextRecognition(inputImage)
        val faceTask = processFaceDetection(inputImage, imageProxy)

        Tasks.whenAllComplete(textTask, faceTask)
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

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

    private fun findCenterFace(faces: List<Face>): Face? {
        return faces.firstOrNull { face ->
            val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
            val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position
            if (leftEye != null && rightEye != null) isFaceInValidRange(leftEye, rightEye) else false
        }
    }

    private fun handleFaceDetected(face: Face) {
        noFaceCount = 0
        updateIsFaceDetected(true)

        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position

        if (leftEye != null && rightEye != null) {
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
            onGazeDetectionResult(GazeDetector.detectGazeDirection(face))
        } else {
            updateIsFaceDetected(false)
        }
    }

    private fun handleFaceNotDetected() {
        noFaceCount++
        if (noFaceCount > NO_FACE_COUNT_THRESHOLD) {
            updateIsFaceDetected(false)
        }
    }

    private fun isNenoonText(text: String): Boolean =
        text == NENOON_TEXT || text == NENOON_TEXT_ALT

    private fun isFaceInValidRange(leftEye: PointF, rightEye: PointF): Boolean {
        if (!isImageSizeUpdated) return false

        val imageCenterX = if (!GlobalValue.isLandscape) {
            inputImageWidth / 2f * 0.8f
        } else {
            inputImageWidth / 2f
        }

        val leftEyeYRatio = leftEye.y / inputImageHeight
        val rightEyeYRatio = rightEye.y / inputImageHeight
        val eyeDistanceRatio = (rightEye.x - leftEye.x) / inputImageWidth

        return leftEye.x < imageCenterX &&
                rightEye.x > imageCenterX &&
                leftEyeYRatio > EYE_MIN_Y_RATIO &&
                rightEyeYRatio > EYE_MIN_Y_RATIO &&
                eyeDistanceRatio > EYE_DISTANCE_MIN_RATIO
    }
}
