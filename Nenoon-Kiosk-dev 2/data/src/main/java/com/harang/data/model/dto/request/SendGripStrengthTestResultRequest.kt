package com.harang.data.model.dto.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendGripStrengthTestResultRequest(
    @SerializedName("surveyId")
    val surveyId: Long,
    @SerializedName("right_grip")
    val rightGrip: Double,
    @SerializedName("left_grip")
    val leftGrip: Double,
    @SerializedName("gripStrength1")
    val gripStrength1: Int = 0,
    @SerializedName("gripStrength2")
    val gripStrength2: Int = 0,
    @SerializedName("gripStrength3")
    val gripStrength3: Int = 0,
    @SerializedName("gripStrength4")
    val gripStrength4: Int = 0,
    @SerializedName("gripStrength5")
    val gripStrength5: Int = 0,
    @SerializedName("gripStrength6")
    val gripStrength6: String = "",
    @SerializedName("gripStrength7")
    val gripStrength7: String = "",
    @SerializedName("gripStrength8")
    val gripStrength8: String = "",
    @SerializedName("gripStrength9")
    val gripStrength9: String = "",
    @SerializedName("gripStrength10")
    val gripStrength10: String = "",
) : Serializable