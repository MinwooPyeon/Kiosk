package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.inprogress

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BInProgressEvent
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BInProgressUiState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BpMeasurementScreenState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureInspectionNavRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResult
import kotlinx.coroutines.delay

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BP170BInProgressRoute(
    navController: NavHostController,
    viewModel: BP170BViewModel = hiltViewModel(),
    toResultScreen: (BloodPressureInspectionResult) -> Unit,
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val dataReceived by viewModel.dataReceived.collectAsState()
    val bloodPressureResult by viewModel.bloodPressureResult.collectAsState()

    var screenState by remember { mutableStateOf(BpMeasurementScreenState.Measuring) }
    var isMeasurementInProgress by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDeviceReady by remember { mutableStateOf(false) }
    var hasShownStandbyWarning by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        screenState = BpMeasurementScreenState.Measuring
        isMeasurementInProgress = true
        errorMessage = null
        isDeviceReady = false
        hasShownStandbyWarning = false
    }

    // Timeout: 2 minutes
    LaunchedEffect(isMeasurementInProgress) {
        if (isMeasurementInProgress) {
            delay(120_000)
            if (isMeasurementInProgress && bloodPressureResult == null) {
                Log.e("BP170BInProgress", "Measurement timeout - no result after 2 minutes")
                navController.navigate(BloodPressureInspectionNavRoute.Error.name) {
                    popUpTo(BloodPressureInspectionNavRoute.Start.name) { inclusive = false }
                }
            }
        }
    }

    LaunchedEffect(dataReceived, bloodPressureResult, connectionState) {
        Log.d(
            "BP170BInProgress",
            "State Update - DataReceived: $dataReceived, Result: ${bloodPressureResult != null}, Connection: $connectionState",
        )

        if (connectionState is BP170BManager.BluetoothConnectionState.ERROR) {
            Log.e(
                "BP170BInProgress",
                "Connection error: ${(connectionState as BP170BManager.BluetoothConnectionState.ERROR).message}",
            )
            navController.navigate(BloodPressureInspectionNavRoute.Error.name) {
                popUpTo(BloodPressureInspectionNavRoute.Start.name) { inclusive = false }
            }
            return@LaunchedEffect
        }

        // Result
        bloodPressureResult?.let { result ->
            Log.d(
                "BP170BInProgress",
                "Result: SBP=${result.systolic}, DBP=${result.diastolic}, Pulse=${result.pulseRate}",
            )
            if (isResultValid(result)) {
                screenState = BpMeasurementScreenState.Completed
                isMeasurementInProgress = false
                TTS.speechTTS(
                    StringProvider.getString(R.string.bp170b_measurement_completed_tts),
                    TextToSpeech.QUEUE_ADD,
                )
            } else {
                Log.e("BP170BInProgress", "Invalid result → error screen")
                navController.navigate(BloodPressureInspectionNavRoute.Error.name) {
                    popUpTo(BloodPressureInspectionNavRoute.Start.name) { inclusive = false }
                }
            }
            return@LaunchedEffect
        }

        // Status messages
        dataReceived?.let { data ->
            Log.d("BP170BInProgress", "Raw status: $data")
            when {
                data.contains("Status: During measurement", ignoreCase = true) -> {
                    isMeasurementInProgress = true
                    errorMessage = null
                    screenState = BpMeasurementScreenState.Measuring
                }
                data.contains("Error Code:", ignoreCase = true) -> {
                    isMeasurementInProgress = false
                    errorMessage = data
                    Log.e("BP170BInProgress", "Device error: $data")
                    navController.navigate(BloodPressureInspectionNavRoute.Error.name) {
                        popUpTo(BloodPressureInspectionNavRoute.Start.name) { inclusive = false }
                    }
                }
                data.contains("Checksum mismatch", ignoreCase = true) -> {
                    Log.w("BP170BInProgress", "Checksum mismatch: $data")
                    // No state change
                }
                else -> {
                    // Other statuses – no change
                }
            }
        }
    }

    val uiState = BP170BInProgressUiState(screenState = screenState)

    BP170BInProgressScreen(
        state = uiState,
        onEvent = { ev ->
            when (ev) {
                BP170BInProgressEvent.CheckResultClicked -> {
                    bloodPressureResult?.let { toResultScreen(it) } ?: run {
                        Log.e("BP170BInProgress", "Completed but result is null → error")
                        navController.navigate(BloodPressureInspectionNavRoute.Error.name) {
                            popUpTo(BloodPressureInspectionNavRoute.Start.name) { inclusive = false }
                        }
                    }
                }
            }
        },
    )
}

private fun isResultValid(result: BloodPressureInspectionResult): Boolean {
    val systolic = result.systolic
    val diastolic = result.diastolic
    val pulseRate = result.pulseRate

    val isBpValid = systolic in 0..300 && diastolic in 0..300
    val isPulseValid = pulseRate in 0..250

    Log.d("BP170BValidation", "Systolic: $systolic, Diastolic: $diastolic, Pulse: $pulseRate")
    Log.d("BP170BValidation", "isBpValid: $isBpValid, isPulseValid: $isPulseValid")

    return isBpValid && isPulseValid
}