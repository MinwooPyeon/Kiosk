package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

/**
 * 여러 스타일을 가진 텍스트 세그먼트를 조합하여 표시하는 컴포넌트
 *
 * @param segments 표시할 텍스트 세그먼트 리스트
 * @param modifier Modifier
 * @param textAlign 텍스트 정렬
 */
@Composable
fun StyledAnnotatedText(
    segments: List<TextSegment>,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            segments.forEach { segment ->
                withStyle(
                    style = SpanStyle(
                        color = segment.color,
                        fontSize = segment.fontSize,
                        fontWeight = segment.fontWeight,
                        fontFamily = segment.fontFamily,
                    )
                ) {
                    append(segment.text)
                }
            }
        },
        textAlign = textAlign,
    )
}

/**
 * 텍스트 세그먼트 데이터 클래스
 *
 * @param text 표시할 텍스트
 * @param color 텍스트 색상
 * @param fontSize 폰트 크기
 * @param fontWeight 폰트 굵기 (선택사항)
 * @param fontFamily 폰트 패밀리 (선택사항)
 */
data class TextSegment(
    val text: String,
    val color: Color,
    val fontSize: TextUnit,
    val fontWeight: FontWeight? = null,
    val fontFamily: FontFamily? = null,
) {
    companion object {
        /**
         * TextStyle로부터 TextSegment를 생성하는 헬퍼 함수
         */
        fun fromStyle(
            text: String,
            color: Color,
            fontSize: TextUnit,
            baseStyle: TextStyle,
        ): TextSegment = TextSegment(
            text = text,
            color = color,
            fontSize = fontSize,
            fontWeight = baseStyle.fontWeight,
            fontFamily = baseStyle.fontFamily,
        )

        fun create(
            text: String,
            color: Color,
            fontSize: TextUnit,
            fontWeight: FontWeight? = null,
            fontFamily: FontFamily? = null,
        ): TextSegment = TextSegment(
            text = text,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
        )
    }
}