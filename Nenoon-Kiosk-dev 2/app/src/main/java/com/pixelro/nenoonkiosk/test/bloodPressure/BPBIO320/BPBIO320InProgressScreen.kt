package com.pixelro.nenoonkiosk.test.bloodPressure.BPBIO320

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.feature.screen.iotdevice.BPBIO320.BPBIO320ViewModel
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.test.bloodPressure.BloodPressureTestResult
import com.pixelro.nenoonkiosk.test.bloodPressure.BloodPressureTestScreen
import com.pixelro.nenoonkiosk.feature.components.PrimaryButton
import com.pixelro.nenoonkiosk.feature.components.ProgressIndicator
import com.pixelro.nenoonkiosk.feature.components.StyledText
import com.pixelro.nenoonkiosk.feature.components.TextStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class BpMeasurementScreenState {
    Measuring,
    Completed
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BPBIO320InProgressScreen(
    navController: NavHostController,
    viewModel: BPBIO320ViewModel,
    toResultScreen: (BloodPressureTestResult) -> Unit,
) {
    val bloodPressureResult by viewModel.bloodPressureResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val testInProgress by viewModel.testInProgress.collectAsState()
    val isLastResultComplete by viewModel.isLastResultComplete.collectAsState()
    var isTestStarted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var currentMeasurementScreenState by remember { mutableStateOf(BpMeasurementScreenState.Measuring) }

    fun error() {
        viewModel.resetTest()
        navController.navigate(BloodPressureTestScreen.Error.name) {
            popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
        }
    }

    LaunchedEffect(Unit) {
        isTestStarted = false
        currentMeasurementScreenState = BpMeasurementScreenState.Measuring

        coroutineScope.launch {
            delay(120000)
            navController.navigate(BloodPressureTestScreen.Error.name) {
                popUpTo(BloodPressureTestScreen.Start.name) { inclusive = false }
            }
        }
    }

    LaunchedEffect(connectionState) {
        if (connectionState == IB_SDKConst.CONNECTED && !testInProgress) {
            viewModel.startMeasurement()
            TTS.speechTTS(StringProvider.getString(R.string.bpbio320_measurement_in_progress), android.speech.tts.TextToSpeech.QUEUE_ADD)
        } else if (connectionState != IB_SDKConst.CONNECTED && connectionState != IB_SDKConst.IDLE) {
            Log.e("BloodPressureInProgress", "Cannot start measurement: device not connected. Connection State: $connectionState")
            error()
        }
    }

    LaunchedEffect(isLastResultComplete, testInProgress, bloodPressureResult, errorMessage) {
        Log.d("BloodPressureInProgress", "Test in progress: $testInProgress")

        if (testInProgress) {
            isTestStarted = true
            currentMeasurementScreenState = BpMeasurementScreenState.Measuring
        } else if (isTestStarted) {
            if (isLastResultComplete) {
                val result = bloodPressureResult
                if (result != null && isResultValid(result) && errorMessage.isNullOrBlank()) {
                    currentMeasurementScreenState = BpMeasurementScreenState.Completed
                    TTS.speechTTS(StringProvider.getString(R.string.bpbio320_measurement_completed), android.speech.tts.TextToSpeech.QUEUE_ADD)
                } else {
                    Log.e("BloodPressureInProgress", "Invalid result or existing error. Navigating to error screen.")
                    error()
                }
            } else if (errorMessage != null && errorMessage!!.isNotBlank() && errorMessage != "null") {
                error()
            } else if (currentMeasurementScreenState == BpMeasurementScreenState.Measuring) {
                Log.e("BloodPressureInProgress", "Test unexpectedly stopped or failed during measurement.")
                error()
            } else {
                currentMeasurementScreenState = BpMeasurementScreenState.Measuring
            }
        }
    }

    LaunchedEffect(connectionState) {
        if (connectionState == IB_SDKConst.DISCONNECTED || connectionState == IB_SDKConst.IDLE) {
            if (currentMeasurementScreenState == BpMeasurementScreenState.Measuring || currentMeasurementScreenState == BpMeasurementScreenState.Measuring) {
                error()
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
                    text = StringProvider.getString(R.string.bpbio320_measurement_in_progress),
                    style = TextStyle.Message,
                )
                Spacer(modifier = Modifier.weight(1f))
                ProgressIndicator()
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    onClick = {
                        Log.e("BP170BInProgress", "Manual termination")
                        error()
                    },
                    text = StringProvider.getString(R.string.bpbio320_measurement_stop)
                )
            }

            BpMeasurementScreenState.Completed -> {
                Spacer(modifier = Modifier.weight(1f))

                StyledText(
                    text = StringProvider.getString(R.string.bpbio320_measurement_completed),
                    style = TextStyle.Message,
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    onClick = {
                        bloodPressureResult?.let { toResultScreen(it) }
                            ?: run {
                                Log.e("BloodPressureInProgress", "Result is null in Completed state, cannot navigate.")
                                error()
                            }
                    },
                    text = StringProvider.getString(R.string.bpbio320_check_result)
                )
            }
        }
    }
}

private fun isResultValid(result: BloodPressureTestResult): Boolean {
    val systolic = result.systolic
    val diastolic = result.diastolic
    val pulseRate = result.pulseRate

    val isBpValid = systolic in 30..300 && diastolic in 30..300
    val isPulseValid = pulseRate in 30..240

    Log.d("BloodPressureValidation", "Systolic: $systolic, Diastolic: $diastolic, Pulse: $pulseRate")
    Log.d("BloodPressureValidation", "isBpValid: $isBpValid, isPulseValid: $isPulseValid")

    return isBpValid && isPulseValid
}