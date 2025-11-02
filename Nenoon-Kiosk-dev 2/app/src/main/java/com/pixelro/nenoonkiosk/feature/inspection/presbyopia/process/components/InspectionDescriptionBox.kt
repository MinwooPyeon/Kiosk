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
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.TestState
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.ui.theme.titleTextStyle

@Composable
fun InspectionDescriptionBox(
    testState: TestState,
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(start = 40.dp, top = 10.dp, end = 40.dp)
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        val fontSize = getFontSize(testState, savedLanguage)

        when (testState) {
            TestState.Started,
            TestState.AdjustingDistance -> {
                StyledAnnotatedText(
                    segments = listOf(
                        TextSegment.fromStyle(
                            text = description1_1,
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle
                        ),
                        TextSegment.fromStyle(
                            text = " 40~50cm ",
                            color = neNoon_blue,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle
                        ),
                        TextSegment.fromStyle(
                            text = description1_3,
                            color = White,
                            fontSize = fontSize,
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
                            color = White,
                            fontSize = fontSize,
                            baseStyle = bodyTextStyle
                        ),
                        TextSegment.fromStyle(
                            text = " $videoGuide2",
                            color = neNoon_blue,
                            fontSize = fontSize,
                            baseStyle = bodyTextStyle
                        ),
                        TextSegment.fromStyle(
                            text = videoGuide3,
                            color = White,
                            fontSize = fontSize,
                            baseStyle = bodyTextStyle
                        )
                    ),
                    textAlign = TextAlign.Center
                )
            }
            TestState.ComingCloser -> {
                StyledAnnotatedText(
                    segments = listOf(
                        TextSegment.fromStyle(
                            text = description2_1,
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle
                        ),
                        TextSegment.fromStyle(
                            text = " $description2_2",
                            color = neNoon_blue,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle
                        ),
                        TextSegment.fromStyle(
                            text = " $description2_3",
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle
                        )
                    ),
                    textAlign = TextAlign.Center
                )
            }
            TestState.NoPresbyopia -> {
                StyledAnnotatedText(
                    segments = listOf(
                        TextSegment.fromStyle(
                            text = under25cmDesc,
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle
                        ),
                        TextSegment.fromStyle(
                            text = " $description2_2",
                            color = neNoon_blue,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle
                        ),
                        TextSegment.fromStyle(
                            text = " $description2_3",
                            color = White,
                            fontSize = fontSize,
                            baseStyle = titleTextStyle
                        )
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun getFontSize(testState: TestState, savedLanguage: String?): TextUnit {
    return when (testState) {
        TestState.ComingCloser -> if (savedLanguage == "ru") 20.sp else 32.sp
        TestState.TextBlinking -> if (savedLanguage == "ru") 30.sp else 44.sp
        else -> if (savedLanguage == "ru") 30.sp else 48.sp
    }
}
