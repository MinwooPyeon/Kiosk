package com.pixelro.nenoonkiosk.test.presbyopia

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@HiltViewModel
class  PresbyopiaViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _testState = MutableStateFlow(TestState.Started)
    var testState: StateFlow<TestState> = _testState

    private val _tryCount = MutableStateFlow(0)

    val tryCount: StateFlow<Int> = _tryCount
    private var firstDistance = 0f
    private var secondDistance = 0f
    private var thirdDistance = 0f
    private val _isTextShowing = MutableStateFlow(true)
    val isTextShowing: StateFlow<Boolean> = _isTextShowing
    private var isTTSDescriptionDone = false
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(getApplication()).build()
    private val _isComingCloserTTSDone = MutableStateFlow(false)
    val isComingCloserTTSDone: StateFlow<Boolean> = _isComingCloserTTSDone

    /**
     * TTS
     */
    fun checkCondition(dist: Float) {
        if (!TTS.tts.isSpeaking) {
            when (_testState.value) {
                TestState.Started -> {
                    isTTSDescriptionDone = false
                    TTS.setOnDoneListener { _testState.update { TestState.AdjustingDistance } }
                    _isComingCloserTTSDone.update { false }
                    when (_tryCount.value) {
                        0 -> TTS.speechTTS(StringProvider.getString(
                            R.string.tts_presbyopia_test_start1), TextToSpeech.QUEUE_ADD)
                        1 -> TTS.speechTTS(StringProvider.getString(
                            R.string.tts_presbyopia_test_start2), TextToSpeech.QUEUE_ADD)
                        2 -> TTS.speechTTS(StringProvider.getString(
                            R.string.tts_presbyopia_test_start3), TextToSpeech.QUEUE_ADD)
                    }
                }

                TestState.AdjustingDistance -> {
                    if (!isTTSDescriptionDone) {
                        TTS.setOnDoneListener { isTTSDescriptionDone = true }
                        TTS.speechTTS(StringProvider.getString(
                            R.string.tts_presbyopia_test_adjustment), TextToSpeech.QUEUE_ADD)
                    }
                    if (dist > 400f && dist < 500f && isTTSDescriptionDone) {
                        TTS.clearOnDoneListener()
                        when (_tryCount.value) {
                            0 -> _testState.update { TestState.TextBlinking }
                            else -> _testState.update { TestState.ComingCloser }
                        }
//                        _testState.update { TestState.TextBlinking }
                        isTTSDescriptionDone = false
                    }
                }

                TestState.TextBlinking -> {
                    if (!isTTSDescriptionDone) {
                        isTTSDescriptionDone = true
                        TTS.speechTTS(StringProvider.getString(
                            R.string.tts_presbyopia_test_guide_video), TextToSpeech.QUEUE_ADD)
                        viewModelScope.launch {
                            for (i in 1..12) {
                                _isTextShowing.update { !it }
                                delay(250)
                            }
                            _testState.update { TestState.ComingCloser }
                            isTTSDescriptionDone = false
                        }
                    }
                }

                TestState.ComingCloser -> {
                    if (!isTTSDescriptionDone) {
                        TTS.setOnDoneListener {
                            isTTSDescriptionDone = true
                            _isComingCloserTTSDone.update { true }
                            TTS.clearOnDoneListener()
                        }
                        TTS.speechTTS(
                            when (_tryCount.value) {
                                0 -> StringProvider.getString(
                                    R.string.tts_presbyopia_test_slowly_like_video,
                                    
                                )
                                else -> StringProvider.getString(
                                    R.string.tts_presbyopia_test_slowly,
                                    
                                )
                            }, TextToSpeech.QUEUE_ADD
                        )
                        when (_tryCount.value) {
                            0 -> TTS.speechTTS(StringProvider.getString(
                                R.string.tts_presbyopia_test_start_after,
                                
                            ), TextToSpeech.QUEUE_ADD)
                            else -> {}
                        }
                    }
                    if (dist < 250f && isTTSDescriptionDone) {
                        TTS.clearOnDoneListener()
                        _testState.update { TestState.NoPresbyopia }
                        isTTSDescriptionDone = false
                    }
                }

                TestState.NoPresbyopia -> {
                    if (!isTTSDescriptionDone) {
                        isTTSDescriptionDone = true
                        TTS.speechTTS(
                            when (_tryCount.value) {
                                0 -> StringProvider.getString(
                                    R.string.tts_presbyopia_test_no_problem1,
                                    
                                )
                                1 -> StringProvider.getString(
                                    R.string.tts_presbyopia_test_no_problem2,
                                    
                                )
                                else -> StringProvider.getString(
                                    R.string.tts_presbyopia_test_no_problem3,
                                    
                                )
                            }, TextToSpeech.QUEUE_ADD
                        )
                    }
                }
            }
        }
    }

    /**
     * 검사 넘어가는 함수
     */
    fun moveToNextStep(dist: Float, handleProgress: (Float) -> Unit, toResultScreen: () -> Unit) {
        when (_tryCount.value) {
            0 -> {
                firstDistance = if (_testState.value == TestState.NoPresbyopia) 25f
                else dist / 10
                _tryCount.update { it + 1 }
                handleProgress(0.33f)
                _isTextShowing.update { true }
                _testState.update { TestState.Started }
            }
            1 -> {
                secondDistance = if (_testState.value == TestState.NoPresbyopia) 25f
                else dist / 10

                _tryCount.update { it + 1 }
                handleProgress(0.66f)
                _isTextShowing.update { true }
                _testState.update { TestState.Started }
            }
            else -> {
                viewModelScope.launch {
                    thirdDistance = if (_testState.value == TestState.NoPresbyopia) 25f
                    else dist / 10

                    handleProgress(1.2f)
                    TTS.speechTTS(StringProvider.getString(
                        R.string.tts_wait_for_result), TextToSpeech.QUEUE_ADD)
                    delay(500)
                    toResultScreen()
                }
            }
        }
    }

    /**
     * 거리 계산 공식
     */
    fun getPresbyopiaTestResult(): PresbyopiaTestResult {
        var max = 2147483647f
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
        return PresbyopiaTestResult(firstDistance, secondDistance, thirdDistance, avgDistance, age)
    }

    fun init() {
        _isTextShowing.update { true }
    }

    enum class TestState {
        Started, AdjustingDistance, TextBlinking, ComingCloser, NoPresbyopia
    }

    init {
        _tryCount.update { 0 }
        exoPlayer.volume = 0f
    }
}