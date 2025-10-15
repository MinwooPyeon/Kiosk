package com.harang.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SendSignUpDataResponse (
    @SerializedName("responseId")
    val responseId: String,
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("data")
    val data: Map<String, Any>
) : Serializable


data class SendSignUpDataRequest (
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String?,
    @SerializedName("password")
    val password: String,
    @SerializedName("loginId")
    val loginId: String,
    @SerializedName("pid")
    val pid: Long,
    @SerializedName("vector")
    val vector: String?,
    @SerializedName("qrUrl")
    val qrUrl: String?,
) : Serializable