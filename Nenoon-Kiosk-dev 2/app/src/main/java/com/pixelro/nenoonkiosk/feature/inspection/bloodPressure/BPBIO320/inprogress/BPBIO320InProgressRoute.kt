package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.inprogress

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BpMeasurementScreenState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.BPBIO320InProgressEvent
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.BPBIO320InProgressUiState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureInspectionNavRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResultContract
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ViewModel
import kotlinx.coroutines.delay

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BPBIO320InProgressRoute(
    navController: NavHostController,
    viewModel: BPBIO320ViewModel,
    toResultScreen: (BloodPressureInspectionResultContract) -> Unit,
) {
    val bloodPressureResult by viewModel.bloodPressureResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val testInProgress by viewModel.testInProgress.collectAsState()
    val isLastResultComplete by viewModel.isLastResultComplete.collectAsState()

    var screenState by remember { mutableStateOf(BpMeasurementScreenState.Measuring) }
    var isTestStarted by remember { mutableStateOf(false) }

    fun goError() {
        viewModel.resetTest()
        navController.navigate(BloodPressureInspectionNavRoute.Error.name) {
            popUpTo(BloodPressureInspectionNavRoute.Start.name) { inclusive = false }
        }
    }

    // 초기화 + 전체 타임아웃 (2분)
    LaunchedEffect(Unit) {
        isTestStarted = false
        screenState = BpMeasurementScreenState.Measuring

        delay(120_000)
        navController.navigate(BloodPressureInspectionNavRoute.Error.name) {
            popUpTo(BloodPressureInspectionNavRoute.Start.name) { inclusive = false }
        }
    }

    // 연결되면 측정 시작, 아니면 에러
    LaunchedEffect(connectionState) {
        if (connectionState == IB_SDKConst.CONNECTED && !testInProgress) {
            viewModel.startMeasurement()
            TTS.speechTTS(
                StringProvider.getString(R.string.bpbio320_measurement_in_progress),
                TextToSpeech.QUEUE_ADD,
            )
        } else if (connectionState != IB_SDKConst.CONNECTED && connectionState != IB_SDKConst.IDLE) {
            Log.e(
                "BPBIO320InProgress",
                "Cannot start measurement: device not connected. State: $connectionState",
            )
            goError()
        }
    }

    // 측정 진행/완료/오류 감시
    LaunchedEffect(isLastResultComplete, testInProgress, bloodPressureResult, errorMessage) {
        if (testInProgress) {
            isTestStarted = true
            screenState = BpMeasurementScreenState.Measuring
        } else if (isTestStarted) {
            if (isLastResultComplete) {
                val result = bloodPressureResult
                if (result != null && isResultValid(result) && errorMessage.isNullOrBlank()) {
                    screenState = BpMeasurementScreenState.Completed
                    TTS.speechTTS(
                        StringProvider.getString(R.string.bpbio320_measurement_completed),
                        TextToSpeech.QUEUE_ADD,
                    )
                } else {
                    Log.e("BPBIO320InProgress", "Invalid result or error exists → error screen")
                    goError()
                }
            } else if (!errorMessage.isNullOrBlank() && errorMessage != "null") {
                goError()
            } else if (screenState == BpMeasurementScreenState.Measuring) {
                Log.e("BPBIO320InProgress", "Test stopped/failed unexpectedly during measuring")
                goError()
            } else {
                screenState = BpMeasurementScreenState.Measuring
            }
        }
    }

    // 측정 중 연결 끊김 처리
    LaunchedEffect(connectionState) {
        if (connectionState == IB_SDKConst.DISCONNECTED || connectionState == IB_SDKConst.IDLE) {
            if (screenState == BpMeasurementScreenState.Measuring) {
                goError()
            }
        }
    }

    val uiState = BPBIO320InProgressUiState(screenState = screenState)

    BPBIO320InProgressScreen(
        state = uiState,
        onEvent = { ev ->
            when (ev) {
                BPBIO320InProgressEvent.StopPressed -> {
                    Log.e("BPBIO320InProgress", "Manual termination")
                    goError()
                }
                BPBIO320InProgressEvent.CheckResultPressed -> {
                    bloodPressureResult?.let { toResultScreen(it) }
                        ?: run {
                            Log.e("BPBIO320InProgress", "Result is null in Completed → error")
                            goError()
                        }
                }
            }
        },
    )
}

private fun isResultValid(result: BloodPressureInspectionResultContract): Boolean {
    val systolic = result.systolic
    val diastolic = result.diastolic
    val pulseRate = result.pulseRate

    val isBpValid = systolic in 30..300 && diastolic in 30..300
    val isPulseValid = pulseRate in 30..240

    Log.d("BloodPressureValidation", "Systolic: $systolic, Diastolic: $diastolic, Pulse: $pulseRate")
    Log.d("BloodPressureValidation", "isBpValid: $isBpValid, isPulseValid: $isPulseValid")

    return isBpValid && isPulseValid
}