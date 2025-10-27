package com.pixelro.nenoonkiosk.feature.facedetection

import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.abs

// Google ML Kit Face Detection 사용

object GazeDetector {
    
    // 얼굴 각도 임계값
    private const val ROT_X_THRESHOLD_UP = 20f      // 위쪽 회전 범위
    private const val ROT_X_THRESHOLD_DOWN = 25f    // 아래쪽 회전 범위
    private const val ROT_Y_THRESHOLD_LEFT = 20f    // 왼쪽 회전 범위
    private const val ROT_Y_THRESHOLD_RIGHT = 20f   // 오른쪽 회전 범위
    private const val ROT_Z_THRESHOLD = 10f         // 기울기 범위
    
    // 눈 열림 확률 임계값
    private const val EYE_OPEN_THRESHOLD = 0.3f
    
    // 시선 방향 감지
    fun detectGazeDirection(face: Face): IrisResult {
        // 1. 얼굴 LandMark
        val leftEyePosition = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
        val rightEyePosition = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position
        
        // 2. 얼굴 회전 각도
        val rotX = face.headEulerAngleX
        val rotY = face.headEulerAngleY
        val rotZ = face.headEulerAngleZ
        
        // 3. 눈 열림 확률
        val leftEyeOpenProbability = face.leftEyeOpenProbability
        val rightEyeOpenProbability = face.rightEyeOpenProbability
        
        // 4. 정면 주시 여부 판단
        val isFacingForward = checkIsFacingForward(
            rotX = rotX,
            rotY = rotY,
            rotZ = rotZ,
            leftEyeOpen = leftEyeOpenProbability,
            rightEyeOpen = rightEyeOpenProbability
        )
        
        // 5. 정면 주시 수치 (0.0 ~ 1.0)
        val gazeScore = calculateGazeScore(
            rotX = rotX,
            rotY = rotY,
            rotZ = rotZ,
            leftEyeOpen = leftEyeOpenProbability,
            rightEyeOpen = rightEyeOpenProbability
        )
        
        return IrisResult(
            isFacingForward = isFacingForward,
            leftEyePosition = leftEyePosition,
            rightEyePosition = rightEyePosition,
            rotX = rotX,
            rotY = rotY,
            rotZ = rotZ,
            leftEyeOpenProbability = leftEyeOpenProbability,
            rightEyeOpenProbability = rightEyeOpenProbability,
            gazeScore = gazeScore
        )
    }
    
    // 정면 주시 여부
    private fun checkIsFacingForward(
        rotX: Float,
        rotY: Float,
        rotZ: Float,
        leftEyeOpen: Float?,
        rightEyeOpen: Float?
    ): Boolean {
        // 1. 얼굴 각도
        val isRotXValid = if (rotX >= 0) {
            // 아래 방향
            rotX < ROT_X_THRESHOLD_DOWN
        } else {
            // 위 방향
            abs(rotX) < ROT_X_THRESHOLD_UP
        }
        
        val isRotYValid = if (rotY >= 0) {
            // 왼쪽 방향
            rotY < ROT_Y_THRESHOLD_LEFT
        } else {
            // 오른쪽 방향
            abs(rotY) < ROT_Y_THRESHOLD_RIGHT
        }
        
        val isRotZValid = abs(rotZ) < ROT_Z_THRESHOLD
        
        val isAngleValid = isRotXValid && isRotYValid && isRotZValid
        
        // 2. 눈 열림 상태
        val areEyesOpen = (leftEyeOpen ?: 0f) > EYE_OPEN_THRESHOLD &&
                          (rightEyeOpen ?: 0f) > EYE_OPEN_THRESHOLD
        
        return isAngleValid && areEyesOpen
    }
    
    // 정면 주시 점수 계산
    private fun calculateGazeScore(
        rotX: Float,
        rotY: Float,
        rotZ: Float,
        leftEyeOpen: Float?,
        rightEyeOpen: Float?
    ): Float {
        // 1. 각도
        val rotXScore = if (rotX >= 0) {
            1f - (rotX / ROT_X_THRESHOLD_DOWN).coerceIn(0f, 1f)
        } else {
            1f - (abs(rotX) / ROT_X_THRESHOLD_UP).coerceIn(0f, 1f)
        }
        
        val rotYScore = if (rotY >= 0) {
            1f - (rotY / ROT_Y_THRESHOLD_LEFT).coerceIn(0f, 1f)
        } else {
            1f - (abs(rotY) / ROT_Y_THRESHOLD_RIGHT).coerceIn(0f, 1f)
        }
        
        val rotZScore = 1f - (abs(rotZ) / ROT_Z_THRESHOLD).coerceIn(0f, 1f)
        
        val angleScore = (rotXScore + rotYScore + rotZScore) / 3f
        
        // 2. 눈 열림
        val eyeScore = ((leftEyeOpen ?: 0f) + (rightEyeOpen ?: 0f)) / 2f
        
        return angleScore * 0.6f + eyeScore * 0.4f
    }
}