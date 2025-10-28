package com.pixelro.nenoonkiosk.feature.facedetection

import android.graphics.PointF
import android.graphics.Rect

object FaceDetectionContract {
    data class State(
        // 얼굴 감지
        val isFaceDetected: Boolean = false,
        val faceBoundingBox: Rect? = null,
        val leftEyePosition: PointF? = null,
        val rightEyePosition: PointF? = null,
        
        // 얼굴 각도
        val rotX: Float = 0f,
        val rotY: Float = 0f,
        val rotZ: Float = 0f,
        
        // 눈 떠짐
        val leftEyeOpenProbability: Float? = null,
        val rightEyeOpenProbability: Float? = null,
        
        // 텍스트 인식
        val isNenoonTextDetected: Boolean = false,
        val textBoundingBox: Rect? = null,
        
        // 시선 추적 (확장 예정)
        val isFacingForward: Boolean = false,
        val gazeScore: Float = 0f
    )

    sealed class SideEffect {
        data class ShowToast(val message: String) : SideEffect()
        data class SpeakTTS(val text: String) : SideEffect()
    }
}
