package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.VisualAcuityViewModel
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.VisualAcuitySttState
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.shortdistance.VisualAcuityInspectionContent as ShortVisualAcuityInspectionContent
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.VisualAcuityInspectionResult

/**
 * 시력 검사 공통 Route 컴포넌트
 *
 * 근거리/원거리 시력 검사를 모두 처리하는 공통 Route
 * ViewModel 상태 관리, FaceDetection, LaunchedEffect(TTS) 등 공통 로직 처리
 *
 * @param inspectionType 검사 타입 (ShortDistanceVisualAcuity / LongDistanceVisualAcuity)
 * @param toResultScreen 검사 완료 후 결과 화면으로 이동하는 콜백
 * @param visualAcuityViewModel 시력 검사 ViewModel
 * @param faceDetectionViewModel 얼굴 인식 ViewModel
 */
@Composable
fun VisualAcuityInspectionRoute(
    inspectionType: InspectionType,
    toResultScreen: (VisualAcuityInspectionResult) -> Unit,
    visualAcuityViewModel: VisualAcuityViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel(),
) {
    // ViewModel 초기화
    LaunchedEffect(true) {
        visualAcuityViewModel.init()
    }
    LaunchedEffect(inspectionType) {
        visualAcuityViewModel.setInspectionType(inspectionType)
    }

    // ViewModel 상태 구독 (거리 조정 및 시력 검사)
    val isMeasuringDistanceVisible =
        visualAcuityViewModel.isMeasuringDistanceContentVisible.collectAsState().value
    val isVisualAcuityVisible =
        visualAcuityViewModel.isVisualAcuityContentVisible.collectAsState().value
    val isLeftEye = visualAcuityViewModel.isLeftEye.collectAsState().value

    // ViewModel 상태 구독 (시력 검사 데이터)
    val randomList = visualAcuityViewModel.randomList.collectAsState().value
    val ansNum = visualAcuityViewModel.ansNum.collectAsState().value
    val sightLevel = visualAcuityViewModel.sightLevel.collectAsState().value
    val sttState = visualAcuityViewModel.sttState.collectAsState().value
    val sttSessionActive = visualAcuityViewModel.sttSessionActive.collectAsState().value
    
    // FaceDetection 상태 구독
    val faceDetectionState by faceDetectionViewModel.container.stateFlow.collectAsState()
    val isFacingForward = faceDetectionState.isFacingForward
    val isFaceDetected = faceDetectionState.isFaceDetected

    // TTS 재생 (시력 검사 시작 시) - inspectionType에 따라 다른 메시지
    LaunchedEffect(isVisualAcuityVisible) {
        if (isVisualAcuityVisible) {
            val ttsStringRes = when (inspectionType) {
                InspectionType.ShortDistanceVisualAcuity -> R.string.tts_short_visualacuity
                //InspectionType.LongDistanceVisualAcuity ->
                else -> R.string.tts_short_visualacuity // fallback
            }
            TTS.speechTTS(
                StringProvider.getString(ttsStringRes),
                TextToSpeech.QUEUE_ADD,
            )
        }
    }

    // AnimatedVisibility 상태
    val measuringDistanceContentVisibleState = remember { MutableTransitionState(true) }
    measuringDistanceContentVisibleState.targetState = isMeasuringDistanceVisible

    val visualAcuityContentVisibleState = remember { MutableTransitionState(false) }
    visualAcuityContentVisibleState.targetState = isVisualAcuityVisible

    // UiState 결정
    val uiState = when {
        isMeasuringDistanceVisible -> VisualAcuityInspectionUiState.MeasuringDistance
        isVisualAcuityVisible -> VisualAcuityInspectionUiState.VisualAcuityTest
        else -> VisualAcuityInspectionUiState.MeasuringDistance
    }

    // 얼굴 인식은 MeasuringDistanceContent 내부의 FaceDetectionWithPreview에서 처리

    // Content 렌더링 - inspectionType에 따라 분기
    when (inspectionType) {
        InspectionType.ShortDistanceVisualAcuity -> {
            ShortVisualAcuityInspectionContent(
                inspectionType = inspectionType,
                uiState = uiState,
                measuringDistanceContentVisibleState = measuringDistanceContentVisibleState,
                visualAcuityContentVisibleState = visualAcuityContentVisibleState,
                isLeftEye = isLeftEye,
                randomList = randomList,
                ansNum = ansNum,
                sightLevel = sightLevel,
                isFaceDetected = isFaceDetected,
                isFacingForward = isFacingForward,
                onNextFromDistance = {
                    visualAcuityViewModel.updateIsMeasuringDistanceContentVisible(false)
                    visualAcuityViewModel.updateIsVisualAcuityContentVisible(true)
                },
                onAnswerSelected = { idx, handleWrong, onComplete ->
                    visualAcuityViewModel.processAnswerSelected(idx, handleWrong, onComplete)
                },
                getInspectionResult = { visualAcuityViewModel.getVisualAcuityInspectionResult() },
                toResultScreen = toResultScreen,
                sttState = sttState,
                sttActive = sttSessionActive,
                onStartVoiceRecognition = { callback ->
                    visualAcuityViewModel.startVoiceRecognition(callback)
                },
                onCancelVoiceRecognition = {
                    visualAcuityViewModel.cancelVoiceRecognition()
                },
            )
        }

        InspectionType.LongDistanceVisualAcuity -> {
            ShortVisualAcuityInspectionContent(
                inspectionType = inspectionType,
                uiState = uiState,
                measuringDistanceContentVisibleState = measuringDistanceContentVisibleState,
                visualAcuityContentVisibleState = visualAcuityContentVisibleState,
                isLeftEye = isLeftEye,
                randomList = randomList,
                ansNum = ansNum,
                sightLevel = sightLevel,
                isFaceDetected = isFaceDetected,
                isFacingForward = isFacingForward,
                onNextFromDistance = {
                    visualAcuityViewModel.updateIsMeasuringDistanceContentVisible(false)
                    visualAcuityViewModel.updateIsVisualAcuityContentVisible(true)
                },
                onAnswerSelected = { idx, handleWrong, onComplete ->
                    visualAcuityViewModel.processAnswerSelected(idx, handleWrong, onComplete)
                },
                getInspectionResult = { visualAcuityViewModel.getVisualAcuityInspectionResult() },
                toResultScreen = toResultScreen,
                sttState = sttState,
                sttActive = sttSessionActive,
                onStartVoiceRecognition = { callback ->
                    visualAcuityViewModel.startVoiceRecognition(callback)
                },
                onCancelVoiceRecognition = {
                    visualAcuityViewModel.cancelVoiceRecognition()
                },
            )
        }

        else -> {
            // Fallback
            ShortVisualAcuityInspectionContent(
                inspectionType = InspectionType.ShortDistanceVisualAcuity,
                uiState = uiState,
                measuringDistanceContentVisibleState = measuringDistanceContentVisibleState,
                visualAcuityContentVisibleState = visualAcuityContentVisibleState,
                isLeftEye = isLeftEye,
                randomList = randomList,
                ansNum = ansNum,
                sightLevel = sightLevel,
                isFaceDetected = isFaceDetected,
                isFacingForward = isFacingForward,
                onNextFromDistance = {
                    visualAcuityViewModel.updateIsMeasuringDistanceContentVisible(false)
                    visualAcuityViewModel.updateIsVisualAcuityContentVisible(true)
                },
                onAnswerSelected = { idx, handleWrong, onComplete ->
                    visualAcuityViewModel.processAnswerSelected(idx, handleWrong, onComplete)
                },
                getInspectionResult = { visualAcuityViewModel.getVisualAcuityInspectionResult() },
                toResultScreen = toResultScreen,
                sttState = sttState,
                sttActive = sttSessionActive,
                onStartVoiceRecognition = { callback ->
                    visualAcuityViewModel.startVoiceRecognition(callback)
                },
                onCancelVoiceRecognition = {
                    visualAcuityViewModel.cancelVoiceRecognition()
                },
            )
        }
    }
}