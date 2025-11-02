package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.BottomWarningButton
import com.pixelro.nenoonkiosk.core.ui.StyledAnnotatedText
import com.pixelro.nenoonkiosk.core.ui.TextSegment
import com.pixelro.nenoonkiosk.feature.inspection.components.WarningOverlay
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.TestState
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components.DistanceDisplay
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components.InspectionContentBox
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components.InspectionDescriptionBox
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun PresbyopiaInspectionScreen(
    context: Context,
    distance: Float,
    testState: TestState,
    tryCount: Int,
    isComingCloserTTSDone: Boolean,
    exoPlayer: ExoPlayer?,
    savedLanguage: String?,
    videoGuideText: TextUnit,
    progress: Float,
    isWarningShowing: Boolean,
    onWarningShow: (Boolean) -> Unit,
    onNextStep: () -> Unit,
    isTTSSpeaking: Boolean = false,
    isFaceDetected: Boolean = true
) {
    // 리소스
    val description1_1 = stringResource(R.string.presbyopia_description1_1)
    val description1_3 = stringResource(R.string.presbyopia_description1_3)
    val videoGuide1 = stringResource(R.string.presbyopia_video_guide_1)
    val videoGuide2 = stringResource(R.string.presbyopia_video_guide_2)
    val videoGuide3 = stringResource(R.string.presbyopia_video_guide_3)
    val description2_1 = stringResource(R.string.presbyopia_description2_1)
    val description2_2 = stringResource(R.string.presbyopia_description2_2)
    val description2_3 = stringResource(R.string.presbyopia_description2_3)
    val under25cmDesc1 = stringResource(R.string.presbyopia_under_25cm_description1)
    val under25cmDesc2 = stringResource(R.string.presbyopia_under_25cm_description2)
    val under25cmDesc3 = stringResource(R.string.presbyopia_under_25cm_description3)
    val dialogAnnouncement1 = stringResource(R.string.dialog_description2_announcement1)
    val dialogAnnouncement2 = stringResource(R.string.dialog_description2_announcement2)
    val dialogAnnouncement3 = stringResource(R.string.dialog_description2_announcement3)
    val testStart1 = stringResource(R.string.presbyopia_test_start1)
    val testStart2 = stringResource(R.string.presbyopia_test_start2)
    val testStart3 = stringResource(R.string.presbyopia_test_start3)
    val videoGuideAbove1 = stringResource(R.string.presbyopia_video_guide_above_1)
    val videoGuideAbove2 = stringResource(R.string.presbyopia_video_guide_above_2)
    val videoGuideAbove3 = stringResource(R.string.presbyopia_video_guide_above_3)
    val nextButton = stringResource(R.string.next)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InspectionDescriptionBox(
                testState = testState,
                tryCount = tryCount,
                savedLanguage = savedLanguage,
                description1_1 = description1_1,
                description1_3 = description1_3,
                videoGuide1 = videoGuide1,
                videoGuide2 = videoGuide2,
                videoGuide3 = videoGuide3,
                description2_1 = description2_1,
                description2_2 = description2_2,
                description2_3 = description2_3,
                under25cmDesc = when (tryCount) {
                    0 -> under25cmDesc1
                    1 -> under25cmDesc2
                    else -> under25cmDesc3
                }
            )

            LinearProgressIndicator(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(20.dp),
                progress = animatedProgress,
                color = neNoon_blue
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .height(500.dp)
                    .width(600.dp)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                InspectionContentBox(
                    context = context,
                    testState = testState,
                    tryCount = tryCount,
                    isTTSSpeaking = isTTSSpeaking,
                    isComingCloserTTSDone = isComingCloserTTSDone,
                    exoPlayer = exoPlayer,
                    testStartText = when (tryCount) {
                        0 -> testStart1
                        1 -> testStart2
                        else -> testStart3
                    },
                    videoGuide1 = videoGuide1,
                    videoGuide2 = videoGuide2,
                    videoGuide3 = videoGuide3,
                    under25cmDesc = when (tryCount) {
                        0 -> under25cmDesc1
                        1 -> under25cmDesc2
                        else -> under25cmDesc3
                    },
                    description2_2 = description2_2,
                    description2_3 = description2_3
                )
            }

            when (testState) {
                TestState.Started -> {}

                TestState.AdjustingDistance -> {
                    DistanceDisplay(
                        distance = distance,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }

                TestState.ComingCloser -> {
                    if (!isComingCloserTTSDone && tryCount == 0) {
                        StyledAnnotatedText(
                            modifier = Modifier.padding(top = 40.dp),
                            segments = listOf(
                                TextSegment.fromStyle(
                                    text = videoGuideAbove1,
                                    color = White,
                                    fontSize = videoGuideText,
                                    baseStyle = bodyTextStyle
                                ),
                                TextSegment.create(
                                    text = " $videoGuideAbove2",
                                    color = neNoon_blue,
                                    fontSize = videoGuideText,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = bodyTextStyle.fontFamily
                                ),
                                TextSegment.fromStyle(
                                    text = videoGuideAbove3,
                                    color = White,
                                    fontSize = videoGuideText,
                                    baseStyle = bodyTextStyle
                                )
                            ),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        DistanceDisplay(
                            distance = distance,
                            modifier = Modifier.padding(top = 40.dp)
                        )
                    }
                }

                else -> {}
            }

            if (((testState == TestState.ComingCloser && ((tryCount == 0 && !isTTSSpeaking) || tryCount == 1 || tryCount == 2)) || testState == TestState.NoPresbyopia)) {
                BottomWarningButton(
                    onClick = onNextStep,
                    text = nextButton,
                    enabled = !isTTSSpeaking,
                    showWarningWhenDisabled = true,
                    onWarningShow = onWarningShow
                )
            }
        }

        if (isWarningShowing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    !isFaceDetected -> {
                        WarningOverlay(
                            text1 = "화면에 ",
                            text2 = "얼굴",
                            text3 = "을 비춰주세요"
                        )
                    }
                    isTTSSpeaking -> {
                        WarningOverlay(
                            text1 = dialogAnnouncement1,
                            text2 = dialogAnnouncement2,
                            text3 = dialogAnnouncement3
                        )
                    }
                    else -> {
                        WarningOverlay(
                            text1 = "거리를 ",
                            text2 = "측정",
                            text3 = "하고 있습니다"
                        )
                    }
                }
            }
        }
    }
}
