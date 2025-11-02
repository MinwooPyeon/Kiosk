package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.inprogress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.GripStrengthInspectionState

@Composable
fun GripStrengthInProgressScreen(
    testState: GripStrengthInspectionState,
    rightGripValue: Double,
    leftGripValue: Double,
    countdown: Int,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.grip_strength_test_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            // 현재 손 표시
            Text(
                text = when (testState) {
                    GripStrengthInspectionState.RightHandReady,
                    GripStrengthInspectionState.RightHand,
                    GripStrengthInspectionState.RightHandCompleted ->
                        stringResource(R.string.right_hand)
                    else ->
                        stringResource(R.string.left_hand)
                },
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            // 상태 표시
            when (testState) {
                GripStrengthInspectionState.RightHandReady,
                GripStrengthInspectionState.LeftHandReady -> {
                    Text(
                        text = stringResource(R.string.press_start_to_begin),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
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
                            fontSize = 24.sp
                        )
                        Text(
                            text = "$countdown",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5722)
                        )
                        LinearProgressIndicator(
                            progress = (10 - countdown) / 10f,
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
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.processing_result),
                            fontSize = 24.sp
                        )
                        Text(
                            text = when (testState) {
                                GripStrengthInspectionState.RightHandCompleted ->
                                    "${rightGripValue}kg"
                                else ->
                                    "${leftGripValue}kg"
                            },
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
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
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (rightGripValue > 0) "${rightGripValue}kg" else "-",
                        fontSize = 24.sp,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.left_hand),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (leftGripValue > 0) "${leftGripValue}kg" else "-",
                        fontSize = 24.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        if (testState == GripStrengthInspectionState.RightHandReady ||
            testState == GripStrengthInspectionState.LeftHandReady) {
            PrimaryButton(
                text = stringResource(R.string.start),
                onClick = onStartClick
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun Preview_GripStrengthInProgress() {
    GripStrengthInProgressScreen(
        testState = GripStrengthInspectionState.RightHandReady,
        rightGripValue = 0.0,
        leftGripValue = 0.0,
        countdown = 10,
        onStartClick = {}
    )
}
