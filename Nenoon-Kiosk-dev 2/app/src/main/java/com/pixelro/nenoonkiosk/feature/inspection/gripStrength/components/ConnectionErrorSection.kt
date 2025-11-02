package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle

@Composable
fun ConnectionErrorSection(onRetry: () -> Unit) {
    StyledText(
        text = stringResource(R.string.dynamometer_connection_error),
        style = TextStyle.Error,
        modifier = Modifier.padding(bottom = 180.dp),
    )
    PrimaryButton(
        onClick = onRetry,
        text = stringResource(R.string.dynamometer_try_again),
        modifier = Modifier.padding(bottom = 20.dp),
    )
}