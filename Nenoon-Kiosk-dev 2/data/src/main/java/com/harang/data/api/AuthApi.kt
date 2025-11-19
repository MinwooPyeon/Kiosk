package com.harang.data.api

import com.harang.data.model.dto.request.*
import com.harang.data.model.dto.response.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {
    // location (기관) 로그인
    @GET("api/v1/location/signin")
    suspend fun sendLocationSignInData(
        @Query("id") id: String,
        @Query("pw") pw: String,
    ): Response<SendLocationSignInDataResponse>

    // 사용자 로그인
    @POST("api/v1/users/login")
    suspend fun sendUserSignInData(
        @Body body: SendUserSignInDataRequest,
    ): Response<SendUserSignInDataResponse>

    // 사용자 회원가입
    @POST("api/v1/users/register")
    suspend fun sendUserSignUpData(
        @Body body: SendSignUpDataRequest,
    ): Response<SendSignUpDataResponse>

    // 얼굴로 사용자 로그인
    @POST("api/v1/users/vector-login")
    suspend fun signInUserWithFace(
        @Body body: SendUserFaceSignInDataRequest,
    ): Response<SendUserSignInDataResponse>

    // 얼굴 데이터 업데이트
    @PATCH("api/v1/users/vector")
    suspend fun updateFaceData(
        @Header("authorization") token: String,
        @Body body: SendUserFaceUpdateDataRequest,
    ): Response<SendUserFaceUpdateDataResponse>

    // QR 코드 업데이트
    @Multipart
    @POST("api/v1/users/qr-image/upload")
    suspend fun updateQrCode(
        @Part qrCode: MultipartBody.Part,
    ): Response<SendUserQrCodeUpdateDataResponse>

    // QR 코드 이미지 가져오기
    @GET("api/v1/users/qr-image/{filename}")
    suspend fun getQrCode(
        @Path("filename") filename: String,
    ): Response<ResponseBody>

    // QR 코드 URL 조회
    @GET("api/v1/users/qr-image")
    suspend fun getQrUrl(
        @Header("authorization") token: String,
    ): Response<SendUserQrCodeUrlResponse>

    // 사용자 프로필 조회
    @GET("api/v1/users/profile")
    suspend fun getUserProfile(
        @Header("authorization") token: String,
    ): Response<GetUserProfileResponse>
}