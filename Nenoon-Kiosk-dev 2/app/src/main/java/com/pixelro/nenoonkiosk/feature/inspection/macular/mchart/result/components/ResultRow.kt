package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.result.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

@Composable
fun ResultRow(label: String, color: Color, value: Int) {
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            modifier = Modifier
                .background(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
            text = label,
            color = color,
            fontSize = 20.sp,
        )
        Box(
            modifier = Modifier
                .padding(end = 20.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Text(
                modifier = Modifier.padding(start = 100.dp),
                text = "${String.format("%.1f", value.toFloat() / 10)}°",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F9F9, apiLevel = 34)
@Composable
fun ResultRowPreview() {
    NenoonKioskTheme {
        ResultRow(
            label = "수직",
            color = Color(0xFF1D71E1),
            value = 35
        )
    }
}
