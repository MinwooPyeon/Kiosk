package com.pixelro.nenoonkiosk.test.gripStrength

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.util.StringProvider

@Composable
fun GripStrengthTestResultContent(
    testResult: GripStrengthTestResult,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp)
    ) {
        Text(
            text = StringProvider.getString(R.string.test_result_my_result, ),
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row {
            Text(
                text = StringProvider.getString(R.string.test_result_left, ) + " : ",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )

            /**
             * 왼쪽 악력 표기
             */
            Text(
                text = "${testResult.leftGrip}kg",
                fontSize = 32.sp,
                color = colorResource(R.color.main),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {

            Text(
                text = StringProvider.getString(R.string.test_result_right, ) + " : ",
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )

            /**
             * 오른쪽 악력 표기
             */
            Text(
                text = "${testResult.rightGrip}kg",
                fontSize = 32.sp,
                color = colorResource(R.color.main),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }


    }
}