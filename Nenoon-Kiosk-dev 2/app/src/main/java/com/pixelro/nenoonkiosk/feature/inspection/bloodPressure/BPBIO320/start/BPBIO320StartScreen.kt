package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.BPBIO320StartEvent
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.BPBIO320StartUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BloodPressureConnectionScreenState

@Composable
fun BPBIO320StartScreen(
    state: BPBIO320StartUiState,
    onEvent: (BPBIO320StartEvent) -> Unit,
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
        Image(
            painter = painterResource(R.drawable.blood_pressure_icon),
            contentDescription = stringResource(R.string.blood_pressure_monitor_image_content_description),
            modifier = Modifier
                .weight(1f)
                .width(400.dp)
                .padding(top = 100.dp),
        )

        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            when (state.screenState) {
                BloodPressureConnectionScreenState.Standby -> {
                    AccentedText(
                        prefix = stringResource(R.string.blood_pressure_monitor_standby_instruction1),
                        accent = stringResource(R.string.blood_pressure_monitor_standby_instruction2),
                        suffix = stringResource(R.string.blood_pressure_monitor_standby_instruction3),
                    )
                    PrimaryButton(
                        onClick = { onEvent(BPBIO320StartEvent.StartConnect) },
                        text = stringResource(R.string.blood_pressure_monitor_start_connection),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                    )
                }

                BloodPressureConnectionScreenState.SearchingOrIdle -> {
                    ProgressIndicator()
                    PrimaryButton(
                        onClick = { onEvent(BPBIO320StartEvent.RetryConnect) },
                        text = stringResource(R.string.blood_pressure_monitor_retry_connection),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                    )
                }

                BloodPressureConnectionScreenState.Connecting -> {
                    ProgressIndicator()
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_connecting),
                        modifier = Modifier.padding(top = 40.dp, bottom = 180.dp),
                    )
                }

                BloodPressureConnectionScreenState.AwaitingStart -> {
                    StyledText(text = stringResource(R.string.blood_pressure_monitor_device_connected))
                    PrimaryButton(
                        onClick = { onEvent(BPBIO320StartEvent.StartTest) },
                        text = stringResource(R.string.start),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                    )
                }

                BloodPressureConnectionScreenState.ConnectionError -> {
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_connection_error),
                        style = TextStyle.Error,
                    )
                    PrimaryButton(
                        onClick = { onEvent(BPBIO320StartEvent.RetryConnect) },
                        text = stringResource(R.string.blood_pressure_monitor_try_again),
                        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                    )
                }
            }

            PrimaryButton(
                onClick = { onEvent(BPBIO320StartEvent.Back) },
                text = stringResource(R.string.back),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BPBIO320 – Standby")
@Composable
private fun Preview_BPBIO320_Standby() {
    BPBIO320StartScreen(
        state = BPBIO320StartUiState(screenState = BloodPressureConnectionScreenState.Standby),
        onEvent = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BPBIO320 – Connecting")
@Composable
private fun Preview_BPBIO320_Connecting() {
    BPBIO320StartScreen(
        state = BPBIO320StartUiState(screenState = BloodPressureConnectionScreenState.Connecting),
        onEvent = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BPBIO320 – AwaitingStart")
@Composable
private fun Preview_BPBIO320_AwaitingStart() {
    BPBIO320StartScreen(
        state = BPBIO320StartUiState(screenState = BloodPressureConnectionScreenState.AwaitingStart),
        onEvent = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BPBIO320 – Error")
@Composable
private fun Preview_BPBIO320_Error() {
    BPBIO320StartScreen(
        state = BPBIO320StartUiState(screenState = BloodPressureConnectionScreenState.ConnectionError),
        onEvent = {},
    )
}