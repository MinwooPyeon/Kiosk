package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaInspectionResult
import com.pixelro.nenoonkiosk.ui.theme.White

@Composable
fun PresbyopiaInspectionResultScreen(
    testResult: PresbyopiaInspectionResult
) {
    val isNormal = testResult.avgDistance.toInt() == 25

    val normalMessage = stringResource(R.string.presbyopia_result_description)
    val abnormalMessagePrefix = stringResource(R.string.presbyopia_result_description2_eye_age)
    val abnormalMessageSuffix = stringResource(R.string.presbyopia_result_description3_abnormal)
    val description2Abnormal = stringResource(R.string.presbyopia_result_description2_abnormal)
    val distancePrefix = stringResource(R.string.presbyopia_result_sub_description1_approximate)
    val distanceSuffix = stringResource(R.string.presbyopia_result_sub_description2_blur)

    val ageRange = " ${testResult.age - 2} ~ ${testResult.age + 2}$description2Abnormal"
    val avgDistance = String.format("%.1f", testResult.avgDistance)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(40.dp),
        verticalArrangement = Arrangement.Center
    ) {
        PresbyopiaResultCard(
            isNormal = isNormal,
            normalMessage = normalMessage,
            abnormalMessagePrefix = abnormalMessagePrefix,
            ageRange = ageRange,
            abnormalMessageSuffix = abnormalMessageSuffix,
            distancePrefix = "$distancePrefix ",
            avgDistance = avgDistance,
            distanceSuffix = distanceSuffix
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PresbyopiaInspectionResultScreenPreview() {
    PresbyopiaInspectionResultScreen(
        testResult = PresbyopiaInspectionResult(
            age = 45,
            firstDistance = 20f,
            secondDistance = 20f,
            thirdDistance = 20f,
            avgDistance = 20f
        )
    )
}
