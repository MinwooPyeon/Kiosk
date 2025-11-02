package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.core.ui.StyledAnnotatedText
import com.pixelro.nenoonkiosk.core.ui.TextSegment
import com.pixelro.nenoonkiosk.feature.inspection.components.VideoPlayerView
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.TestState
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.ui.theme.selectLargeTextStyle
import com.pixelro.nenoonkiosk.ui.theme.titleTextStyle

@Composable
fun InspectionContentBox(
    context: Context,
    testState: TestState,
    tryCount: Int,
    isTTSSpeaking: Boolean,
    isComingCloserTTSDone: Boolean,
    exoPlayer: ExoPlayer?,
    testStartText: String,
    videoGuide1: String,
    videoGuide2: String,
    videoGuide3: String,
    under25cmDesc: String,
    description2_2: String,
    description2_3: String,
    modifier: Modifier = Modifier
) {
    when (testState to isTTSSpeaking) {
        TestState.Started to true,
        TestState.Started to false -> {
            Text(
                text = testStartText,
                style = selectLargeTextStyle.copy(fontSize = 60.sp),
                textAlign = TextAlign.Center
            )
        }
        TestState.AdjustingDistance to true,
        TestState.AdjustingDistance to false -> {
            when (tryCount) {
                0 -> {
                    VideoPlayerView(
                        context = context,
                        exoPlayer = exoPlayer
                    )
                }
                else -> {
                    PresbyopiaTestImage(tryCount = tryCount)
                }
            }
        }
        TestState.ComingCloser to true -> {
            when (tryCount) {
                0 -> {
                    if (isComingCloserTTSDone) {
                        PresbyopiaTestImage(tryCount = tryCount)
                    } else {
                        VideoPlayerView(
                            context = context,
                            exoPlayer = exoPlayer
                        )
                    }
                }
                else -> {
                    PresbyopiaTestImage(tryCount = tryCount)
                }
            }
        }
        else -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (testState) {
                    TestState.NoPresbyopia -> {
                        StyledAnnotatedText(
                            segments = listOf(
                                TextSegment.fromStyle(
                                    text = under25cmDesc,
                                    color = Black,
                                    fontSize = 44.sp,
                                    baseStyle = titleTextStyle
                                ),
                                TextSegment.fromStyle(
                                    text = " $description2_2",
                                    color = neNoon_blue,
                                    fontSize = 44.sp,
                                    baseStyle = titleTextStyle
                                ),
                                TextSegment.fromStyle(
                                    text = " $description2_3",
                                    color = Black,
                                    fontSize = 44.sp,
                                    baseStyle = titleTextStyle
                                )
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                    TestState.TextBlinking -> {
                        StyledAnnotatedText(
                            segments = listOf(
                                TextSegment.fromStyle(
                                    text = videoGuide1,
                                    color = Black,
                                    fontSize = 60.sp,
                                    baseStyle = selectLargeTextStyle
                                ),
                                TextSegment.fromStyle(
                                    text = " $videoGuide2",
                                    color = neNoon_blue,
                                    fontSize = 60.sp,
                                    baseStyle = selectLargeTextStyle
                                ),
                                TextSegment.fromStyle(
                                    text = videoGuide3,
                                    color = Black,
                                    fontSize = 60.sp,
                                    baseStyle = selectLargeTextStyle
                                )
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        PresbyopiaTestImage(tryCount = tryCount)
                    }
                }
            }
        }
    }
}
