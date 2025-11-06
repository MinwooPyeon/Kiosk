package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.inprogress

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
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BInProgressEvent
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BInProgressUiState
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BpMeasurementScreenState

@Composable
fun BP170BInProgressScreen(
    state: BP170BInProgressUiState,
    onEvent: (BP170BInProgressEvent) -> Unit,
    modifier: Modifier = Modifier
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
                    text = stringResource(R.string.bp170b_measuring_message),
                    style = TextStyle.Message,
                )
                Spacer(modifier = Modifier.weight(1f))
                ProgressIndicator()
                Spacer(modifier = Modifier.weight(1f))
            }
            BpMeasurementScreenState.Completed -> {
                Spacer(modifier = Modifier.weight(1f))
                StyledText(
                    text = stringResource(R.string.bp170b_measurement_completed_message),
                    style = TextStyle.Message,
                )
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    onClick = { onEvent(BP170BInProgressEvent.CheckResultClicked) },
                    text = stringResource(R.string.bp170b_check_results),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP170B – Measuring")
@Composable
private fun Preview_BP170B_Measuring() {
    BP170BInProgressScreen(
        state = BP170BInProgressUiState(screenState = BpMeasurementScreenState.Measuring),
        onEvent = {},
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "BP170B – Completed")
@Composable
private fun Preview_BP170B_Completed() {
    BP170BInProgressScreen(
        state = BP170BInProgressUiState(screenState = BpMeasurementScreenState.Completed),
        onEvent = {},
    )
}