package com.pixelro.nenoonkiosk.feature.inspection.gripStrength

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.InGripManager
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripViewModel
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

enum class GripStrengthTestScreen {
    Start,
    Instructions,
    InProgress,
    Error,
}

@Composable
fun GripStrengthTestContent(
    toResultScreen: (GripStrengthTestResult) -> Unit,
    navController: NavHostController,
    isSignedIn: Boolean,
    gripStrengthViewModel: InGripViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel,
) {
//    toResultScreen(gripStrengthViewModel.getGripStrengthData())

    val localNavController = rememberNavController()
    val context = LocalContext.current
    val isDynamometerInitialized by InGripManager.isInitialized.collectAsState()

    // LaunchedEffect for handling Bluetooth connection state and data reception
    LaunchedEffect(Unit) {
        InGripManager.connectionState.collectLatest { state ->

            if (!isDynamometerInitialized) {
                InGripManager.init(context)
            }

            when (state) {
                is InGripManager.BluetoothConnectionState.DISCONNECTED -> {
                    localNavController.popBackStack(GripStrengthTestScreen.Start.name, false)
                }
                else -> { /* CONNECTING, ERROR states do not change main testState yet */ }
            }
        }
    }

    NavHost(navController = localNavController, startDestination = GripStrengthTestScreen.Start.name) {
        composable(GripStrengthTestScreen.Start.name) {
            GripStrengthStartScreen(
                navController = localNavController,
                viewModel = gripStrengthViewModel,
                onBack = { navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false) },
            )
        }
        composable(GripStrengthTestScreen.Instructions.name) {
            GripStrengthInstructionsScreen(
                navController = localNavController,
                viewModel = gripStrengthViewModel,
            )
        }
        composable(GripStrengthTestScreen.InProgress.name) {
            LaunchedEffect(Unit) {
                gripStrengthViewModel.resetTest()
            }
            GripStrengthInProgressScreen(
                navController = localNavController,
                viewModel = gripStrengthViewModel,
                toResultScreen = toResultScreen,
            )
        }
        composable(GripStrengthTestScreen.Error.name) {
            GripStrengthErrorScreen(
                onReturn = {
                    navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
                },
                onLogout = {
                    loginViewModel.userSignOut()
                    navController.navigate(NavConstants.ROUTE_SIGN_IN)
                },
                navController = localNavController,
                isSignedIn = isSignedIn,
                viewModel = gripStrengthViewModel,
            )
        }
    }
}
