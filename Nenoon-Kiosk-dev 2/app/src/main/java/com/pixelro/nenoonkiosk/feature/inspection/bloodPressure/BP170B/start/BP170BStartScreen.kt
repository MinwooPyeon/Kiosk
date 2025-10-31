package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.pixelro.nenoonkiosk.core.ui.InstructionItem
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BConnectionScreenState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BStartEvent
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BStartUiState

@Composable
fun BP170BStartScreen(
    state: BP170BStartUiState,
    onEvent: (BP170BStartEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            when (state.screenState) {
                BP170BConnectionScreenState.Standby,
                BP170BConnectionScreenState.Connecting,
                BP170BConnectionScreenState.AwaitingStart -> {
                    InstructionItem(
                        titleText = stringResource(R.string.bp170b_step_1_title),
                        prefix = stringResource(R.string.bp170b_step_1_prefix),
                        accent = stringResource(R.string.bp170b_step_1_accent),
                        suffix = stringResource(R.string.bp170b_step_1_suffix),
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    InstructionItem(
                        titleText = stringResource(R.string.bp170b_step_2_title),
                        prefix = stringResource(R.string.bp170b_step_2_prefix),
                        accent = stringResource(R.string.bp170b_step_2_accent),
                        suffix = stringResource(R.string.bp170b_step_2_suffix),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                BP170BConnectionScreenState.ConnectionError -> {
                    Spacer(modifier = Modifier.weight(1f))
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_connection_error),
                        style = TextStyle.Error,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PrimaryButton(
                        onClick = { onEvent(BP170BStartEvent.RetryScan) },
                        text = stringResource(R.string.blood_pressure_monitor_try_again),
                        modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                    )
                }

                BP170BConnectionScreenState.TurnOffDevice -> {
                    Spacer(modifier = Modifier.weight(1f))
                    StyledText(
                        text = stringResource(R.string.bp170b_initialization_required),
                        style = TextStyle.Error,
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    StyledText(
                        text = stringResource(R.string.bp170b_turn_off_bp_monitor),
                        style = TextStyle.Error,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                BP170BConnectionScreenState.DeviceCheck -> {
                    Spacer(modifier = Modifier.weight(1f))
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_searching_device),
                        style = TextStyle.Message,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ProgressIndicator()
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            PrimaryButton(
                onClick = { onEvent(BP170BStartEvent.Back) },
                text = stringResource(R.string.back),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP170B – DeviceCheck")
@Composable
private fun Preview_BP170B_DeviceCheck() {
    BP170BStartScreen(
        state = BP170BStartUiState(screenState = BP170BConnectionScreenState.DeviceCheck),
        onEvent = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP170B – Standby")
@Composable
private fun Preview_BP170B_Standby() {
    BP170BStartScreen(
        state = BP170BStartUiState(screenState = BP170BConnectionScreenState.Standby),
        onEvent = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP170B – TurnOff")
@Composable
private fun Preview_BP170B_TurnOff() {
    BP170BStartScreen(
        state = BP170BStartUiState(screenState = BP170BConnectionScreenState.TurnOffDevice),
        onEvent = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP170B – Error")
@Composable
private fun Preview_BP170B_Error() {
    BP170BStartScreen(
        state = BP170BStartUiState(screenState = BP170BConnectionScreenState.ConnectionError),
        onEvent = {},
    )
}