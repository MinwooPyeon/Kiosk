package com.harang.data.model.dto.response

import com.google.gson.annotations.SerializedName

data class GetUserProfileResponse(
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>,
)

