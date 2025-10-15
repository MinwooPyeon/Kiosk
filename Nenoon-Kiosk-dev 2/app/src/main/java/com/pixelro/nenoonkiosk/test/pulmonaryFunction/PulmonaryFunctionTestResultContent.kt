package com.pixelro.nenoonkiosk.test.pulmonaryFunction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.data.StringProvider

@Composable
fun PulmonaryFunctionTestResultTestResultContent(
    testResult: PulmonaryFunctionTestResult
) {
    Column(
        modifier = Modifier
            .padding(40.dp)
            .fillMaxWidth()
            .background(
                color = Color(0xfff7f7f7),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(40.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(StringProvider.getString(
                    R.string.pulmonary_capacity_label,
                    
                ) + " : ")
                withStyle(
                    style = SpanStyle(
                        color = Color(0xff1d71e1),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(String.format("%.1fL", testResult.pulmonaryCapacity))
                }
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = buildAnnotatedString {
                append(StringProvider.getString(
                    R.string.pulmonary_power_label,
                    
                )  + " : ")
                withStyle(
                    style = SpanStyle(
                        color = Color(0xff1d71e1),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(
                        String.format("%.1fF", testResult.pulmonaryPower)
                    )
                }
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = buildAnnotatedString {
                append(StringProvider.getString(
                    R.string.pulmonary_age_label,
                    
                ) + " : ")
                withStyle(
                    style = SpanStyle(
                        color = Color(0xff1d71e1),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(StringProvider.getString(R.string.pulmonary_age_format, testResult.pulmonaryAge.toString()))
                }
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}