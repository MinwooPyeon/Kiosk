package com.pixelro.nenoonkiosk.feature.screensaver

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import com.pixelro.nenoonkiosk.ui.theme.selectLargeTextStyle

@Composable
fun ScreenSaverScreen(
    exoPlayer: ExoPlayer?,
    savedLanguage: String?,
) {
    //리소스
    val description1 = stringResource(R.string.screensaver_description1)
    val description2 = stringResource(R.string.screensaver_description2)
    val description3 = stringResource(R.string.screensaver_description3)

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
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current

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
        DisposableEffect(true) {
            systemUiController.systemBarsDarkContentEnabled = false
            exoPlayer?.play()
            onDispose {
                systemUiController.systemBarsDarkContentEnabled = true
                exoPlayer?.stop()
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        // 안내 text
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
        // 영상
        if (exoPlayer != null) {
            AndroidView(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(
                            color = White,
                        ),
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
            )
        } else { //preview용
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(
                            color = Gray,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "비디오를 삽입해주세요",
                    color = White,
                    style = selectLargeTextStyle,
                )
            }
        }
        Spacer(
            modifier =
                Modifier
                    .weight(1f),
        )
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
