package com.harang.data.datasource

import com.harang.data.api.SurveyApi
import com.harang.data.model.dto.response.GetPastSurveyId
import com.harang.data.model.dto.request.*
import com.harang.data.model.dto.response.*

class SurveyRemoteDataSource(
    private val api: SurveyApi,
) {
    suspend fun sendSurveyData(
        token: String?,
        request: SendSurveyDataRequest,
    ): SendSurveyDataResponse? {
        return try {
            val res =
                if (token == null) {
                    api.sendSurveyData(request).body()
                } else {
                    api.sendSurveyData(token, request).body()
                }
            res
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getPastSurveyId(token: String): GetPastSurveyId? {
        return try {
            api.getSurveyStatus(token).body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun generateResultsChart(token: String): GetPastSurveyId? {
        return try {
            val res = api.generateResultsChart(token).body()
            res
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
