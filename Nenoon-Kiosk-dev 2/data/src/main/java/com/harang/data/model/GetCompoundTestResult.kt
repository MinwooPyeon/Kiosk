package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetCompoundTestResult(
    @SerializedName("responseId")
    val responseId: String?,
    @SerializedName("createAt")
    val createAt: String?,
    @SerializedName("data")
    val data: CompoundTestResultData?,
) : Serializable

data class CompoundTestResultData(
    @SerializedName("results")
    val results: List<CompoundTestResultAPI>?,
    @SerializedName("totalCount")
    val totalCount: Int?,
    @SerializedName("message")
    val message: String?,
) : Serializable

data class CompoundTestResultAPI(
    @SerializedName("tid")
    val tid: Int?,
    @SerializedName("userLoginId")
    val userLoginId: String?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("createAt")
    val createAt: String?,
    @SerializedName("updateAt")
    val updateAt: String?,
    @SerializedName("dementia")
    val dementia: DementiaTestResultAPI?,
    @SerializedName("bloodPressure")
    val bloodPressure: BloodPressureTestResultAPI?,
    @SerializedName("gripStrength")
    val gripStrength: GripStrengthTestResultAPI?,
    @SerializedName("eyePresbyopia")
    val eyePresbyopia: PresbyopiaTestResultAPI?,
    @SerializedName("eyeAmsler")
    val eyeAmsler: AmslerTestResultAPI?,
    @SerializedName("eyeMCharts")
    val eyeMCharts: MChartsTestResultAPI?,
    @SerializedName("eyeSight")
    val eyeSight: SightTestResultAPI?,
    @SerializedName("strabismus")
    val strabismus: Any?,
    @SerializedName("pulmonary")
    val pulmonary: PulmonaryTestResultAPI?,
) : Serializable

data class DementiaTestResultAPI(
    @SerializedName("tid") val tid: Int?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("dementiaQuestion1") val dementiaQuestion1: String?,
    @SerializedName("dementiaQuestion2") val dementiaQuestion2: String?,
    @SerializedName("dementiaQuestion3") val dementiaQuestion3: String?,
    @SerializedName("dementiaQuestion4") val dementiaQuestion4: String?,
    @SerializedName("dementiaQuestion5") val dementiaQuestion5: String?,
) : Serializable

data class BloodPressureTestResultAPI(
    @SerializedName("tid") val tid: Int?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("systolic") val systolic: Double?,
    @SerializedName("diastolic") val diastolic: Double?,
    @SerializedName("pulseRate") val pulseRate: Double?,
) : Serializable

data class GripStrengthTestResultAPI(
    @SerializedName("tid") val tid: Int?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("leftGrip") val leftGrip: Double?,
    @SerializedName("rightGrip") val rightGrip: Double?,
) : Serializable

data class PresbyopiaTestResultAPI(
    @SerializedName("tid") val tid: Int?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("distance1") val distance1: Double?,
    @SerializedName("distance2") val distance2: Double?,
    @SerializedName("distance3") val distance3: Double?,
    @SerializedName("distanceAvg") val distanceAvg: Double?,
    @SerializedName("presbyopiaAge") val presbyopiaAge: Int?,
) : Serializable

data class AmslerTestResultAPI(
    @SerializedName("tid") val tid: Int?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("leftMacularLoc") val leftMacularLoc: String?,
    @SerializedName("rightMacularLoc") val rightMacularLoc: String?,
) : Serializable

data class MChartsTestResultAPI(
    @SerializedName("tid") val tid: Int?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("leftEyeVer") val leftEyeVer: String?,
    @SerializedName("rightEyeVer") val rightEyeVer: String?,
    @SerializedName("leftEyeHor") val leftEyeHor: String?,
    @SerializedName("rightEyeHor") val rightEyeHor: String?,
) : Serializable

data class SightTestResultAPI(
    @SerializedName("tid") val tid: Int?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("leftSight") val leftSight: String?,
    @SerializedName("rightSight") val rightSight: String?,
) : Serializable

data class PulmonaryTestResultAPI(
    @SerializedName("tid") val tid: Int?,
    @SerializedName("createAt") val createAt: String?,
    @SerializedName("pulmonaryPower") val pulmonaryPower: Double?,
    @SerializedName("pulmonaryCapacity") val pulmonaryCapacity: Double?,
    @SerializedName("pulmonaryAge") val pulmonaryAge: Int?,
) : Serializable
