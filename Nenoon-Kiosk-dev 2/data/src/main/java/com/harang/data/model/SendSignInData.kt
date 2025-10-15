package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SendLocationSignInDataResponse (
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
) : Serializable

data class SendUserSignInDataResponse (
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
) : Serializable

data class SendUserSignInDataRequest (
    @SerializedName("loginId")
    val loginId: String,
    @SerializedName("password")
    val password: String,
) : Serializable

data class SendUserFaceSignInDataRequest (
    @SerializedName("vector")
    val vector: String,
    @SerializedName("threshold")
    val threshold: Double,
) : Serializable

data class SendUserFaceUpdateDataRequest (
    @SerializedName("vector")
    val vector: String,
) : Serializable


data class SendUserFaceUpdateDataResponse (
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
) : Serializable

data class SendUserQrCodeUpdateDataResponse (
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
)

data class SendUserQrCodeUrlResponse (
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
)

data class GetUserProfileResponse (
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
)