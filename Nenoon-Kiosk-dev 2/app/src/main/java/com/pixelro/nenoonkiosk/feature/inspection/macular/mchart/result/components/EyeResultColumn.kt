package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.result.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.feature.main.nenoonApp
import com.pixelro.nenoonkiosk.ui.theme.LightGray2
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.Yellow
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun RowScope.EyeResultColumn(
    title: String,
    verticalLabel: String,
    horizontalLabel: String,
    verticalValue: Int,
    horizontalValue: Int,
    verticalColor: Color,
    horizontalColor: Color,
    backgroundColor: Color
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .height(200.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(20.dp),
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
        )

        ResultRow(label = verticalLabel, color = verticalColor, value = verticalValue)
        ResultRow(label = horizontalLabel, color = horizontalColor, value = horizontalValue)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 800)
@Composable
fun EyeResultColumnPreview() {
    NenoonKioskTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            EyeResultColumn(
                title = "왼쪽 눈",
                verticalLabel = "수직",
                horizontalLabel = "수평",
                verticalValue = 35,
                horizontalValue = 25,
                verticalColor = neNoon_blue,
                horizontalColor = Yellow,
                backgroundColor = LightGray2
            )

            Spacer(modifier = Modifier.width(16.dp))

            EyeResultColumn(
                title = "오른쪽 눈",
                verticalLabel = "수직",
                horizontalLabel = "수평",
                verticalValue = 40,
                horizontalValue = 20,
                verticalColor = neNoon_blue,
                horizontalColor = Yellow,
                backgroundColor = LightGray2
            )
        }
    }
}
