package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.result.components.EyeResultColumn
import com.pixelro.nenoonkiosk.ui.theme.LightGray2
import com.pixelro.nenoonkiosk.ui.theme.Yellow
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun MChartTestResultContent(
    testResult: MChartInspectionResult,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .padding(start = 40.dp, top = 40.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.test_result_my_result),
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
        )

        Row(
            modifier = Modifier
                .padding(start = 40.dp, top = 20.dp, end = 40.dp)
                .fillMaxWidth(),
        ) {
            // 왼쪽 눈 결과
            EyeResultColumn(
                title = stringResource(R.string.test_result_left),
                verticalLabel = stringResource(R.string.mchart_result_vertical),
                horizontalLabel = stringResource(R.string.mchart_result_horizontal),
                verticalValue = testResult.leftEyeVertical,
                horizontalValue = testResult.leftEyeHorizontal,
                verticalColor = neNoon_blue,
                horizontalColor = Yellow,
                backgroundColor = LightGray2
            )

            Spacer(modifier = Modifier.width(20.dp))

            // 오른쪽 눈 결과
            EyeResultColumn(
                title = stringResource(R.string.test_result_right),
                verticalLabel = stringResource(R.string.mchart_result_vertical),
                horizontalLabel = stringResource(R.string.mchart_result_horizontal),
                verticalValue = testResult.rightEyeVertical,
                horizontalValue = testResult.rightEyeHorizontal,
                verticalColor = neNoon_blue,
                horizontalColor = Yellow,
                backgroundColor = LightGray2
            )
        }
    }
}

@Preview(apiLevel = 34, widthDp = 800, heightDp = 1280, showBackground = true)
@Composable
fun MChartTestResultContentPreview() {
    MChartTestResultContent(
        testResult = MChartInspectionResult(
            leftEyeVertical = 35,
            leftEyeHorizontal = 20,
            rightEyeVertical = 25,
            rightEyeHorizontal = 15,
        ),
    )
}
