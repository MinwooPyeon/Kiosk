package com.pixelro.nenoonkiosk.test.bloodPressure.BP170B

import android.annotation.SuppressLint
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.manager.BP170BManager
import com.pixelro.nenoonkiosk.feature.screen.iotdevice.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.test.bloodPressure.BloodPressureTestResult
import com.pixelro.nenoonkiosk.test.bloodPressure.BloodPressureTestScreen
import com.pixelro.nenoonkiosk.feature.components.PrimaryButton
import com.pixelro.nenoonkiosk.feature.components.ProgressIndicator
import com.pixelro.nenoonkiosk.feature.components.StyledText
import com.pixelro.nenoonkiosk.feature.components.TextStyle

enum class BpMeasurementScreenState {
    Measuring,
    Completed
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

    // Effect to observe dataReceived for general status updates
    LaunchedEffect(dataReceived) {
        dataReceived?.let { data ->
            Log.d("BP170BInProgress", "Data received (raw status): $data")
            when {
                data.contains("Status: During measurement", ignoreCase = true) -> {
                    isMeasurementInProgress = true
                    errorMessage = null
                }
                data.contains("Error Code:", ignoreCase = true) -> {
                    isMeasurementInProgress = false
                    errorMessage = data
                }
                // "Status: Measurement complete" is now handled by bloodPressureResult != null
                else -> {
                    // Other status messages, don't change core measurement state
                }
            }
        }
    }

    // Effect to observe bloodPressureResult for measurement completion
    LaunchedEffect(bloodPressureResult) {
        bloodPressureResult?.let { result ->
            Log.d("BP170BInProgress", "Blood pressure result received: SBP=${result.systolic}, DBP=${result.diastolic}, Pulse=${result.pulseRate}")
            if (isResultValid(result) && errorMessage.isNullOrBlank() && connectionState !is BP170BManager.BluetoothConnectionState.ERROR) {
                currentMeasurementScreenState = BpMeasurementScreenState.Completed
                isMeasurementInProgress = false // Ensure measurement state is off
                TTS.speechTTS(StringProvider.getString(R.string.bp170b_measurement_completed_tts), android.speech.tts.TextToSpeech.QUEUE_ADD)
            } else {
                Log.e("BP170BInProgress", "Invalid blood pressure result or existing error. Navigating to error screen.")
                navController.navigate(BloodPressureTestScreen.Error.name) {
                    popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
                }
            }
        }
    }

    // Main logic for screen state transitions based on all observed states
    LaunchedEffect(isMeasurementInProgress, errorMessage, connectionState, bloodPressureResult) {
        Log.d("BP170BInProgress", "State Update - InProgress: $isMeasurementInProgress, Result: ${bloodPressureResult != null}, Error: $errorMessage, Connection: $connectionState")

        if (bloodPressureResult != null) {
            // Measurement is complete if we have a valid result
            currentMeasurementScreenState = BpMeasurementScreenState.Completed
            isMeasurementInProgress = false // Explicitly set to false
        } else if (isMeasurementInProgress) {
            // If the manager reports "During measurement" status
            currentMeasurementScreenState = BpMeasurementScreenState.Measuring
        } else if (errorMessage != null && errorMessage!!.isNotBlank()) {
            // If an explicit error message is present
            navController.navigate(BloodPressureTestScreen.Error.name) {
                popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
            }
        } else if (currentMeasurementScreenState == BpMeasurementScreenState.Measuring && bloodPressureResult == null) {
            // If we were in Measuring state but no result arrived, and no explicit error, it means measurement failed or stopped unexpectedly.
            Log.e("BP170BInProgress", "Measurement unexpectedly stopped or failed (no result received).")
            navController.navigate(BloodPressureTestScreen.Error.name) {
                popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
            }
            TTS.speechTTS(StringProvider.getString(R.string.bp170b_measurement_error_tts), android.speech.tts.TextToSpeech.QUEUE_ADD)
        } else {
            // Default to instructions if no other state is active
            currentMeasurementScreenState = BpMeasurementScreenState.Measuring
        }
    }

    // Effect to handle connection loss/error
    LaunchedEffect(connectionState) {
        if (connectionState == BP170BManager.BluetoothConnectionState.DISCONNECTED || connectionState is BP170BManager.BluetoothConnectionState.ERROR) {
            if (currentMeasurementScreenState == BpMeasurementScreenState.Measuring || currentMeasurementScreenState == BpMeasurementScreenState.Measuring) {
                isMeasurementInProgress = false
                errorMessage = (connectionState as? BP170BManager.BluetoothConnectionState.ERROR)?.message
                TTS.speechTTS(StringProvider.getString(R.string.bp170b_connection_lost_tts), android.speech.tts.TextToSpeech.QUEUE_ADD)
                navController.navigate(BloodPressureTestScreen.Error.name) {
                    popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(40.dp)
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
                    text = StringProvider.getString(R.string.bp170b_check_results)
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