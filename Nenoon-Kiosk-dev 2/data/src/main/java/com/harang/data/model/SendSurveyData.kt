package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendSurveyDataRequest(
    @SerializedName("age")
    val age: Int,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("glasses")
    val glasses: Boolean,
    @SerializedName("surgery")
    val surgery: String,
    @SerializedName("diabetes")
    val diabetes: Boolean,
    @SerializedName("pid")
    val pid: Long = -1L,
    @SerializedName("survey1")
    val survey1: Int = 0,
    @SerializedName("survey2")
    val survey2: Int = 0,
    @SerializedName("survey3")
    val survey3: Int = 0,
    @SerializedName("survey4")
    val survey4: Int = 0,
    @SerializedName("survey5")
    val survey5: Int = 0,
    @SerializedName("survey6")
    val survey6: String = "",
    @SerializedName("survey7")
    val survey7: String = "",
    @SerializedName("survey8")
    val survey8: String = "",
    @SerializedName("survey9")
    val survey9: String = "",
    @SerializedName("survey10")
    val survey10: String = "",
)

data class SendSurveyDataResponse(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>,
) : Serializable
