package com.harang.data.api

import com.harang.data.model.AmslerTestResult
import com.harang.data.model.BloodPressureTestResult
import com.harang.data.model.DementiaTestResult
import com.harang.data.model.GetCompoundTestResult
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
import com.harang.data.model.SendMChartTestResultRequest
import com.harang.data.model.SendMChartTestResultResponse
import com.harang.data.model.SendPresbyopiaTestResultRequest
import com.harang.data.model.SendPresbyopiaTestResultResponse
import com.harang.data.model.SendPulmonaryFunctionTestResultRequest
import com.harang.data.model.SendPulmonaryFunctionTestResultResponse
import com.harang.data.model.SendShortVisualAcuityTestResultRequest
import com.harang.data.model.SendShortVisualAcuityTestResultResponse
import com.harang.data.model.SightTestResult
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
