package com.pixelro.nenoonkiosk.feature.screensaver

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.ui.theme.Black

/**
 * 스크린세이버 메인 화면
 *
 * @param exoPlayer ExoPlayer 인스턴스 (NenoonViewModel에서 주입)
 * @param savedLanguage 저장된 언어 설정
 */
@Composable
fun ScreenSaverScreen(
    exoPlayer: ExoPlayer?,
    savedLanguage: String?,
) {
    val transition = rememberInfiniteTransition()
    val shiftVal by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = 1000
                    },
                repeatMode = RepeatMode.Reverse,
            ),
    )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    color = Black,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Spacer(modifier = Modifier.weight(1f))

        // 안내 텍스트
        ScreenSaverGuideText(
            shiftVal = shiftVal,
            savedLanguage = savedLanguage,
        )

        // 비디오 플레이어
        ScreenSaverVideo(exoPlayer = exoPlayer)

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewScreenSaverScreenHorizental() {
    ScreenSaverScreen(
        exoPlayer = null,
        savedLanguage = "ko",
    )
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
fun PreviewScreenSaverScreenVertical() {
    ScreenSaverScreen(
        exoPlayer = null,
        savedLanguage = "ko",
    )
}
