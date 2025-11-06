package com.harang.data.api

import com.harang.data.model.dto.AmslerTestResult
import com.harang.data.model.dto.BloodPressureTestResult
import com.harang.data.model.dto.DementiaTestResult
import com.harang.data.model.dto.GetCompoundTestResult
import com.harang.data.model.dto.GripStrengthTestResult
import com.harang.data.model.dto.MchartsTestResult
import com.harang.data.model.dto.PresbyopiaTestResult
import com.harang.data.model.dto.PulmonaryTestResult
import com.harang.data.model.dto.request.*
import com.harang.data.model.dto.response.*
import com.harang.data.model.dto.SightTestResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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

    // 복합 검사 결과 가져오기
    @GET("api/v1/users/survey-results")
    suspend fun getCompoundTestResult(
        @Header("authorization") token: String,
    ): Response<GetCompoundTestResult>
}
