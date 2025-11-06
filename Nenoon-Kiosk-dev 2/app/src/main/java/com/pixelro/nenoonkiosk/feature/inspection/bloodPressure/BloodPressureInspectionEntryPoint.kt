package com.pixelro.nenoonkiosk.feature.inspection.bloodPressure

/**
 * 혈압 검사 진입점
 *
 * 네비게이션 흐름:
 * 1. Start (연결 화면)
 *    - BPBIO320ManagementRoute 또는 BP170BManagementRoute 사용 (showStartTest = true)
 *    - 혈압계 기기 검색 → 연결 → "검사 시작" 버튼 표시
 *    - "검사 시작" 클릭 → Instructions로 이동
 *
 * 2. Instructions (사용 설명)
 *    - 측정 방법 안내
 *    - "시작" 클릭 → InProgress로 이동
 *
 * 3. InProgress (측정 중)
 *    - 혈압 측정 진행
 *    - 측정 완료 → Result 화면으로 이동
 *
 * 4. Error (에러)
 *    - 연결 끊김 또는 측정 오류 시 표시
 *
 * 특이사항:
 * - 연결 해제 시 자동으로 Start 화면으로 복귀 (LaunchedEffect)
 * - BPBIO320ManagementRoute, BP170BManagementRoute는 iotdevice/에서 재사용
 * - SharedPreferencesManager로 BPBIO320(24년형) vs BP170B(25년형) 구분
 */

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
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BPBIO320.inprogress.BPBIO320InProgressRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.error.BloodPressureErrorRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.instructions.BloodPressureInstructionsRoute
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResult
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ManagementRoute
import com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320.BPBIO320ViewModel
import com.pixelro.nenoonkiosk.feature.iotdevice.BP170B.BP170BManagementRoute

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
                    BPBIO320ManagementRoute(
                        navController = localNavController,
                        viewModel = bpbiO320ViewModel,
                        showStartTest = true,
                        onStartTest = {
                            localNavController.navigate(BloodPressureInspectionNavRoute.Instructions.name)
                        },
                        onBack = { navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false) },
                    )
                SharedPreferencesManager.BloodPressureMonitorType.BP170B ->
                    BP170BManagementRoute(
                        navController = localNavController,
                        viewModel = bP170BViewModel,
                        showStartTest = true,
                        onStartTest = {
                            localNavController.navigate(BloodPressureInspectionNavRoute.Instructions.name)
                        },
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
                    loginViewModel.userSignOut()
                    navController.navigate(NavConstants.ROUTE_SIGN_IN)
                },
                navController = localNavController,
                isSignedIn = isSignedIn,
            )
        }
    }
}
