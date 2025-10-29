package com.pixelro.nenoonkiosk.feature.screensaver

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.exoplayer.ExoPlayer
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelro.nenoonkiosk.core.constants.NavConstants

@Composable
fun ScreenSaverRoute(
    exoPlayer: ExoPlayer,
    isSignedIn: Boolean,
    initializeTestDoneStatus: () -> Unit,
    screenSaverViewModel: ScreenSaverViewModel = hiltViewModel(),
) {
    val localContext = LocalContext.current
    val sharedPreferences = remember {
        localContext.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(true) {
        screenSaverViewModel.setMediaItem(
            isSignedIn = isSignedIn,
            exoPlayer = exoPlayer,
        )
        initializeTestDoneStatus()
    }

    DisposableEffect(true) {
        systemUiController.systemBarsDarkContentEnabled = false
        exoPlayer.play()
        onDispose {
            systemUiController.systemBarsDarkContentEnabled = true
            exoPlayer.pause()
            exoPlayer.clearVideoSurface()
        }
    }

    ScreenSaverScreen(
        exoPlayer = exoPlayer,
        savedLanguage = savedLanguage,
    )
}