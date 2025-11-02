package com.pixelro.nenoonkiosk.feature.inspection.presbyopia

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.navigation.InspectionRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@HiltViewModel
class PresbyopiaViewModel @Inject constructor(
    private val navigator: Navigator,
    application: Application
) : AndroidViewModel(application), ContainerHost<PresbyopiaUiState, Nothing> {

    override val container = container<PresbyopiaUiState, Nothing>(
        PresbyopiaUiState()
    )

    private var firstDistance = 0f
    private var secondDistance = 0f
    private var thirdDistance = 0f
    private var isTTSDescriptionDone = false

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(getApplication()).build().apply {
        volume = 0f
    }

    fun init() = intent {
        reduce {
            PresbyopiaUiState(
                testState = TestState.Started,
                tryCount = 0,
                isTextShowing = true,
                isComingCloserTTSDone = false
            )
        }
    }

    fun checkCondition(dist: Float) = intent {
        val isTTSSpeaking = try {
            TTS.tts.isSpeaking
        } catch (e: Exception) {
            false
        }

        if (!isTTSSpeaking) {
            when (state.testState) {
                TestState.Started -> {
                    isTTSDescriptionDone = false
                    TTS.setOnDoneListener {
                        intent {
                            reduce {
                                state.copy(
                                    testState = TestState.AdjustingDistance,
                                    isComingCloserTTSDone = false
                                )
                            }
                        }
                    }
                    when (state.tryCount) {
                        0 -> TTS.speechTTS(
                            StringProvider.getString(R.string.tts_presbyopia_test_start1),
                            TextToSpeech.QUEUE_ADD
                        )
                        1 -> TTS.speechTTS(
                            StringProvider.getString(R.string.tts_presbyopia_test_start2),
                            TextToSpeech.QUEUE_ADD
                        )
                        2 -> TTS.speechTTS(
                            StringProvider.getString(R.string.tts_presbyopia_test_start3),
                            TextToSpeech.QUEUE_ADD
                        )
                    }
                }

                TestState.AdjustingDistance -> {
                    if (!isTTSDescriptionDone) {
                        TTS.setOnDoneListener {
                            isTTSDescriptionDone = true
                        }
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_presbyopia_test_adjustment),
                            TextToSpeech.QUEUE_ADD
                        )
                    }

                    if (dist in 400f..500f && isTTSDescriptionDone) {
                        TTS.clearOnDoneListener()
                        reduce {
                            state.copy(
                                testState = if (state.tryCount == 0) {
                                    TestState.TextBlinking
                                } else {
                                    TestState.ComingCloser
                                }
                            )
                        }
                        isTTSDescriptionDone = false
                    }
                }

                TestState.TextBlinking -> {
                    if (!isTTSDescriptionDone) {
                        isTTSDescriptionDone = true
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_presbyopia_test_guide_video),
                            TextToSpeech.QUEUE_ADD
                        )

                        viewModelScope.launch {
                            for (i in 1..12) {
                                intent {
                                    reduce { state.copy(isTextShowing = !state.isTextShowing) }
                                }
                                delay(250)
                            }
                            intent {
                                reduce { state.copy(testState = TestState.ComingCloser) }
                            }
                            isTTSDescriptionDone = false
                        }
                    }
                }

                TestState.ComingCloser -> {
                    if (!isTTSDescriptionDone) {
                        TTS.setOnDoneListener {
                            isTTSDescriptionDone = true
                            intent {
                                reduce { state.copy(isComingCloserTTSDone = true) }
                            }
                            TTS.clearOnDoneListener()
                        }
                        TTS.speechTTS(
                            when (state.tryCount) {
                                0 -> StringProvider.getString(R.string.tts_presbyopia_test_slowly_like_video)
                                else -> StringProvider.getString(R.string.tts_presbyopia_test_slowly)
                            },
                            TextToSpeech.QUEUE_ADD
                        )

                        if (state.tryCount == 0) {
                            TTS.speechTTS(
                                StringProvider.getString(R.string.tts_presbyopia_test_start_after),
                                TextToSpeech.QUEUE_ADD
                            )
                        }
                    }

                    if (dist < 250f && isTTSDescriptionDone) {
                        TTS.clearOnDoneListener()
                        reduce { state.copy(testState = TestState.NoPresbyopia) }
                        isTTSDescriptionDone = false
                    }
                }

                TestState.NoPresbyopia -> {
                    if (!isTTSDescriptionDone) {
                        isTTSDescriptionDone = true
                        TTS.speechTTS(
                            when (state.tryCount) {
                                0 -> StringProvider.getString(R.string.tts_presbyopia_test_no_problem1)
                                1 -> StringProvider.getString(R.string.tts_presbyopia_test_no_problem2)
                                else -> StringProvider.getString(R.string.tts_presbyopia_test_no_problem3)
                            },
                            TextToSpeech.QUEUE_ADD
                        )
                    }
                }
            }
        }
    }

    fun moveToNextStep(dist: Float, handleProgress: (Float) -> Unit) = intent {
        when (state.tryCount) {
            0 -> {
                firstDistance = if (state.testState == TestState.NoPresbyopia) 25f else dist / 10
                reduce {
                    state.copy(
                        tryCount = 1,
                        isTextShowing = true,
                        testState = TestState.Started
                    )
                }
                handleProgress(0.33f)
            }
            1 -> {
                secondDistance = if (state.testState == TestState.NoPresbyopia) 25f else dist / 10
                reduce {
                    state.copy(
                        tryCount = 2,
                        isTextShowing = true,
                        testState = TestState.Started
                    )
                }
                handleProgress(0.66f)
            }
            else -> {
                viewModelScope.launch {
                    thirdDistance = if (state.testState == TestState.NoPresbyopia) 25f else dist / 10
                    handleProgress(1.2f)
                    TTS.speechTTS(
                        StringProvider.getString(R.string.tts_wait_for_result),
                        TextToSpeech.QUEUE_ADD
                    )
                    delay(500)
                    navigateToResult()
                }
            }
        }
    }

    private fun navigateToResult() = intent {
        val result = getPresbyopiaTestResult()
        // TODO: 결과 데이터 전달 방법 구현 필요
        navigator.navigate(InspectionRoute.InspectionResult)
    }

    fun getPresbyopiaTestResult(): PresbyopiaInspectionResult {
        var max = Float.MAX_VALUE
        val avgDistance = (firstDistance + secondDistance + thirdDistance) / 3
        var age = 25

        for (entry in AccommodationData.allEntries) {
            var diff = avgDistance - entry.x
            if (diff < 0) diff = -diff
            if (diff < max) {
                max = avgDistance - entry.x
                age = entry.y.toInt()
            }
        }

        return PresbyopiaInspectionResult(
            firstDistance,
            secondDistance,
            thirdDistance,
            avgDistance,
            age
        )
    }

    fun prepareAdjustingDistanceVideo() {
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.setMediaItem(
            MediaItem.fromUri(
                RawResourceDataSource.buildRawResourceUri(R.raw.measuring_distance_video_2)
            )
        )
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun prepareComingCloserVideo() {
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        exoPlayer.setMediaItem(
            MediaItem.fromUri(
                RawResourceDataSource.buildRawResourceUri(R.raw.presbyopia_video_2_new)
            )
        )
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
        TTS.clearOnDoneListener()
    }
}
