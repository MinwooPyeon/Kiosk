package com.pixelro.nenoonkiosk.feature.screensaver

import android.util.Log
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
import kotlinx.coroutines.Dispatchers
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
            viewModelScope.launch(Dispatchers.Main) {
                Log.d("ScreenSaverViewModel", "setMediaItem 호출됨 - isSignedIn: $isSignedIn")
                exoPlayer.clearMediaItems()

                // 데이터베이스에서 스크린세이버 광고 가져오기
                val ads = screenSaverRepository.getScreenSaverAds()
                Log.d("ScreenSaverViewModel", "광고 개수: ${ads.size}")
                ads.forEachIndexed { index, ad ->
                    Log.d("ScreenSaverViewModel", "광고 $index - ID: ${ad.id}, URL: ${ad.url}, Order: ${ad.order}, Language: ${ad.language}")
                }

                // 로그인 상태와 관계없이 DB 광고 사용
                if (ads.isNotEmpty()) {
                    Log.d("ScreenSaverViewModel", "DB 광고 사용")
                    ads.forEach { ad ->
                        exoPlayer.addMediaItem(MediaItem.fromUri(ad.url))
                    }
                } else {
                    // 광고가 없으면 기본 영상 사용
                    Log.d("ScreenSaverViewModel", "광고 없음 - 기본 영상 사용")
                    exoPlayer.setMediaItem(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.ad_sub)))
                }

                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
        }
    }
