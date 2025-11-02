package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.shortdistance

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pixelro.nenoonkiosk.feature.facedetection.MeasuringDistanceContent
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.VisualAcuityInspectionCommonContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.VisualAcuityInspectionUiState
import com.pixelro.nenoonkiosk.ui.theme.Black

/**
 * 근거리 시력 검사 UI 컴포넌트 (순수 UI)
 *
 * ## 검사 흐름 (2단계)
 * 1단계: 거리 조정 (MeasuringDistanceContent)
 *    ↓
 *   40~50cm 거리 맞추기 완료
 *    ↓
 * 2단계: 시력 검사 (VisualAcuityInspectionCommonScreen)
 *    ↓
 *   란돌트 C 검사 (좌안 → 우안)
 *    ↓
 *   결과 화면
 *
 * @param uiState 현재 UI 상태 (MeasuringDistance, VisualAcuityTest)
 * @param measuringDistanceContentVisibleState 거리 조정 화면 표시 여부
 * @param visualAcuityContentVisibleState 시력 검사 화면 표시 여부
 * @param isLeftEye 현재 검사 중인 눈 (true: 좌안, false: 우안)
 * @param randomList 랜덤 방향 리스트 (3개)
 * @param ansNum 정답 방향 (2~7)
 * @param sightLevel 시력 난이도 (1~10)
 * @param isFaceDetected 얼굴 인식 여부
 * @param isFacingForward 정면 응시 여부
 * @param onNextFromDistance 거리 조정 완료 후 다음 단계로 진행하는 콜백
 * @param onAnswerSelected 답변 선택 시 호출되는 콜백 (idx, handleWrong)
 */
@Composable
fun ShortVisualAcuityInspectionScreen(
    uiState: VisualAcuityInspectionUiState,
    measuringDistanceContentVisibleState: MutableTransitionState<Boolean>,
    visualAcuityContentVisibleState: MutableTransitionState<Boolean>,
    isLeftEye: Boolean,
    randomList: List<Int>,
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    onNextFromDistance: () -> Unit,
    onAnswerSelected: (Int, (Float) -> Unit) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            MeasuringDistanceContent(
                measuringDistanceContentVisibleState = measuringDistanceContentVisibleState,
                toNextContent = onNextFromDistance,
                selectedTestType = InspectionType.ShortDistanceVisualAcuity,
                isLeftEye = isLeftEye
            )

            VisualAcuityInspectionCommonContent(
                visualAcuityInspectionCommonContentVisibleState = visualAcuityContentVisibleState,
                randomList = randomList,
                ansNum = ansNum,
                sightLevel = sightLevel,
                isFaceDetected = isFaceDetected,
                isFacingForward = isFacingForward,
                onAnswerSelected = onAnswerSelected
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    device = "spec:width=800dp,height=1280dp,dpi=240"
)
@Composable
private fun PreviewShortDistanceVisualAcuityInspection_MeasuringDistance() {
    val measuringDistanceState = MutableTransitionState(true)
    val visualAcuityState = MutableTransitionState(false)

    ShortVisualAcuityInspectionScreen(
        uiState = VisualAcuityInspectionUiState.MeasuringDistance,
        measuringDistanceContentVisibleState = measuringDistanceState,
        visualAcuityContentVisibleState = visualAcuityState,
        isLeftEye = true,
        randomList = listOf(2, 5, 7),
        ansNum = 2,
        sightLevel = 1,
        isFaceDetected = true,
        isFacingForward = true,
        onNextFromDistance = {},
        onAnswerSelected = { _, _ -> }
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    device = "spec:width=800dp,height=1280dp,dpi=240"
)
@Composable
private fun PreviewShortDistanceVisualAcuityInspection_VisualAcuityTest() {
    val measuringDistanceState = MutableTransitionState(false)
    val visualAcuityState = MutableTransitionState(true)

    ShortVisualAcuityInspectionScreen(
        uiState = VisualAcuityInspectionUiState.VisualAcuityTest,
        measuringDistanceContentVisibleState = measuringDistanceState,
        visualAcuityContentVisibleState = visualAcuityState,
        isLeftEye = false,
        randomList = listOf(3, 4, 6),
        ansNum = 3,
        sightLevel = 5,
        isFaceDetected = true,
        isFacingForward = true,
        onNextFromDistance = {},
        onAnswerSelected = { _, _ -> }
    )
}
