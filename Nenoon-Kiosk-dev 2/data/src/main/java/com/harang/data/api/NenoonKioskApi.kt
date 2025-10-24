package com.harang.data.api

import com.harang.data.model.AmslerTestResult
import com.harang.data.model.BloodPressureTestResult
import com.harang.data.model.DementiaTestResult
import com.harang.data.model.GetCompoundTestResult
import com.harang.data.model.GetPastSurveyId
import com.harang.data.model.GetUserProfileResponse
import com.harang.data.model.GripStrengthTestResult
import com.harang.data.model.MchartsTestResult
import com.harang.data.model.PresbyopiaTestResult
import com.harang.data.model.PulmonaryTestResult
import com.harang.data.model.SendAmslerGridTestResultRequest
import com.harang.data.model.SendAmslerGridTestResultResponse
import com.harang.data.model.SendBloodPressureTestResultRequest
import com.harang.data.model.SendBloodPressureTestResultResponse
import com.harang.data.model.SendDementiaTestResultRequest
import com.harang.data.model.SendDementiaTestResultResponse
import com.harang.data.model.SendGripStrengthTestResultRequest
import com.harang.data.model.SendGripStrengthTestResultResponse
import com.harang.data.model.SendLocationSignInDataResponse
import com.harang.data.model.SendMChartTestResultRequest
import com.harang.data.model.SendMChartTestResultResponse
import com.harang.data.model.SendPresbyopiaTestResultRequest
import com.harang.data.model.SendPresbyopiaTestResultResponse
import com.harang.data.model.SendPulmonaryFunctionTestResultRequest
import com.harang.data.model.SendPulmonaryFunctionTestResultResponse
import com.harang.data.model.SendShortVisualAcuityTestResultRequest
import com.harang.data.model.SendShortVisualAcuityTestResultResponse
import com.harang.data.model.SendSignUpDataRequest
import com.harang.data.model.SendSignUpDataResponse
import com.harang.data.model.SendSurveyDataRequest
import com.harang.data.model.SendSurveyDataResponse
import com.harang.data.model.SendUserFaceSignInDataRequest
import com.harang.data.model.SendUserFaceUpdateDataRequest
import com.harang.data.model.SendUserFaceUpdateDataResponse
import com.harang.data.model.SendUserQrCodeUpdateDataResponse
import com.harang.data.model.SendUserQrCodeUrlResponse
import com.harang.data.model.SendUserSignInDataRequest
import com.harang.data.model.SendUserSignInDataResponse
import com.harang.data.model.SightTestResult
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

interface NenoonKioskApi {
    // region API Functions with Token

    // 시력 검사
    @POST("api/v1/test/result/save-with-token")
    suspend fun sendShortVisualAcuityTestResult(
        @Header("authorization") token: String,
        @Body body: SightTestResult,
    ): Response<SendShortVisualAcuityTestResultResponse>

    // 노안 검사
    @POST("api/v1/test/result/save-with-token")
    suspend fun sendPresbyopiaTestResult(
        @Header("authorization") token: String,
        @Body body: PresbyopiaTestResult,
    ): Response<SendPresbyopiaTestResultResponse>

    // M차트 검사
    @POST("api/v1/test/result/save-with-token")
    suspend fun sendMChartTestResult(
        @Header("authorization") token: String,
        @Body body: MchartsTestResult,
    ): Response<SendMChartTestResultResponse>

    // 암슬러 격자 검사
    @POST("api/v1/test/result/save-with-token")
    suspend fun sendAmslerGridResult(
        @Header("authorization") token: String,
        @Body body: AmslerTestResult,
    ): Response<SendAmslerGridTestResultResponse>

    // 혈압 측정
    @POST("api/v1/test/result/save-with-token")
    suspend fun sendBloodPressureTestResult(
        @Header("authorization") token: String,
        @Body body: BloodPressureTestResult,
    ): Response<SendBloodPressureTestResultResponse>

    // 치매 진단
    @POST("api/v1/test/result/save-with-token")
    suspend fun sendDementiaTestResult(
        @Header("authorization") token: String,
        @Body body: DementiaTestResult,
    ): Response<SendDementiaTestResultResponse>

    // 악력 측정
    @POST("api/v1/test/result/save-with-token")
    suspend fun sendGripStrengthTestResult(
        @Header("authorization") token: String,
        @Body body: GripStrengthTestResult,
    ): Response<SendGripStrengthTestResultResponse>

