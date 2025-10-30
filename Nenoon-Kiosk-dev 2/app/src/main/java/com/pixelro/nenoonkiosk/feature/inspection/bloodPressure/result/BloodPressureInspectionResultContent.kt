package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R


@Composable
private fun BloodPressureMetricRow(
    label: String,
    valueText: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "$label : ",
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = valueText,
            fontSize = 32.sp,
            color = colorResource(R.color.main),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}
@Composable
fun BloodPressureInspectionResultContent(
    testResult: BloodPressureInspectionResult,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
    ) {
        Text(
            text = stringResource(R.string.test_result_my_result),
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(40.dp))

        BloodPressureMetricRow(
            label = stringResource(R.string.blood_pressure_monitor_systolic),
            valueText = "${testResult.systolic}mmHg",
        )

        Spacer(modifier = Modifier.height(20.dp))

        BloodPressureMetricRow(
            label = stringResource(R.string.blood_pressure_monitor_diastolic),
            valueText = "${testResult.diastolic}mmHg",
        )

        Spacer(modifier = Modifier.height(20.dp))

        BloodPressureMetricRow(
            label = stringResource(R.string.blood_pressure_monitor_heart_rate),
            valueText = "${testResult.pulseRate}bpm",
        )
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 600, name = "BP Result – Normal")
@Composable
private fun Preview_BloodPressureTestResultContent_Normal() {
    BloodPressureInspectionResultContent(
        testResult = BloodPressureInspectionResult(
            systolic = 118,
            diastolic = 76,
            pulseRate = 68,
        ),
    )
}

@Preview(showBackground = true, widthDp = 888, heightDp = 600, name = "BP Result – High")
@Composable
private fun Preview_BloodPressureTestResultContent_High() {
    BloodPressureInspectionResultContent(
        testResult = BloodPressureInspectionResult(
            systolic = 148,
            diastolic = 96,
            pulseRate = 88,
        ),
    )
}
