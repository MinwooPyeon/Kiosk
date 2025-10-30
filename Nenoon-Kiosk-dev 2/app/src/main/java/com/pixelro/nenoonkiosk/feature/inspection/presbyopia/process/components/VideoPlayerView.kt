package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pixelro.nenoonkiosk.ui.theme.LightGray
import com.pixelro.nenoonkiosk.ui.theme.titleTextStyle

/**
 * ExoPlayer를 사용하여 비디오를 재생하는 컴포넌트
 *
 * @param context Android Context
 * @param exoPlayer ExoPlayer 인스턴스 (null인 경우 플레이스홀더 표시)
 * @param placeholderText 플레이스홀더에 표시할 텍스트 (기본값: "검사 영상")
 * @param modifier Modifier
 */
@Composable
fun VideoPlayerView(
    context: Context,
    exoPlayer: ExoPlayer?,
    placeholderText: String = "검사 영상",
    modifier: Modifier = Modifier,
) {
    if (exoPlayer != null) {
        Surface(
            modifier = modifier.fillMaxSize(),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
            )
        }
    } else {
        // 프리뷰용 플레이스홀더
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(LightGray),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = placeholderText,
                style = titleTextStyle.copy(color = Color.Gray, fontSize = 32.sp),
            )
        }
    }
}