    // 폐기능 측정
    @POST("api/v1/test/result/save-with-token")
    suspend fun sendPulmonaryFunctionTestResult(
        @Header("authorization") token: String,
        @Body body: PulmonaryTestResult,
    ): Response<SendPulmonaryFunctionTestResultResponse>

    // 설문 데이터 전송 (사용자 토큰 포함)
    @POST("api/v1/survey/user")
    suspend fun sendSurveyData(
        @Header("authorization") token: String,
        @Body body: SendSurveyDataRequest,
    ): Response<SendSurveyDataResponse>

    // 설문 상태 확인
    @GET("api/v1/users/survey-status")
    suspend fun getSurveyStatus(
        @Header("authorization") token: String,
    ): Response<GetPastSurveyId>

    // 결과 열 생성
    @POST("api/v1/users/survey-result")
    suspend fun generateResultsChart(
        @Header("authorization") token: String,
    ): Response<GetPastSurveyId>

    // 얼굴 데이터 업데이트
    @PATCH("api/v1/users/vector")
    suspend fun updateFaceData(
        @Header("authorization") token: String,
        @Body body: SendUserFaceUpdateDataRequest,
    ): Response<SendUserFaceUpdateDataResponse>

    // QR 코드 URL 조회
    @GET("api/v1/users/qr-image")
    suspend fun getQrUrl(
        @Header("authorization") token: String,
    ): Response<SendUserQrCodeUrlResponse>

    // 암슬러 격자 검사
    @POST("api/v1/test/result/amsler")
    suspend fun sendAmslerGridResult(
        @Body body: SendAmslerGridTestResultRequest,
    ): Response<SendAmslerGridTestResultResponse>

    // 노안 검사
    @POST("api/v1/test/result/presbyopia")
    suspend fun sendPresbyopiaTestResult(
        @Body body: SendPresbyopiaTestResultRequest,
    ): Response<SendPresbyopiaTestResultResponse>

    // M차트 검사
    @POST("api/v1/test/result/mCharts")
    suspend fun sendMChartTestResult(
        @Body body: SendMChartTestResultRequest,
    ): Response<SendMChartTestResultResponse>

    // 시력 검사
    @POST("api/v1/test/result/sight")
    suspend fun sendShortVisualAcuityTestResult(
        @Body body: SendShortVisualAcuityTestResultRequest,
    ): Response<SendShortVisualAcuityTestResultResponse>

    // 치매 진단
    @POST("api/v1/test/result/dementia")
    suspend fun sendDementiaTestResult(
        @Body body: SendDementiaTestResultRequest,
    ): Response<SendDementiaTestResultResponse>

    // 악력 측정
    @POST("api/v1/test/result/gripstrength")
    suspend fun sendGripStrengthTestResult(
        @Body body: SendGripStrengthTestResultRequest,
    ): Response<SendGripStrengthTestResultResponse>

    // 혈압 측정
    @POST("api/v1/test/result/bp")
    suspend fun sendBloodPressureTestResult(
        @Body body: SendBloodPressureTestResultRequest,
    ): Response<SendBloodPressureTestResultResponse>

    // 폐기능 측정
    @POST("api/v1/test/result/pulmonary")
    suspend fun sendPulmonaryFunctionTestResult(
        @Body body: SendPulmonaryFunctionTestResultRequest,
    ): Response<SendPulmonaryFunctionTestResultResponse>

    // 설문 데이터 전송
    @POST("api/v1/survey")
    suspend fun sendSurveyData(
        @Body body: SendSurveyDataRequest,
    ): Response<SendSurveyDataResponse>

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

    // 얼굴로 사용자 로그인
    @POST("api/v1/users/vector-login")
    suspend fun signInUserWithFace(
        @Body body: SendUserFaceSignInDataRequest,
    ): Response<SendUserSignInDataResponse>

    // 사용자 회원가입
    @POST("api/v1/users/register")
    suspend fun sendUserSignUpData(
        @Body body: SendSignUpDataRequest,
    ): Response<SendSignUpDataResponse>

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

    // 복합 검사 결과 가져오기
    @GET("api/v1/users/survey-results")
    suspend fun getCompoundTestResult(
        @Header("authorization") token: String,
    ): Response<GetCompoundTestResult>

    // 사용자 프로필 조회
    @GET("api/v1/users/profile")
    suspend fun getUserProfile(
        @Header("authorization") token: String,
    ): Response<GetUserProfileResponse>
}
