package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton

@Composable
fun AwaitingStartSection(onStartTest: () -> Unit) {
    AccentedText(
        prefix = stringResource(R.string.dynamometer_device_connected1),
        accent = stringResource(R.string.dynamometer_device_connected2),
        suffix =stringResource(R.string.dynamometer_device_connected3),
    )
    PrimaryButton(
        onClick = onStartTest,
        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
        text = stringResource(R.string.grip_strength_start_test),
    )
}