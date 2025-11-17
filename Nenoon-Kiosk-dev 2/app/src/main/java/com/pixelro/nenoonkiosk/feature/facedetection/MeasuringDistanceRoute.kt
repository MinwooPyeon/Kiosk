package com.pixelro.nenoonkiosk.feature.facedetection

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType

/**
 * MeasuringDistance Route
 *
 * ViewModel과 비즈니스 로직을 처리하는 레이어
 */
@Composable
fun MeasuringDistanceRoute(
    measuringDistanceContentVisibleState: MutableTransitionState<Boolean>,
    toNextContent: () -> Unit,
    selectedTestType: InspectionType,
    isLeftEye: Boolean,
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(
            NavConstants.PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
    }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val faceDetectionTextSize = if (savedLanguage == "ru") 20.sp else 35.sp
    val warningBoxTextSize = if (savedLanguage == "ru") 25.sp else 50.sp
    val testStartTextSize = if (savedLanguage == "ru") 20.sp else 40.sp

    // ViewModel 상태 수집
    val isFaceDetected by faceDetectionViewModel.isFaceDetected.collectAsState()
    val isRightEyeCovered by faceDetectionViewModel.isRightEyeCovered.collectAsState()
    val isLeftEyeCovered by faceDetectionViewModel.isLeftEyeCovered.collectAsState()
    val isDistanceOK by faceDetectionViewModel.isDistanceOK.collectAsState()
    val isNenoonTextDetected by faceDetectionViewModel.isNenoonTextDetected.collectAsState()
    val screenToFaceDistance by faceDetectionViewModel.screenToFaceDistance.collectAsState()
    val leftEyePosition by faceDetectionViewModel.leftEyePosition.collectAsState()
    val rightEyePosition by faceDetectionViewModel.rightEyePosition.collectAsState()
    val inputImageSizeX by faceDetectionViewModel.inputImageSizeX.collectAsState()

    val isOccluderPickedTTSDone by faceDetectionViewModel.isOccluderPickedTTSDone.collectAsState()
    val isFaceDetectedTTSDone by faceDetectionViewModel.isFaceDetectedTTSDone.collectAsState()
    val isEyeCoveredTTSDone by faceDetectionViewModel.isEyeCoveredTTSDone.collectAsState()
    val isDistanceMeasuredTTSDone by faceDetectionViewModel.isDistanceMeasuredTTSDone.collectAsState()
    val isPressStartButtonTTSDone by faceDetectionViewModel.isPressStartButtonTTSDone.collectAsState()

    // LaunchedEffect: TTS 상태 초기화
    LaunchedEffect(isLeftEye) {
        if (isLeftEye) {
            faceDetectionViewModel.updateIsOccluderPickedTTSDone(false)
        }
        faceDetectionViewModel.updateIsFaceDetectedTTSDone(false)
        faceDetectionViewModel.updateIsEyeCoveredTTSDone(false)
        faceDetectionViewModel.updateIsDistanceMeasuredTTSDone(false)
        faceDetectionViewModel.updateIsPressStartButtonTTSDone(false)
    }

    // TTS 로직
    if (
        isOccluderPickedTTSDone &&
        !isFaceDetectedTTSDone &&
        !TTS.tts.isSpeaking
    ) {
        if (isLeftEye) {
            TTS.speechTTS(
                StringProvider.getString(R.string.tts_align_middle_center),
                TextToSpeech.QUEUE_ADD,
            )
        }
        faceDetectionViewModel.updateIsFaceDetectedTTSDone(true)
    }

    if (
        isFaceDetectedTTSDone &&
        isFaceDetected &&
        !isEyeCoveredTTSDone &&
        !TTS.tts.isSpeaking
    ) {
        faceDetectionViewModel.updateIsEyeCoveredTTSDone(true)
        when (isLeftEye) {
            true ->
                TTS.speechTTS(
                    StringProvider.getString(R.string.tts_cover_right_eye),
                    TextToSpeech.QUEUE_ADD,
                )
            false ->
                TTS.speechTTS(
                    StringProvider.getString(R.string.tts_cover_left_eye),
                    TextToSpeech.QUEUE_ADD,
                )
        }
    }

    if (
        isEyeCoveredTTSDone &&
        when (isLeftEye) {
            true -> isRightEyeCovered
            false -> isLeftEyeCovered
        } &&
        !isDistanceMeasuredTTSDone &&
        !TTS.tts.isSpeaking
    ) {
        faceDetectionViewModel.updateIsDistanceMeasuredTTSDone(true)
        TTS.speechTTS(
            StringProvider.getString(R.string.tts_cover_distance),
            TextToSpeech.QUEUE_ADD,
        )
    }

    if (
        isDistanceMeasuredTTSDone &&
        isDistanceOK == 1 &&
        !isPressStartButtonTTSDone &&
        !TTS.tts.isSpeaking
    ) {
        faceDetectionViewModel.updateIsPressStartButtonTTSDone(true)
        TTS.speechTTS(
            StringProvider.getString(R.string.tts_start_button),
            TextToSpeech.QUEUE_ADD,
        )
    }

    MeasuringDistanceScreen(
        measuringDistanceContentVisibleState = measuringDistanceContentVisibleState,
        toNextContent = toNextContent,
        onStartButtonClick = {
            if (TTS.tts.isSpeaking) {
                TTS.tts.stop()
            }
            toNextContent()
        },
        selectedTestType = selectedTestType,
        isLeftEye = isLeftEye,
        faceDetectionTextSize = faceDetectionTextSize,
        warningBoxTextSize = warningBoxTextSize,
        testStartTextSize = testStartTextSize,
        isFaceDetected = isFaceDetected,
        isRightEyeCovered = isRightEyeCovered,
        isLeftEyeCovered = isLeftEyeCovered,
        isDistanceOK = isDistanceOK,
        isNenoonTextDetected = isNenoonTextDetected,
        screenToFaceDistance = screenToFaceDistance,
        leftEyePosition = leftEyePosition,
        rightEyePosition = rightEyePosition,
        inputImageSizeX = inputImageSizeX,
        onUpdateIsDistanceOK = faceDetectionViewModel::updateIsDistanceOK,
    )
}