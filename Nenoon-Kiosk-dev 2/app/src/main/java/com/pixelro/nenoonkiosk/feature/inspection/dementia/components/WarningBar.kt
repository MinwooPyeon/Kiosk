package com.pixelro.nenoonkiosk.feature.inspection.dementia.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.Red

@Composable
fun WarningBar(
    warningTextSize: TextUnit
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    bottom = (GlobalValue.navigationBarPadding + 20).dp,
                    top = 10.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .padding(end = 20.dp)
                    .width(44.dp),
                painter = painterResource(id = R.drawable.icon_warning),
                contentDescription = ""
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Gray,
                            fontSize = warningTextSize
                        )
                    ) { append(stringResource(R.string.test_list_screen_warning1)) }
                    withStyle(
                        style = SpanStyle(
                            color = Red,
                            fontSize = warningTextSize,
                            fontWeight = FontWeight.Bold
                        )
                    ) { append(stringResource(R.string.test_list_screen_warning2)) }
                    withStyle(
                        style = SpanStyle(
                            color = Gray,
                            fontSize = warningTextSize
                        )
                    ) { append(stringResource(R.string.test_list_screen_warning3)) }
                }
            )
        }
    }
}