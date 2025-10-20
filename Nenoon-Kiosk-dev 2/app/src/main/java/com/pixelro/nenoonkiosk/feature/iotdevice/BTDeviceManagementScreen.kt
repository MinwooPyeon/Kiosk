package com.pixelro.nenoonkiosk.feature.iotdevice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.InGripManager
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ViewModel
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle

@Composable
fun BTDeviceManagementScreen(
    navController: NavController,
    bPBIO320ViewModel: BPBIO320ViewModel,
) {
    val bPBIO320ConnectionState by bPBIO320ViewModel.connectionState.collectAsState()
    val dynamometerConnectionState by InGripManager.connectionState.collectAsState()

    val isBPBIO320Connected = bPBIO320ConnectionState == IB_SDKConst.CONNECTED
    val isDynamometerConnected = dynamometerConnectionState == InGripManager.BluetoothConnectionState.CONNECTED

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .padding(
                    start = 40.dp,
                    top = (GlobalValue.statusBarPadding + 20).dp,
                    end = 40.dp,
                    bottom = 20.dp
                )
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    modifier = Modifier
                        .width(32.dp)
                        .clickable {
                            navController.popBackStack(NavConstants.ROUTE_SIGN_IN, false)
                        },
                    painter = painterResource(id = R.drawable.close_button_black),
                    contentDescription = ""
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = StringProvider.getString(R.string.bt_device_management_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(
            modifier = Modifier
                .padding(bottom = 5.dp, start = 5.dp, end = 5.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = Color(0xff000000)
                )
        )

        Spacer(modifier = Modifier.size(40.dp))

        if (isBPBIO320Connected) {
            StyledText(StringProvider.getString(R.string.bt_device_management_bp_connected),
                TextStyle.Success,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        } else {
            StyledText(
                StringProvider.getString(R.string.bt_device_management_bp_disconnected),
                TextStyle.Error,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
        Spacer(modifier = Modifier.size(20.dp))
        PrimaryButton(
            text = StringProvider.getString(R.string.bt_device_management_manage_bp) + " (BPBIO320)",
            onClick = { navController.navigate(NavConstants.ROUTE_BPBIO320_CONNECT) },
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.size(40.dp))

        if (isDynamometerConnected) {
            StyledText(
                StringProvider.getString(R.string.bt_device_management_dynamometer_connected),
                TextStyle.Success,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        } else {
            StyledText(
                StringProvider.getString(R.string.bt_device_management_dynamometer_disconnected),
                TextStyle.Error,
                TextAlign.Start,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }
        Spacer(modifier = Modifier.size(20.dp))
        PrimaryButton(
            text = StringProvider.getString(R.string.bt_device_management_manage_dynamometer),
            onClick = { navController.navigate(NavConstants.ROUTE_INGRIP_CONNECT) },
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}