package com.pixelro.nenoonkiosk.feature.inspection.externaldevicelist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState

/**
 * 외부 장비 검사 목록 화면의 Route
 *
 * ViewModel의 State를 구독하고 Screen에 전달합니다.
 * 필요한 상태를 파라미터로 받아 ViewModel과 동기화합니다.
 */
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ExternalDeviceInspectionListRoute(
    isBloodPressureDone: Boolean,
    isGripStrengthDone: Boolean,
    isSenior: Boolean,
    viewModel: ExternalDeviceInspectionListViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()

    // 검사 완료 상태 동기화
    LaunchedEffect(isBloodPressureDone, isGripStrengthDone, isSenior) {
        viewModel.updateInspectionStatus(
            isBloodPressureDone = isBloodPressureDone,
            isGripStrengthDone = isGripStrengthDone,
            isSenior = isSenior
        )
    }

    // 화면 초기화
    LaunchedEffect(Unit) {
        viewModel.initializeScreen()
    }

    ExternalDeviceInspectionListScreen(
        state = state,
        onSideEffect = viewModel::onSideEffect,
        onBackClick = viewModel::navigateBack,
        onSettingsClick = viewModel::navigateToSettings,
        onNavigateToInspection = viewModel::navigateToInspection,
        onNavigateToSurvey = viewModel::navigateToSurvey
    )
}
