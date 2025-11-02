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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.MeasuringDistanceContent
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun MChartInspectionRoute(
    mChartViewModel: MChartViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        mChartViewModel.init()
    }

    DisposableEffect(Unit) {
        onDispose {
            mChartViewModel.exoPlayer.release()
        }
    }

    val state = mChartViewModel.collectAsState().value

    val measuringDistanceVisibleState = remember { MutableTransitionState(true) }
    measuringDistanceVisibleState.targetState = state.isMeasuringDistanceVisible

    val mChartVisibleState = remember { MutableTransitionState(false) }
    mChartVisibleState.targetState = state.isMChartVisible

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

    DisposableEffect(Unit) {
        onDispose {
            TTS.clearOnDoneListener()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MeasuringDistanceContent(
            measuringDistanceContentVisibleState = measuringDistanceVisibleState,
            toNextContent = {
                mChartViewModel.updateIsMeasuringDistanceVisible(false)
                mChartViewModel.updateIsMChartVisible(true)
            },
            selectedTestType = InspectionType.MChart,
            isLeftEye = state.isLeftEye
        )

        AnimatedVisibility(
            visibleState = mChartVisibleState,
            enter = AnimationProvider.enterTransition,
            exit = AnimationProvider.exitTransition
        ) {
            MChartInspectionScreen(
                uiState = state,
                exoPlayer = mChartViewModel.exoPlayer,
                onStraightClick = {
                    if (state.isVertical && state.isLeftEye) {
                        mChartViewModel.updateLeftVerticalValue()
                        mChartViewModel.updateCurrentLevel(0)
                        mChartViewModel.updateIsVertical(false)
                    } else if (!state.isVertical && state.isLeftEye) {
                        mChartViewModel.updateLeftHorizontalValue()
                        mChartViewModel.toNextMChartTest()
                        mChartViewModel.updateIsMChartVisible(false)
                        mChartViewModel.updateIsMeasuringDistanceVisible(true)
                    } else if (state.isVertical) {
                        mChartViewModel.updateRightVerticalValue()
                        mChartViewModel.updateCurrentLevel(0)
                        mChartViewModel.updateIsVertical(false)
                    } else {
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_end),
                            TextToSpeech.QUEUE_ADD
                        )
                        mChartViewModel.updateRightHorizontalValue()
                        mChartViewModel.navigateToResult()
                    }
                },
                onBentClick = {
                    if (state.currentLevel >= 19) {
                        if (state.isVertical && state.isLeftEye) {
                            mChartViewModel.updateLeftVerticalValue()
                            mChartViewModel.updateCurrentLevel(0)
                            mChartViewModel.updateIsVertical(false)
                        } else if (!state.isVertical && state.isLeftEye) {
                            mChartViewModel.updateLeftHorizontalValue()
                            mChartViewModel.toNextMChartTest()
                            mChartViewModel.updateIsMChartVisible(false)
                            mChartViewModel.updateIsMeasuringDistanceVisible(true)
                        } else if (state.isVertical) {
                            mChartViewModel.updateRightVerticalValue()
                            mChartViewModel.updateCurrentLevel(0)
                            mChartViewModel.updateIsVertical(false)
                        } else {
                            TTS.speechTTS(
                                StringProvider.getString(R.string.tts_end),
                                TextToSpeech.QUEUE_ADD
                            )
                            mChartViewModel.updateRightHorizontalValue()
                            mChartViewModel.navigateToResult()
                        }
                    } else {
                        mChartViewModel.updateCurrentLevel(state.currentLevel + 1)
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
