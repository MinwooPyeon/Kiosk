package com.pixelro.nenoonkiosk.feature.screensaver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.ui.theme.selectLargeTextStyle

/**
 * 스크린세이버 안내 텍스트 컴포넌트
 *
 * 사용자에게 화면을 터치하도록 안내하는 애니메이션 텍스트를 표시
 *
 * @param shiftVal 애니메이션 값 (0f ~ 0.5f)
 * @param savedLanguage 저장된 언어 설정 (한국어일 경우 작은 폰트 사용)
 */
@Composable
fun ScreenSaverGuideText(
    shiftVal: Float,
    savedLanguage: String?,
) {
    val description1 = stringResource(R.string.screensaver_description1)
    val description2 = stringResource(R.string.screensaver_description2)
    val description3 = stringResource(R.string.screensaver_description3)

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = description1,
            style = selectLargeTextStyle,
            color = White,
            textAlign = TextAlign.Center,
        )
        Text(
            text = description2,
            style = selectLargeTextStyle,
            color = neNoon_blue,
            modifier =
                Modifier.graphicsLayer {
                    translationY = shiftVal * 60f
                },
            textAlign = TextAlign.Center,
        )
        Text(
            text = description3,
            style = if (savedLanguage == "ko") bodyTextStyle else selectLargeTextStyle,
            color = White,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewScreenSaverGuideTextHorizontal() {
    ScreenSaverGuideText(
        shiftVal = 0.3f,
        savedLanguage = "ko",
    )
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
fun PreviewScreenSaverGuideTextVertical() {
    ScreenSaverGuideText(
        shiftVal = 0.3f,
        savedLanguage = "en",
    )
}