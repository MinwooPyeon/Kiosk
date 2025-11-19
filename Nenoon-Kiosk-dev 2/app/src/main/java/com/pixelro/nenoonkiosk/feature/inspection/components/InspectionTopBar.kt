package com.pixelro.nenoonkiosk.feature.inspection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.core.constants.GlobalValue

@Composable
fun InspectionTopBar(
    title: String,
    titleColor: Color,
    closePainterId: Any, // painterResource(...) 결과
    onClickClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(
                start = 40.dp,
                top = 20.dp,
                end = 40.dp,
                bottom = 20.dp
            )
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                modifier = Modifier
                    .width(32.dp)
                    .clickable(onClick = onClickClose),
                painter = closePainterId as androidx.compose.ui.graphics.painter.Painter,
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}