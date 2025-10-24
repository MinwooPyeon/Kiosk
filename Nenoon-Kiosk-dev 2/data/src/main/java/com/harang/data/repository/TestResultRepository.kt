package com.harang.data.repository

import com.harang.data.datasource.TestResultRemoteDataSource
import com.harang.data.model.GetCompoundTestResult
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TestResultRepository(
    private val remoteDataSource: TestResultRemoteDataSource,
) {
    suspend fun sendPresbyopiaTestResult(
        token: String?,
        request: SendPresbyopiaTestResultRequest,
    ): SendPresbyopiaTestResultResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.sendPresbyopiaTestResult(request, token)
        }
    }

    suspend fun sendVisualAcuityTestResult(
        token: String?,
        request: SendShortVisualAcuityTestResultRequest,
    ): SendShortVisualAcuityTestResultResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.sendVisualAcuityTestResult(request, token)
        }
    }

    suspend fun sendAmslerGridTestResult(
        token: String?,
        request: SendAmslerGridTestResultRequest,
    ): SendAmslerGridTestResultResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.sendAmslerGridTestResult(request, token)
        }
    }

    suspend fun sendMChartTestResult(
        token: String?,
        request: SendMChartTestResultRequest,
    ): SendMChartTestResultResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.sendMChartTestResult(request, token)
        }
    }

    suspend fun sendDementiaTestResult(
        token: String?,
        request: SendDementiaTestResultRequest,
    ): SendDementiaTestResultResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.sendDementiaTestResult(request, token)
        }
    }

    suspend fun sendGripStrengthTestResult(
        token: String?,
        request: SendGripStrengthTestResultRequest,
    ): SendGripStrengthTestResultResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.sendGripStrengthTestResult(request, token)
        }
    }

    suspend fun sendBloodPressureTestResult(
        token: String?,
        request: SendBloodPressureTestResultRequest,
    ): SendBloodPressureTestResultResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.sendBloodPressureTestResult(request, token)
        }
    }

    suspend fun sendPulmonaryFunctionTestResult(
        token: String?,
        request: SendPulmonaryFunctionTestResultRequest,
    ): SendPulmonaryFunctionTestResultResponse? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.sendPulmonaryFunctionTestResult(request, token)
        }
    }

    suspend fun getCompoundTestResult(token: String): GetCompoundTestResult? {
        return withContext(Dispatchers.IO) {
            remoteDataSource.getCompoundTestResult(token)
        }
    }
}
