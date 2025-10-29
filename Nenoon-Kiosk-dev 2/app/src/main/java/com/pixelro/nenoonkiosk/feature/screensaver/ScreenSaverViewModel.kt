package com.pixelro.nenoonkiosk.feature.screensaver

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.harang.data.repository.ScreenSaverRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class ScreenSaverViewModel @Inject constructor(
    application: Application,
    private val screenSaverRepository: ScreenSaverRepository,
    private val navigator: Navigator
) : AndroidViewModel(application), ContainerHost<ScreenSaverUiState, Nothing> {

    override val container: Container<ScreenSaverUiState, Nothing> =
        container(ScreenSaverUiState())

    init {
        loadLanguage()
    }

    private fun loadLanguage() = intent {
        val context = getApplication<Application>()
        val sp = context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val savedLanguage = sp.getString("language", "ko") ?: "ko"

        reduce {
            state.copy(language = savedLanguage)
        }
    }

    fun setMediaItem(isSignedIn: Boolean, exoPlayer: ExoPlayer) = intent {
        val videoUri = if (isSignedIn) {
            screenSaverRepository.getScreenSaverVideoURI()
        } else {
            RawResourceDataSource.buildRawResourceUri(R.raw.ad_sub).toString()
        }

        exoPlayer.setMediaItem(MediaItem.fromUri(videoUri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        reduce {
            state.copy(isVideoReady = true)
        }
    }

    fun playVideo(exoPlayer: ExoPlayer) {
        exoPlayer.play()
    }

    fun pauseVideo(exoPlayer: ExoPlayer) {
        exoPlayer.pause()
        exoPlayer.clearVideoSurface()
    }
}
