package com.pixelro.nenoonkiosk.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IconTextButton(
    onClick: () -> Unit,
    iconId: Int,
    text: String,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .border(border = BorderStroke(1.dp, Color(0xffc3c3c3)), shape = RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = iconId),
                contentDescription = "",
                tint = Color.Black
            )
            Text(
                modifier = Modifier.padding(20.dp),
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}