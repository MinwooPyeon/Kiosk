package com.harang.data.api

import com.harang.data.model.dto.response.GetPastSurveyId
import com.harang.data.model.dto.request.*
import com.harang.data.model.dto.response.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SurveyApi {
    // 결과 열 생성
    @POST("api/v1/users/survey-result")
    suspend fun generateResultsChart(
        @Header("authorization") token: String,
    ): Response<GetPastSurveyId>

    // 설문 상태 확인
    @GET("api/v1/users/survey-status")
    suspend fun getSurveyStatus(
        @Header("authorization") token: String,
    ): Response<GetPastSurveyId>

    // 설문 데이터 전송 (사용자 토큰 포함)
    @POST("api/v1/survey/user")
    suspend fun sendSurveyData(
        @Header("authorization") token: String,
        @Body body: SendSurveyDataRequest,
    ): Response<SendSurveyDataResponse>

    // 설문 데이터 전송
    @POST("api/v1/survey")
    suspend fun sendSurveyData(
        @Body body: SendSurveyDataRequest,
    ): Response<SendSurveyDataResponse>
}