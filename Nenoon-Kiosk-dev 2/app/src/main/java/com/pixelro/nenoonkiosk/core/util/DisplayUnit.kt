package com.pixelro.nenoonkiosk.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

// mm 단위를 dp로 변경
@Composable
fun rememberMmAsDp(mm: Float): Dp {
    val context = LocalContext.current
    val density = LocalDensity.current

    return remember(context, density, mm) {
        val metrics = context.resources.displayMetrics
        val px = mm * metrics.xdpi / 25.4f
        with(density) {
            px.toDp()
        }
    }
}

