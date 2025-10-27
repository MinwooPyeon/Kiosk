package com.harang.data.model.dto.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SendUserSignInDataRequest(
    @SerializedName("loginId")
    val loginId: String,
    @SerializedName("password")
    val password: String,
) : Serializable