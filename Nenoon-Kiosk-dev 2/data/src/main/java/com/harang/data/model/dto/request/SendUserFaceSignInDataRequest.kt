package com.harang.data.model.dto.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendUserFaceSignInDataRequest(
    @SerializedName("vector")
    val vector: String,
    @SerializedName("threshold")
    val threshold: Double,
) : Serializable
