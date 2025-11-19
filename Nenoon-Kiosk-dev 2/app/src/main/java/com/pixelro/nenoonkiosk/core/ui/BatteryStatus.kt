package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R

@Composable
fun BatteryStatus(
    batteryLevel: Int?,
    hidden: Boolean = false,
    binaryMode: Boolean = true,
) {
    val batteryIcons =
        intArrayOf(
            R.drawable.battery_0_bar,
            R.drawable.battery_1_bar,
            R.drawable.battery_2_bar,
            R.drawable.battery_3_bar,
            R.drawable.battery_4_bar,
            R.drawable.battery_5_bar,
            R.drawable.battery_6_bar,
            R.drawable.battery_full,
        )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .alpha(if (hidden) 0f else 1f),
    ) {
        Text(
            text =
                if (batteryLevel == null) {
                    "..."
                } else if (binaryMode && batteryLevel > 0) {
                    "✓"
                } else if (binaryMode) {
                    "!"
                } else {
                    "$batteryLevel%"
                },
            fontSize = 64.sp,
        )

        Icon(
            painter =
                painterResource(
                    if (batteryLevel != null && binaryMode && batteryLevel > 0) {
                        batteryIcons[7]
                    } else if (binaryMode) {
                        batteryIcons[0]
                    } else {
                        batteryIcons[((batteryLevel ?: 0) * 0.07f).toInt()]
                    },
                ),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
    }
}
