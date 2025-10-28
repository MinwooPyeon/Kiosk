package com.pixelro.nenoonkiosk.feature.screensaver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.harang.data.repository.ScreenSaverRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.TestRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ScreenSaverViewModel @Inject constructor(
    private val navigator: Navigator,
    private val screenSaverRepository: ScreenSaverRepository
) : ViewModel(), ContainerHost<ScreenSaverUiState, ScreenSaverSideEffect> {

    override val container: Container<ScreenSaverUiState, ScreenSaverSideEffect> =
        container(ScreenSaverUiState.Initializing)

    fun initializeVideo(exoPlayer: ExoPlayer, isSignedIn: Boolean) = intent {
        viewModelScope.launch {
            runCatching {
                if (isSignedIn) {
                    val videoURI = screenSaverRepository.getScreenSaverVideoURI()
                    exoPlayer.setMediaItem(MediaItem.fromUri(videoURI))
                } else {
                    val defaultUri =
                        RawResourceDataSource.buildRawResourceUri(R.raw.ad_sub).toString()
                    exoPlayer.setMediaItem(MediaItem.fromUri(defaultUri))
                }
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                reduce { ScreenSaverUiState.Ready(isVideoPlaying = true) }
            }.onFailure { e ->
                android.util.Log.e("ScreenSaverVM", "Video initialization failed", e)
            }
        }
    }

    fun onScreenTouched(isSignedIn: Boolean) = intent {
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
}
