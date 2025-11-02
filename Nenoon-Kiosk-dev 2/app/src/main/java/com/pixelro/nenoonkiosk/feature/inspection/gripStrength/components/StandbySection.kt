package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton

@Composable
fun StandbySection(onStart: () -> Unit) {
    AccentedText(
        prefix = stringResource(R.string.dynamometer_standby_instruction1),
        accent = stringResource(R.string.dynamometer_standby_instruction2),
        suffix = stringResource(R.string.dynamometer_standby_instruction3),
    )
    PrimaryButton(
        onClick = onStart,
        text = stringResource(R.string.dynamometer_start_connection),
        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
    )
}