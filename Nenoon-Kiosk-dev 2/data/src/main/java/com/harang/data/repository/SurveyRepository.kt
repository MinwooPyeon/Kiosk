package com.harang.data.repository

import com.harang.data.datasource.SharedPreferencesDataSource
import com.harang.data.datasource.SurveyRemoteDataSource
import com.harang.data.model.GetPastSurveyId
import com.harang.data.model.SendSurveyDataRequest
import com.harang.data.model.SendSurveyDataResponse
import com.harang.data.vo.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SurveyRepository(
    private val remoteDataSource: SurveyRemoteDataSource,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource,
) {
    suspend fun sendSurveyData(
        token: String?,
        request: SendSurveyDataRequest,
    ): SendSurveyDataResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.sendSurveyData(token, request)
        }
    }

    suspend fun getLocationId(): Long {
        return withContext(Dispatchers.IO) {
            sharedPreferencesDataSource.getLong(Constants.PREF_LOCATION_ID)
        }
    }

    suspend fun getPastSurveyId(token: String): GetPastSurveyId? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.getPastSurveyId(token)
        }
    }

    suspend fun generateResultsChart(token: String): GetPastSurveyId? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.generateResultsChart(token)
        }
    }
}
