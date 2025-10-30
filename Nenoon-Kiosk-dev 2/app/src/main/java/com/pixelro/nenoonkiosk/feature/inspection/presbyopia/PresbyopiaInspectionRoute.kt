package com.pixelro.nenoonkiosk.feature.inspection.presbyopia

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetection
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.PresbyopiaInspectionContent
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.toUiState

@Composable
fun PresbyopiaInspectionRoute(
    toResultScreen: (PresbyopiaInspectionResult) -> Unit,
    presbyopiaViewModel: PresbyopiaViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val videoGuideText = if (savedLanguage == "ru") 30.sp else 40.sp

    // ViewModel 상태
    val distance = faceDetectionViewModel.screenToFaceDistance.collectAsState().value
    val testState = presbyopiaViewModel.testState.collectAsState().value
    val tryCount = presbyopiaViewModel.tryCount.collectAsState().value
    val isComingCloserTTSDone = presbyopiaViewModel.isComingCloserTTSDone.collectAsState().value
    val exoPlayer = presbyopiaViewModel.exoPlayer

    // 로컬 UI 상태
    val coroutineScope = rememberCoroutineScope()
    val isWarningShowing = remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0.1f) }

    // 얼굴 인식
    FaceDetection()

    // 비디오 준비
    LaunchedEffect(testState, tryCount) {
        when (testState to tryCount) {
            PresbyopiaViewModel.TestState.AdjustingDistance to 0 -> {
                presbyopiaViewModel.prepareAdjustingDistanceVideo()
            }
            PresbyopiaViewModel.TestState.ComingCloser to 0 -> {
                if (!isComingCloserTTSDone) {
                    presbyopiaViewModel.prepareComingCloserVideo()
                }
            }
        }
    }

    // 생명주기 관리
    DisposableEffect(true) {
        onDispose {
            exoPlayer?.release()
            TTS.clearOnDoneListener()
        }
    }

    // 거리 체크
    SideEffect {
        presbyopiaViewModel.checkCondition(distance)
    }

    PresbyopiaInspectionContent(
        context = context,
        distance = distance,
        uiState = testState.toUiState(),
        tryCount = tryCount,
        isComingCloserTTSDone = isComingCloserTTSDone,
        exoPlayer = exoPlayer,
        savedLanguage = savedLanguage,
        videoGuideText = videoGuideText,
        progress = progress,
        isWarningShowing = isWarningShowing.value,
        onWarningShow = { show -> isWarningShowing.value = show },
        onNextStep = {
            presbyopiaViewModel.moveToNextStep(
                dist = distance,
                handleProgress = { progress = it },
            ) {
                toResultScreen(presbyopiaViewModel.getPresbyopiaTestResult())
            }
        },
        isTTSSpeaking = try {
            TTS.tts.isSpeaking
        } catch (e: Exception) {
            false
        },
    )
}