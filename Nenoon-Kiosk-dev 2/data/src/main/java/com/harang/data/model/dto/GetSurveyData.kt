package com.harang.data.model.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// API 응답 데이터 클래스
data class SendUserTestResultResponse(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>,
) : Serializable

// 시력 검사 데이터 클래스
data class SightTestResult(
    @SerializedName("testType") val testType: String,
    @SerializedName("testData") val testData: SightTestData,
)

data class SightTestData(
    @SerializedName("testType") val testType: String,
    @SerializedName("distance") val distance: Int,
    @SerializedName("leftSight") val leftSight: Int,
    @SerializedName("rightSight") val rightSight: Int,
    @SerializedName("leftPerspective") val leftPerspective: String,
    @SerializedName("rightPerspective") val rightPerspective: String,
    @SerializedName("test1") val test1: Int,
    @SerializedName("test2") val test2: Int,
    @SerializedName("test3") val test3: Int,
    @SerializedName("test4") val test4: Int,
    @SerializedName("test5") val test5: Int,
    @SerializedName("test6") val test6: String,
    @SerializedName("test7") val test7: String,
    @SerializedName("test8") val test8: String,
    @SerializedName("test9") val test9: String,
    @SerializedName("test10") val test10: String,
)

// 노안 검사 데이터 클래스
data class PresbyopiaTestResult(
    @SerializedName("testType") val testType: String,
    @SerializedName("testData") val testData: PresbyopiaTestData,
)

data class PresbyopiaTestData(
    @SerializedName("distance1") val distance1: Int,
    @SerializedName("distance2") val distance2: Int,
    @SerializedName("distance3") val distance3: Int,
    @SerializedName("distanceAvg") val distanceAvg: Int,
    @SerializedName("presbyopia1") val presbyopia1: Int,
    @SerializedName("presbyopia2") val presbyopia2: Int,
    @SerializedName("presbyopia3") val presbyopia3: Int,
    @SerializedName("presbyopia4") val presbyopia4: Int,
    @SerializedName("presbyopia5") val presbyopia5: Int,
    @SerializedName("presbyopia6") val presbyopia6: String,
    @SerializedName("presbyopia7") val presbyopia7: String,
    @SerializedName("presbyopia8") val presbyopia8: String,
    @SerializedName("presbyopia9") val presbyopia9: String,
    @SerializedName("presbyopia10") val presbyopia10: String,
)

// M차트 검사 데이터 클래스
data class MchartsTestResult(
    @SerializedName("testType") val testType: String,
    @SerializedName("testData") val testData: MchartsTestData,
)

data class MchartsTestData(
    @SerializedName("distance") val distance: Int,
    @SerializedName("leftEyeVer") val leftEyeVer: Int,
    @SerializedName("rightEyeVer") val rightEyeVer: Int,
    @SerializedName("leftEyeHor") val leftEyeHor: Int,
    @SerializedName("rightEyeHor") val rightEyeHor: Int,
    @SerializedName("mChart1") val mChart1: Int,
    @SerializedName("mChart2") val mChart2: Int,
    @SerializedName("mChart3") val mChart3: Int,
    @SerializedName("mChart4") val mChart4: Int,
    @SerializedName("mChart5") val mChart5: Int,
    @SerializedName("mChart6") val mChart6: String,
    @SerializedName("mChart7") val mChart7: String,
    @SerializedName("mChart8") val mChart8: String,
    @SerializedName("mChart9") val mChart9: String,
    @SerializedName("mChart10") val mChart10: String,
)

// 암슬러 격자 검사 데이터 클래스
data class AmslerTestResult(
    @SerializedName("testType") val testType: String,
    @SerializedName("testData") val testData: AmslerTestData,
)

data class AmslerTestData(
    @SerializedName("distance") val distance: Int,
    @SerializedName("leftMacularLoc") val leftMacularLoc: String,
    @SerializedName("rightMacularLoc") val rightMacularLoc: String,
    @SerializedName("Amsler1") val amsler1: Int,
    @SerializedName("Amsler2") val amsler2: Int,
    @SerializedName("Amsler3") val amsler3: Int,
    @SerializedName("Amsler4") val amsler4: Int,
    @SerializedName("Amsler5") val amsler5: Int,
    @SerializedName("Amsler6") val amsler6: String,
    @SerializedName("Amsler7") val amsler7: String,
    @SerializedName("Amsler8") val amsler8: String,
    @SerializedName("Amsler9") val amsler9: String,
    @SerializedName("Amsler10") val amsler10: String,
)

// 혈압 측정 데이터 클래스
data class BloodPressureTestResult(
    @SerializedName("testType") val testType: String,
    @SerializedName("testData") val testData: BloodPressureTestData,
)

data class BloodPressureTestData(
    @SerializedName("systolic") val systolic: Double,
    @SerializedName("diastolic") val diastolic: Double,
    @SerializedName("pulse_rate") val pulseRate: Double,
)

// 치매 진단 데이터 클래스
data class DementiaTestResult(
    @SerializedName("testType") val testType: String,
    @SerializedName("testData") val testData: DementiaTestData,
)

data class DementiaTestData(
    @SerializedName("dementia_question1") val dementiaQuestion1: String,
    @SerializedName("dementia_question2") val dementiaQuestion2: String,
    @SerializedName("dementia_question3") val dementiaQuestion3: String,
    @SerializedName("dementia_question4") val dementiaQuestion4: String,
    @SerializedName("dementia_question5") val dementiaQuestion5: String,
    @SerializedName("dementia_question6") val dementiaQuestion6: String,
    @SerializedName("dementia_question7") val dementiaQuestion7: String,
    @SerializedName("dementia_question8") val dementiaQuestion8: String,
    @SerializedName("dementia_question9") val dementiaQuestion9: String,
    @SerializedName("dementia_question10") val dementiaQuestion10: String,
    @SerializedName("dementia_question11") val dementiaQuestion11: String,
    @SerializedName("dementia_question12") val dementiaQuestion12: String,
    @SerializedName("dementia_question13") val dementiaQuestion13: String,
    @SerializedName("dementia_question14") val dementiaQuestion14: String,
)

// 악력 측정 데이터 클래스
data class GripStrengthTestResult(
    @SerializedName("testType") val testType: String,
    @SerializedName("testData") val testData: GripStrengthTestData,
)

data class GripStrengthTestData(
    @SerializedName("left_grip") val leftGrip: Double,
    @SerializedName("right_grip") val rightGrip: Double,
)

// 폐기능 측정 데이터 클래스
data class PulmonaryTestResult(
    @SerializedName("testType") val testType: String,
    @SerializedName("testData") val testData: PulmonaryTestData,
)

data class PulmonaryTestData(
    @SerializedName("pulmonaryPower") val pulmonaryPower: Double,
    @SerializedName("pulmonaryCapacity") val pulmonaryCapacity: Double,
    @SerializedName("pulmonaryAge") val pulmonaryAge: Int,
)
