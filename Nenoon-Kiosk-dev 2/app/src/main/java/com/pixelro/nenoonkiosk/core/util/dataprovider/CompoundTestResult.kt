package com.pixelro.nenoonkiosk.core.util.dataprovider

import com.harang.data.model.dto.CompoundTestResultAPI
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaViewModel
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.result.GripStrengthInspectionResultContract
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.MacularDisorderType
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.result.MChartInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.shortdistance.ShortVisualAcuityInspectionResult

data class CompoundTestResult(
    val shortVisualAcuityInspectionResult: ShortVisualAcuityInspectionResult?,
    val presbyopiaInspectionResult: PresbyopiaInspectionResult?,
    val amslerGridTestResult: AmslerGridTestResult?,
    val mChartInspectionResult: MChartInspectionResult?,
    val bloodPressureTestResult: BloodPressureInspectionResult?,
    val gripStrengthTestResult: GripStrengthInspectionResultContract?,
    val dementiaTestResult: DementiaInspectionResult?,
    val createAt: String?, // Added createAt property
) {
    // This constructor now takes CompoundTestResultAPI as a parameter
    constructor(apiResult: CompoundTestResultAPI) : this(
        shortVisualAcuityInspectionResult =
            apiResult.eyeSight?.run {
                val left = leftSight
                val right = rightSight
                if (left == null || left == "null" || right == null || right == "null") {
                    null
                } else {
                    try {
                        ShortVisualAcuityInspectionResult(left.toInt(), right.toInt())
                    } catch (e: NumberFormatException) {
                        null
                    }
                }
            },
        presbyopiaInspectionResult =
            apiResult.eyePresbyopia?.run {
                val d1 = distance1
                val d2 = distance2
                val d3 = distance3
                val dAvg = distanceAvg
                if (d1 == null || d2 == null || d3 == null || dAvg == null) {
                    null
                } else {
                    PresbyopiaInspectionResult(d1.toFloat(), d2.toFloat(), d3.toFloat(), dAvg.toFloat())
                }
            },
        amslerGridTestResult =
            apiResult.eyeAmsler?.run {
                val leftLoc = leftMacularLoc
                val rightLoc = rightMacularLoc
                if (leftLoc == null || leftLoc == "null" || rightLoc == null || rightLoc == "null") {
                    null
                } else {
                    AmslerGridTestResult(
                        leftEyeDisorderType = leftLoc.split(",").map { it.trim().toMacularDisorderType() },
                        rightEyeDisorderType = rightLoc.split(",").map { it.trim().toMacularDisorderType() },
                    )
                }
            },
        mChartInspectionResult =
            apiResult.eyeMCharts?.run {
                val leftVer = leftEyeVer
                val rightVer = rightEyeVer
                val leftHor = leftEyeHor
                val rightHor = rightEyeHor
                if (leftVer == null || leftVer == "null" || rightVer == null || rightVer == "null" || leftHor == null || leftHor == "null" || rightHor == null || rightHor == "null") {
                    null
                } else {
                    try {
                        MChartInspectionResult(leftVer.toInt(), rightVer.toInt(), leftHor.toInt(), rightHor.toInt())
                    } catch (e: NumberFormatException) {
                        null
                    }
                }
            },
        bloodPressureTestResult =
            apiResult.bloodPressure?.run {
                val sys = systolic
                val dias = diastolic
                val pulse = pulseRate
                if (sys == null || dias == null || pulse == null) {
                    null
                } else {
                    try {
                        BloodPressureInspectionResult(sys.toInt(), dias.toInt(), pulse.toInt())
                    } catch (e: NumberFormatException) {
                        null
                    }
                }
            },
        gripStrengthTestResult =
            apiResult.gripStrength?.run {
                val right = rightGrip
                val left = leftGrip
                if (right == null || left == null) {
                    null
                } else {
                    GripStrengthInspectionResultContract(right, left)
                }
            },
        dementiaTestResult =
            apiResult.dementia?.run {
                val scores =
                    listOf(
                        dementiaQuestion1,
                        dementiaQuestion2,
                        dementiaQuestion3,
                        dementiaQuestion4,
                        dementiaQuestion5,
                    )
                if (scores.any { it == null || it == "null" }) {
                    null
                } else {
                    DementiaInspectionResult(scores.map { it!!.toDementiaAnswer() })
                }
            },
        createAt = apiResult.createAt, // Assigning the createAt value
    )
}

private fun String.toDementiaAnswer(): DementiaViewModel.DementiaAnswer {
    return when (this) {
        "Yes" -> DementiaViewModel.DementiaAnswer.Yes
        "No" -> DementiaViewModel.DementiaAnswer.No
        else -> DementiaViewModel.DementiaAnswer.None
    }
}

private fun String.toMacularDisorderType(): MacularDisorderType {
    return when (this) {
        "n" -> MacularDisorderType.Normal
        "d" -> MacularDisorderType.Distorted
        "b" -> MacularDisorderType.Blacked
        "w" -> MacularDisorderType.Whited
        else -> MacularDisorderType.Normal
    }
}
