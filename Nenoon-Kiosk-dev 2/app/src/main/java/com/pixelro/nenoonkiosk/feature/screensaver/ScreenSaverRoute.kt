package com.pixelro.nenoonkiosk.feature.screensaver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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

    LaunchedEffect(Unit) {
        viewModel.setMediaItem(isSignedIn, exoPlayer)
    }

    DisposableEffect(Unit) {
        systemUiController.systemBarsDarkContentEnabled = false
        viewModel.playVideo(exoPlayer)

        onDispose {
            systemUiController.systemBarsDarkContentEnabled = true
            viewModel.pauseVideo(exoPlayer)
        }
    }

    ScreenSaverScreen(
        state = ScreenSaverUiState(
            isVideoReady = state.isVideoReady,
            videoUri = "",
            language = state.language
        ),
        exoPlayer = exoPlayer
    )
}
