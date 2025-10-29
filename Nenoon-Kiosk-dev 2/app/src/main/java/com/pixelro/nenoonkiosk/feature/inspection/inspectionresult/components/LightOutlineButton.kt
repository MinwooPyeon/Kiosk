package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun LightOutlineButton(textRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .border(
                border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                shape = RoundedCornerShape(8.dp),
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(20.dp),
            text = StringProvider.getStringComposable(textRes),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}