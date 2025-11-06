package com.pixelro.nenoonkiosk.feature.iotdevice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.core.manager.InGripManager
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.ui.TopBarOrientation
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ViewModel
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripViewModel


/**
 * 블루투스 디바이스 관리 메인 메뉴 화면
 *
 * 연결 가능한 IoT 디바이스 목록을 표시하고 각 디바이스 관리 화면으로 이동하는 메뉴 역할을 합니다.
 *
 * 표시 디바이스:
 * - BPBIO320 (24년형 혈압계)
 * - BP170B (25년형 혈압계)
 * - InGrip (악력계)
 *
 * @param navController 네비게이션 컨트롤러
 * @param bpbio320ViewModel BPBIO320 ViewModel (24년형 혈압계)
 * @param bp170bViewModel BP170B ViewModel (25년형 혈압계)
 * @param inGripViewModel InGrip ViewModel (악력계)
 */
@Composable
fun BTDeviceMenuScreen(
    navController: NavController,
    bpbio320ViewModel: BPBIO320ViewModel,
    bp170bViewModel: BP170BViewModel,
    inGripViewModel: InGripViewModel,
) {
    val bpbio320ConnectionState by bpbio320ViewModel.connectionState.collectAsState()
    val bp170bConnectionState by bp170bViewModel.connectionState.collectAsState()
    val inGripConnectionState by InGripManager.connectionState.collectAsState()

    val isBPBIO320Connected = bpbio320ConnectionState == IB_SDKConst.CONNECTED
    val isBP170BConnected = bp170bConnectionState == BP170BManager.BluetoothConnectionState.CONNECTED
    val isInGripConnected = inGripConnectionState == InGripManager.BluetoothConnectionState.CONNECTED

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        NenoonTopBar(
            title = stringResource(R.string.bt_device_management_title),
            orientation = TopBarOrientation.Vertical,
            showBackButton = true,
            onBackClicked = {
                navController.popBackStack(NavConstants.ROUTE_SIGN_IN, false)
            },
        )

        Spacer(modifier = Modifier.size(20.dp))

        //24년형 혈압계 연결 상태
        if (isBPBIO320Connected) {
            StyledText(
                stringResource(R.string.bt_device_management_bp_connected),
                TextStyle.Success,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        } else {
            StyledText(
                stringResource(R.string.bt_device_management_bp_disconnected),
                TextStyle.Error,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        PrimaryButton(
            text = stringResource(R.string.bt_device_management_manage_bp) + " (BPBIO320)",
            onClick = { navController.navigate(NavConstants.ROUTE_BPBIO320_CONNECT) },
            modifier = Modifier.padding(horizontal = 40.dp),
        )

        Spacer(modifier = Modifier.size(24.dp))

        //25년형 혈압계 연결 상태
        if (isBP170BConnected) {
            StyledText(
                stringResource(R.string.bt_device_management_bp_connected),
                TextStyle.Success,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        } else {
            StyledText(
                stringResource(R.string.bt_device_management_bp_disconnected),
                TextStyle.Error,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        PrimaryButton(
            text = stringResource(R.string.bt_device_management_manage_bp) + " (BP170B)",
            onClick = { navController.navigate(NavConstants.ROUTE_BP170B_CONNECT) },
            modifier = Modifier.padding(horizontal = 40.dp),
        )

        Spacer(modifier = Modifier.size(24.dp))

        //악력계 연결 상태
        if (isInGripConnected) {
            StyledText(
                stringResource(R.string.bt_device_management_dynamometer_connected),
                TextStyle.Success,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        } else {
            StyledText(
                stringResource(R.string.bt_device_management_dynamometer_disconnected),
                TextStyle.Error,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        PrimaryButton(
            text = StringProvider.getString(R.string.bt_device_management_manage_dynamometer),
            onClick = { navController.navigate(NavConstants.ROUTE_INGRIP_CONNECT) },
            modifier = Modifier.padding(horizontal = 40.dp),
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
