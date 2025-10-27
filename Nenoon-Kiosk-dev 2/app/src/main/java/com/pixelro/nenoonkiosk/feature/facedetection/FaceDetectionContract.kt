package com.pixelro.nenoonkiosk.feature.facedetection

import android.graphics.PointF
import android.graphics.Rect

// Face Detection 기능 구현

object FaceDetectionContract {
    sealed class Intent {
        data class UpdateFaceData(
            val boundingBox: Rect,
            val leftEyePosition: PointF?,
            val rightEyePosition: PointF?,
            val rotX: Float,
            val rotY: Float,
            val rotZ: Float,
            val leftEyeOpenProbability: Float?,
            val rightEyeOpenProbability: Float?
        ) : Intent()

        // 텍스트 인식 데이터 업데이트
        data class UpdateTextData(val textBoundingBox: Rect?) : Intent()

        // 얼굴 감지 여부 업데이트
        data class UpdateIsFaceDetected(val isDetected: Boolean) : Intent()
        
        // Nenoon 텍스트 감지 여부 업데이트
        data class UpdateIsNenoonTextDetected(val isDetected: Boolean) : Intent()
        
        // 시선 추적 결과 업데이트
        data class UpdateGazeResult(val irisResult: IrisResult) : Intent()
    }

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
        
        // 시선 추적 (내용 추가 예정)
        val isFacingForward: Boolean = false,
        val gazeScore: Float = 0f
    )

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
        data class SpeakTTS(val text: String) : Effect()
    }
}

