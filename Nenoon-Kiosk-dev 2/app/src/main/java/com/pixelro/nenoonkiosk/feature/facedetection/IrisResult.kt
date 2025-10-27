package com.pixelro.nenoonkiosk.feature.facedetection

import android.graphics.PointF

// 시선 추적 결과 데이터 클래스
 
data class IrisResult(
    // 왼쪽 눈 위치
    val leftEyePosition: PointF?,

    // 오른쪽 눈 위치
    val rightEyePosition: PointF?
)

