package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendBloodPressureTestResultRequest(
    @SerializedName("surveyId")
    val surveyId: Long,
    @SerializedName("systolic")
    val systolic: Int,
    @SerializedName("diastolic")
    val diastolic: Int,
    @SerializedName("pulse_rate")
    val pulseRate: Int,
    @SerializedName("bloodPressure1")
    val bloodPressure1: Int = 0,
    @SerializedName("bloodPressure2")
    val bloodPressure2: Int = 0,
    @SerializedName("bloodPressure3")
    val bloodPressure3: Int = 0,
    @SerializedName("bloodPressure4")
    val bloodPressure4: Int = 0,
    @SerializedName("bloodPressure5")
    val bloodPressure5: Int = 0,
    @SerializedName("bloodPressure6")
    val bloodPressure6: String = "",
    @SerializedName("bloodPressure7")
    val bloodPressure7: String = "",
    @SerializedName("bloodPressure8")
    val bloodPressure8: String = "",
    @SerializedName("bloodPressure9")
    val bloodPressure9: String = "",
    @SerializedName("bloodPressure10")
    val bloodPressure10: String = "",
) : Serializable

data class SendBloodPressureTestResultResponse(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>,
) : Serializable
