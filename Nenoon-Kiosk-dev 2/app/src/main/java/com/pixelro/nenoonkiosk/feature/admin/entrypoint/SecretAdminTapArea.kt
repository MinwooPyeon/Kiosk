package com.pixelro.nenoonkiosk.feature.admin.entrypoint

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// SecretAdminTapArea.kt
@Composable
fun SecretAdminTapArea(
    onSecretActivated: () -> Unit,
    modifier: Modifier = Modifier,
    tapAreaSize: Dp = 100.dp,
    requiredTaps: Int = 10,
    timeoutMillis: Long = 2000L
) {
    var tapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }

    Box(
        modifier = modifier
            .size(tapAreaSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastTapTime > timeoutMillis) {
                    tapCount = 0
                }

                tapCount++
                lastTapTime = currentTime

                if (tapCount >= requiredTaps) {
                    tapCount = 0
                    onSecretActivated()
                }
            }
    )
}