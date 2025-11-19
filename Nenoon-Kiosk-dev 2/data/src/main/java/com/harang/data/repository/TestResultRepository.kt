package com.harang.data.repository

import com.harang.data.datasource.TestResultRemoteDataSource
import com.harang.data.model.dto.GetCompoundTestResult
import com.harang.data.model.dto.request.*
import com.harang.data.model.dto.response.*
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
