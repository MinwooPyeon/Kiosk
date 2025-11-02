package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.progress

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.MeasuringDistanceContent
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartViewModel
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.result.MChartInspectionResult

@Composable
fun MChartInspectionRoute(
    toResultScreen: (MChartInspectionResult) -> Unit,
    mChartViewModel: MChartViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) { mChartViewModel.init() }
    DisposableEffect(Unit) { onDispose { mChartViewModel.exoPlayer.release() } }

    // ------------------------------
    // 👁️ 거리측정 / MChart 노출 상태
    // ------------------------------
    val measuringDistanceVisibleState = remember { MutableTransitionState(true) }
    measuringDistanceVisibleState.targetState =
        mChartViewModel.isMeasuringDistanceContentVisible.collectAsState().value

    val mChartVisibleState = remember { MutableTransitionState(false) }
    mChartVisibleState.targetState =
        mChartViewModel.isMChartContentVisible.collectAsState().value

    // ------------------------------
    // 👁️ UI 상태 수집
    // ------------------------------
    val uiState = MChartInspectionUiState(
        isLeftEye = mChartViewModel.isLeftEye.collectAsState().value,
        isVertical = mChartViewModel.isVertical.collectAsState().value,
        currentLevel = mChartViewModel.currentLevel.collectAsState().value,
        imageId = mChartViewModel.mChartImageId.collectAsState().value,
        isTTSSpeaking = mChartViewModel.isTTSSpeaking.collectAsState().value,
        isTesting = mChartViewModel.isTesting.collectAsState().value
    )

    // ------------------------------
    // 🎤 TTS 초기화 및 정리
    // ------------------------------
    LaunchedEffect(Unit) {
        TTS.setOnDoneListener { mChartViewModel.updateIsTTSSpeaking(false) }
        TTS.speechTTS(
            StringProvider.getString(R.string.tts_straight_or_bent),
            TextToSpeech.QUEUE_ADD
        )
        TTS.speechTTS(
            StringProvider.getString(R.string.tts_start),
            TextToSpeech.QUEUE_ADD
        )
    }
    DisposableEffect(Unit) { onDispose { TTS.clearOnDoneListener() } }

    // ------------------------------
    // 🧠 실제 화면 구성
    // ------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 거리 측정 화면
        MeasuringDistanceContent(
            measuringDistanceContentVisibleState = measuringDistanceVisibleState,
            toNextContent = {
                mChartViewModel.updateIsMeasuringDistanceContentVisible(false)
                mChartViewModel.updateIsMChartContentVisible(true)
            },
            selectedTestType = InspectionType.MChart,
            isLeftEye = uiState.isLeftEye,
        )

        // M-Chart 검사 화면
        AnimatedVisibility(
            visibleState = mChartVisibleState,
            enter = AnimationProvider.enterTransition,
            exit = AnimationProvider.exitTransition
        ) {
            MChartInspectionContent(
                uiState = uiState,
                exoPlayer = mChartViewModel.exoPlayer,
                onStraightClick = {
                    if (uiState.isVertical && uiState.isLeftEye) {
                        mChartViewModel.updateLeftVerticalValue()
                        mChartViewModel.updateCurrentLevel(0)
                        mChartViewModel.updateIsVertical(false)
                    } else if (!uiState.isVertical && uiState.isLeftEye) {
                        mChartViewModel.updateLeftHorizontalValue()
                        mChartViewModel.toNextMChartTest()
                        mChartViewModel.updateIsMChartContentVisible(false)
                        mChartViewModel.updateIsMeasuringDistanceContentVisible(true)
                    } else if (uiState.isVertical) {
                        mChartViewModel.updateRightVerticalValue()
                        mChartViewModel.updateCurrentLevel(0)
                        mChartViewModel.updateIsVertical(false)
                    } else {
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_end),
                            TextToSpeech.QUEUE_ADD
                        )
                        mChartViewModel.updateRightHorizontalValue()
                        toResultScreen(mChartViewModel.getMChartTestResult())
                    }
                },
                onBentClick = {
                    if (uiState.currentLevel >= 19) {
                        if (uiState.isVertical && uiState.isLeftEye) {
                            mChartViewModel.updateLeftVerticalValue()
                            mChartViewModel.updateCurrentLevel(0)
                            mChartViewModel.updateIsVertical(false)
                        } else if (!uiState.isVertical && uiState.isLeftEye) {
                            mChartViewModel.updateLeftHorizontalValue()
                            mChartViewModel.toNextMChartTest()
                            mChartViewModel.updateIsMChartContentVisible(false)
                            mChartViewModel.updateIsMeasuringDistanceContentVisible(true)
                        } else if (uiState.isVertical) {
                            mChartViewModel.updateRightVerticalValue()
                            mChartViewModel.updateCurrentLevel(0)
                            mChartViewModel.updateIsVertical(false)
                        } else {
                            TTS.speechTTS(
                                StringProvider.getString(R.string.tts_end),
                                TextToSpeech.QUEUE_ADD
                            )
                            mChartViewModel.updateRightHorizontalValue()
                            toResultScreen(mChartViewModel.getMChartTestResult())
                        }
                    } else {
                        mChartViewModel.updateCurrentLevel(uiState.currentLevel + 1)
                    }
                },
                onTTSDone = {
                    mChartViewModel.updateIsTTSSpeaking(false)
                    mChartViewModel.updateIsTesting(true)
                }
            )
        }
    }
}
