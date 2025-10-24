package com.pixelro.nenoonkiosk.feature.inspection.result

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harang.data.model.SendAmslerGridTestResultRequest
import com.harang.data.model.SendBloodPressureTestResultRequest
import com.harang.data.model.SendDementiaTestResultRequest
import com.harang.data.model.SendGripStrengthTestResultRequest
import com.harang.data.model.SendMChartTestResultRequest
import com.harang.data.model.SendPresbyopiaTestResultRequest
import com.harang.data.model.SendPulmonaryFunctionTestResultRequest
import com.harang.data.model.SendShortVisualAcuityTestResultRequest
import com.harang.data.repository.TestResultRepository
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.util.dataprovider.TestType
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaTestResult
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartTestResult
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaTestResult
import com.pixelro.nenoonkiosk.feature.inspection.pulmonaryFunction.PulmonaryFunctionTestResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance.ShortVisualAcuityTestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestResultViewModel
    @Inject
    constructor(
        application: Application,
        private val testResultRepository: TestResultRepository,
    ) : AndroidViewModel(application) {
        private val _printerName = MutableStateFlow("")
        val printerName: StateFlow<String> = _printerName
        private val _printerMacAddress = MutableStateFlow("")
        val printerMacAddress: StateFlow<String> = _printerMacAddress
        private val _nemonicList = MutableStateFlow(emptyList<Pair<String, String>>())
        val nemonicList: StateFlow<List<Pair<String, String>>> = _nemonicList
        private val _printString = MutableStateFlow("")
        val printString: StateFlow<String> = _printString

        fun sendResultToServer(
            surveyId: Long,
            testType: TestType,
            testResult: Any?,
            token: String?,
        ) {
            viewModelScope.launch {
                when (testType) {
                    TestType.Presbyopia -> {
                        testResult as PresbyopiaTestResult
                        // Internal Save
                        SharedPreferencesManager.updatePresbyopiaResult(
                            firstDistance = testResult.firstDistance,
                            secondDistance = testResult.secondDistance,
                            thirdDistance = testResult.thirdDistance,
                            avgDistance = testResult.avgDistance,
                            age = null,
                        )
                        Log.d("TestResultViewModel", "Presbyopia result saved internally.")

                        // Send to Server
                        val request =
                            SendPresbyopiaTestResultRequest(
                                surveyId = surveyId,
                                distance1 = testResult.firstDistance.toInt(),
                                distance2 = testResult.secondDistance.toInt(),
                                distance3 = testResult.thirdDistance.toInt(),
                                distanceAvg = testResult.avgDistance.toInt(),
                            )
                        val response = testResultRepository.sendPresbyopiaTestResult(token, request)
                        Log.d("TestResultViewModel", "Presbyopia result sent to server: $response")
                    }
                    TestType.ShortDistanceVisualAcuity -> {
                        testResult as ShortVisualAcuityTestResult
                        // Internal Save
                        SharedPreferencesManager.updateVisualAcuityResult(
                            leftEye = testResult.leftEye,
                            rightEye = testResult.rightEye,
                        )
                        Log.d("TestResultViewModel", "ShortVisualAcuity result saved internally.")

                        // Send to Server
                        val request =
                            SendShortVisualAcuityTestResultRequest(
                                surveyId = surveyId,
                                leftSight = testResult.leftEye,
                                rightSight = testResult.rightEye,
                            )
                        val response = testResultRepository.sendVisualAcuityTestResult(token, request)
                        Log.d("TestResultViewModel", "ShortVisualAcuity result sent to server: $response")
                    }
                    TestType.AmslerGrid -> {
                        testResult as AmslerGridTestResult
                        var leftMacularLoc = testResult.leftEyeDisorderType.toString()
                        leftMacularLoc =
                            leftMacularLoc.replace(" ", "").replace("[", "")
                                .replace("]", "").replace("Normal", "n")
                                .replace("Distorted", "d").replace("Blacked", "b")
                                .replace("Whited", "w")
                        var rightMacularLoc = testResult.rightEyeDisorderType.toString()
                        rightMacularLoc =
                            rightMacularLoc.replace(" ", "").replace("[", "")
                                .replace("]", "").replace("Normal", "n")
                                .replace("Distorted", "d").replace("Blacked", "b")
                                .replace("Whited", "w")

                        // Internal Save
                        SharedPreferencesManager.updateAmslerGridResult(
                            leftMacularLoc = leftMacularLoc,
                            rightMacularLoc = rightMacularLoc,
                        )
                        Log.d("TestResultViewModel", "AmslerGrid result saved internally.")

                        // Send to Server
                        val request =
                            SendAmslerGridTestResultRequest(
                                surveyId = surveyId,
                                leftMacularLoc = leftMacularLoc,
                                rightMacularLoc = rightMacularLoc,
                            )
                        val response = testResultRepository.sendAmslerGridTestResult(token, request)
                        Log.d("TestResultViewModel", "AmslerGrid result sent to server: $response")
                    }
                    TestType.MChart -> {
                        testResult as MChartTestResult
                        // Internal Save
                        SharedPreferencesManager.updateMChartResult(
                            leftEyeVertical = testResult.leftEyeVertical.toFloat(),
                            rightEyeVertical = testResult.rightEyeVertical.toFloat(),
                            leftEyeHorizontal = testResult.leftEyeHorizontal.toFloat(),
                            rightEyeHorizontal = testResult.rightEyeHorizontal.toFloat(),
                        )
                        Log.d("TestResultViewModel", "MChart result saved internally.")

                        // Send to Server
                        val request =
                            SendMChartTestResultRequest(
                                surveyId = surveyId,
                                leftEyeVer = testResult.leftEyeVertical,
                                rightEyeVer = testResult.rightEyeVertical,
                                leftEyeHor = testResult.leftEyeHorizontal,
                                rightEyeHor = testResult.rightEyeHorizontal,
                            )
                        val response = testResultRepository.sendMChartTestResult(token, request)
                        Log.d("TestResultViewModel", "MChart result sent to server: $response")
                    }
                    TestType.Dementia -> {
                        testResult as DementiaTestResult
                        // Internal Save
                        SharedPreferencesManager.updateDementiaResult(
                            q1 = testResult.scores.getOrNull(0)?.toString(),
                            q2 = testResult.scores.getOrNull(1)?.toString(),
                            q3 = testResult.scores.getOrNull(2)?.toString(),
                            q4 = testResult.scores.getOrNull(3)?.toString(),
                            q5 = testResult.scores.getOrNull(4)?.toString(),
                            q6 = testResult.scores.getOrNull(5)?.toString(),
                            q7 = testResult.scores.getOrNull(6)?.toString(),
                            q8 = testResult.scores.getOrNull(7)?.toString(),
                            q9 = testResult.scores.getOrNull(8)?.toString(),
                            q10 = testResult.scores.getOrNull(9)?.toString(),
                            q11 = testResult.scores.getOrNull(10)?.toString(),
                            q12 = testResult.scores.getOrNull(11)?.toString(),
                            q13 = testResult.scores.getOrNull(12)?.toString(),
                            q14 = testResult.scores.getOrNull(13)?.toString(),
                        )
                        Log.d("TestResultViewModel", "Dementia result saved internally.")

                        // Send to Server
                        val request =
                            SendDementiaTestResultRequest(
                                surveyId = surveyId,
                                question1 = testResult.scores[0].toString(),
                                question2 = testResult.scores[1].toString(),
                                question3 = testResult.scores[2].toString(),
                                question4 = testResult.scores[3].toString(),
                                question5 = testResult.scores[4].toString(),
                                question6 = testResult.scores[5].toString(),
                                question7 = testResult.scores[6].toString(),
                                question8 = testResult.scores[7].toString(),
                                question9 = testResult.scores[8].toString(),
                                question10 = testResult.scores[9].toString(),
                                question11 = testResult.scores[10].toString(),
                                question12 = testResult.scores[11].toString(),
                                question13 = testResult.scores[12].toString(),
                                question14 = testResult.scores[13].toString(),
                            )
                        val response = testResultRepository.sendDementiaTestResult(token, request)
                    }
                    TestType.GripStrength -> {
                        testResult as GripStrengthTestResult
                        // Internal Save
                        SharedPreferencesManager.updateGripStrengthResult(
                            leftGrip = testResult.leftGrip.toFloat(),
                            rightGrip = testResult.rightGrip.toFloat(),
                        )
                        Log.d("TestResultViewModel", "GripStrength result saved internally.")

                        // Send to Server
                        val request =
                            SendGripStrengthTestResultRequest(
                                surveyId = surveyId,
                                leftGrip = testResult.leftGrip,
                                rightGrip = testResult.rightGrip,
                            )
                        val response = testResultRepository.sendGripStrengthTestResult(token, request)
                        Log.d("TestResultViewModel", "GripStrength result sent to server: $response")
                    }
                    TestType.BloodPressure -> {
                        testResult as BloodPressureTestResult
                        SharedPreferencesManager.updateBloodPressureResult(
                            systolic = testResult.systolic.toFloat(),
                            diastolic = testResult.diastolic.toFloat(),
                            pulseRate = testResult.pulseRate.toFloat(),
                        )
                        Log.d("TestResultViewModel", "BloodPressure result saved internally.")

                        val request =
                            SendBloodPressureTestResultRequest(
                                surveyId = surveyId,
                                systolic = testResult.systolic,
                                diastolic = testResult.diastolic,
                                pulseRate = testResult.pulseRate,
                            )
                        val response = testResultRepository.sendBloodPressureTestResult(token, request)
                        Log.d("TestResultViewModel", "BloodPressure result sent to server: $response")
                    }
                    TestType.PulmonaryFunction -> {
                        testResult as PulmonaryFunctionTestResult
                        SharedPreferencesManager.updatePulmonaryFunctionResult(
                            pulmonaryPower = testResult.pulmonaryPower,
                            pulmonaryCapacity = testResult.pulmonaryCapacity,
                            pulmonaryAge = testResult.pulmonaryAge,
                        )
                        Log.d("TestResultViewModel", "PulmonaryFunction result saved internally.")

                        val request =
                            SendPulmonaryFunctionTestResultRequest(
                                surveyId = surveyId,
                                pulmonaryPower = testResult.pulmonaryPower,
                                pulmonaryCapacity = testResult.pulmonaryCapacity,
                                pulmonaryAge = testResult.pulmonaryAge,
                            )
                        val response = testResultRepository.sendPulmonaryFunctionTestResult(token, request)
                        Log.d("TestResultViewModel", "PulmonaryFunction result sent to server: $response")
                    }
                    TestType.Presbyopia_Glasses -> {
                        // TODO: Implement internal save and server send for Presbyopia_Glasses
                        Log.d("TestResultViewModel", "Presbyopia_Glasses result not yet implemented for internal save or server send.")
                    }
                    TestType.Concentration_Glasses -> {
                        // TODO: Implement internal save and server send for Concentration_Glasses
                        Log.d("TestResultViewModel", "Concentration_Glasses result not yet implemented for internal save or server send.")
                    }
                    TestType.None,
                    TestType.LongDistanceVisualAcuity,
                    TestType.ChildrenVisualAcuity,
                    -> {
                        Log.d("TestResultViewModel", "Unhandled TestType: $testType")
                    }

//                TestType.Phoria -> {
//                    testResult as SawiTestResult
//                    // Internal Save
//                    SharedPreferencesManager.updateSawiResult(
//                        hDev = testResult.hDev,
//                        vDev = testResult.vDev,
//                        suppression = testResult.suppression
//                    )
//                    Log.d("TestResultViewModel", "Phoria result saved internally.")
//
//                    // TODO: Implement server send for Phoria
//                    // val request = SendPhoriaTestResultRequest(...)
//                    // val response = testResultRepository.sendPhoriaTestResult(token, request)
//                }
//                TestType.Aniseikonia -> {
//                    testResult as FudoTestResult
//                    // Internal Save
//                    SharedPreferencesManager.updateFudoResult(
//                        signedDifferencePercent = testResult.signedDifferencePercent
//                    )
//                    Log.d("TestResultViewModel", "Aniseikonia result saved internally.")
//
//                    // TODO: Implement server send for Aniseikonia
//                    // val request = SendAniseikoniaTestResultRequest(...)
//                    // val response = testResultRepository.sendAniseikoniaTestResult(token, request)
//                }
                    TestType.Phoria -> TODO()
                    TestType.Aniseikonia -> TODO()
                }
            }
        }
    }
