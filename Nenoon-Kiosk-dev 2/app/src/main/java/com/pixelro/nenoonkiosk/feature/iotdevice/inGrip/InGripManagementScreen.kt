package com.pixelro.nenoonkiosk.feature.iotdevice.inGrip

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
import androidx.compose.ui.graphics.Color
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
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.components.ConnectionStateContent

/**
 * 악력계 블루투스 연결 관리 화면
 *
 * 악력계 기기 검색, 연결, 연결 해제 등의 기능을 제공합니다.
 *
 * @param state 악력계 관리 화면의 UI 상태 (연결 상태, 배터리 레벨, 사용 가능한 기기 목록 등)
 * @param onEvent 사용자 이벤트 핸들러 (연결 시작, 기기 선택, 연결 해제, 재시도, 뒤로가기 등)
 * @param modifier Compose Modifier
 * @param showStartTest 검사 시작 버튼 표시 여부 (기본값: false)
 */
@Composable
fun InGripManagementScreen(
    state: InGripManagementUiState,
    onEvent: (InGripManagementEvent) -> Unit,
    modifier: Modifier = Modifier,
    showStartTest: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (!showStartTest) {
            StyledText(stringResource(R.string.dynamometer_title), TextStyle.Title)
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
                    BatteryStatus(state.batteryLevel, hidden = !state.isBatteryFetching && state.batteryLevel == null)

                    Image(
                        painter = painterResource(R.drawable.grip_strength_icon),
                        contentDescription = stringResource(R.string.dynamometer_image_content_description),
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
                        ConnectionStateContent(
                            state = state,
                            onEvent = onEvent,
                            showStartTest = showStartTest,
                        )
                    }

                    SecondaryButton(
                        onClick = { onEvent(InGripManagementEvent.Back) },
                        text = stringResource(R.string.back),
                        iconDrawable = R.drawable.icon_back_black,
                    )
                }
            }
        } else {
            BatteryStatus(state.batteryLevel, hidden = !state.isBatteryFetching && state.batteryLevel == null)

            Image(
                painter = painterResource(R.drawable.grip_strength_icon),
                contentDescription = stringResource(R.string.dynamometer_image_content_description),
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
                    ConnectionStateContent(
                        state = state,
                        onEvent = onEvent,
                        showStartTest = showStartTest,
                    )
                }

                SecondaryButton(
                    onClick = { onEvent(InGripManagementEvent.Back) },
                    text = stringResource(R.string.back),
                    iconDrawable = R.drawable.icon_back_black,
                )
            }
        }
    }
}

@Preview(name = "Standby", showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun InGripManagementScreenStandbyPreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.Standby,
        ),
        onEvent = {},
    )
}

@Preview(name = "Searching", showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun InGripManagementScreenSearchingPreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.DeviceSelection,
            connecting = true,
        ),
        onEvent = {},
    )
}

@Preview(name = "Connecting", showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun InGripManagementScreenConnectingPreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.Connecting,
        ),
        onEvent = {},
    )
}

@Preview(name = "Connected", showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun InGripManagementScreenConnectedPreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.AwaitingStart,
            batteryLevel = 85,
        ),
        onEvent = {},
    )
}

@Preview(name = "Connection Error", showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun InGripManagementScreenErrorPreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.ConnectionError,
        ),
        onEvent = {},
    )
}

@Preview(name = "Standby Landscape", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun InGripManagementScreenStandbyLandscapePreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.Standby,
        ),
        onEvent = {},
    )
}

@Preview(name = "Searching Landscape", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun InGripManagementScreenSearchingLandscapePreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.DeviceSelection,
            connecting = true,
        ),
        onEvent = {},
    )
}

@Preview(name = "Connecting Landscape", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun InGripManagementScreenConnectingLandscapePreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.Connecting,
        ),
        onEvent = {},
    )
}

@Preview(name = "Connected Landscape", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun InGripManagementScreenConnectedLandscapePreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.AwaitingStart,
            batteryLevel = 85,
        ),
        onEvent = {},
    )
}

@Preview(name = "Connection Error Landscape", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun InGripManagementScreenErrorLandscapePreview() {
    InGripManagementScreen(
        state = InGripManagementUiState(
            connectionState = InGripConnectionUiState.ConnectionError,
        ),
        onEvent = {},
    )
}