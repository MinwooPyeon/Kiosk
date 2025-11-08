package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.result

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
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun GripStrengthInspectionResultContent(
    testResult: GripStrengthInspectionResultContract,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(40.dp),
    ) {
        Text(
            text = stringResource(R.string.test_result_my_result),
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row {
            Text(
                text = stringResource(R.string.test_result_left) + " : ",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
            )

            /**
             * 왼쪽 악력 표기
             */
            Text(
                text = "${testResult.leftGrip}kg",
                fontSize = 32.sp,
                color = neNoon_blue,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text(
                text = stringResource(R.string.test_result_right) + " : ",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
            )

            /**
             * 오른쪽 악력 표기
             */
            Text(
                text = "${testResult.rightGrip}kg",
                fontSize = 32.sp,
                color = neNoon_blue,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, name = "Grip Result – Example")
@Composable
private fun Preview_GripStrengthTestResultContent() {
    GripStrengthInspectionResultContent(
        testResult = GripStrengthInspectionResultContract(leftGrip = 28.4, rightGrip = 32.1)
    )
}
