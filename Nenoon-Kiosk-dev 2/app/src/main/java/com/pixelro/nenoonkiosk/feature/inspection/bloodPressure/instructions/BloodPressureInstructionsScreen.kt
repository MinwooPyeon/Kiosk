package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.instructions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.ui.InstructionItem
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.TtsWarning
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.components.WarningOverlay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun BloodPressureInstructionsScreen(
    state: BloodPressureInstructionsUiState,
    ttsWarningActive: MutableStateFlow<Boolean>,
    bloodPressureMonitorType: SharedPreferencesManager.BloodPressureMonitorType,
    isStandbyMode: Boolean = false,
    toInProgress: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(32.dp),
        ) {
            when (bloodPressureMonitorType) {
                SharedPreferencesManager.BloodPressureMonitorType.BP170B -> {
                    // BP170B: 2 steps
                    InstructionItem(
                        titleText = stringResource(R.string.bp170b_step_1_title),
                        prefix = stringResource(R.string.bp170b_step_1_prefix),
                        accent = stringResource(R.string.bp170b_step_1_accent),
                        suffix = stringResource(R.string.bp170b_step_1_suffix),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    InstructionItem(
                        titleText = stringResource(R.string.bp170b_step_2_title),
                        prefix = stringResource(R.string.bp170b_step_2_prefix),
                        accent = stringResource(R.string.bp170b_step_2_accent),
                        suffix = stringResource(R.string.bp170b_step_2_suffix),
                    )
                }
                SharedPreferencesManager.BloodPressureMonitorType.BPBIO320 -> {
                    // BPBIO320: 3 steps
                    InstructionItem(
                        titleText = stringResource(R.string.bpbio320_instructions_step1_title),
                        instructionText = stringResource(R.string.bpbio320_instructions_step1_text),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    InstructionItem(
                        titleText = stringResource(R.string.bpbio320_instructions_step2_title),
                        prefix = stringResource(R.string.bpbio320_instructions_step2_prefix),
                        accent = stringResource(R.string.bpbio320_instructions_step2_accent),
                        suffix = stringResource(R.string.bpbio320_instructions_step2_suffix),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    InstructionItem(
                        titleText = stringResource(R.string.bpbio320_instructions_step3_title),
                        prefix = stringResource(R.string.bpbio320_instructions_step3_prefix),
                        accent = stringResource(R.string.bpbio320_instructions_step3_accent),
                        suffix = stringResource(R.string.bpbio320_instructions_step3_suffix),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(2f))

            // BP170B는 버튼 없음 (기기에서 직접 시작)
            if (bloodPressureMonitorType == SharedPreferencesManager.BloodPressureMonitorType.BPBIO320) {
                PrimaryButton(
                    onClick = {
                        if (state.ttsSpeaking) {
                            ttsWarningActive.update { true }
                        } else {
                            toInProgress()
                        }
                    },
                    text = stringResource(R.string.bpbio320_start_test_button),
                )
            }
        }

        // BP170B Standby mode 경고
        if (isStandbyMode) {
            WarningOverlay(
                text1 = stringResource(R.string.tts_bp170b_turn_off_device_initialization),
                text2 = "",
                text3 = "",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        TtsWarning(ttsWarningActive)
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP170B Instructions")
@Composable
private fun Preview_BP170B_Instructions() {
    BloodPressureInstructionsScreen(
        state = BloodPressureInstructionsUiState(ttsSpeaking = false),
        ttsWarningActive = MutableStateFlow(false),
        bloodPressureMonitorType = SharedPreferencesManager.BloodPressureMonitorType.BP170B,
        isStandbyMode = false,
        toInProgress = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP170B Instructions (Standby)")
@Composable
private fun Preview_BP170B_Instructions_Standby() {
    BloodPressureInstructionsScreen(
        state = BloodPressureInstructionsUiState(ttsSpeaking = false),
        ttsWarningActive = MutableStateFlow(false),
        bloodPressureMonitorType = SharedPreferencesManager.BloodPressureMonitorType.BP170B,
        isStandbyMode = true,
        toInProgress = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BPBIO320 Instructions")
@Composable
private fun Preview_BPBIO320_Instructions() {
    BloodPressureInstructionsScreen(
        state = BloodPressureInstructionsUiState(ttsSpeaking = false),
        ttsWarningActive = MutableStateFlow(false),
        bloodPressureMonitorType = SharedPreferencesManager.BloodPressureMonitorType.BPBIO320,
        isStandbyMode = false,
        toInProgress = {},
    )
}