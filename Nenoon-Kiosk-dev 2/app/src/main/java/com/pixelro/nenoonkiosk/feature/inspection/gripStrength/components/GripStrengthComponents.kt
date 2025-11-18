package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.BatteryStatus
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider


@Composable
fun BatteryHeader(batteryPercent: Int?, isLoading: Boolean) {
    BatteryStatus(batteryPercent, hidden = !isLoading && batteryPercent == null)
}

@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun DeviceList(
    devices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (devices.isEmpty()) return
    StyledText(
        text = StringProvider.getString(R.string.dynamometer_select_device),
        style = TextStyle.Message,
        modifier = Modifier.padding(top = 60.dp)
    )
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        items(devices.size) { idx ->
            val device = devices[idx]
            ListItem(
                headlineContent = {
                    StyledText(
                        text = device.name
                            ?: StringProvider.getString(R.string.dynamometer_unknown_device_name),
                        textAlign = TextAlign.Start,
                    )
                },
                overlineContent = {
                    StyledText(
                        text = device.address ?: "",
                        style = TextStyle.Hint,
                        textAlign = TextAlign.Start
                    )
                },
                modifier = Modifier
                    .border(1.dp, colorResource(R.color.gray2), RoundedCornerShape(8.dp))
                    .clickable { onClick(device) }
            )
            Divider()
        }
    }
}


@Composable
fun CountdownBig(value: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        StyledText(text = "$value", style = TextStyle.BigNumber)
    }
}


@Composable
fun GripValueLine(label: String, valueKg: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        StyledText(text = "$label : ", style = TextStyle.Title)
        StyledText(text = valueKg, style = TextStyle.Title)
    }
}


@Preview(apiLevel = 34)
@Composable
private fun CountdownPreview() {
    CountdownBig(7)
}