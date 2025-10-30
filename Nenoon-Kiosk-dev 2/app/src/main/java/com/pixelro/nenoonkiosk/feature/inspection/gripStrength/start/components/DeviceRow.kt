package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.DeviceUi

@Composable
fun DeviceRow(device: DeviceUi, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = colorResource(R.color.gray2),
                shape = RoundedCornerShape(8.dp),
            )
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        StyledText(
            text = device.name.ifBlank { stringResource(R.string.dynamometer_unknown_device_name) },
            textAlign = TextAlign.Start,
        )
        StyledText(
            text = device.address,
            style = TextStyle.Hint,
            textAlign = TextAlign.Start,
        )
    }
}