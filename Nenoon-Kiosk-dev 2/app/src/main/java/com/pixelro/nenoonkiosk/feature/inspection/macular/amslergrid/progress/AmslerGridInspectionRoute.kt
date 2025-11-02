package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import com.pixelro.nenoonkiosk.feature.facedetection.MeasuringDistanceContent
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import org.orbitmvi.orbit.compose.collectAsState
import kotlin.math.tan

@Composable
fun AmslerGridInspectionRoute(
    amslerGridViewModel: AmslerGridViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        amslerGridViewModel.init()
        amslerGridViewModel.startBlinking()
        amslerGridViewModel.updateIsLookAtTheDotTTSDone(false)
        amslerGridViewModel.updateIsSelectTTSDone(false)
    }

    val state = amslerGridViewModel.collectAsState().value
    val rotX = faceDetectionViewModel.rotX.collectAsState().value
    val rotY = faceDetectionViewModel.rotY.collectAsState().value

    val measuringDistanceVisible = remember { MutableTransitionState(true) }
    measuringDistanceVisible.targetState = state.isMeasuringDistanceVisible

    val amslerGridVisible = remember { MutableTransitionState(false) }
    amslerGridVisible.targetState = state.isAmslerGridVisible

    // TTS 처리
    if (!state.isLookAtTheDotTTSDone) {
        amslerGridViewModel.updateIsLookAtTheDotTTSDone(true)
        TTS.speechTTS(StringProvider.getString(R.string.tts_blink), TextToSpeech.QUEUE_ADD)
    }

    if (state.isLookAtTheDotTTSDone && state.isFaceCenter && !state.isSelectTTSDone) {
        amslerGridViewModel.updateIsSelectTTSDone(true)
        TTS.setOnDoneListener {
            amslerGridViewModel.updateIsTestStarted(true)
            TTS.clearOnDoneListener()
        }
        TTS.speechTTS(StringProvider.getString(R.string.tts_description), TextToSpeech.QUEUE_ADD)
        TTS.speechTTS(StringProvider.getString(R.string.tts_start), TextToSpeech.QUEUE_ADD)
    }

    // 중앙 정렬 판정
    LaunchedEffect(state.isBlinkingDone, rotX, rotY) {
        val px = 450f - (400f * tan(rotY * 0.0174533)).toFloat()
        val py = 450f - (400f * tan((rotX + 10) * 0.0174533)).toFloat()
        if (state.isBlinkingDone && !state.isFaceCenter && px > 400f && px < 500f && py > 400f && py < 500f) {
            amslerGridViewModel.updateIsFaceCenter(true)
        }
    }

    AnimatedVisibility(
        visibleState = measuringDistanceVisible,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition
    ) {
        MeasuringDistanceContent(
            measuringDistanceContentVisibleState = measuringDistanceVisible,
            toNextContent = {
                amslerGridViewModel.updateIsMeasuringDistanceVisible(false)
                amslerGridViewModel.updateIsAmslerGridVisible(true)
            },
            selectedTestType = InspectionType.AmslerGrid,
            isLeftEye = state.isLeftEye
        )
    }

    AnimatedVisibility(
        visibleState = amslerGridVisible,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition
    ) {
        AmslerGridInspectionScreen(
            state = state,
            rotX = rotX,
            rotY = rotY,
            exoPlayer = amslerGridViewModel.exoPlayer,
            onAreaPressed = { position ->
                amslerGridViewModel.updateCurrentSelectedPosition(position)
            },
            onCompletePressed = {
                if (state.isLeftEye) {
                    amslerGridViewModel.updateIsLeftEye(false)
                    amslerGridViewModel.updateLeftSelectedArea()
                    amslerGridViewModel.updateIsMeasuringDistanceVisible(true)
                    amslerGridViewModel.updateIsAmslerGridVisible(false)
                } else {
                    amslerGridViewModel.updateRightSelectedArea()
                    amslerGridViewModel.updateIsAmslerGridVisible(false)
                    TTS.speechTTS(StringProvider.getString(R.string.tts_end), TextToSpeech.QUEUE_ADD)
                    amslerGridViewModel.navigateToResult()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
