package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 설정 버튼
 */
@Composable
fun SettingsButton(toSettingsScreen: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            modifier = Modifier
                .size(72.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    toSettingsScreen()
                },
            painter = painterResource(id = R.drawable.icon_settings),
            contentDescription = "settings",
            tint = Gray
        )
    }
}

/* ---------- PREVIEW ---------- */

@Preview(showBackground = true, name = "기본 크기 (72dp)")
@Composable
private fun SettingsButtonDefaultPreview() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier.size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            SettingsButton(toSettingsScreen = {})
        }
    }
}

@Preview(showBackground = true, name = "작은 크기 (64dp)")
@Composable
private fun SettingsButtonSmallPreview() {
    NenoonKioskTheme {
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            SettingsButton(toSettingsScreen = {})
        }
    }
}
