package com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.ui.theme.Gray

/**
 * 블루투스 디바이스 리스트 아이템
 *
 * 블루투스 디바이스의 이름과 주소를 표시하는 클릭 가능한 리스트 아이템입니다.
 *
 * @param device 표시할 블루투스 디바이스
 * @param onClick 아이템 클릭 시 호출되는 콜백
 * @param modifier Compose Modifier
 */
@SuppressLint("MissingPermission")
@Composable
fun DeviceListItem(
    device: BluetoothDevice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            StyledText(
                text = device.name ?: stringResource(R.string.dynamometer_unknown_device_name),
                textAlign = TextAlign.Start,
            )
        },
        supportingContent = {
            StyledText(
                device.address,
                style = TextStyle.Hint,
                textAlign = TextAlign.Start,
            )
        },
        modifier = modifier
            .border(
                width = 1.dp,
                color = Gray,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable { onClick() },
    )
}