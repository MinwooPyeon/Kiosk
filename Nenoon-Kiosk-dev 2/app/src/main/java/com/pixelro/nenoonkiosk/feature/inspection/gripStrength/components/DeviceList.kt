package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.DeviceUi

@Composable
fun DeviceList(
    devices: List<DeviceUi>,
    onClick: (DeviceUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier,
    ) {
        items(devices) { device ->
            DeviceRow(device = device, onClick = { onClick(device) })
        }
    }
}