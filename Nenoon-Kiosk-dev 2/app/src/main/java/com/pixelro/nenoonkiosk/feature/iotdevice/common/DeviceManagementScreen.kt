package com.pixelro.nenoonkiosk.feature.iotdevice.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.BatteryStatus
import com.pixelro.nenoonkiosk.core.ui.SecondaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.BP170BConnectionUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.BP170BManagementUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ConnectionUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ManagementUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripConnectionUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripManagementUiState
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.components.BP170BConnectionStateContent
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.components.BPBIO320ConnectionStateContent
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.components.InGripConnectionStateContent
import com.pixelro.nenoonkiosk.ui.theme.White

/**
 * IoT 디바이스 관리 화면 공통 레이아웃
 *
 * 악력계, 혈압계 등 IoT 디바이스 연결 관리 화면의 공통 UI 구조를 제공합니다.
 *
 * @param titleRes 화면 제목 리소스 ID (showStartTest=true일 때는 표시 안 함)
 * @param imageRes 디바이스 이미지 리소스 ID
 * @param imageContentDescriptionRes 이미지 설명 리소스 ID
 * @param batteryLevel 배터리 레벨 (null이면 숨김)
 * @param hideBattery 배터리 표시 숨김 여부
 * @param showStartTest 검사 시작 화면 모드 (true일 때 제목 숨김)
 * @param onBack 뒤로가기 버튼 클릭 핸들러
 * @param connectionContent 연결 상태별 콘텐츠 (Composable)
 * @param modifier Compose Modifier
 */
@Composable
fun DeviceManagementScreen(
    @StringRes titleRes: Int,
    @DrawableRes imageRes: Int,
    @StringRes imageContentDescriptionRes: Int,
    batteryLevel: Int?,
    hideBattery: Boolean,
    showStartTest: Boolean,
    onBack: () -> Unit,
    connectionContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (!showStartTest) {
            StyledText(stringResource(titleRes), TextStyle.Title)
        }

        if (isLandscape()) {
            Row(
                modifier = Modifier.weight(3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(
                    modifier = Modifier.weight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    BatteryStatus(batteryLevel, hidden = hideBattery)

                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = stringResource(imageContentDescriptionRes),
                        modifier = Modifier
                            .width(500.dp)
                            .height(300.dp),
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        connectionContent()
                    }

                    SecondaryButton(
                        onClick = onBack,
                        text = stringResource(R.string.back),
                        iconDrawable = R.drawable.icon_back_black,
                    )
                }
            }
        } else {
            BatteryStatus(batteryLevel, hidden = hideBattery)

            Image(
                painter = painterResource(imageRes),
                contentDescription = stringResource(imageContentDescriptionRes),
                modifier = Modifier.weight(1f).width(500.dp),
            )

            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    connectionContent()
                }

                SecondaryButton(
                    onClick = onBack,
                    text = stringResource(R.string.back),
                    iconDrawable = R.drawable.icon_back_black,
                )
            }
        }
    }
}

/* ---------- PREVIEW ---------- */

@Preview(
    showBackground = true,
    name = "InGrip - 가로 (연결 완료, 설정)",
    backgroundColor = 0xFFFFFFFF,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun DeviceManagementInGripConnectedPreview() {
    NenoonKioskTheme {
        DeviceManagementScreen(
            titleRes = R.string.dynamometer_title,
            imageRes = R.drawable.grip_strength_icon,
            imageContentDescriptionRes = R.string.dynamometer_image_content_description,
            batteryLevel = 85,
            hideBattery = false,
            showStartTest = false,
            onBack = {},
            connectionContent = {
                InGripConnectionStateContent(
                    state = InGripManagementUiState(
                        connectionState = InGripConnectionUiState.AwaitingStart,
                        batteryLevel = 85,
                        availableDevices = emptyList(),
                        connecting = false,
                    ),
                    onEvent = {},
                    showStartTest = false,
                )
            }
        )
    }
}

@Preview(
    showBackground = true,
    name = "InGrip - 가로 (연결 완료, 검사)",
    backgroundColor = 0xFFFFFFFF,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun DeviceManagementInGripTestPreview() {
    NenoonKioskTheme {
        DeviceManagementScreen(
            titleRes = R.string.dynamometer_title,
            imageRes = R.drawable.grip_strength_icon,
            imageContentDescriptionRes = R.string.dynamometer_image_content_description,
            batteryLevel = 85,
            hideBattery = false,
            showStartTest = true,
            onBack = {},
            connectionContent = {
                InGripConnectionStateContent(
                    state = InGripManagementUiState(
                        connectionState = InGripConnectionUiState.AwaitingStart,
                        batteryLevel = 85,
                        availableDevices = emptyList(),
                        connecting = false,
                    ),
                    onEvent = {},
                    showStartTest = true,
                )
            }
        )
    }
}

@Preview(
    showBackground = true,
    name = "BP170B - 가로 (Standby)",
    backgroundColor = 0xFFFFFFFF,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun DeviceManagementBP170BStandbyPreview() {
    NenoonKioskTheme {
        DeviceManagementScreen(
            titleRes = R.string.blood_pressure_monitor_title,
            imageRes = R.drawable.blood_pressure_icon,
            imageContentDescriptionRes = R.string.blood_pressure_monitor_image_content_description,
            batteryLevel = null,
            hideBattery = true,
            showStartTest = false,
            onBack = {},
            connectionContent = {
                BP170BConnectionStateContent(
                    state = BP170BManagementUiState(
                        connectionState = BP170BConnectionUiState.Standby,
                        connecting = false,
                        availableDevices = emptyList(),
                    ),
                    onEvent = {},
                    showStartTest = false,
                )
            }
        )
    }
}

@Preview(
    showBackground = true,
    name = "BPBIO320 - 가로 (연결 완료)",
    backgroundColor = 0xFFFFFFFF,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun DeviceManagementBPBIO320ConnectedPreview() {
    NenoonKioskTheme {
        DeviceManagementScreen(
            titleRes = R.string.blood_pressure_monitor_title,
            imageRes = R.drawable.blood_pressure_icon,
            imageContentDescriptionRes = R.string.blood_pressure_monitor_image_content_description,
            batteryLevel = 72,
            hideBattery = false,
            showStartTest = false,
            onBack = {},
            connectionContent = {
                BPBIO320ConnectionStateContent(
                    state = BPBIO320ManagementUiState(
                        connectionState = BPBIO320ConnectionUiState.AwaitingStart,
                        batteryLevel = 72,
                        deviceName = "BPBIO320",
                    ),
                    onEvent = {},
                    showStartTest = false,
                )
            }
        )
    }
}

@Preview(
    showBackground = true,
    name = "InGrip - 세로 (Standby)",
    backgroundColor = 0xFFFFFFFF,
    widthDp = 800,
    heightDp = 1280
)
@Composable
private fun DeviceManagementInGripPortraitPreview() {
    NenoonKioskTheme {
        DeviceManagementScreen(
            titleRes = R.string.dynamometer_title,
            imageRes = R.drawable.grip_strength_icon,
            imageContentDescriptionRes = R.string.dynamometer_image_content_description,
            batteryLevel = null,
            hideBattery = true,
            showStartTest = false,
            onBack = {},
            connectionContent = {
                InGripConnectionStateContent(
                    state = InGripManagementUiState(
                        connectionState = InGripConnectionUiState.Standby,
                        batteryLevel = null,
                        availableDevices = emptyList(),
                        connecting = false,
                    ),
                    onEvent = {},
                    showStartTest = false,
                )
            }
        )
    }
}
