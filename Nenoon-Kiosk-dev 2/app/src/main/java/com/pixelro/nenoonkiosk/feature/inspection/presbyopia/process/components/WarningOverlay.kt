package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.core.ui.StyledAnnotatedText
import com.pixelro.nenoonkiosk.core.ui.TextSegment
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.ui.theme.titleTextStyle

/**
 * 경고 메시지를 오버레이로 표시하는 컴포넌트 ("음성 안내가 진행중입니다.")
 *
 * @param text1 경고 메시지의 첫 번째 부분 (일반 텍스트)
 * @param text2 경고 메시지의 두 번째 부분 (강조 텍스트, 파란색)
 * @param text3 경고 메시지의 세 번째 부분 (일반 텍스트)
 * @param modifier Modifier
 */
@Composable
fun WarningOverlay(
    text1: String,
    text2: String,
    text3: String,
    modifier: Modifier = Modifier,
) {
    StyledAnnotatedText(
        modifier = modifier
            .padding(start = 40.dp, end = 40.dp, bottom = 360.dp)
            .fillMaxWidth()
            .background(
                color = White,
                shape = RoundedCornerShape(8.dp),
            )
            .border(
                width = 2.dp,
                color = Black,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(20.dp),
        segments = listOf(
            TextSegment.fromStyle(
                text = text1,
                color = Black,
                fontSize = 40.sp,
                baseStyle = titleTextStyle,
            ),
            TextSegment.fromStyle(
                text = text2,
                color = neNoon_blue,
                fontSize = 40.sp,
                baseStyle = titleTextStyle,
            ),
            TextSegment.fromStyle(
                text = text3,
                color = Black,
                fontSize = 40.sp,
                baseStyle = titleTextStyle,
            ),
        ),
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewWarningOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        WarningOverlay(
            text1 = "음성 안내가 ",
            text2 = "진행 중",
            text3 = "입니다.",
        )
    }
}