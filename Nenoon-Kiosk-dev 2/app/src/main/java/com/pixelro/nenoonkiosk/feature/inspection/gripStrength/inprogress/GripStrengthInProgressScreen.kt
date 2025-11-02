package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.inprogress

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.AccentStyle
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthInspectionState
import kotlin.math.round

data class GripInProgressUiState(
    val testState: GripStrengthInspectionState = GripStrengthInspectionState.RightHandReady,
    val rightGripValue: Double = 0.0,
    val leftGripValue: Double = 0.0,
    val countdown: Int = 10
)

sealed class GripInProgressEvent {
    object StartPressed : GripInProgressEvent()
}

@SuppressLint("CoroutineCreationDuringComposition", "MissingPermission")
@Composable
fun GripStrengthInProgressScreen(
    state: GripInProgressUiState,
    onEvent: (GripInProgressEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedRight by animateFloatAsState(
        targetValue = state.rightGripValue.toFloat(),
        animationSpec = tween(1000),
        label = "animRight",
    )

    val animatedLeft by animateFloatAsState(
        targetValue = state.leftGripValue.toFloat(),
        animationSpec = tween(1000),
        label = "animLeft",
    )

    Column(
        modifier = modifier
            .padding(40.dp)
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        when (state.testState) {
            GripStrengthInspectionState.LeftHandReady -> AccentedText(
                prefix = stringResource(R.string.grip_strength_left_hand_ready_text1),
                accent = stringResource(R.string.grip_strength_left_hand_ready_text2),
                suffix = stringResource(R.string.grip_strength_left_hand_ready_text3),
            )
            GripStrengthInspectionState.RightHandReady -> AccentedText(
                prefix = stringResource(R.string.grip_strength_right_hand_ready_text1),
                accent = stringResource(R.string.grip_strength_right_hand_ready_text2),
                suffix = stringResource(R.string.grip_strength_right_hand_ready_text3),
            )
            GripStrengthInspectionState.RightHandCompleted -> StyledText(stringResource(R.string.grip_strength_right_hand_completed_text))
            GripStrengthInspectionState.LeftHandCompleted -> StyledText(stringResource(R.string.grip_strength_left_hand_completed_text))
            GripStrengthInspectionState.RightHand -> StyledText(stringResource(R.string.grip_strength_right_hand_instruction_tts))
            GripStrengthInspectionState.LeftHand -> StyledText(stringResource(R.string.grip_strength_left_hand_instruction_tts))
            else -> Unit
        }

        Spacer(modifier = Modifier.height(64.dp))

        when (state.testState) {
            GripStrengthInspectionState.RightHandReady,
            GripStrengthInspectionState.LeftHandReady -> {
                StyledText(text = stringResource(R.string.grip_strength_press_button_and_squeeze_text))
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    text = stringResource(R.string.grip_strength_start_button),
                    onClick = { onEvent(GripInProgressEvent.StartPressed) },
                )
            }
            GripStrengthInspectionState.RightHand,
            GripStrengthInspectionState.LeftHand -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    StyledText(text = "${state.countdown}", style = TextStyle.BigNumber)
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            GripStrengthInspectionState.RightHandCompleted -> {
                AccentedText(
                    prefix = stringResource(R.string.grip_strength_right_hand_value),
                    accent = " ${round(animatedRight * 10.0f) / 10.0f}kg",
                    suffix = "",
                    accentStyle = AccentStyle.Blue,
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            GripStrengthInspectionState.LeftHandCompleted -> {
                AccentedText(
                    prefix = stringResource(R.string.grip_strength_left_hand_value),
                    accent = " ${round(animatedLeft * 10.0f) / 10.0f}kg",
                    suffix = "",
                    accentStyle = AccentStyle.Blue,
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Right – Ready")
@Composable
private fun Preview_RightReady() {
    GripStrengthInProgressScreen(
        state = GripInProgressUiState(testState = GripStrengthInspectionState.RightHandReady),
        onEvent = {},
    )
}
