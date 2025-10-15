package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetPastSurveyId(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
) : Serializable