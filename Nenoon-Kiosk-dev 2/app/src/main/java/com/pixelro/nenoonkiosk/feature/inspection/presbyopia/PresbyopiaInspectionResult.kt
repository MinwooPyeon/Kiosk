package com.pixelro.nenoonkiosk.feature.inspection.presbyopia

data class PresbyopiaInspectionResult(
    val firstDistance: Float = 1f,
    val secondDistance: Float = 1f,
    val thirdDistance: Float = 1f,
    val avgDistance: Float = 1f,
    val age: Int = 1,
)
