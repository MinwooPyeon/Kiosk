package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.core.ui.StyledAnnotatedText
import com.pixelro.nenoonkiosk.core.ui.TextSegment
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.PresbyopiaInspectionUiState
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.ui.theme.titleTextStyle

/**
 * 검사 방법 안내 문구를 표시하는 박스 컴포넌트
 *
 * @param uiState 현재 UI 상태
 * @param tryCount 시도 횟수
 * @param savedLanguage 저장된 언어 설정
 * @param description1_1 첫 번째 설명 텍스트 (Part 1)
 * @param description1_3 첫 번째 설명 텍스트 (Part 3)
 * @param videoGuide1 비디오 가이드 텍스트 (Part 1)
 * @param videoGuide2 비디오 가이드 텍스트 (Part 2)
 * @param videoGuide3 비디오 가이드 텍스트 (Part 3)
 * @param description2_1 두 번째 설명 텍스트 (Part 1)
 * @param description2_2 두 번째 설명 텍스트 (Part 2)
 * @param description2_3 두 번째 설명 텍스트 (Part 3)
 * @param under25cmDesc 25cm 미만 설명 텍스트
 * @param modifier Modifier
 */
@Composable
fun InspectionDescriptionBox(
    uiState: PresbyopiaInspectionUiState,
    tryCount: Int,
    savedLanguage: String?,
    description1_1: String,
    description1_3: String,
    videoGuide1: String,
    videoGuide2: String,
    videoGuide3: String,
    description2_1: String,
    description2_2: String,
    description2_3: String,
    under25cmDesc: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(start = 40.dp, top = 10.dp, end = 40.dp)
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center,
    ) {
        val fontSize = getFontSize(uiState, savedLanguage)

        when (uiState) {
            PresbyopiaInspectionUiState.Started,
            PresbyopiaInspectionUiState.AdjustingDistance -> {
                StyledAnnotatedText(
                    segments = listOf(
                        TextSegment.fromStyle(
                            text = description1_1,
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle,
                        ),
                        TextSegment.fromStyle(
                            text = " 40~50cm ",
                            color = neNoon_blue,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle,
                        ),
                        TextSegment.fromStyle(
                            text = description1_3,
                            color = White,
                            fontSize = fontSize,
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
                            color = White,
                            fontSize = fontSize,
                            baseStyle = bodyTextStyle,
                        ),
                        TextSegment.fromStyle(
                            text = " $videoGuide2",
                            color = neNoon_blue,
                            fontSize = fontSize,
                            baseStyle = bodyTextStyle,
                        ),
                        TextSegment.fromStyle(
                            text = videoGuide3,
                            color = White,
                            fontSize = fontSize,
                            baseStyle = bodyTextStyle,
                        ),
                    ),
                    textAlign = TextAlign.Center,
                )
            }
            PresbyopiaInspectionUiState.ComingCloser -> {
                StyledAnnotatedText(
                    segments = listOf(
                        TextSegment.fromStyle(
                            text = description2_1,
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle,
                        ),
                        TextSegment.fromStyle(
                            text = " $description2_2",
                            color = neNoon_blue,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle,
                        ),
                        TextSegment.fromStyle(
                            text = " $description2_3",
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle,
                        ),
                    ),
                    textAlign = TextAlign.Center,
                )
            }
            PresbyopiaInspectionUiState.NoPresbyopia -> {
                StyledAnnotatedText(
                    segments = listOf(
                        TextSegment.fromStyle(
                            text = under25cmDesc,
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle,
                        ),
                        TextSegment.fromStyle(
                            text = " $description2_2",
                            color = neNoon_blue,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle,
                        ),
                        TextSegment.fromStyle(
                            text = " $description2_3",
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle,
                        ),
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * UI 상태와 언어 설정에 따른 폰트 크기 반환
 */
private fun getFontSize(uiState: PresbyopiaInspectionUiState, savedLanguage: String?): TextUnit {
    return when (uiState) {
        PresbyopiaInspectionUiState.ComingCloser -> if (savedLanguage == "ru") 20.sp else 32.sp
        PresbyopiaInspectionUiState.TextBlinking -> if (savedLanguage == "ru") 30.sp else 44.sp
        else -> if (savedLanguage == "ru") 30.sp else 48.sp
    }
}