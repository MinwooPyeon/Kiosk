package com.pixelro.nenoonkiosk.feature.iotdevice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.core.util.StringProvider


/**
 * 블루투스 디바이스 관리 메인 메뉴 화면 (순수 UI)
 *
 * @param isBPBIO320Connected BPBIO320 연결 상태
 * @param isBP170BConnected BP170B 연결 상태
 * @param isInGripConnected InGrip 연결 상태
 * @param selectedBloodPressureMonitor 현재 선택된 기본 혈압계
 * @param onBloodPressureMonitorChange 혈압계 변경 콜백
 * @param onBPBIO320ManageClick BPBIO320 관리 클릭 콜백
 * @param onBP170BManageClick BP170B 관리 클릭 콜백
 * @param onInGripManageClick InGrip 관리 클릭 콜백
 * @param onBackClick 뒤로가기 클릭 콜백
 */
@Composable
fun BTDeviceMenuScreen(
    isBPBIO320Connected: Boolean,
    isBP170BConnected: Boolean,
    isInGripConnected: Boolean,
    selectedBloodPressureMonitor: SharedPreferencesManager.BloodPressureMonitorType,
    onBloodPressureMonitorChange: (SharedPreferencesManager.BloodPressureMonitorType) -> Unit,
    onBPBIO320ManageClick: () -> Unit,
    onBP170BManageClick: () -> Unit,
    onInGripManageClick: () -> Unit,
    onBackClick: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        NenoonTopBar(
            title = stringResource(R.string.bt_device_management_title),
            orientation = TopBarOrientation.Vertical,
            showBackButton = true,
            onBackClicked = onBackClick,
        )

        Spacer(modifier = Modifier.size(20.dp))

        //24년형 혈압계 연결 상태
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isBPBIO320Connected) {
                StyledText(
                    text = stringResource(R.string.bt_device_management_bp_connected),
                    style = TextStyle.Success,
                    textAlign = TextAlign.Start,
                )
            } else {
                StyledText(
                    text = stringResource(R.string.bt_device_management_bp_disconnected),
                    style = TextStyle.Error,
                    textAlign = TextAlign.Start,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 기본 혈압계 설정 버튼
            TextButton(
                onClick = {
                    val newType =
                        if (selectedBloodPressureMonitor == SharedPreferencesManager.BloodPressureMonitorType.BPBIO320) {
                            SharedPreferencesManager.BloodPressureMonitorType.BP170B
                        } else {
                            SharedPreferencesManager.BloodPressureMonitorType.BPBIO320
                        }
                    onBloodPressureMonitorChange(newType)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.change_default_blood_pressure_monitor),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                StyledText(
                    text = "${stringResource(R.string.default_blood_pressure_monitor)}: ${if (selectedBloodPressureMonitor == SharedPreferencesManager.BloodPressureMonitorType.BPBIO320) "BPBIO320" else "BP170B"}",
                    textAlign = TextAlign.Start,
                )
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        PrimaryButton(
            text = stringResource(R.string.bt_device_management_manage_bp) + " (BPBIO320)",
            onClick = onBPBIO320ManageClick,
            modifier = Modifier.padding(horizontal = 40.dp),
        )

        Spacer(modifier = Modifier.size(24.dp))

        //25년형 혈압계 연결 상태
        if (isBP170BConnected) {
            StyledText(
                text = stringResource(R.string.bt_device_management_bp_connected),
                style = TextStyle.Success,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        } else {
            StyledText(
                text = stringResource(R.string.bt_device_management_bp_disconnected),
                style = TextStyle.Error,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        PrimaryButton(
            text = stringResource(R.string.bt_device_management_manage_bp) + " (BP170B)",
            onClick = onBP170BManageClick,
            modifier = Modifier.padding(horizontal = 40.dp),
        )

        Spacer(modifier = Modifier.size(24.dp))

        //악력계 연결 상태
        if (isInGripConnected) {
            StyledText(
                text = stringResource(R.string.bt_device_management_dynamometer_connected),
                style = TextStyle.Success,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        } else {
            StyledText(
                text = stringResource(R.string.bt_device_management_dynamometer_disconnected),
                style = TextStyle.Error,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        PrimaryButton(
            text = StringProvider.getString(R.string.bt_device_management_manage_dynamometer),
            onClick = onInGripManageClick,
            modifier = Modifier.padding(horizontal = 40.dp),
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
