
package com.pixelro.nenoonkiosk.feature.inspection.gripStrength

/**
 * 악력 검사 진입점
 *
 * 네비게이션 흐름:
 * 1. Start (연결 화면)
 *    - InGripManagementRoute 사용 (showStartTest = true)
 *    - 악력계 기기 검색 → 연결 → "검사 시작" 버튼 표시
 *    - "검사 시작" 클릭 → Instructions로 이동
 *
 * 2. Instructions (사용 설명)
 *    - 측정 방법 안내
 *    - "시작" 클릭 → InProgress로 이동
 *
 * 3. InProgress (측정 중)
 *    - 악력 측정 진행
 *    - 측정 완료 → Result 화면으로 이동
 *
 * 4. Error (에러)
 *    - 연결 끊김 또는 측정 오류 시 표시
 *
 * 특이사항:
 * - 연결 해제 시 자동으로 Start 화면으로 복귀 (LaunchedEffect)
 * - InGripManagementRoute는 iotdevice/inGrip에서 재사용
 */

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
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.InGripManagementRoute
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
    
    LaunchedEffect(Unit) {
        if (!InGripManager.isInitialized.value) {
            InGripManager.init(context)
        }
    }

    LaunchedEffect(Unit) {
        InGripManager.connectionState.collectLatest { state ->
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
            InGripManagementRoute(
                navController = localNavController,
                viewModel = gripStrengthViewModel,
                showStartTest = true,
                onStartTest = {
                    localNavController.navigate(GripStrengthInspectionNavRoute.Instructions.name)
                },
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
