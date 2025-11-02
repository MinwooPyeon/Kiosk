package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.DeviceUi

@Composable
fun DeviceSelectionSection(
    devices: List<DeviceUi>,
    isConnecting: Boolean,
    onSelect: (DeviceUi) -> Unit,
    onRetry: () -> Unit,
) {
    if (devices.isNotEmpty()) {
        StyledText(
            stringResource(R.string.dynamometer_select_device),
            TextStyle.Message,
            modifier = Modifier.padding(top = 60.dp),
        )
        DeviceList(
            devices = devices,
            onClick = onSelect,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        )
        Spacer(modifier = Modifier.height(200.dp))
    } else {
        if (isConnecting) {
            ProgressIndicator()
            StyledText(
                text = stringResource(R.string.dynamometer_searching),
                modifier = Modifier.padding(bottom = 180.dp),
            )
        } else {
            StyledText(
                stringResource(R.string.dynamometer_not_connected_instruction),
                style = TextStyle.Error,
            )
            PrimaryButton(
                onClick = onRetry,
                text = stringResource(R.string.dynamometer_retry_connection),
                modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
            )
        }
    }
}
