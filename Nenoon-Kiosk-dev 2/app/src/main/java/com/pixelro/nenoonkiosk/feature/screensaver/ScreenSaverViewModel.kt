package com.pixelro.nenoonkiosk.feature.screensaver

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.harang.data.repository.ScreenSaverRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.TestRoute
import com.pixelro.nenoonkiosk.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(UnstableApi::class)
@HiltViewModel
class ScreenSaverViewModel @Inject constructor(
    application: Application,
    private val navigator: Navigator,
    private val screenSaverRepository: ScreenSaverRepository,
    private val authRepository: AuthRepository
) : AndroidViewModel(application), ContainerHost<ScreenSaverUiState, ScreenSaverSideEffect> {

    override val container: Container<ScreenSaverUiState, ScreenSaverSideEffect> =
        container(ScreenSaverUiState.Initializing)

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build().apply {
        repeatMode = Player.REPEAT_MODE_ONE
        volume = 0f
    }

    fun initializeVideo() = intent {
        val isSignedIn = authRepository.isSignedIn()

        runCatching {
            if (isSignedIn) {
                val videoURI = screenSaverRepository.getScreenSaverVideoURI()
                exoPlayer.setMediaItem(MediaItem.fromUri(videoURI))
            } else {
                val defaultUri = RawResourceDataSource.buildRawResourceUri(R.raw.ad_sub).toString()
                exoPlayer.setMediaItem(MediaItem.fromUri(defaultUri))
            }
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            reduce { ScreenSaverUiState.Ready(isVideoPlaying = true) }
        }.onFailure { e ->
            android.util.Log.e("ScreenSaverVM", "Video initialization failed", e)
        }
    }

    fun onScreenTouched() = intent {
        val isSignedIn = authRepository.isSignedIn()

        val targetRoute = if (isSignedIn) {
            TestRoute.CategoryList
        } else {
            Route.Intro
        }

        navigator.navigateAndClearBackStack(targetRoute)

        val sideEffect = if (isSignedIn) {
            ScreenSaverSideEffect.NavigateToCategoryList
        } else {
            ScreenSaverSideEffect.NavigateToIntro
        }
        postSideEffect(sideEffect)
    }

    override fun onCleared() {
        exoPlayer.release()
        super.onCleared()
    }
}
