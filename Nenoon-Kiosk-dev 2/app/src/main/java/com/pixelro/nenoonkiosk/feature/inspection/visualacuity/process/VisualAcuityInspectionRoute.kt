package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.VisualAcuityViewModel
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.shortdistance.ShortVisualAcuityInspectionScreen
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun VisualAcuityInspectionRoute(
    inspectionType: InspectionType,
    visualAcuityViewModel: VisualAcuityViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        visualAcuityViewModel.init()
    }

    val state = visualAcuityViewModel.collectAsState().value
    val faceDetectionState = faceDetectionViewModel.collectAsState().value

    val measuringDistanceVisible = remember { MutableTransitionState(true) }
    measuringDistanceVisible.targetState = state.isMeasuringDistanceVisible

    val visualAcuityVisible = remember { MutableTransitionState(false) }
    visualAcuityVisible.targetState = state.isVisualAcuityVisible

    LaunchedEffect(state.isVisualAcuityVisible) {
        if (state.isVisualAcuityVisible) {
            val ttsStringRes = when (inspectionType) {
                InspectionType.ShortDistanceVisualAcuity -> R.string.tts_short_visualacuity
                else -> R.string.tts_short_visualacuity
            }
            TTS.speechTTS(
                StringProvider.getString(ttsStringRes),
                TextToSpeech.QUEUE_ADD
            )
        }
    }

    val uiState = when {
        state.isMeasuringDistanceVisible -> VisualAcuityInspectionUiState.MeasuringDistance
        state.isVisualAcuityVisible -> VisualAcuityInspectionUiState.VisualAcuityTest
        else -> VisualAcuityInspectionUiState.MeasuringDistance
    }

    when (inspectionType) {
        InspectionType.ShortDistanceVisualAcuity -> {
            ShortVisualAcuityInspectionScreen(
                uiState = uiState,
                measuringDistanceContentVisibleState = measuringDistanceVisible,
                visualAcuityContentVisibleState = visualAcuityVisible,
                isLeftEye = state.isLeftEye,
                randomList = state.randomList,
                ansNum = state.ansNum,
                sightLevel = state.sightLevel,
                isFaceDetected = faceDetectionState.isFaceDetected,
                isFacingForward = faceDetectionState.isFacingForward,
                onNextFromDistance = {
                    visualAcuityViewModel.updateIsMeasuringDistanceVisible(false)
                    visualAcuityViewModel.updateIsVisualAcuityVisible(true)
                },
                onAnswerSelected = { idx, handleWrong ->
                    visualAcuityViewModel.processAnswerSelected(idx, handleWrong)
                }
            )
        }
        else -> {
            ShortVisualAcuityInspectionScreen(
                uiState = uiState,
                measuringDistanceContentVisibleState = measuringDistanceVisible,
                visualAcuityContentVisibleState = visualAcuityVisible,
                isLeftEye = state.isLeftEye,
                randomList = state.randomList,
                ansNum = state.ansNum,
                sightLevel = state.sightLevel,
                isFaceDetected = faceDetectionState.isFaceDetected,
                isFacingForward = faceDetectionState.isFacingForward,
                onNextFromDistance = {
                    visualAcuityViewModel.updateIsMeasuringDistanceVisible(false)
                    visualAcuityViewModel.updateIsVisualAcuityVisible(true)
                },
                onAnswerSelected = { idx, handleWrong ->
                    visualAcuityViewModel.processAnswerSelected(idx, handleWrong)
                }
            )
        }
    }
}
