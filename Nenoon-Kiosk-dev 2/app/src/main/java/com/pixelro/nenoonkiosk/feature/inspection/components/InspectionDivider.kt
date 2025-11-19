package com.pixelro.nenoonkiosk.feature.inspection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.LightGray100

@Composable
fun InspectionDivider() {
    Spacer(
        modifier = Modifier
            .padding(bottom = 5.dp, start = 5.dp, end = 5.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(LightGray100)
    )
}