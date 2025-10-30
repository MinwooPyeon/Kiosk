package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText

@Composable
fun ConnectingSection() {
    ProgressIndicator()
    StyledText(
        text = stringResource(R.string.dynamometer_connecting),
        modifier = Modifier.padding(bottom = 180.dp),
    )
}