package com.pixelro.nenoonkiosk.feature.inspection.dementia.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnswerCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    biasPaddingStart: Dp
) {
    val shape = RoundedCornerShape(8.dp)
    val blue = Color(0xFF1D71E1)

    Box(
        modifier = Modifier
            .padding(start = biasPaddingStart)
            .clip(shape)
            .width(355.dp)
            .heightIn(min = 180.dp)
            .border(BorderStroke(4.dp, blue), shape)
            .background(if (selected) blue else Color.White, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 60.sp,
            color = if (selected) Color.White else blue,
            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
        )
    }
}