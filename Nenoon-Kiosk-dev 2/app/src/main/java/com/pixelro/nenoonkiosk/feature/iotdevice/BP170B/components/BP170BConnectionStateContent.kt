package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.components

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.BP170BConnectionUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.BP170BManagementEvent
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.BP170BManagementUiState
import com.pixelro.nenoonkiosk.feature.iotdevice.common.components.AccentedTextWithButton
import com.pixelro.nenoonkiosk.feature.iotdevice.common.components.TextWithButton
import com.pixelro.nenoonkiosk.feature.iotdevice.common.components.TextWithTwoButtons

@SuppressLint("MissingPermission")
@Composable
fun BP170BConnectionStateContent(
    state: BP170BManagementUiState,
    onEvent: (BP170BManagementEvent) -> Unit,
    modifier: Modifier = Modifier,
    showStartTest: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (state.connectionState) {
            BP170BConnectionUiState.Standby -> {
                AccentedTextWithButton(
                    prefixRes = R.string.blood_pressure_monitor_standby_instruction1,
                    accentRes = R.string.blood_pressure_monitor_standby_instruction2,
                    suffixRes = R.string.blood_pressure_monitor_standby_instruction3,
                    buttonTextRes = R.string.blood_pressure_monitor_start_connection,
                    onButtonClick = { onEvent(BP170BManagementEvent.StartConnection) }
                )
            }

            BP170BConnectionUiState.DeviceSelection -> {
                if (state.availableDevices.isNotEmpty()) {
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_select_device),
                        style = TextStyle.Message,
                        modifier = Modifier.padding(top = 60.dp),
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                    ) {
                        items(state.availableDevices) { device ->
                            ListItem(
                                headlineContent = {
                                    StyledText(
                                        text = device.name ?: stringResource(R.string.dynamometer_unknown_device_name),
                                        textAlign = TextAlign.Start,
                                    )
                                },
                                supportingContent = {
                                    StyledText(
                                        text = device.address,
                                        style = TextStyle.Hint,
                                        textAlign = TextAlign.Start,
                                    )
                                },
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = colorResource(R.color.gray2),
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .clickable {
                                        onEvent(BP170BManagementEvent.DeviceSelected(device))
                                    },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    if (state.connecting) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            ProgressIndicator()
                            StyledText(
                                text = stringResource(R.string.blood_pressure_monitor_searching_device),
                                modifier = Modifier.padding(bottom = 180.dp),
                            )
                        }
                    } else {
                        StyledText(
                            text = stringResource(R.string.blood_pressure_monitor_connection_error),
                            style = TextStyle.Error,
                        )
                        PrimaryButton(
                            onClick = { onEvent(BP170BManagementEvent.Retry) },
                            text = stringResource(R.string.blood_pressure_monitor_retry_connection),
                            modifier = Modifier.padding(top = 180.dp, bottom = 20.dp),
                        )
                    }
                }
            }

            BP170BConnectionUiState.Connecting -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    ProgressIndicator()
                    StyledText(
                        text = stringResource(R.string.blood_pressure_monitor_connecting),
                        modifier = Modifier.padding(bottom = 180.dp),
                    )
                }
            }

            BP170BConnectionUiState.AwaitingStart -> {
                if (showStartTest) {
                    TextWithTwoButtons(
                        textRes = R.string.blood_pressure_monitor_device_connected,
                        primaryButtonTextRes = R.string.start,
                        onPrimaryButtonClick = { onEvent(BP170BManagementEvent.StartTest) },
                        secondaryButtonTextRes = R.string.blood_pressure_monitor_disconnect,
                        onSecondaryButtonClick = { onEvent(BP170BManagementEvent.Disconnect) }
                    )
                } else {
                    TextWithButton(
                        textRes = R.string.blood_pressure_monitor_device_connected,
                        buttonTextRes = R.string.blood_pressure_monitor_disconnect,
                        onButtonClick = { onEvent(BP170BManagementEvent.Disconnect) }
                    )
                }
            }

            BP170BConnectionUiState.ConnectionError -> {
                Spacer(modifier = Modifier.weight(1f))
                StyledText(
                    text = stringResource(R.string.blood_pressure_monitor_connection_error),
                    style = TextStyle.Error,
                )
                Spacer(modifier = Modifier.weight(1f))
                PrimaryButton(
                    onClick = { onEvent(BP170BManagementEvent.Retry) },
                    text = stringResource(R.string.blood_pressure_monitor_try_again),
                    modifier = Modifier.padding(top = 120.dp, bottom = 20.dp),
                )
            }

            BP170BConnectionUiState.TurnOffDevice -> {
                Spacer(modifier = Modifier.weight(1f))
                StyledText(
                    text = stringResource(R.string.bp170b_initialization_required),
                    style = TextStyle.Error,
                )
                Spacer(modifier = Modifier.height(40.dp))
                StyledText(
                    text = stringResource(R.string.bp170b_turn_off_bp_monitor),
                    style = TextStyle.Error,
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
