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
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetection
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.PresbyopiaInspectionScreen
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PresbyopiaInspectionRoute(
    presbyopiaViewModel: PresbyopiaViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val videoGuideText = if (savedLanguage == "ru") 30.sp else 40.sp

    // ViewModel 상태
    val distance by faceDetectionViewModel.screenToFaceDistance.collectAsState()
    val isFaceDetected by faceDetectionViewModel.isFaceDetected.collectAsState()
    val state by presbyopiaViewModel.collectAsState()
    val exoPlayer = presbyopiaViewModel.exoPlayer

    // 로컬 UI 상태
    val isWarningShowing = remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0.1f) }

    // 얼굴 인식
    FaceDetection()

    // 얼굴 인식 및 거리 측정 문제 감지
    LaunchedEffect(distance, isFaceDetected, state.testState) {
        if (state.testState == TestState.Started ||
            state.testState == TestState.AdjustingDistance ||
            state.testState == TestState.ComingCloser
        ) {
            isWarningShowing.value = when {
                !isFaceDetected -> true
                distance <= 0f || distance > 1000f -> true
                else -> false
            }
        } else {
            isWarningShowing.value = false
        }
    }

    // 초기화
    LaunchedEffect(Unit) {
        presbyopiaViewModel.init()
    }

    // 비디오 준비
    LaunchedEffect(state.testState, state.tryCount) {
        when (state.testState to state.tryCount) {
            TestState.AdjustingDistance to 0 -> {
                presbyopiaViewModel.prepareAdjustingDistanceVideo()
            }
            TestState.ComingCloser to 0 -> {
                if (!state.isComingCloserTTSDone) {
                    presbyopiaViewModel.prepareComingCloserVideo()
                }
            }
        }
    }

    // 생명주기 관리
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            TTS.clearOnDoneListener()
        }
    }

    // 거리 체크
    SideEffect {
        presbyopiaViewModel.checkCondition(distance)
    }

    PresbyopiaInspectionScreen(
        context = context,
        distance = distance,
        testState = state.testState,
        tryCount = state.tryCount,
        isComingCloserTTSDone = state.isComingCloserTTSDone,
        exoPlayer = exoPlayer,
        savedLanguage = savedLanguage,
        videoGuideText = videoGuideText,
        progress = progress,
        isWarningShowing = isWarningShowing.value,
        onWarningShow = { show -> isWarningShowing.value = show },
        onNextStep = {
            presbyopiaViewModel.moveToNextStep(
                dist = distance,
                handleProgress = { progress = it }
            )
        },
        isTTSSpeaking = try {
            TTS.tts.isSpeaking
        } catch (e: Exception) {
            false
        },
        isFaceDetected = isFaceDetected
    )
}
