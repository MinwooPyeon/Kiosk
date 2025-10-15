package com.pixelro.nenoonkiosk.test.bloodPressure

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.bTManager.BP170B.BP170BManager
import com.pixelro.nenoonkiosk.bTManager.BP170B.BP170BViewModel
import com.pixelro.nenoonkiosk.bTManager.BPBIO320.BPBIO320ViewModel
import com.pixelro.nenoonkiosk.constants.NavConstants
import com.pixelro.nenoonkiosk.data.SharedPreferencesManager
import com.pixelro.nenoonkiosk.user.SignInViewModel
import com.pixelro.nenoonkiosk.test.bloodPressure.BP170B.BP170BInProgressScreen
import com.pixelro.nenoonkiosk.test.bloodPressure.BP170B.BP170BStartScreen
import com.pixelro.nenoonkiosk.test.bloodPressure.BPBIO320.BPBIO320InProgressScreen
import com.pixelro.nenoonkiosk.test.bloodPressure.BPBIO320.BPBIO320StartScreen

enum class BloodPressureTestScreen {
    Start,
    Instructions,
    InProgress,
    Error
}

@Composable
fun BloodPressureTestContent(
    toResultScreen: (BloodPressureTestResult) -> Unit,
    navController: NavHostController,
    isSignedIn: Boolean,
    bpbiO320ViewModel: BPBIO320ViewModel,
    bP170BViewModel: BP170BViewModel = hiltViewModel(),
    signInViewModel: SignInViewModel,
) {

    val localNavController = rememberNavController()
    val bPBIO320ConnectionState by bpbiO320ViewModel.connectionState.collectAsState()
    val bP170BConnectionState by bP170BViewModel.connectionState.collectAsState()
    val bloodPressureMonitorType = SharedPreferencesManager.getBloodPressureMonitorType()

    LaunchedEffect(bPBIO320ConnectionState, bP170BConnectionState) {
        if ((bloodPressureMonitorType == SharedPreferencesManager.BloodPressureMonitorType.BPBIO320 && 
                    bPBIO320ConnectionState == IB_SDKConst.DISCONNECTED) ||
            (bloodPressureMonitorType == SharedPreferencesManager.BloodPressureMonitorType.BP170B && 
                    bP170BConnectionState == BP170BManager.BluetoothConnectionState.DISCONNECTED)) {
            localNavController.popBackStack(BloodPressureTestScreen.Start.name, false)
        }
    }

    NavHost(navController = localNavController, startDestination = BloodPressureTestScreen.Start.name) {


        composable(BloodPressureTestScreen.Start.name) {

            when (bloodPressureMonitorType) {
                SharedPreferencesManager.BloodPressureMonitorType.BPBIO320 ->
                    BPBIO320StartScreen(
                        navController = localNavController,
                        viewModel = bpbiO320ViewModel,
                        onBack = { navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false) }
                    )
                SharedPreferencesManager.BloodPressureMonitorType.BP170B ->
                    BP170BStartScreen(
                        navController = localNavController,
                        viewModel = bP170BViewModel,
                        onBack = { navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false) }
                    )
            }
        }
        composable(BloodPressureTestScreen.Instructions.name) {
            BloodPressureInstructionsScreen(
                navController = localNavController,
            )
        }
        composable(BloodPressureTestScreen.InProgress.name) {
            when (bloodPressureMonitorType) {
                SharedPreferencesManager.BloodPressureMonitorType.BPBIO320 ->
                    BPBIO320InProgressScreen(
                        navController = localNavController,
                        viewModel = bpbiO320ViewModel,
                        toResultScreen = toResultScreen
                    )
                SharedPreferencesManager.BloodPressureMonitorType.BP170B ->
                    BP170BInProgressScreen(
                        navController = localNavController,
                        viewModel = bP170BViewModel,
                        toResultScreen = toResultScreen
                    )
            }
        }
        composable(BloodPressureTestScreen.Error.name) {

            BloodPressureErrorScreen(
                onReturn = {
                    navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
                },
                onLogout = {
                    signInViewModel.userSignOut()
                    navController.navigate(NavConstants.ROUTE_SIGN_IN)
                },
                navController = localNavController,
                isSignedIn = isSignedIn,
            )
        }
    }
}