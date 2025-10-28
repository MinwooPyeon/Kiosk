package com.pixelro.nenoonkiosk.feature.facedetection

import android.graphics.PointF

// 시선 추적 결과 데이터 클래스
 
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

