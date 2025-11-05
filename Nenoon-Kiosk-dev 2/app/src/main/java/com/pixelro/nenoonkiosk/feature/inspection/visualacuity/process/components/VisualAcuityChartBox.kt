package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.feature.inspection.components.WarningOverlay
import com.pixelro.nenoonkiosk.ui.theme.White

@Composable
fun VisualAcuityChartBox(
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(top = 40.dp)
            .background(
                color = White,
                shape = RoundedCornerShape(8.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        LandoltCImage(
            ansNum = ansNum,
            sightLevel = sightLevel,
        )

        if (!isFaceDetected || !isFacingForward) {
            WarningOverlay(
                text1 = "정면을 ",
                text2 = "주시",
                text3 = "해주세요",
                modifier = Modifier.padding(top = 300.dp)
            )
        }
    }
}
