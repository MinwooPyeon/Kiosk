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
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.VisualAcuitySttState
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.VisualAcuityInspectionCommonContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.VisualAcuityInspectionUiState
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.VisualAcuityChartSizeMode
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.VisualAcuityInspectionResult
import com.pixelro.nenoonkiosk.ui.theme.Black

/**
 * 시력 검사 UI 컴포넌트 (순수 UI)
 *
 * ## 검사 흐름 (2단계)
 * 1단계: 거리 조정 (MeasuringDistanceContent)
 *    ↓
 *   근거리: 40~50cm / 원거리: 3m 거리 맞추기 완료
 *    ↓
 * 2단계: 시력 검사 (VisualAcuityInspectionCommonContent)
 *    ↓
 *   란돌트 C 검사 (좌안 → 우안)
 *    ↓
 *   결과 화면
 *
 * ## 주요 구성
 * - **Phase 1**: 거리 조정 화면
 * - **Phase 2**: 란돌트 C 시력 검사 (좌안 먼저, 그 다음 우안)
 * - 두 화면은 AnimatedVisibility로 전환됨
 *
 * @param inspectionType 검사 타입 (ShortDistanceVisualAcuity / LongDistanceVisualAcuity)
 * @param uiState 현재 UI 상태 (MeasuringDistance, VisualAcuityTest)
 * @param measuringDistanceContentVisibleState 거리 조정 화면 표시 여부 (AnimatedVisibility 제어)
 * @param visualAcuityContentVisibleState 시력 검사 화면 표시 여부 (AnimatedVisibility 제어)
 * @param isLeftEye 현재 검사 중인 눈 (true: 좌안, false: 우안)
 * @param randomList 랜덤 방향 리스트 (3개)
 * @param ansNum 정답 방향 (2~7)
 * @param sightLevel 시력 난이도 (1~10)
 * @param isFaceDetected 얼굴 인식 여부
 * @param isFacingForward 정면 응시 여부
 * @param onNextFromDistance 거리 조정 완료 후 다음 단계로 진행하는 콜백
 * @param onAnswerSelected 답변 선택 시 호출되는 콜백
 * @param getInspectionResult 검사 결과를 가져오는 함수
 * @param toResultScreen 검사 완료 후 결과 화면으로 이동하는 콜백
 */
@Composable
fun VisualAcuityInspectionContent(
    inspectionType: InspectionType,
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
    onAnswerSelected: (Int, (Float) -> Unit, () -> Unit) -> Unit,
    getInspectionResult: () -> VisualAcuityInspectionResult,
    toResultScreen: (VisualAcuityInspectionResult) -> Unit,
    sttState: VisualAcuitySttState,
    sttActive: Boolean,
    onStartVoiceRecognition: ((String) -> Unit) -> Unit,
    onCancelVoiceRecognition: () -> Unit,
) {
    val sttEnabled = when (inspectionType) {
        InspectionType.ShortDistanceVisualAcuity -> sttState == VisualAcuitySttState.ShortDigit
        InspectionType.LongDistanceVisualAcuity -> sttState == VisualAcuitySttState.LongDigit
        else -> false
    }

    val chartSizeMode =
        when (inspectionType) {
            InspectionType.LongDistanceVisualAcuity -> VisualAcuityChartSizeMode.LongDistance
            else -> VisualAcuityChartSizeMode.ShortDistance
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Black,
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
                toNextContent = onNextFromDistance,
                selectedTestType = inspectionType,
                isLeftEye = isLeftEye,
            )
            /**
             * 시력 검사 화면
             */
            VisualAcuityInspectionCommonContent(
                visualAcuityInspectionCommonContentVisibleState = visualAcuityContentVisibleState,
                randomList = randomList,
                ansNum = ansNum,
                sightLevel = sightLevel,
                isFaceDetected = isFaceDetected,
                isFacingForward = isFacingForward,
                onAnswerSelected = onAnswerSelected,
                getInspectionResult = getInspectionResult,
                toResultScreen = toResultScreen,
                sttEnabled = sttEnabled,
                sttActive = sttActive,
                onStartVoiceRecognition = onStartVoiceRecognition,
                onCancelVoiceRecognition = onCancelVoiceRecognition,
                chartSizeMode = chartSizeMode,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewShortDistanceVisualAcuityInspection_MeasuringDistance() {
    // Phase 1: 거리 조정 화면 상태
    val measuringDistanceState = MutableTransitionState(true)
    val visualAcuityState = MutableTransitionState(false)

    VisualAcuityInspectionContent(
        inspectionType = InspectionType.ShortDistanceVisualAcuity,
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
        onAnswerSelected = { _, _, _ -> },
        getInspectionResult = { VisualAcuityInspectionResult(leftEye = 10, rightEye = 10) },
        toResultScreen = {},
        sttState = VisualAcuitySttState.ShortDigit,
        sttActive = false,
        onStartVoiceRecognition = {},
        onCancelVoiceRecognition = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewShortDistanceVisualAcuityInspection_VisualAcuityTest() {
    // Phase 2: 시력 검사 화면 상태
    val measuringDistanceState = MutableTransitionState(false)
    val visualAcuityState = MutableTransitionState(true)

    VisualAcuityInspectionContent(
        inspectionType = InspectionType.ShortDistanceVisualAcuity,
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
        onAnswerSelected = { _, _, _ -> },
        getInspectionResult = { VisualAcuityInspectionResult(leftEye = 5, rightEye = 5) },
        toResultScreen = {},
        sttState = VisualAcuitySttState.ShortDigit,
        sttActive = false,
        onStartVoiceRecognition = {},
        onCancelVoiceRecognition = {},
    )
}
