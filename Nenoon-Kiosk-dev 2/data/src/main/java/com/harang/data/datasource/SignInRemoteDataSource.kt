package com.harang.data.datasource

import com.harang.data.api.AuthApi
import com.harang.data.model.GetUserProfileResponse
import com.harang.data.model.SendLocationSignInDataResponse
import com.harang.data.model.SendSignUpDataRequest
import com.harang.data.model.SendSignUpDataResponse
import com.harang.data.model.SendUserFaceSignInDataRequest
import com.harang.data.model.SendUserFaceUpdateDataRequest
import com.harang.data.model.SendUserFaceUpdateDataResponse
import com.harang.data.model.SendUserQrCodeUpdateDataResponse
import com.harang.data.model.SendUserQrCodeUrlResponse
import com.harang.data.model.SendUserSignInDataRequest
import com.harang.data.model.SendUserSignInDataResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class SignInRemoteDataSource(
    private val api: AuthApi,
) {
    suspend fun locationSignIn(
        id: String,
        pw: String,
    ): SendLocationSignInDataResponse? {
        return try {
            api.sendLocationSignInData(
                id = id,
                pw = pw,
            ).body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun userSignIn(
        id: String,
        pw: String,
    ): SendUserSignInDataResponse? {
        return try {
            val data =
                api.sendUserSignInData(
                    SendUserSignInDataRequest(
                        loginId = id,
                        password = pw,
                    ),
                ).body()
            data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun userSignUp(
        id: String,
        pw: String,
        name: String,
        email: String?,
        pid: Long,
        vector: String?,
        qrUrl: String?,
    ): SendSignUpDataResponse? {
        return try {
            api.sendUserSignUpData(
                SendSignUpDataRequest(
                    name = name,
                    email = email,
                    password = pw,
                    loginId = id,
                    pid = pid,
                    vector = vector,
                    qrUrl = qrUrl,
                ),
            ).body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun userSignInWithFace(
        vector: String,
        threshold: Double,
    ): SendUserSignInDataResponse? {
        return try {
            api.signInUserWithFace(
                SendUserFaceSignInDataRequest(
                    vector = vector,
                    threshold = threshold,
                ),
            ).body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun userUpdateFace(
        token: String,
        vector: String,
    ): SendUserFaceUpdateDataResponse? {
        return try {
            api.updateFaceData(
                token = token,
                SendUserFaceUpdateDataRequest(
                    vector = vector,
                ),
            ).body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateQrCode(qrCode: MultipartBody.Part): SendUserQrCodeUpdateDataResponse? {
        return try {
            api.updateQrCode(
                qrCode = qrCode,
            ).body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getQrCode(filename: String): ResponseBody? {
        return try {
            api.getQrCode(
                filename = filename,
            ).body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getQrUrl(token: String): SendUserQrCodeUrlResponse? {
        return try {
            val data =
                api.getQrUrl(
                    token = token,
                ).body()
            data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserProfile(token: String): GetUserProfileResponse? {
        return try {
            val data =
                api.getUserProfile(
                    token = token,
                ).body()
            data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
