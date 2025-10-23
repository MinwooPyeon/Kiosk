package com.harang.data.datasource

import com.harang.data.api.NenoonKioskApi
import com.harang.data.model.GetPastSurveyId
import com.harang.data.model.SendSurveyDataRequest
import com.harang.data.model.SendSurveyDataResponse

class SurveyRemoteDataSource(
    private val api: NenoonKioskApi,
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
