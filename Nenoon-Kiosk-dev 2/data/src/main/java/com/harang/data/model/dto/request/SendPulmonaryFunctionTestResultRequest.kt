package com.harang.data.model.dto.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendPulmonaryFunctionTestResultRequest(
    @SerializedName("surveyId")
    val surveyId: Long,
    @SerializedName("pulmonaryPower")
    val pulmonaryPower: Double,
    @SerializedName("pulmonaryCapacity")
    val pulmonaryCapacity: Double,
    @SerializedName("pulmonaryAge")
    val pulmonaryAge: Int,
    @SerializedName("pulmonaryFunction1")
    val pulmonaryFunction1: Int = 0,
    @SerializedName("pulmonaryFunction2")
    val pulmonaryFunction2: Int = 0,
    @SerializedName("pulmonaryFunction3")
    val pulmonaryFunction3: Int = 0,
    @SerializedName("pulmonaryFunction4")
    val pulmonaryFunction4: Int = 0,
    @SerializedName("pulmonaryFunction5")
    val pulmonaryFunction5: Int = 0,
    @SerializedName("pulmonaryFunction6")
    val pulmonaryFunction6: String = "",
    @SerializedName("pulmonaryFunction7")
    val pulmonaryFunction7: String = "",
    @SerializedName("pulmonaryFunction8")
    val pulmonaryFunction8: String = "",
    @SerializedName("pulmonaryFunction9")
    val pulmonaryFunction9: String = "",
    @SerializedName("pulmonaryFunction10")
    val pulmonaryFunction10: String = "",
) : Serializable