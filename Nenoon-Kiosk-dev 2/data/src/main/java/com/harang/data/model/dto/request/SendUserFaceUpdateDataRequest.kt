package com.harang.data.model.dto.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendUserFaceUpdateDataRequest(
    @SerializedName("vector")
    val vector: String,
) : Serializable
