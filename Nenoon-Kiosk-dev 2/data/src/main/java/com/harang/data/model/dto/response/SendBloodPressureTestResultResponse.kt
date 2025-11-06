package com.harang.data.model.dto.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendBloodPressureTestResultResponse(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>,
) : Serializable