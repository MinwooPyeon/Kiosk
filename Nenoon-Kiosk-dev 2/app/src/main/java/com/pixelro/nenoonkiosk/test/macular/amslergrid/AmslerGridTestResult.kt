package com.pixelro.nenoonkiosk.test.macular.amslergrid

data class AmslerGridTestResult(
    val leftEyeDisorderType: List<MacularDisorderType> = listOf(),
    val rightEyeDisorderType: List<MacularDisorderType> = listOf()
)
