package com.pixelro.nenoonkiosk.feature.facedetection

import android.graphics.PointF
import android.graphics.Rect

data class FaceDetectionUiState(
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

sealed class FaceDetectionSideEffect {
    data class ShowToast(val message: String) : FaceDetectionSideEffect()
    data class SpeakTTS(val text: String) : FaceDetectionSideEffect()
}

data class IrisResult(
    // 정면을 주시하고 있는지
    val isFacingForward: Boolean,

    // 왼쪽 눈 위치
    val leftEyePosition: PointF?,

    // 오른쪽 눈 위치
    val rightEyePosition: PointF?,

    // 머리 회전 X축 각도
    val rotX: Float,

    // 머리 회전 Y축 각도
    val rotY: Float,

    // 머리 회전 Z축 각도
    val rotZ: Float,

    // 왼쪽 눈 열림
    val leftEyeOpenProbability: Float?,

    // 오른쪽 눈 열림
    val rightEyeOpenProbability: Float?,

    // 정면 주시 점수
    val gazeScore: Float
)
