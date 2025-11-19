package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.inprogress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BpMeasurementScreenState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.BPBIO320InProgressEvent
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.BPBIO320InProgressUiState

@Composable
fun BPBIO320InProgressScreen(
    state: BPBIO320InProgressUiState,
    onEvent: (BPBIO320InProgressEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(40.dp)
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (state.screenState) {
            BpMeasurementScreenState.Measuring -> {
                Spacer(modifier = Modifier.weight(1f))
                StyledText(
                    text = stringResource(R.string.bpbio320_measurement_in_progress),
                    style = TextStyle.Message,
                )
                Spacer(modifier = Modifier.weight(1f))
                ProgressIndicator()
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    onClick = { onEvent(BPBIO320InProgressEvent.StopPressed) },
                    text = stringResource(R.string.bpbio320_measurement_stop),
                )
            }
            BpMeasurementScreenState.Completed -> {
                Spacer(modifier = Modifier.weight(1f))
                StyledText(
                    text = stringResource(R.string.bpbio320_measurement_completed),
                    style = TextStyle.Message,
                )
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    onClick = { onEvent(BPBIO320InProgressEvent.CheckResultPressed) },
                    text = stringResource(R.string.bpbio320_check_result),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BPBIO320 – Measuring")
@Composable
private fun Preview_BPBIO320_Measuring() {
    BPBIO320InProgressScreen(
        state = BPBIO320InProgressUiState(screenState = BpMeasurementScreenState.Measuring),
        onEvent = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BPBIO320 – Completed")
@Composable
private fun Preview_BPBIO320_Completed() {
    BPBIO320InProgressScreen(
        state = BPBIO320InProgressUiState(screenState = BpMeasurementScreenState.Completed),
        onEvent = {},
    )
}
