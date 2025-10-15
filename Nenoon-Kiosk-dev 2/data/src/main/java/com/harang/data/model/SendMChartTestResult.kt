package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendMChartTestResultRequest(
    @SerializedName("surveyId")
    val surveyId: Long,
    @SerializedName("distance")
    val distance: Int = 30,
    @SerializedName("leftEyeVer")
    val leftEyeVer: Int,
    @SerializedName("rightEyeVer")
    val rightEyeVer: Int,
    @SerializedName("leftEyeHor")
    val leftEyeHor: Int,
    @SerializedName("rightEyeHor")
    val rightEyeHor: Int,
    @SerializedName("mChart1")
    val mChart1: Int = 0,
    @SerializedName("mChart2")
    val mChart2: Int = 0,
    @SerializedName("mChart3")
    val mChart3: Int = 0,
    @SerializedName("mChart4")
    val mChart4: Int = 0,
    @SerializedName("mChart5")
    val mChart5: Int = 0,
    @SerializedName("mChart6")
    val mChart6: String = "",
    @SerializedName("mChart7")
    val mChart7: String = "",
    @SerializedName("mChart8")
    val mChart8: String = "",
    @SerializedName("mChart9")
    val mChart9: String = "",
    @SerializedName("mChart10")
    val mChart10: String = "",
) : Serializable

data class SendMChartTestResultResponse(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
) : Serializable