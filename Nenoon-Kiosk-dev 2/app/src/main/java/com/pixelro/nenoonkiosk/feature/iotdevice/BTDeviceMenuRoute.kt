package com.pixelro.nenoonkiosk.feature.iotdevice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.core.manager.InGripManager
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ViewModel
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BTDeviceMenu Route
 *
 * ViewModel과 NavController를 연결하고 비즈니스 로직을 처리하는 레이어
 */
@Composable
fun BTDeviceMenuRoute(
    navController: NavController,
    bpbio320ViewModel: BPBIO320ViewModel,
    bp170bViewModel: BP170BViewModel,
    inGripViewModel: InGripViewModel,
) {
    // ViewModel 상태 수집
    val bpbio320ConnectionState by bpbio320ViewModel.connectionState.collectAsState()
    val bp170bConnectionState by bp170bViewModel.connectionState.collectAsState()
    val inGripConnectionState by InGripManager.connectionState.collectAsState()

    val isBPBIO320Connected = bpbio320ConnectionState == IB_SDKConst.CONNECTED
    val isBP170BConnected = bp170bConnectionState == BP170BManager.BluetoothConnectionState.CONNECTED
    val isInGripConnected = inGripConnectionState == InGripManager.BluetoothConnectionState.CONNECTED

    // 혈압계 설정 상태
    var selectedBloodPressureMonitor by remember {
        mutableStateOf(SharedPreferencesManager.getBloodPressureMonitorType())
    }

    val coroutineScope = rememberCoroutineScope()

    // Screen에 전달
    BTDeviceMenuScreen(
        isBPBIO320Connected = isBPBIO320Connected,
        isBP170BConnected = isBP170BConnected,
        isInGripConnected = isInGripConnected,
        selectedBloodPressureMonitor = selectedBloodPressureMonitor,
        onBloodPressureMonitorChange = { newType ->
            selectedBloodPressureMonitor = newType
            // IO 스레드에서 SharedPreferences 저장
            coroutineScope.launch(Dispatchers.IO) {
                SharedPreferencesManager.putBloodPressureMonitorType(newType)
            }
        },
        onBPBIO320ManageClick = {
            navController.navigate(NavConstants.ROUTE_BPBIO320_CONNECT)
        },
        onBP170BManageClick = {
            navController.navigate(NavConstants.ROUTE_BP170B_CONNECT)
        },
        onInGripManageClick = {
            navController.navigate(NavConstants.ROUTE_INGRIP_CONNECT)
        },
        onBackClick = {
            navController.popBackStack(NavConstants.ROUTE_SIGN_IN, false)
        }
    )
}