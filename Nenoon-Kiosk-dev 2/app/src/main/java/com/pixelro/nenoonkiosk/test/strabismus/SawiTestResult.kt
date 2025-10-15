package com.pixelro.nenoonkiosk.test.strabismus

// 사위 검사 결과 데이터
data class SawiTestResult(
    val hDev: Float? = null, // 수평 편위량 (프리즘 디옵터)
    val vDev: Float? = null, // 수직 편위량 (프리즘 디옵터)
    val suppression: Int? = 0 // 0: 없음, 1: 우안, 2: 좌안
)
