package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.ui.theme.selectLargeTextStyle
import kotlin.math.roundToInt

/**
 * 현재 거리를 표시하는 컴포넌트
 *
 * @param distance 거리 값 (밀리미터 단위, 10으로 나누면 센티미터)
 * @param modifier Modifier
 */
@Composable
fun DistanceDisplay(
    distance: Float,
    modifier: Modifier = Modifier,
) {
    // 거리에 따른 색상 결정 (40~50cm 범위일 때 파란색, 그 외 빨간색)
    val textColor = when {
        distance in 400f..500f -> neNoon_blue
        else -> Color.Red
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "${(distance / 10).roundToInt()}cm",
            style = selectLargeTextStyle.copy(
                fontSize = 140.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            ),
        )
    }
}