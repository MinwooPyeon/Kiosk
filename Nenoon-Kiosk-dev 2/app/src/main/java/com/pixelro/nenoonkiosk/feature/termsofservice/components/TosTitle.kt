package com.pixelro.nenoonkiosk.feature.termsofservice.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider


@Composable
fun TosTitle() {
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = Color(0xff1d71e1),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append(StringProvider.getStringComposable(R.string.terms_of_service_title_primary) + "\n")
            }
            withStyle(
                SpanStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(StringProvider.getStringComposable(R.string.terms_of_service_title_secondary))
            }
        },
        style = TextStyle(
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        ),
        modifier = Modifier.fillMaxWidth()
    )
}