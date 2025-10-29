package com.pixelro.nenoonkiosk.feature.inspection.dementia.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomCounter(index1Based: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(70.dp)
                .background(color = Color(0xFF1D71E1), shape = RoundedCornerShape(8.dp))
                .border(BorderStroke(4.dp, Color.White), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index1Based.toString(),
                fontSize = 40.sp,
                color = Color.White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
            )
        }
    }
}