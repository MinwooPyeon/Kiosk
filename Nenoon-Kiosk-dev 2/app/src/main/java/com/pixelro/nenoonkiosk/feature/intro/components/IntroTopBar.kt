package com.pixelro.nenoonkiosk.feature.intro.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.core.ui.SettingsButton

@Composable
fun IntroTopBar(toSettingsScreen: () -> Unit) {
    Box(
        modifier =
            Modifier
                .padding(
                    start = 40.dp,
                    end = 40.dp,
                    bottom = 20.dp,
                )
                .fillMaxWidth()
                .height(40.dp),
    ) {
        SettingsButton(toSettingsScreen)
    }
}