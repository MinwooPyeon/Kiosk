package com.pixelro.nenoonkiosk.feature.inspection.visualacuity

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.util.STT
import com.pixelro.nenoonkiosk.core.util.stt.SttConfig
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.VisualAcuityInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

enum class VisualAcuitySttState {
    Inactive,
    ShortDigit,
    LongDigit,
}

@HiltViewModel
class VisualAcuityViewModel
@Inject
constructor(
    application: Application,
) : AndroidViewModel(application) {
    private var inspectionType: InspectionType = InspectionType.ShortDistanceVisualAcuity
    private val _isMeasuringDistanceContentVisible = MutableStateFlow(true)
    val isMeasuringDistanceContentVisible: StateFlow<Boolean> = _isMeasuringDistanceContentVisible
    private val _isCoveredEyeCheckingContentVisible = MutableStateFlow(false)
    val isCoveredEyeCheckingContentVisible: StateFlow<Boolean> = _isCoveredEyeCheckingContentVisible
    private val _isVisualAcuityContentVisible = MutableStateFlow(false)
    val isVisualAcuityContentVisible: StateFlow<Boolean> = _isVisualAcuityContentVisible
    private val _sightLevel = MutableStateFlow(1)
    val sightLevel: StateFlow<Int> = _sightLevel
    private val _isLeftEye = MutableStateFlow(true)
    val isLeftEye: StateFlow<Boolean> = _isLeftEye
    private var leftEyeSightValue = 1
    private var rightEyeSightValue = 1
    private val _randomList = MutableStateFlow<List<Int>>(listOf(0))
    val randomList: StateFlow<List<Int>> = _randomList
    private var _ansNum = MutableStateFlow(0)
    val ansNum: StateFlow<Int> = _ansNum
    private val _sttState = MutableStateFlow(VisualAcuitySttState.Inactive)
    val sttState: StateFlow<VisualAcuitySttState> = _sttState
    private val _sttSessionActive = MutableStateFlow(false)
    val sttSessionActive: StateFlow<Boolean> = _sttSessionActive
    private val _sttHadSpeech = MutableStateFlow(false)
    val sttHadSpeech: StateFlow<Boolean> = _sttHadSpeech
    private var wrongCount = 0f

    fun updateIsMeasuringDistanceContentVisible(visible: Boolean) {
        _isMeasuringDistanceContentVisible.update { visible }
        if (visible) {
            _sttState.update { VisualAcuitySttState.Inactive }
            _sttSessionActive.update { false }
            _sttHadSpeech.update { false }
            STT.stopContinuousListening()
        }
    }

    fun updateIsVisualAcuityContentVisible(visible: Boolean) {
        _isVisualAcuityContentVisible.update { visible }
        val targetState =
            when (inspectionType) {
                InspectionType.LongDistanceVisualAcuity -> VisualAcuitySttState.LongDigit
                else -> VisualAcuitySttState.ShortDigit
            }
        _sttState.update { if (visible) targetState else VisualAcuitySttState.Inactive }
        if (!visible) {
            STT.stopContinuousListening()
        }
    }

    fun onSttSessionStateChanged(active: Boolean, hadSpeech: Boolean) {
        _sttSessionActive.update { active }
        _sttHadSpeech.update { hadSpeech }
    }

    fun startVoiceRecognition(onResult: (String) -> Unit) {
        if (_sttSessionActive.value) return
        if (_sttState.value != VisualAcuitySttState.ShortDigit &&
            _sttState.value != VisualAcuitySttState.LongDigit
        ) {
            return
        }

        _sttSessionActive.update { true }
        _sttHadSpeech.update { false }
        STT.enableVisualAcuityNumberHints(boost = 180)

        STT.startContinuousListening(
            language = "ko-KR",
            onResult = { result ->
                _sttSessionActive.update { false }
                _sttHadSpeech.update { true }
                onResult(result)
            },
            onError = { _ ->
                _sttSessionActive.update { false }
            },
            onReady = {
                _sttSessionActive.update { true }
            },
            restartDelayOnResultMs = SttConfig.Recognition.AUTO_RESTART_DELAY_MS,
            restartDelayOnErrorMs = SttConfig.Recognition.AUTO_RESTART_DELAY_MS + 250L,
            shortUtterance = true,
            autoStopTimeoutMs = SttConfig.Recognition.DEFAULT_AUTO_STOP_TIMEOUT_MS,
        )
    }

    fun cancelVoiceRecognition() {
        STT.stopContinuousListening()
        _sttSessionActive.update { false }
    }

    private var sightHistory =
        mutableMapOf(
            1 to Pair(0, 0),
            2 to Pair(0, 0),
            3 to Pair(0, 0),
            4 to Pair(0, 0),
            5 to Pair(0, 0),
            6 to Pair(0, 0),
            7 to Pair(0, 0),
            8 to Pair(0, 0),
            9 to Pair(0, 0),
            10 to Pair(0, 0),
        )

    fun processAnswerSelected(
        idx: Int,
        handleWrong: (Float) -> Unit,
        toResultScreen: () -> Unit,
    ) {
        Log.d("VisualAcuityVM", "processAnswerSelected idx=$idx, ansNum=${ansNum.value}, randomList=${randomList.value}")
        var isEnd = false
        /**
         * choose number
         */
        if (idx != 3) {
            /**
             * if correct
             */
            if (ansNum.value == _randomList.value[idx]) {
                sightHistory[_sightLevel.value] =
                    Pair(
                        sightHistory[_sightLevel.value]!!.first + 1,
                        sightHistory[_sightLevel.value]!!.second,
                    )
                /**
                 * if first trial
                 */
                if (sightHistory[_sightLevel.value]!!.first == 1 && sightHistory[_sightLevel.value]!!.second == 0) {
                    /**
                     * if level == 10
                     */
                    if (_sightLevel.value == 10) {
                        viewModelScope.launch {
                            handleWrong(1.2f)
                            delay(500)
                            isEnd = true
                            moveToNextStep(
                                handleWrong,
                                toResultScreen,
                            )
                        }
                    } else {
                        _sightLevel.update { it + 1 }
                    }
                }
            }
            /**
             * if wrong
             */
            else {
                wrongCount++
                handleWrong(wrongCount / (wrongCount + 1f))
                sightHistory[_sightLevel.value] =
                    Pair(
                        sightHistory[_sightLevel.value]!!.first,
                        sightHistory[_sightLevel.value]!!.second + 1,
                    )
            }
        }
        /**
         * choose question mark
         */
        else {
            wrongCount++
            handleWrong(wrongCount / (wrongCount + 1f))
            sightHistory[_sightLevel.value] =
                Pair(
                    sightHistory[_sightLevel.value]!!.first,
                    sightHistory[_sightLevel.value]!!.second + 1,
                )
        }
        /**
         * if 3rd trial
         */
        if (sightHistory[_sightLevel.value]!!.first + sightHistory[_sightLevel.value]!!.second >= 3) {
            /**
             * if correct >= 2
             */
            if (sightHistory[_sightLevel.value]!!.first >= 2) {
                /**
                 * if level == 10
                 */
                if (_sightLevel.value == 10) {
                    viewModelScope.launch {
                        handleWrong(1.2f)
                        delay(500)
                        isEnd = true
                        moveToNextStep(
                            handleWrong,
                            toResultScreen,
                        )
                    }
                } else {
                    _sightLevel.update { it + 1 }
                }
            }
            /**
             * if correct < 1
             */
            else {
                viewModelScope.launch {
                    handleWrong(1.2f)
                    delay(500)
                    isEnd = true
                    Log.d("VisualAcuityVM", "moveToNextStep triggered (wrong)")
                    moveToNextStep(
                        handleWrong,
                        toResultScreen,
                    )
                }
            }
        }
        if (!isEnd) updateRandomList()
    }

    fun getVisualAcuityInspectionResult(): VisualAcuityInspectionResult {
        return VisualAcuityInspectionResult(
            leftEyeSightValue,
            rightEyeSightValue,
        )
    }

    private fun moveToNextStep(
        handleWrong: (Float) -> Unit,
        toResultScreen: () -> Unit,
    ) {
        Log.d("VisualAcuityVM", "moveToNextStep leftEye=${_isLeftEye.value}")
        wrongCount = 0f
        handleWrong(0.1f)
        if (_isLeftEye.value) {
            sightHistory =
                mutableMapOf(
                    1 to Pair(0, 0),
                    2 to Pair(0, 0),
                    3 to Pair(0, 0),
                    4 to Pair(0, 0),
                    5 to Pair(0, 0),
                    6 to Pair(0, 0),
                    7 to Pair(0, 0),
                    8 to Pair(0, 0),
                    9 to Pair(0, 0),
                    10 to Pair(0, 0),
                )
            leftEyeSightValue = _sightLevel.value
            _isMeasuringDistanceContentVisible.update { true }
            _isVisualAcuityContentVisible.update { false }
            _sttState.update { VisualAcuitySttState.Inactive }
            STT.stopContinuousListening()
            STT.setStateObserver { active, hadSpeech ->
                onSttSessionStateChanged(active, hadSpeech)
            }
            _isLeftEye.update { false }
        } else {
            rightEyeSightValue = _sightLevel.value
            STT.stopContinuousListening()
            STT.setStateObserver { active, hadSpeech ->
                onSttSessionStateChanged(active, hadSpeech)
            }
            _isVisualAcuityContentVisible.update { false }
            toResultScreen()
            return
        }
        viewModelScope.launch {
            delay(450)
            _sightLevel.update { 1 }
        }
    }

    private fun updateRandomList() {
        val previous = _randomList.value
        var attempts = 0
        var candidates: List<Int>
        do {
            candidates =
                buildList {
                    val used = mutableSetOf<Int>()
                    repeat(3) {
                        var value: Int
                        do {
                            value = (2..7).random()
                        } while (!used.add(value))
                        add(value)
                    }
                }
            attempts++
        } while (candidates == previous && attempts < 10)
        _randomList.value = candidates

        val prevNum = _ansNum.value
        var newNum = candidates.random()
        if (candidates.size > 1) {
            val iterator = candidates.iterator()
            while (newNum == prevNum && iterator.hasNext()) {
                newNum = iterator.next()
            }
            if (newNum == prevNum) {
                newNum = candidates.first()
            }
        }
        _ansNum.value = newNum
    }

    fun init() {
        wrongCount = 0f
        _isLeftEye.update { true }
        _sightLevel.update { 1 }
        updateRandomList()
        leftEyeSightValue = 1
        rightEyeSightValue = 1
        sightHistory =
            mutableMapOf(
                1 to Pair(0, 0),
                2 to Pair(0, 0),
                3 to Pair(0, 0),
                4 to Pair(0, 0),
                5 to Pair(0, 0),
                6 to Pair(0, 0),
                7 to Pair(0, 0),
                8 to Pair(0, 0),
                9 to Pair(0, 0),
                10 to Pair(0, 0),
            )
        _isMeasuringDistanceContentVisible.update { true }
        _isCoveredEyeCheckingContentVisible.update { false }
        _isVisualAcuityContentVisible.update { false }
        _sttState.update { VisualAcuitySttState.Inactive }
        STT.stopContinuousListening()
        STT.setStateObserver { active, hadSpeech ->
            onSttSessionStateChanged(active, hadSpeech)
        }
    }

    fun setInspectionType(type: InspectionType) {
        inspectionType = type
        if (_isVisualAcuityContentVisible.value) {
            val targetState =
                when (inspectionType) {
                    InspectionType.LongDistanceVisualAcuity -> VisualAcuitySttState.LongDigit
                    else -> VisualAcuitySttState.ShortDigit
                }
            _sttState.update { targetState }
        }
    }

    override fun onCleared() {
        STT.setStateObserver(null)
        super.onCleared()
    }
}
