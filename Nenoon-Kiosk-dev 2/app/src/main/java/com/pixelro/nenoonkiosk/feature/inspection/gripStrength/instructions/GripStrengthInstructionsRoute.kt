package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.instructions

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
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthInspectionNavRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun GripStrengthInstructionsRoute(
    navController: NavHostController,
) {
    var ttsSpeaking by remember { mutableStateOf(false) }
    val ttsWarningActive = remember { MutableStateFlow(false) }


    LaunchedEffect(Unit) {
        TTS.stopTTS()
        TTS.forceKoreanLanguage()


        suspend fun trySpeak() {
            if (TTS.isInitialized()) {
                TTS.speechTTS(StringProvider.getString(R.string.tts_ingrip_instructions), TextToSpeech.QUEUE_ADD)
                ttsSpeaking = true
                TTS.setOnDoneListener { ttsSpeaking = false }
            } else {
                delay(500)
                if (TTS.isInitialized()) {
                    TTS.forceKoreanLanguage()
                    TTS.speechTTS(StringProvider.getString(R.string.tts_ingrip_instructions), TextToSpeech.QUEUE_ADD)
                    ttsSpeaking = true
                    TTS.setOnDoneListener { ttsSpeaking = false }
                }
            }
        }
        trySpeak()
    }


    val uiState = GripInstructionsUiState(ttsSpeaking = ttsSpeaking)


    GripStrengthInstructionsScreen(
        state = uiState,
        ttsWarningActive = ttsWarningActive,
        onEvent = { ev ->
            when (ev) {
                GripInstructionsEvent.StartPressed -> {
                    if (ttsSpeaking) {
                        ttsWarningActive.update { true }
                    } else {
                        navController.navigate(GripStrengthInspectionNavRoute.InProgress.name)
                    }
                }
            }
        },
    )
}