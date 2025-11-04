package com.pixelro.nenoonkiosk.feature.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.ui.theme.LightGray

@Composable
fun SettingItem(
    text: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .clickable { onClick() }
                .fillMaxWidth()
                .padding(start = 40.dp, top = 24.dp, bottom = 24.dp)
        ) {
            Text(
                text = text,
                fontSize = 36.sp,
                color = textColor
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LightGray)
        )
    }
}
