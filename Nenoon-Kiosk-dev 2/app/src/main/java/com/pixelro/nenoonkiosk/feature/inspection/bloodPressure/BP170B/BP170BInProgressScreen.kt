package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestScreen
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.BP170BViewModel

enum class BpMeasurementScreenState {
    Measuring,
    Completed,
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BP170BInProgressScreen(
    navController: NavHostController,
    viewModel: BP170BViewModel = hiltViewModel(),
    toResultScreen: (BloodPressureTestResult) -> Unit, // This now expects BP170BManager.BloodPressureTestResult
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val dataReceived by viewModel.dataReceived.collectAsState() // General status messages
    val bloodPressureResult by viewModel.bloodPressureResult.collectAsState() // Actual BP/Pulse data

    var currentMeasurementScreenState by remember { mutableStateOf(BpMeasurementScreenState.Measuring) }
    var isMeasurementInProgress by remember { mutableStateOf(true) } // Renamed from simulatedTestInProgress
    var errorMessage by remember { mutableStateOf<String?>(null) } // Renamed from simulatedErrorMessage

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Reset states when the screen is first composed
        currentMeasurementScreenState = BpMeasurementScreenState.Measuring
        isMeasurementInProgress = true
        errorMessage = null
    }

    // Timeout handling
    LaunchedEffect(isMeasurementInProgress) {
        if (isMeasurementInProgress) {
            kotlinx.coroutines.delay(120000) // 2 minutes timeout
            if (isMeasurementInProgress && bloodPressureResult == null) {
                Log.e("BP170BInProgress", "Measurement timeout - no result received after 2 minutes")
                errorMessage = "측정 시간이 초과되었습니다. 다시 시도해주세요."
                navController.navigate(BloodPressureTestScreen.Error.name) {
                    popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
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
            Log.e("BP170BInProgress", "Connection error: ${(connectionState as BP170BManager.BluetoothConnectionState.ERROR).message}")
            navController.navigate(BloodPressureTestScreen.Error.name) {
                popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
            }
            return@LaunchedEffect
        }

        // Handle blood pressure result
        bloodPressureResult?.let { result ->
            Log.d(
                "BP170BInProgress",
                "Blood pressure result received: SBP=${result.systolic}, DBP=${result.diastolic}, Pulse=${result.pulseRate}",
            )
            if (isResultValid(result)) {
                currentMeasurementScreenState = BpMeasurementScreenState.Completed
                isMeasurementInProgress = false
                TTS.speechTTS(StringProvider.getString(R.string.bp170b_measurement_completed_tts), TextToSpeech.QUEUE_ADD)
            } else {
                Log.e("BP170BInProgress", "Invalid blood pressure result. Navigating to error screen.")
                navController.navigate(BloodPressureTestScreen.Error.name) {
                    popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
                }
            }
            return@LaunchedEffect
        }

        // Handle status messages
        dataReceived?.let { data ->
            Log.d("BP170BInProgress", "Data received (raw status): $data")
            when {
                data.contains("Status: During measurement", ignoreCase = true) -> {
                    isMeasurementInProgress = true
                    errorMessage = null
                    currentMeasurementScreenState = BpMeasurementScreenState.Measuring
                }
                data.contains("Error Code:", ignoreCase = true) -> {
                    isMeasurementInProgress = false
                    errorMessage = data
                    Log.e("BP170BInProgress", "Device error: $data")
                    navController.navigate(BloodPressureTestScreen.Error.name) {
                        popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
                    }
                }
                data.contains("Checksum mismatch", ignoreCase = true) -> {
                    Log.w("BP170BInProgress", "Checksum mismatch detected: $data")
                    // Don't change state for checksum mismatch, just log it
                }
                else -> {
                    // Other status messages, don't change core measurement state
                    Log.d("BP170BInProgress", "Other status message: $data")
                }
            }
        }
    }


    Column(
        modifier =
            Modifier
                .padding(40.dp)
                .fillMaxSize()
                .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (currentMeasurementScreenState) {
            BpMeasurementScreenState.Measuring -> {
                Spacer(modifier = Modifier.weight(1f))
                StyledText(
                    text = StringProvider.getString(R.string.bp170b_measuring_message),
                    style = TextStyle.Message,
                )
                Spacer(modifier = Modifier.weight(1f))
                ProgressIndicator()
                Spacer(modifier = Modifier.weight(1f))
//                PrimaryButton(
//                    onClick = {
//                        Log.e("BP170BInProgress", "Manual termination")
//                        navController.navigate(BloodPressureTestScreen.Error.name) {
//                            popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
//                        }
//                    },
//                    text = StringProvider.getString(R.string.bp170b_stop_test)
//                )
            }

            BpMeasurementScreenState.Completed -> {
                Spacer(modifier = Modifier.weight(1f))

                StyledText(
                    text = StringProvider.getString(R.string.bp170b_measurement_completed_message),
                    style = TextStyle.Message,
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    onClick = {
                        bloodPressureResult?.let { result -> // Use the actual result received
                            toResultScreen(result)
                        } ?: run {
                            // Fallback, should ideally not be reached if in BpMeasurementScreenState.Completed
                            Log.e("BP170BInProgress", "Completed state reached but bloodPressureResult is null. Navigating to error.")
                            navController.navigate(BloodPressureTestScreen.Error.name) {
                                popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
                            }
                        }
                    },
                    text = StringProvider.getString(R.string.bp170b_check_results),
                )
            }
        }
    }
}

// This function now explicitly uses BloodPressureTestResult from BP170BManager's package
private fun isResultValid(result: BloodPressureTestResult): Boolean {
    val systolic = result.systolic
    val diastolic = result.diastolic
    val pulseRate = result.pulseRate

    val isBpValid = systolic in 30..300 && diastolic in 30..300
    val isPulseValid = pulseRate in 30..240

    Log.d("BP170BValidation", "Systolic: $systolic, Diastolic: $diastolic, Pulse: $pulseRate")
    Log.d("BP170BValidation", "isBpValid: $isBpValid, isPulseValid: $isPulseValid")

    return isBpValid && isPulseValid
}
