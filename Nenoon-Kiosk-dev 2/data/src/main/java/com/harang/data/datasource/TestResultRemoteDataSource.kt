package com.harang.data.datasource

import android.util.Log
import com.harang.data.api.NenoonKioskApi
import com.harang.data.model.dto.*
import com.harang.data.model.dto.request.*
import com.harang.data.model.dto.response.*

class TestResultRemoteDataSource(
    private val api: NenoonKioskApi,
) {
    suspend fun sendPresbyopiaTestResult(
        request: SendPresbyopiaTestResultRequest,
        token: String?,
    ): SendPresbyopiaTestResultResponse? {
        return try {
            if (token != null) {
                val testData =
                    PresbyopiaTestData(
                        distance1 = request.distance1,
                        distance2 = request.distance2,
                        distance3 = request.distance3,
                        distanceAvg = request.distanceAvg,
                        presbyopia1 = request.presbyopia1,
                        presbyopia2 = request.presbyopia2,
                        presbyopia3 = request.presbyopia3,
                        presbyopia4 = request.presbyopia4,
                        presbyopia5 = request.presbyopia5,
                        presbyopia6 = request.presbyopia6,
                        presbyopia7 = request.presbyopia7,
                        presbyopia8 = request.presbyopia8,
                        presbyopia9 = request.presbyopia9,
                        presbyopia10 = request.presbyopia10,
                    )
                val newRequest = PresbyopiaTestResult(testType = "presbyopia", testData = testData)
                api.sendPresbyopiaTestResult(token, newRequest).body()
            } else {
                api.sendPresbyopiaTestResult(request).body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun sendVisualAcuityTestResult(
        request: SendShortVisualAcuityTestResultRequest,
        token: String?,
    ): SendShortVisualAcuityTestResultResponse? {
        return try {
            if (token != null) {
                val testData =
                    SightTestData(
                        testType = request.testType,
                        distance = request.distance,
                        leftSight = request.leftSight,
                        rightSight = request.rightSight,
                        leftPerspective = request.leftPerspective,
                        rightPerspective = request.rightPerspective,
                        test1 = request.test1,
                        test2 = request.test2,
                        test3 = request.test3,
                        test4 = request.test4,
                        test5 = request.test5,
                        test6 = request.test6,
                        test7 = request.test7,
                        test8 = request.test8,
                        test9 = request.test9,
                        test10 = request.test10,
                    )
                val newRequest = SightTestResult(testType = "sight", testData = testData)
                api.sendShortVisualAcuityTestResult(token, newRequest).body()
            } else {
                api.sendShortVisualAcuityTestResult(request).body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun sendAmslerGridTestResult(
        request: SendAmslerGridTestResultRequest,
        token: String?,
    ): SendAmslerGridTestResultResponse? {
        return try {
            if (token != null) {
                val testData =
                    AmslerTestData(
                        distance = request.distance,
                        leftMacularLoc = request.leftMacularLoc,
                        rightMacularLoc = request.rightMacularLoc,
                        amsler1 = request.Amsler1,
                        amsler2 = request.Amsler2,
                        amsler3 = request.Amsler3,
                        amsler4 = request.Amsler4,
                        amsler5 = request.Amsler5,
                        amsler6 = request.Amsler6,
                        amsler7 = request.Amsler7,
                        amsler8 = request.Amsler8,
                        amsler9 = request.Amsler9,
                        amsler10 = request.Amsler10,
                    )
                val newRequest = AmslerTestResult(testType = "amsler", testData = testData)
                api.sendAmslerGridResult(token, newRequest).body()
            } else {
                api.sendAmslerGridResult(request).body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun sendMChartTestResult(
        request: SendMChartTestResultRequest,
        token: String?,
    ): SendMChartTestResultResponse? {
        return try {
            if (token != null) {
                val testData =
                    MchartsTestData(
                        distance = request.distance,
                        leftEyeVer = request.leftEyeVer,
                        rightEyeVer = request.rightEyeVer,
                        leftEyeHor = request.leftEyeHor,
                        rightEyeHor = request.rightEyeHor,
                        mChart1 = request.mChart1,
                        mChart2 = request.mChart2,
                        mChart3 = request.mChart3,
                        mChart4 = request.mChart4,
                        mChart5 = request.mChart5,
                        mChart6 = request.mChart6,
                        mChart7 = request.mChart7,
                        mChart8 = request.mChart8,
                        mChart9 = request.mChart9,
                        mChart10 = request.mChart10,
                    )
                val newRequest = MchartsTestResult(testType = "mcharts", testData = testData)
                api.sendMChartTestResult(token, newRequest).body()
            } else {
                api.sendMChartTestResult(request).body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun sendDementiaTestResult(
        request: SendDementiaTestResultRequest,
        token: String?,
    ): SendDementiaTestResultResponse? {
        return try {
            if (token != null) {
                val testData =
                    DementiaTestData(
                        dementiaQuestion1 = request.question1,
                        dementiaQuestion2 = request.question2,
                        dementiaQuestion3 = request.question3,
                        dementiaQuestion4 = request.question4,
                        dementiaQuestion5 = request.question5,
                        dementiaQuestion6 = request.question6,
                        dementiaQuestion7 = request.question7,
                        dementiaQuestion8 = request.question8,
                        dementiaQuestion9 = request.question9,
                        dementiaQuestion10 = request.question10,
                        dementiaQuestion11 = request.question11,
                        dementiaQuestion12 = request.question12,
                        dementiaQuestion13 = request.question13,
                        dementiaQuestion14 = request.question14,
                    )
                val newRequest = DementiaTestResult(testType = "dementia", testData = testData)
                api.sendDementiaTestResult(token, newRequest).body()
            } else {
                val data = api.sendDementiaTestResult(request).body()
                data
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun sendGripStrengthTestResult(
        request: SendGripStrengthTestResultRequest,
        token: String?,
    ): SendGripStrengthTestResultResponse? {
        return try {
            if (token != null) {
                val testData =
                    GripStrengthTestData(
                        leftGrip = request.leftGrip,
                        rightGrip = request.rightGrip,
                    )
                val newRequest = GripStrengthTestResult(testType = "gripstrength", testData = testData)
                api.sendGripStrengthTestResult(token, newRequest).body()
            } else {
                api.sendGripStrengthTestResult(request).body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun sendBloodPressureTestResult(
        request: SendBloodPressureTestResultRequest,
        token: String?,
    ): SendBloodPressureTestResultResponse? {
        return try {
            if (token != null) {
                val testData =
                    BloodPressureTestData(
                        systolic = request.systolic.toDouble(),
                        diastolic = request.diastolic.toDouble(),
                        pulseRate = request.pulseRate.toDouble(),
                    )
                val newRequest = BloodPressureTestResult(testType = "bloodpressure", testData = testData)
                api.sendBloodPressureTestResult(token, newRequest).body()
            } else {
                api.sendBloodPressureTestResult(request).body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun sendPulmonaryFunctionTestResult(
        request: SendPulmonaryFunctionTestResultRequest,
        token: String?,
    ): SendPulmonaryFunctionTestResultResponse? {
        return try {
            if (token != null) {
                val testData =
                    PulmonaryTestData(
                        pulmonaryPower = request.pulmonaryPower,
                        pulmonaryCapacity = request.pulmonaryCapacity,
                        pulmonaryAge = request.pulmonaryAge,
                    )
                val newRequest = PulmonaryTestResult(testType = "pulmonary", testData = testData)
                api.sendPulmonaryFunctionTestResult(token, newRequest).body()
            } else {
                val res = api.sendPulmonaryFunctionTestResult(request).body()
                res
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getCompoundTestResult(token: String): GetCompoundTestResult? {
        return try {
            val data = api.getCompoundTestResult(token).body()
            Log.d("RRRRRRRRR", "\n$token\n->\n$data")
            data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
