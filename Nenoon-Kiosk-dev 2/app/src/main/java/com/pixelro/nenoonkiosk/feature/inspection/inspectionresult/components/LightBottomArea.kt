package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun LightBottomArea(
    printEnabled: Boolean,
    onPrint: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    // 경고문
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp, end = 40.dp),
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color(0xff999999), fontSize = 16.sp)) {
                append(StringProvider.getStringComposable(R.string.test_list_screen_warning1))
            }
            withStyle(style = SpanStyle(color = Color(0xffff0000), fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
                append(StringProvider.getStringComposable(R.string.test_list_screen_warning2))
            }
            withStyle(style = SpanStyle(color = Color(0xff999999), fontSize = 16.sp)) {
                append(StringProvider.getStringComposable(R.string.test_list_screen_warning3))
            }
        },
    )

    // 하단 버튼 영역
    Box(
        modifier = Modifier
            .padding(bottom = 40.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (printEnabled) {
                Box(
                    modifier = Modifier
                        .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(8.dp))
                        .border(
                            border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable { onPrint() },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            modifier = Modifier.width(28.dp),
                            painter = painterResource(id = R.drawable.icon_print),
                            contentDescription = "",
                        )
                        Text(
                            modifier = Modifier.padding(20.dp),
                            text = StringProvider.getStringComposable(R.string.result_button1_print),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            LightOutlineButton(textRes = R.string.result_dementia_back, onClick = onBack)
            LightOutlineButton(textRes = R.string.settings_signout, onClick = onLogout)
        }
    }
}