package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.instructions

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
import com.pixelro.nenoonkiosk.core.util.StringProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun BloodPressureInstructionsScreen(
    state: BloodPressureInstructionsUiState,
    ttsWarningActive: MutableStateFlow<Boolean>,
    toInProgress: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(40.dp),
    ) {
        InstructionItem(
            titleText = stringResource(R.string.bpbio320_instructions_step1_title),
            instructionText = stringResource(R.string.bpbio320_instructions_step1_text),
        )
        Spacer(modifier = Modifier.height(40.dp))
        InstructionItem(
            titleText = stringResource(R.string.bpbio320_instructions_step2_title),
            prefix = stringResource(R.string.bpbio320_instructions_step2_prefix),
            accent = stringResource(R.string.bpbio320_instructions_step2_accent),
            suffix = stringResource(R.string.bpbio320_instructions_step2_suffix),
        )
        Spacer(modifier = Modifier.height(40.dp))
        InstructionItem(
            titleText = stringResource(R.string.bpbio320_instructions_step3_title),
            prefix = stringResource(R.string.bpbio320_instructions_step3_prefix),
            accent = stringResource(R.string.bpbio320_instructions_step3_accent),
            suffix = stringResource(R.string.bpbio320_instructions_step3_suffix),
        )

        Spacer(modifier = Modifier.weight(2f))
        PrimaryButton(
            onClick = {
                if (state.ttsSpeaking) {
                    ttsWarningActive.update { true }
                } else {
                    toInProgress
                }
            },
            text = stringResource(R.string.bpbio320_start_test_button),
        )
    }
    TtsWarning(ttsWarningActive)
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP Instructions – Idle")
@Composable
private fun Preview_BP_Instructions_Idle() {
    BloodPressureInstructionsScreen(
        state = BloodPressureInstructionsUiState(ttsSpeaking = false),
        ttsWarningActive = MutableStateFlow(false),
        toInProgress = {},
    )
}


@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP Instructions – Speaking")
@Composable
private fun Preview_BP_Instructions_Speaking() {
    BloodPressureInstructionsScreen(
        state = BloodPressureInstructionsUiState(ttsSpeaking = true),
        ttsWarningActive = MutableStateFlow(false),
        toInProgress = {},
    )
}