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
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.error.GripStrengthErrorRoute
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.inprogress.GripStrengthInProgressRoute
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.instructions.GripStrengthInstructionsRoute
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.result.GripStrengthInspectionResultContract
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.start.GripStrengthStartRoute
import kotlinx.coroutines.flow.collectLatest

enum class GripStrengthInspectionNavRoute {
    Start,
    Instructions,
    InProgress,
    Error,
}

@Composable
fun GripStrengthInspectionEntryPoint(
    toResultScreen: (GripStrengthInspectionResultContract) -> Unit,
    navController: NavHostController,
    isSignedIn: Boolean,
    gripStrengthViewModel: InGripViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel,
) {
    val localNavController = rememberNavController()
    val context = LocalContext.current
    val isDynamometerInitialized by InGripManager.isInitialized.collectAsState()

    LaunchedEffect(Unit) {
        InGripManager.connectionState.collectLatest { state ->
            if (!isDynamometerInitialized) {
                InGripManager.init(context)
            }

            when (state) {
                is InGripManager.BluetoothConnectionState.DISCONNECTED -> {
                    localNavController.popBackStack(GripStrengthInspectionNavRoute.Start.name, false)
                }
                else -> { /* CONNECTING, ERROR states do not change main testState yet */ }
            }
        }
    }

    NavHost(navController = localNavController, startDestination = GripStrengthInspectionNavRoute.Start.name) {
        composable(GripStrengthInspectionNavRoute.Start.name) {
            GripStrengthStartRoute(
                navController = localNavController,
                viewModel = gripStrengthViewModel,
                onBack = {
                    navController.popBackStack(
                        NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST,
                        false
                    )
                },
            )
        }
        composable(GripStrengthInspectionNavRoute.Instructions.name) {
            GripStrengthInstructionsRoute(
                navController = localNavController,
            )
        }
        composable(GripStrengthInspectionNavRoute.InProgress.name) {
            LaunchedEffect(Unit) {
                gripStrengthViewModel.resetTest()
            }
            GripStrengthInProgressRoute(
                navController = localNavController,
                viewModel = gripStrengthViewModel,
                toResultScreen = toResultScreen,
            )
        }
        composable(GripStrengthInspectionNavRoute.Error.name) {
            GripStrengthErrorRoute(
                onReturn = {
                    navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
                },
                onLogout = {
                    loginViewModel.userSignOut()
                    navController.navigate(NavConstants.ROUTE_SIGN_IN)
                },
                navController = localNavController,
                isSignedIn = isSignedIn,
            )
        }
    }
}
