package com.pixelro.nenoonkiosk.feature.inspection.strabismus

// 부등상시 검사 결과 데이터
data class FudoTestResult(
    // 망막상 크기 차이 (%). 양수: 오른눈이 큼, 음수: 왼눈이 큼
    val signedDifferencePercent: Float? = null
)
