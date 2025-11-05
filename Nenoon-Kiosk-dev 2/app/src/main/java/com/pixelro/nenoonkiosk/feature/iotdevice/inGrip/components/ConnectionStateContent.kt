package com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripConnectionUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripManagementEvent
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripManagementUiState

/**
 * 연결 상태별 콘텐츠
 *
 * @param state UI 상태
 * @param onEvent 이벤트 핸들러
 * @param showStartTest 검사 시작 버튼 표시 여부 (기본값: false)
 */
@Composable
fun ConnectionStateContent(
    state: InGripManagementUiState,
    onEvent: (InGripManagementEvent) -> Unit,
    modifier: Modifier = Modifier,
    showStartTest: Boolean = false,
) {
    when {
        // 대기 상태
        state.connectionState == InGripConnectionUiState.Standby -> {
            AccentedTextWithButton(
                prefixRes = R.string.dynamometer_standby_instruction1,
                accentRes = R.string.dynamometer_standby_instruction2,
                suffixRes = R.string.dynamometer_standby_instruction3,
                buttonTextRes = R.string.dynamometer_start_connection,
                onButtonClick = { onEvent(InGripManagementEvent.StartConnection) },
            )
        }

        // 디바이스 선택 - 디바이스 목록 있음
        state.availableDevices.isNotEmpty() -> {
            StyledText(
                stringResource(R.string.dynamometer_select_device),
                TextStyle.Message,
                modifier = Modifier.padding(top = 60.dp),
            )
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp),
            ) {
                items(state.availableDevices) { device ->
                    DeviceListItem(
                        device = device,
                        onClick = { onEvent(InGripManagementEvent.DeviceSelected(device)) },
                    )
                }
            }
        }

        // 검색 중
        state.connecting -> {
            LoadingWithText(
                textRes = R.string.dynamometer_searching,
            )
        }

        // 연결 중
        state.connectionState == InGripConnectionUiState.Connecting -> {
            LoadingWithText(
                textRes = R.string.dynamometer_connecting,
            )
        }

        // 연결 완료
        state.connectionState == InGripConnectionUiState.AwaitingStart -> {
            if (showStartTest) {
                AccentedTextWithTwoButtons(
                    prefixRes = R.string.dynamometer_device_connected1,
                    accentRes = R.string.dynamometer_device_connected2,
                    suffixRes = R.string.dynamometer_device_connected3,
                    primaryButtonTextRes = R.string.dynamometer_device_connected2,
                    onPrimaryButtonClick = { onEvent(InGripManagementEvent.StartTest) },
                    secondaryButtonTextRes = R.string.dynamometer_disconnect,
                    onSecondaryButtonClick = { onEvent(InGripManagementEvent.Disconnect) },
                )
            } else {
                AccentedTextWithButton(
                    prefixRes = R.string.dynamometer_device_connected1,
                    accentRes = R.string.dynamometer_device_connected2,
                    suffixRes = R.string.dynamometer_device_connected3,
                    buttonTextRes = R.string.dynamometer_disconnect,
                    onButtonClick = { onEvent(InGripManagementEvent.Disconnect) },
                )
            }
        }

        // 연결 오류
        state.connectionState == InGripConnectionUiState.ConnectionError -> {
            TextWithButton(
                textRes = R.string.dynamometer_connection_error,
                buttonTextRes = R.string.dynamometer_try_again,
                onButtonClick = { onEvent(InGripManagementEvent.Retry) },
                topPadding = 80.dp,
                textStyle = TextStyle.Error,
            )
        }

        // 디바이스 없음
        else -> {
            TextWithButton(
                textRes = R.string.dynamometer_not_connected_instruction,
                buttonTextRes = R.string.dynamometer_retry_connection,
                onButtonClick = { onEvent(InGripManagementEvent.Retry) },
                topPadding = 180.dp,
                textStyle = TextStyle.Error,
            )
        }
    }
}

@Composable
private fun AccentedTextWithButton(
    prefixRes: Int,
    accentRes: Int,
    suffixRes: Int,
    buttonTextRes: Int,
    onButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AccentedText(
                prefix = stringResource(prefixRes),
                accent = stringResource(accentRes),
                suffix = stringResource(suffixRes),
            )
        }
        PrimaryButton(
            onClick = onButtonClick,
            text = stringResource(buttonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun AccentedTextWithTwoButtons(
    prefixRes: Int,
    accentRes: Int,
    suffixRes: Int,
    primaryButtonTextRes: Int,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonTextRes: Int,
    onSecondaryButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AccentedText(
                prefix = stringResource(prefixRes),
                accent = stringResource(accentRes),
                suffix = stringResource(suffixRes),
            )
        }
        PrimaryButton(
            onClick = onPrimaryButtonClick,
            text = stringResource(primaryButtonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
        PrimaryButton(
            onClick = onSecondaryButtonClick,
            text = stringResource(secondaryButtonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun TextWithButton(
    textRes: Int,
    buttonTextRes: Int,
    onButtonClick: () -> Unit,
    topPadding: androidx.compose.ui.unit.Dp = 180.dp,
    textStyle: TextStyle = TextStyle.Message,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            StyledText(
                stringResource(textRes),
                style = textStyle,
            )
        }
        PrimaryButton(
            onClick = onButtonClick,
            text = stringResource(buttonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun LoadingWithText(textRes: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ProgressIndicator()
        StyledText(
            text = stringResource(textRes),
        )
    }
}