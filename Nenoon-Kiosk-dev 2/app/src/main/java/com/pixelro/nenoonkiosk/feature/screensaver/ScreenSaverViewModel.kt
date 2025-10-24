package com.pixelro.nenoonkiosk.feature.screensaver

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.harang.data.repository.ScreenSaverRepository
import com.pixelro.nenoonkiosk.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(UnstableApi::class)
@HiltViewModel
class ScreenSaverViewModel
    @Inject
    constructor(
        private val screenSaverRepository: ScreenSaverRepository,
    ) : ViewModel() {
        fun setMediaItem(
            isSignedIn: Boolean,
            exoPlayer: ExoPlayer,
        ) {
            viewModelScope.launch {
                if (isSignedIn) {
                    val videoURI = screenSaverRepository.getScreenSaverVideoURI()
                    exoPlayer.setMediaItem(MediaItem.fromUri(videoURI))
                } else {
                    // 화면 보호기 기본 영상 변경
                    exoPlayer.setMediaItem(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.ad_sub)))
                }
                exoPlayer.prepare()
                exoPlayer.playWhenReady
            }
        }
    }
