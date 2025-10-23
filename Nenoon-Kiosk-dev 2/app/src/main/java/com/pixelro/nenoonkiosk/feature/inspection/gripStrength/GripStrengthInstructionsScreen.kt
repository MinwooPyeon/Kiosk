package com.pixelro.nenoonkiosk.feature.inspection.gripStrength

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.InstructionItem
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.TtsWarning
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun GripStrengthInstructionsScreen(
    navController: NavHostController,
    viewModel: InGripViewModel,
) {
    var ttsSpeaking by remember { mutableStateOf(false) }
    val ttsWarningActive = MutableStateFlow(false)

    LaunchedEffect(Unit) {
        TTS.stopTTS()
        TTS.speechTTS(StringProvider.getString(R.string.tts_ingrip_instructions), TextToSpeech.QUEUE_ADD)
        ttsSpeaking = true
        TTS.setOnDoneListener {
            ttsSpeaking = false
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(40.dp),
    ) {
        InstructionItem(
            titleText = StringProvider.getString(R.string.grip_instructions_step1_title),
            instructionText = StringProvider.getString(R.string.grip_instructions_step1_text),
        )
        Spacer(modifier = Modifier.height(40.dp))
        InstructionItem(
            titleText = StringProvider.getString(R.string.grip_instructions_step2_title),
            prefix = StringProvider.getString(R.string.grip_instructions_step2_prefix),
            accent = StringProvider.getString(R.string.grip_instructions_step2_accent),
            suffix = StringProvider.getString(R.string.grip_instructions_step2_suffix),
        )
        Spacer(modifier = Modifier.height(40.dp))
        InstructionItem(
            titleText = StringProvider.getString(R.string.grip_instructions_step3_title),
            prefix = StringProvider.getString(R.string.grip_instructions_step3_prefix),
            accent = StringProvider.getString(R.string.grip_instructions_step3_accent),
            suffix = StringProvider.getString(R.string.grip_instructions_step3_suffix),
        )

        Spacer(modifier = Modifier.weight(2f))
        PrimaryButton(
            onClick = {
                if (ttsSpeaking) {
                    ttsWarningActive.update { true }
                } else {
                    navController.navigate(GripStrengthTestScreen.InProgress.name)
                }
            },
            text = StringProvider.getString(R.string.grip_instructions_start_button),
        )
    }

    TtsWarning(ttsWarningActive)
}
