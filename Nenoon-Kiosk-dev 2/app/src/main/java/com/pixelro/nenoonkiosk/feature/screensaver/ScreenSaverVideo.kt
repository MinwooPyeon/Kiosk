package com.pixelro.nenoonkiosk.feature.screensaver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.selectLargeTextStyle

/**
 * 스크린세이버 비디오 컴포넌트
 *
 * ExoPlayer를 사용하여 비디오를 재생
 *
 * @param exoPlayer ExoPlayer 인스턴스 (null이면 프리뷰 모드)
 */
@Composable
fun ScreenSaverVideo(exoPlayer: ExoPlayer?) {
    val context = LocalContext.current

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
    } else {
        // 프리뷰용 플레이스홀더
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
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewScreenSaverVideoHorizontal() {
    ScreenSaverVideo(exoPlayer = null)
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
fun PreviewScreenSaverVideoVertical() {
    ScreenSaverVideo(exoPlayer = null)
}