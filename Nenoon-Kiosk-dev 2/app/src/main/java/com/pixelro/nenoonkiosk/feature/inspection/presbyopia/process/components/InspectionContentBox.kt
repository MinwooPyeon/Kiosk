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
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.PresbyopiaInspectionUiState
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.ui.theme.selectLargeTextStyle
import com.pixelro.nenoonkiosk.ui.theme.titleTextStyle

/**
 * 검사 중앙 박스의 내용물을 표시하는 컴포넌트
 *
 * @param context Android Context
 * @param uiState 현재 UI 상태
 * @param tryCount 시도 횟수
 * @param isTTSSpeaking TTS 음성 재생 중 여부
 * @param isComingCloserTTSDone ComingCloser 상태의 TTS 완료 여부
 * @param exoPlayer ExoPlayer 인스턴스
 * @param testStartText 시작 텍스트
 * @param videoGuide1 비디오 가이드 텍스트 (Part 1)
 * @param videoGuide2 비디오 가이드 텍스트 (Part 2)
 * @param videoGuide3 비디오 가이드 텍스트 (Part 3)
 * @param under25cmDesc 25cm 미만 설명 텍스트
 * @param description2_2 두 번째 설명 텍스트 (Part 2)
 * @param description2_3 두 번째 설명 텍스트 (Part 3)
 * @param modifier Modifier
 */
@Composable
fun InspectionContentBox(
    context: Context,
    uiState: PresbyopiaInspectionUiState,
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
    modifier: Modifier = Modifier,
) {
    when (uiState to isTTSSpeaking) {
        PresbyopiaInspectionUiState.Started to true,
        PresbyopiaInspectionUiState.Started to false,
        -> {
            Text(
                text = testStartText,
                style = selectLargeTextStyle.copy(fontSize = 60.sp),
                textAlign = TextAlign.Center,
            )
        }
        PresbyopiaInspectionUiState.AdjustingDistance to true,
        PresbyopiaInspectionUiState.AdjustingDistance to false,
        -> {
            when (tryCount) {
                0 -> {
                    VideoPlayerView(
                        context = context,
                        exoPlayer = exoPlayer,
                    )
                }
                else -> {
                    PresbyopiaTestImage(tryCount = tryCount)
                }
            }
        }

        PresbyopiaInspectionUiState.ComingCloser to true -> {
            when (tryCount) {
                0 -> {
                    if (isComingCloserTTSDone) {
                        PresbyopiaTestImage(tryCount = tryCount)
                    } else {
                        VideoPlayerView(
                            context = context,
                            exoPlayer = exoPlayer,
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
                verticalArrangement = Arrangement.Center,
            ) {
                when (uiState) {
                    PresbyopiaInspectionUiState.NoPresbyopia -> {
                        StyledAnnotatedText(
                            segments = listOf(
                                TextSegment.fromStyle(
                                    text = under25cmDesc,
                                    color = Black,
                                    fontSize = 44.sp,
                                    baseStyle = titleTextStyle,
                                ),
                                TextSegment.fromStyle(
                                    text = " $description2_2",
                                    color = neNoon_blue,
                                    fontSize = 44.sp,
                                    baseStyle = titleTextStyle,
                                ),
                                TextSegment.fromStyle(
                                    text = " $description2_3",
                                    color = Black,
                                    fontSize = 44.sp,
                                    baseStyle = titleTextStyle,
                                ),
                            ),
                            textAlign = TextAlign.Center,
                        )
                    }

                    PresbyopiaInspectionUiState.TextBlinking -> {
                        StyledAnnotatedText(
                            segments = listOf(
                                TextSegment.fromStyle(
                                    text = videoGuide1,
                                    color = Black,
                                    fontSize = 60.sp,
                                    baseStyle = selectLargeTextStyle,
                                ),
                                TextSegment.fromStyle(
                                    text = " $videoGuide2",
                                    color = neNoon_blue,
                                    fontSize = 60.sp,
                                    baseStyle = selectLargeTextStyle,
                                ),
                                TextSegment.fromStyle(
                                    text = videoGuide3,
                                    color = Black,
                                    fontSize = 60.sp,
                                    baseStyle = selectLargeTextStyle,
                                ),
                            ),
                            textAlign = TextAlign.Center,
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