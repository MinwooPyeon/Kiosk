package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun BottomWhiteButton(textRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
            .fillMaxWidth()
            .height(120.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color(0xffffffff), shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = StringProvider.getStringComposable(textRes),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}