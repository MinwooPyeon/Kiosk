package com.pixelro.nenoonkiosk.feature.strabismus.model

// 부등상시 검사 결과 데이터(서버 전송 미구현-TestResultViewModel)
data class AniseikoniaTestResult(
    // 망막상 크기 차이 (%). 양수: 오른눈이 큼, 음수: 왼눈이 큼
    val signedDifferencePercent: Float? = null,
)