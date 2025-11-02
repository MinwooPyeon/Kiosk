package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.inprogress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthInspectionState

// Contract 유지 (기존 그대로)
data class GripInProgressUiState(
    val testState: GripStrengthInspectionState = GripStrengthInspectionState.RightHandReady,
    val rightGripValue: Double = 0.0,
    val leftGripValue: Double = 0.0,
    val countdown: Int = 10
)

sealed class GripInProgressEvent {
    object StartPressed : GripInProgressEvent()
}

// 변경: 파라미터만 변경, UI는 그대로
@Composable
fun GripStrengthInProgressScreen(
    state: GripInProgressUiState,
    onEvent: (GripInProgressEvent) -> Unit
) {
    // 기존 UI 코드 전부 그대로 사용
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.grip_strength_test_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.text_primary)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                // 현재 손 표시
                Text(
                    text = when {
                        state.testState == GripStrengthInspectionState.RightHandReady ||
                                state.testState == GripStrengthInspectionState.RightHand ||
                                state.testState == GripStrengthInspectionState.RightHandCompleted ->
                            stringResource(R.string.right_hand)
                        else ->
                            stringResource(R.string.left_hand)
                    },
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.primary)
                )

                // 상태별 UI
                when (state.testState) {
                    GripStrengthInspectionState.RightHandReady,
                    GripStrengthInspectionState.LeftHandReady -> {
                        Text(
                            text = stringResource(R.string.press_start_to_begin),
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.text_secondary)
                        )
                    }
                    GripStrengthInspectionState.RightHand,
                    GripStrengthInspectionState.LeftHand -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.measuring),
                                fontSize = 24.sp,
                                color = colorResource(id = R.color.text_secondary)
                            )
                            Text(
                                text = "${state.countdown}",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.accent)
                            )
                            LinearProgressIndicator(
                                progress = (10 - state.countdown) / 10f,
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(8.dp)
                            )
                        }
                    }
                    GripStrengthInspectionState.RightHandCompleted,
                    GripStrengthInspectionState.LeftHandCompleted -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.processing_result),
                                fontSize = 24.sp,
                                color = colorResource(id = R.color.text_secondary)
                            )
                            Text(
                                text = when (state.testState) {
                                    GripStrengthInspectionState.RightHandCompleted ->
                                        "${state.rightGripValue}kg"
                                    else ->
                                        "${state.leftGripValue}kg"
                                },
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.success)
                            )
                        }
                    }
                }

                // 결과 요약
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.right_hand),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.text_primary)
                        )
                        Text(
                            text = if (state.rightGripValue > 0) "${state.rightGripValue}kg" else "-",
                            fontSize = 24.sp,
                            color = colorResource(id = R.color.text_secondary)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.left_hand),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.text_primary)
                        )
                        Text(
                            text = if (state.leftGripValue > 0) "${state.leftGripValue}kg" else "-",
                            fontSize = 24.sp,
                            color = colorResource(id = R.color.text_secondary)
                        )
                    }
                }
            }

            if (state.testState == GripStrengthInspectionState.RightHandReady ||
                state.testState == GripStrengthInspectionState.LeftHandReady) {
                PrimaryButton(
                    text = stringResource(R.string.button_start),
                    onClick = { onEvent(GripInProgressEvent.StartPressed) }
                )
            } else {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun Preview_GripStrengthInProgress() {
    GripStrengthInProgressScreen(
        state = GripInProgressUiState(
            testState = GripStrengthInspectionState.RightHandReady
        ),
        onEvent = {}
    )
}
