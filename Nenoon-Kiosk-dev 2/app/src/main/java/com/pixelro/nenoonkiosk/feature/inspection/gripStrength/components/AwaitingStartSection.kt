package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText

@Composable
fun AwaitingStartSection(
    batteryPercent: Int?,  // 추가
    isBatteryFetching: Boolean,  // 추가
    onStartTest: () -> Unit
) {
    AccentedText(
        prefix = stringResource(R.string.dynamometer_device_connected1),
        accent = stringResource(R.string.dynamometer_device_connected2),
        suffix = stringResource(R.string.dynamometer_device_connected3),
    )

    // 배터리 정보 표시 (선택적)
    if (isBatteryFetching) {
        ProgressIndicator()
    } else if (batteryPercent != null) {
        StyledText(
            text =  batteryPercent.toString(),
            modifier = Modifier.padding(top = 20.dp)
        )
    }

    PrimaryButton(
        onClick = onStartTest,
        modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
        text = stringResource(R.string.grip_strength_start_test),
    )
}
