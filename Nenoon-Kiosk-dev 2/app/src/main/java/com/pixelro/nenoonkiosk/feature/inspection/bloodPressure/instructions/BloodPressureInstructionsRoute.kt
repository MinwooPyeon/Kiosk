package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.instructions

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureInspectionNavRoute
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BloodPressureInstructionsRoute(navController: NavHostController) {
    var ttsSpeaking by remember { mutableStateOf(false) }
    val ttsWarningActive = MutableStateFlow(false)

    LaunchedEffect(Unit) {
        TTS.stopTTS()
        TTS.speechTTS(
            StringProvider.getString(R.string.tts_bpbio320_instructions),
            TextToSpeech.QUEUE_ADD
        )
        ttsSpeaking = true
        TTS.setOnDoneListener {
            ttsSpeaking = false
        }
    }
    val uiState = BloodPressureInstructionsUiState(ttsSpeaking = ttsSpeaking)
    BloodPressureInstructionsScreen(
        state = uiState,
        ttsWarningActive = ttsWarningActive,
        toInProgress = { navController.navigate(BloodPressureInspectionNavRoute.InProgress.name)}
    )
}