package com.pixelro.nenoonkiosk.feature.inspection

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.main.NenoonViewModel

@SuppressLint("NewApi")
@Composable
fun InspectionScreenRoute(
    viewModel: NenoonViewModel,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    val selectedTestType by viewModel.selectedTestType.collectAsState()
    val systemUiController = rememberSystemUiController()

    // 뒤로가기 동작
    BackHandler(enabled = true) {
        when (selectedTestType) {
            InspectionType.Dementia ->
                navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
            InspectionType.GripStrength, InspectionType.BloodPressure ->
                navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
            InspectionType.Presbyopia_Glasses, InspectionType.Concentration_Glasses ->
                navController.popBackStack(NavConstants.ROUTE_EXERCISE_LIST, false)
            else -> {
                TTS.stopTTS()
                navController.popBackStack(NavConstants.ROUTE_TEST_LIST, false)
            }
        }
        viewModel.resetScreenSaverTimer()
    }

    // 시스템바/화면보호기 타이머 제어
    DisposableEffect(selectedTestType) {
        viewModel.updateScreenSaverTimerValue(Int.MAX_VALUE)
        systemUiController.systemBarsDarkContentEnabled =
            selectedTestType in setOf(InspectionType.Dementia, InspectionType.GripStrength, InspectionType.BloodPressure)

        onDispose {
            viewModel.updateScreenSaverTimerValue(60)
            viewModel.resetScreenSaverTimer()
            systemUiController.systemBarsDarkContentEnabled = true
        }
    }

    // UI는 순수 렌더
    InspectionScreen(
        testType = selectedTestType,
        onClickClose = {
            when (selectedTestType) {
                InspectionType.Dementia ->
                    navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
                InspectionType.GripStrength, InspectionType.BloodPressure ->
                    navController.popBackStack(NavConstants.ROUTE_EXTERNAL_DEVICE_TEST_LIST, false)
                InspectionType.Presbyopia_Glasses, InspectionType.Concentration_Glasses ->
                    navController.popBackStack(NavConstants.ROUTE_EXERCISE_LIST, false)
                else -> {
                    TTS.stopTTS()
                    navController.popBackStack(NavConstants.ROUTE_TEST_LIST, false)
                }
            }
            viewModel.resetScreenSaverTimer()
        },
        content = content
    )
}