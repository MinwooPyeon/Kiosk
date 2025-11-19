package com.pixelro.nenoonkiosk.feature.categorylist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.White

@Composable
fun InspectionCategoryButton(
    iconRes: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier

) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = title,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1D71E1,
    widthDp = 600,
    heightDp = 115,
    apiLevel = 34
)
@Composable
fun InspectionCategoryButtonPreview() {
    InspectionCategoryButton(
        iconRes = R.drawable.blood_pressure_and_grip_strength_icon,
        title = "혈압, 악력",
        onClick = {}
    )
}