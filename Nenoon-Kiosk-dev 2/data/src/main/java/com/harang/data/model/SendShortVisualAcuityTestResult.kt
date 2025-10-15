package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDateTime

data class SendShortVisualAcuityTestResultRequest(
    @SerializedName("surveyId")
    val surveyId: Long,
    @SerializedName("testType")
    val testType: String = "short",
    @SerializedName("distance")
    val distance: Int = 40,
    @SerializedName("leftSight")
    val leftSight: Int,
    @SerializedName("rightSight")
    val rightSight: Int,
    @SerializedName("leftPerspective")
    val leftPerspective: String = "normal",
    @SerializedName("rightPerspective")
    val rightPerspective: String = "normal",
    @SerializedName("test1")
    val test1: Int = 0,
    @SerializedName("test2")
    val test2: Int = 0,
    @SerializedName("test3")
    val test3: Int = 0,
    @SerializedName("test4")
    val test4: Int = 0,
    @SerializedName("test5")
    val test5: Int = 0,
    @SerializedName("test6")
    val test6: String = "",
    @SerializedName("test7")
    val test7: String = "",
    @SerializedName("test8")
    val test8: String = "",
    @SerializedName("test9")
    val test9: String = "",
    @SerializedName("test10")
    val test10: String = "",
) : Serializable

data class SendShortVisualAcuityTestResultResponse(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
) : Serializable