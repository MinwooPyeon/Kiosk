package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.instructions

import android.speech.tts.TextToSpeech
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
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureInspectionNavRoute
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BloodPressureInstructionsRoute(
    navController: NavHostController,
    bp170BViewModel: BP170BViewModel = hiltViewModel(),
) {
    var ttsSpeaking by remember { mutableStateOf(false) }
    val ttsWarningActive = MutableStateFlow(false)
    val bloodPressureMonitorType = SharedPreferencesManager.getBloodPressureMonitorType()

    // BP170B: 기기 상태 감지
    val dataReceived by bp170BViewModel.dataReceived.collectAsState()
    val isStandbyMode = remember(dataReceived) {
        bloodPressureMonitorType == SharedPreferencesManager.BloodPressureMonitorType.BP170B &&
        dataReceived?.contains("Status: Standby mode", ignoreCase = true) == true
    }

    LaunchedEffect(dataReceived) {
        if (bloodPressureMonitorType == SharedPreferencesManager.BloodPressureMonitorType.BP170B) {
            // Device ready 또는 During measurement 상태면 측정 화면으로 이동(유저가 혈압계 기기에서 시작을 누른 상태)
            if (dataReceived?.contains("Status: Device ready", ignoreCase = true) == true ||
                dataReceived?.contains("Status: During measurement", ignoreCase = true) == true) {
                TTS.stopTTS() // TTS 중지 후 화면 전환
                navController.navigate(BloodPressureInspectionNavRoute.InProgress.name)
            }
        }
    }

    LaunchedEffect(Unit) {
        TTS.stopTTS()
        val ttsMessage = when (bloodPressureMonitorType) {
            SharedPreferencesManager.BloodPressureMonitorType.BP170B ->
                StringProvider.getString(R.string.tts_bp170b_standby_instructions)
            SharedPreferencesManager.BloodPressureMonitorType.BPBIO320 ->
                StringProvider.getString(R.string.tts_bpbio320_instructions)
        }
        TTS.speechTTS(ttsMessage, TextToSpeech.QUEUE_ADD)
        ttsSpeaking = true
        TTS.setOnDoneListener {
            ttsSpeaking = false
        }
    }
    val uiState = BloodPressureInstructionsUiState(ttsSpeaking = ttsSpeaking)
    BloodPressureInstructionsScreen(
        state = uiState,
        ttsWarningActive = ttsWarningActive,
        bloodPressureMonitorType = bloodPressureMonitorType,
        isStandbyMode = isStandbyMode,
        toInProgress = { navController.navigate(BloodPressureInspectionNavRoute.InProgress.name)}
    )
}