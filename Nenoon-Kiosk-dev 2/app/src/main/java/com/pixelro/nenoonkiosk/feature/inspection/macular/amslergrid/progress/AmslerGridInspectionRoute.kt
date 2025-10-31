package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.progress

import android.speech.tts.TextToSpeech
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import com.pixelro.nenoonkiosk.feature.facedetection.MeasuringDistanceContent
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridEvent
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridUiState
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridViewModel
import kotlin.math.tan

@OptIn(UnstableApi::class)
@Composable
fun AmslerGridInspectionRoute(
    toResultScreen: (AmslerGridTestResult) -> Unit,
    amslerGridViewModel: AmslerGridViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel(),
) {
    // 최초 초기화
    LaunchedEffect(Unit) { amslerGridViewModel.init() }

    // 애니메이션 가시성 (기존 MutableTransitionState 유지)
    val measuringDistanceVisible = remember { MutableTransitionState(true) }
    measuringDistanceVisible.targetState = amslerGridViewModel.isMeasuringDistanceContentVisible.collectAsState().value

    val amslerGridVisible = remember { MutableTransitionState(false) }
    amslerGridVisible.targetState = amslerGridViewModel.isAmslerGridContentVisible.collectAsState().value

    // 상태 수집
    val isBlinkingDone = amslerGridViewModel.isBlinkingDone.collectAsState().value
    val isDotShowing = amslerGridViewModel.isDotShowing.collectAsState().value
    val isFaceCenter = amslerGridViewModel.isFaceCenter.collectAsState().value
    val isSelectTTSDone = amslerGridViewModel.isSelectTTSDone.collectAsState().value
    val isTestStarted = amslerGridViewModel.isTestStarted.collectAsState().value
    val isLeft = amslerGridViewModel.isLeftEye.collectAsState().value
    val rotX = faceDetectionViewModel.rotX.collectAsState().value
    val rotY = faceDetectionViewModel.rotY.collectAsState().value
    val currentSelectedArea = amslerGridViewModel.currentSelectedArea.collectAsState().value

    // TTS 흐름
    if (!amslerGridViewModel.isLookAtTheDotTTSDone.collectAsState().value) {
        amslerGridViewModel.updateIsLookAtTheDotTTSDone(true)
        TTS.speechTTS(StringProvider.getString(R.string.tts_blink), TextToSpeech.QUEUE_ADD)
    }
    if (amslerGridViewModel.isLookAtTheDotTTSDone.collectAsState().value && isFaceCenter && !isSelectTTSDone) {
        amslerGridViewModel.updateIsSelectTTSDone(true)
        TTS.setOnDoneListener {
            amslerGridViewModel.updateIsTestStarted(true)
            TTS.clearOnDoneListener()
        }
        TTS.speechTTS(StringProvider.getString(R.string.tts_description), TextToSpeech.QUEUE_ADD)
        TTS.speechTTS(StringProvider.getString(R.string.tts_start), TextToSpeech.QUEUE_ADD)
    }

    // 점멸 시작 플래그 초기화
    LaunchedEffect(Unit) {
        amslerGridViewModel.startBlinking()
        amslerGridViewModel.updateIsLookAtTheDotTTSDone(false)
        amslerGridViewModel.updateIsSelectTTSDone(false)
    }

    // 헤더 텍스트 계산
    val headerAnnotated = remember(isBlinkingDone, isFaceCenter) {
        when (!isBlinkingDone) {
            true -> buildAnnotatedString { append(StringProvider.getString(R.string.amsler_description1_blink)) }
            false -> if (isFaceCenter) {
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xff1d71e1), fontWeight = FontWeight.Bold)) {
                        append(StringProvider.getString(R.string.amsler_description2_distortion_1))
                    }
                    append(" " + StringProvider.getString(R.string.amsler_description2_distortion_2))
                    withStyle(SpanStyle(color = Color(0xffff0000), fontWeight = FontWeight.Bold)) {
                        append(" " + StringProvider.getString(R.string.amsler_description2_distortion_3) + " ")
                    }
                    append(StringProvider.getString(R.string.amsler_description2_distortion_4))
                }
            } else {
                buildAnnotatedString { append(StringProvider.getString(R.string.amsler_description3_center)) }
            }
        }
    }

    // 중앙 정렬 판정(파란 점이 중앙 근처일 때)
    LaunchedEffect(isBlinkingDone, rotX, rotY) {
        val px = 450f - (400f * tan(rotY * 0.0174533)).toFloat()
        val py = 450f - (400f * tan((rotX + 10) * 0.0174533)).toFloat()
        if (isBlinkingDone && !isFaceCenter && px > 400f && px < 500f && py > 400f && py < 500f) {
            amslerGridViewModel.updateIsFaceCenter(true)
        }
    }

    val shouldPlayGuideVideo = isBlinkingDone && isFaceCenter && TTS.tts.isSpeaking && !isTestStarted && isLeft

    val context = LocalContext.current
    val videoSlot: @Composable (() -> Unit) = {
        val exoPlayer = amslerGridViewModel.exoPlayer
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    useController = false
                    exoPlayer.setMediaItem(
                        MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.amsler_video_3)),
                    )
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
            },
        )
    }

    val uiState = AmslerGridUiState(
        showMeasuringDistance = measuringDistanceVisible.currentState || measuringDistanceVisible.targetState,
        showAmslerGrid = amslerGridVisible.currentState || amslerGridVisible.targetState,
        isBlinkingDone = isBlinkingDone,
        isDotShowing = isDotShowing,
        isSelectTtsDone = isSelectTTSDone,
        isTestStarted = isTestStarted,
        isLeftEye = isLeft,
        rotX = rotX,
        rotY = rotY,
        selectedAreas = currentSelectedArea,
        headerAnnotated = headerAnnotated,
        shouldPlayGuideVideo = shouldPlayGuideVideo,
    )

    AmslerGridInspectionContent(
        measuringDistanceVisible = measuringDistanceVisible.apply {
            // MeasuringDistanceContent를 이 자식으로 직접 배치 (UI만)
        }.also {
            // 내부에 MeasuringDistanceContent 실제 삽입
        },
        amslerGridVisible = amslerGridVisible,
        state = uiState,
        isFaceCenter = isFaceCenter,
        onEvent = { ev ->
            when (ev) {
                AmslerGridEvent.ProceedFromDistance -> {
                    amslerGridViewModel.updateIsMeasuringDistanceContentVisible(false)
                    amslerGridViewModel.updateIsAmslerGridContentVisible(true)
                }
                is AmslerGridEvent.AreaPressed -> {
                    amslerGridViewModel.updateCurrentSelectedPosition(ev.position)
                }
                AmslerGridEvent.CompletePressed -> {
                    if (isLeft) {
                        amslerGridViewModel.updateIsLeftEye(false)
                        amslerGridViewModel.updateLeftSelectedArea()
                        amslerGridViewModel.updateIsMeasuringDistanceContentVisible(true)
                        amslerGridViewModel.updateIsAmslerGridContentVisible(false)
                    } else {
                        amslerGridViewModel.updateRightSelectedArea()
                        amslerGridViewModel.updateIsAmslerGridContentVisible(false)
                        TTS.speechTTS(StringProvider.getString(R.string.tts_end), TextToSpeech.QUEUE_ADD)
                        toResultScreen(amslerGridViewModel.getAmslerGridTestResult())
                    }
                }
            }
        },
        guideVideo = if (shouldPlayGuideVideo) videoSlot else null,
        modifier = Modifier.fillMaxSize(),
    )

    // MeasuringDistanceContent는 Route에서 실제로 배치 (Screen은 슬롯만 가짐)
    AnimatedVisibility(
        visibleState = measuringDistanceVisible,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition,
    ) {
        MeasuringDistanceContent(
            measuringDistanceContentVisibleState = measuringDistanceVisible,
            toNextContent = { amslerGridViewModel.updateIsMeasuringDistanceContentVisible(false).also {
                amslerGridViewModel.updateIsAmslerGridContentVisible(true)
            } },
            selectedTestType = InspectionType.AmslerGrid,
            isLeftEye = isLeft,
        )
    }
}
