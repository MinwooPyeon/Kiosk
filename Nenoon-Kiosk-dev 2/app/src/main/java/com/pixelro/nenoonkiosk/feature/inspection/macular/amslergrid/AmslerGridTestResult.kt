package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid

data class AmslerGridTestResult(
    val leftEyeDisorderType: List<MacularDisorderType> = listOf(),
    val rightEyeDisorderType: List<MacularDisorderType> = listOf()
)
