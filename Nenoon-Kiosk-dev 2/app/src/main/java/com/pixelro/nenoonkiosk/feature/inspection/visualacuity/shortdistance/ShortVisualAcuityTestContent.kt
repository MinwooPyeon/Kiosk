package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.facedetection.MeasuringDistanceContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.VisualAcuityTestCommonContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.VisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.VisualAcuityViewModel

@Composable
fun ShortDistanceVisualAcuityTestContent(
    toResultScreen: (VisualAcuityTestResult) -> Unit,
    visualAcuityViewModel: VisualAcuityViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        visualAcuityViewModel.init()
    }
    val measuringDistanceContentVisibleState = remember { MutableTransitionState(true) }
    measuringDistanceContentVisibleState.targetState = visualAcuityViewModel.isMeasuringDistanceContentVisible.collectAsState().value
    val visualAcuityContentVisibleState = remember { MutableTransitionState(false) }
    visualAcuityContentVisibleState.targetState = visualAcuityViewModel.isVisualAcuityContentVisible.collectAsState().value
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(0xff000000),
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            /**
             * 거리 조절 화면
             */
            MeasuringDistanceContent(
                measuringDistanceContentVisibleState = measuringDistanceContentVisibleState,
                toNextContent = {
                    visualAcuityViewModel.updateIsMeasuringDistanceContentVisible(false)
                    visualAcuityViewModel.updateIsVisualAcuityContentVisible(true)
                },
                selectedTestType = InspectionType.ShortDistanceVisualAcuity,
                isLeftEye = visualAcuityViewModel.isLeftEye.collectAsState().value,
            )
            /**
             * 시력 검사 화면
             */
            VisualAcuityTestCommonContent(
                visualAcuityTestCommonContentVisibleState = visualAcuityContentVisibleState,
                toResultScreen = toResultScreen,
            )
        }
    }
}
