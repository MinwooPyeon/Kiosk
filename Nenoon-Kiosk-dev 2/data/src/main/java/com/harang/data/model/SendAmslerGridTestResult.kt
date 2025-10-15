package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDateTime

data class SendAmslerGridTestResultRequest(
    @SerializedName("surveyId")
    val surveyId: Long,
    @SerializedName("distance")
    val distance: Int = 30,
    @SerializedName("leftMacularLoc")
    val leftMacularLoc: String,
    @SerializedName("rightMacularLoc")
    val rightMacularLoc: String,
    @SerializedName("Amsler1")
    val Amsler1: Int = 0,
    @SerializedName("Amsler2")
    val Amsler2: Int = 0,
    @SerializedName("Amsler3")
    val Amsler3: Int = 0,
    @SerializedName("Amsler4")
    val Amsler4: Int = 0,
    @SerializedName("Amsler5")
    val Amsler5: Int = 0,
    @SerializedName("Amsler6")
    val Amsler6: String = "",
    @SerializedName("Amsler7")
    val Amsler7: String = "",
    @SerializedName("Amsler8")
    val Amsler8: String = "",
    @SerializedName("Amsler9")
    val Amsler9: String = "",
    @SerializedName("Amsler10")
    val Amsler10: String = "",
) : Serializable

data class SendAmslerGridTestResultResponse(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
) : Serializable