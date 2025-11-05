package com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ConnectionUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ManagementEvent
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ManagementUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.common.components.AccentedTextWithButton
import com.pixelro.nenoonkiosk.feature.iotdevice.common.components.LoadingWithText
import com.pixelro.nenoonkiosk.feature.iotdevice.common.components.TextWithButton
import com.pixelro.nenoonkiosk.feature.iotdevice.common.components.TextWithTwoButtons

/**
 * 연결 상태별 콘텐츠
 *
 * @param state UI 상태
 * @param onEvent 이벤트 핸들러
 * @param showStartTest 검사 시작 버튼 표시 여부 (기본값: false)
 */
@Composable
fun BPBIO320ConnectionStateContent(
    state: BPBIO320ManagementUiState,
    onEvent: (BPBIO320ManagementEvent) -> Unit,
    modifier: Modifier = Modifier,
    showStartTest: Boolean = false,
) {
    when (state.connectionState) {
        // 대기 상태
        BPBIO320ConnectionUiState.Standby -> {
            AccentedTextWithButton(
                prefixRes = R.string.blood_pressure_monitor_standby_instruction1,
                accentRes = R.string.blood_pressure_monitor_standby_instruction2,
                suffixRes = R.string.blood_pressure_monitor_standby_instruction3,
                buttonTextRes = R.string.blood_pressure_monitor_start_connection,
                onButtonClick = { onEvent(BPBIO320ManagementEvent.StartConnection) },
            )
        }

        // 검색 중 또는 유휴
        BPBIO320ConnectionUiState.SearchingOrIdle -> {
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
                    ProgressIndicator()
                }
                PrimaryButton(
                    onClick = { onEvent(BPBIO320ManagementEvent.Retry) },
                    text = stringResource(R.string.blood_pressure_monitor_retry_connection),
                    modifier = Modifier.padding(bottom = 20.dp),
                )
            }
        }

        // 연결 중
        BPBIO320ConnectionUiState.Connecting -> {
            LoadingWithText(
                textRes = R.string.blood_pressure_monitor_connecting,
            )
        }

        // 연결 완료
        BPBIO320ConnectionUiState.AwaitingStart -> {
            if (showStartTest) {
                TextWithTwoButtons(
                    textRes = R.string.blood_pressure_monitor_device_connected,
                    primaryButtonTextRes = R.string.test_predescription_blood_pressure_title1,
                    onPrimaryButtonClick = { onEvent(BPBIO320ManagementEvent.StartTest) },
                    secondaryButtonTextRes = R.string.blood_pressure_monitor_disconnect,
                    onSecondaryButtonClick = { onEvent(BPBIO320ManagementEvent.Disconnect) },
                )
            } else {
                TextWithButton(
                    textRes = R.string.blood_pressure_monitor_device_connected,
                    buttonTextRes = R.string.blood_pressure_monitor_disconnect,
                    onButtonClick = { onEvent(BPBIO320ManagementEvent.Disconnect) },
                )
            }
        }

        // 연결 오류
        BPBIO320ConnectionUiState.ConnectionError -> {
            TextWithButton(
                textRes = R.string.blood_pressure_monitor_connection_error,
                buttonTextRes = R.string.blood_pressure_monitor_try_again,
                onButtonClick = { onEvent(BPBIO320ManagementEvent.Retry) },
                textStyle = TextStyle.Error,
            )
        }
    }
}