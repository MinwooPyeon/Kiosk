package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.inprogress.BP170BInProgressRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BP170B.start.BP170BStartRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.inprogress.BPBIO320InProgressRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.start.BPBIO320StartRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.error.BloodPressureErrorRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.instructions.BloodPressureInstructionsRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResult
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ViewModel

enum class BloodPressureInspectionNavRoute {
    Start,
    Instructions,
    InProgress,
    Error,
}

@Composable
fun BloodPressureInspectionEntryPoint(
    toResultScreen: (BloodPressureInspectionResult) -> Unit,
    navController: NavHostController,
    isSignedIn: Boolean,
    bpbiO320ViewModel: BPBIO320ViewModel,
    bP170BViewModel: BP170BViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel,
) {
    val localNavController = rememberNavController()
    val bPBIO320ConnectionState by bpbiO320ViewModel.connectionState.collectAsState()
    val bP170BConnectionState by bP170BViewModel.connectionState.collectAsState()
    val bloodPressureMonitorType = SharedPreferencesManager.getBloodPressureMonitorType()

    LaunchedEffect(bPBIO320ConnectionState, bP170BConnectionState) {
        if ((
                bloodPressureMonitorType == SharedPreferencesManager.BloodPressureMonitorType.BPBIO320 &&
                    bPBIO320ConnectionState == IB_SDKConst.DISCONNECTED
            ) ||
            (
                bloodPressureMonitorType == SharedPreferencesManager.BloodPressureMonitorType.BP170B &&
                    bP170BConnectionState == BP170BManager.BluetoothConnectionState.DISCONNECTED
            )
        ) {
            localNavController.popBackStack(BloodPressureInspectionNavRoute.Start.name, false)
        }
    }

    NavHost(navController = localNavController, startDestination = BloodPressureInspectionNavRoute.Start.name) {
        composable(BloodPressureInspectionNavRoute.Start.name) {
            when (bloodPressureMonitorType) {
                SharedPreferencesManager.BloodPressureMonitorType.BPBIO320 ->
                    BPBIO320StartRoute(
                        navController = localNavController,
                        viewModel = bpbiO320ViewModel,
                        onBack = { navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false) },
                    )
                SharedPreferencesManager.BloodPressureMonitorType.BP170B ->
                    BP170BStartRoute(
                        navController = localNavController,
                        viewModel = bP170BViewModel,
                        onBack = { navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false) },
                    )
            }
        }
        composable(BloodPressureInspectionNavRoute.Instructions.name) {
            BloodPressureInstructionsRoute(
                navController = localNavController,
            )
        }
        composable(BloodPressureInspectionNavRoute.InProgress.name) {
            when (bloodPressureMonitorType) {
                SharedPreferencesManager.BloodPressureMonitorType.BPBIO320 ->
                    BPBIO320InProgressRoute(
                        navController = localNavController,
                        viewModel = bpbiO320ViewModel,
                        toResultScreen = toResultScreen,
                    )
                SharedPreferencesManager.BloodPressureMonitorType.BP170B ->
                    BP170BInProgressRoute(
                        navController = localNavController,
                        viewModel = bP170BViewModel,
                        toResultScreen = toResultScreen,
                    )
            }
        }
        composable(BloodPressureInspectionNavRoute.Error.name) {
            BloodPressureErrorRoute(
                onReturn = {
                    navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
                },
                onLogout = {
                    navController.navigate(NavConstants.ROUTE_SIGN_IN)
                },
                navController = localNavController,
                isSignedIn = isSignedIn,
            )
        }
    }
}
