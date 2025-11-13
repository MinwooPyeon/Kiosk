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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.core.util.isLandscape
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

    val isLandscape = isLandscape()

    if (isLandscape) {
        // 가로 모드
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            ScreenSaverGuideText(
                shiftVal = shiftVal,
                savedLanguage = savedLanguage,
            )

            Spacer(modifier = Modifier.height(30.dp))

            ScreenSaverVideo(
                exoPlayer = exoPlayer,
                useAspectRatio = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    } else {
        // 세로 모드
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.weight(1f))

            ScreenSaverGuideText(
                shiftVal = shiftVal,
                savedLanguage = savedLanguage,
            )

            Spacer(modifier = Modifier.height(40.dp))

            ScreenSaverVideo(
                exoPlayer = exoPlayer,
                useAspectRatio = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 1920,
    heightDp = 1080,
    name = "Landscape - 32 inch Full HD"
)
@Composable
fun PreviewScreenSaverScreen32InchFullHD() {
    ScreenSaverScreen(
        exoPlayer = null,
        savedLanguage = "ko",
    )
}

@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    name = "Landscape - Standard Tablet"
)
@Composable
fun PreviewScreenSaverScreenHorizontal() {
    ScreenSaverScreen(
        exoPlayer = null,
        savedLanguage = "ko",
    )
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    name = "Portrait - Standard Tablet"
)
@Composable
fun PreviewScreenSaverScreenVertical() {
    ScreenSaverScreen(
        exoPlayer = null,
        savedLanguage = "ko",
    )
}
