package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.instructions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.InstructionItem
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.TtsWarning
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun GripStrengthInstructionsScreen(
    state: GripInstructionsUiState,
    ttsWarningActive: MutableStateFlow<Boolean>,
    onEvent: (GripInstructionsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
    ) {
        InstructionItem(
            titleText = stringResource(R.string.grip_instructions_step1_title),
            instructionText = stringResource(R.string.grip_instructions_step1_text),
        )
        Spacer(modifier = Modifier.height(20.dp))
        InstructionItem(
            titleText = stringResource(R.string.grip_instructions_step2_title),
            prefix = stringResource(R.string.grip_instructions_step2_prefix),
            accent = stringResource(R.string.grip_instructions_step2_accent),
            suffix = stringResource(R.string.grip_instructions_step2_suffix),
        )
        Spacer(modifier = Modifier.height(20.dp))
        InstructionItem(
            titleText = stringResource(R.string.grip_instructions_step3_title),
            prefix = stringResource(R.string.grip_instructions_step3_prefix),
            accent = stringResource(R.string.grip_instructions_step3_accent),
            suffix = stringResource(R.string.grip_instructions_step3_suffix),
        )


        Spacer(modifier = Modifier.weight(2f))
        PrimaryButton(
            onClick = { onEvent(GripInstructionsEvent.StartPressed) },
            text = stringResource(R.string.grip_instructions_start_button),
        )
    }


    TtsWarning(ttsWarningActive)
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280, name = "Instructions – Idle")
@Composable
private fun Preview_Instructions_Idle() {
    GripStrengthInstructionsScreen(
        state = GripInstructionsUiState(ttsSpeaking = false),
        ttsWarningActive = MutableStateFlow(false),
        onEvent = {},
    )
}


@Preview(showBackground = true, widthDp = 1280, heightDp = 800, name = "Instructions – Speaking")
@Composable
private fun Preview_Instructions_Horizental() {
    GripStrengthInstructionsScreen(
        state = GripInstructionsUiState(ttsSpeaking = true),
        ttsWarningActive = MutableStateFlow(false),
        onEvent = {},
    )
}
