package com.pixelro.nenoonkiosk.feature.screensaver

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelro.nenoonkiosk.core.navigation.LocalNavigator
import ke.co.banit.idle_detector_compose.LocalIdleReset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

@UnstableApi
@Composable
fun ScreenSaverRoute(
    exoPlayer: ExoPlayer,
    isSignedIn: Boolean,
    viewModel: ScreenSaverViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value
    val systemUiController = rememberSystemUiController()
    val navigator = LocalNavigator.current
    val idleReset = LocalIdleReset.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.setMediaItem(isSignedIn, exoPlayer)
    }

    // ScreenSaver 화면에서는 idle 타이머를 계속 리셋하여 재진입 방지
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            idleReset?.invoke()
        }
    }

    DisposableEffect(Unit) {
        systemUiController.systemBarsDarkContentEnabled = false
        viewModel.playVideo(exoPlayer)

        onDispose {
            systemUiController.systemBarsDarkContentEnabled = true
            viewModel.pauseVideo(exoPlayer)
        }
    }

    // 화면 전체에 터치 감지를 위한 Box
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    // 터치 시 이전 화면으로 복귀 (코루틴 스코프에서 실행)
                    coroutineScope.launch {
                        navigator.navigateBack()
                        // idle 타이머 리셋
                        idleReset?.invoke()
                    }
                }
            }
    ) {
        ScreenSaverScreen(
            state = ScreenSaverUiState(
                isVideoReady = state.isVideoReady,
                videoUri = "",
                language = state.language
            ),
            exoPlayer = exoPlayer
        )
    }
}
