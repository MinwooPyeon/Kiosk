package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDateTime

data class SendPresbyopiaTestResultRequest(
    @SerializedName("surveyId")
    val surveyId: Long,
    @SerializedName("distance1")
    val distance1: Int,
    @SerializedName("distance2")
    val distance2: Int,
    @SerializedName("distance3")
    val distance3: Int,
    @SerializedName("distanceAvg")
    val distanceAvg: Int,
    @SerializedName("presbyopia1")
    val presbyopia1: Int = 0,
    @SerializedName("presbyopia2")
    val presbyopia2: Int = 0,
    @SerializedName("presbyopia3")
    val presbyopia3: Int = 0,
    @SerializedName("presbyopia4")
    val presbyopia4: Int = 0,
    @SerializedName("presbyopia5")
    val presbyopia5: Int = 0,
    @SerializedName("presbyopia6")
    val presbyopia6: String = "",
    @SerializedName("presbyopia7")
    val presbyopia7: String = "",
    @SerializedName("presbyopia8")
    val presbyopia8: String = "",
    @SerializedName("presbyopia9")
    val presbyopia9: String = "",
    @SerializedName("presbyopia10")
    val presbyopia10: String = ""
) : Serializable

data class SendPresbyopiaTestResultResponse(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
) : Serializable