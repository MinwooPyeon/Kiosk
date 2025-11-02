package com.pixelro.nenoonkiosk.feature.inspection.gripStrength

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.manager.InGripManager
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.result.GripStrengthInspectionResultContract
import com.pixelro.nenoonkiosk.feature.iotdevice.inGrip.DynamometerConnectionScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class GripStrengthViewModel @Inject constructor(
    private val navigator: Navigator,
    application: Application
) : AndroidViewModel(application), ContainerHost<GripStrengthUiState, Nothing> {

    override val container = container<GripStrengthUiState, Nothing>(
        GripStrengthUiState()
    )

    private var gripMeasurementTimer: CountDownTimer? = null
    private val testFailureThreshold = 5.0

    init {
        observeBluetoothState()
        observeDevices()
        observeBattery()
        observeDynamometerData()
    }

    fun init(isSignedIn: Boolean) = intent {
        reduce {
            GripStrengthUiState(
                currentScreen = GripScreen.START,
                screenState = DynamometerConnectionScreenState.Standby,
                isSignedIn = isSignedIn
            )
        }
        TTS.stopTTS()
        TTS.speechTTS(
            StringProvider.getString(R.string.dynamometer_initial_instruction),
            TextToSpeech.QUEUE_ADD
        )
    }

    private fun observeBluetoothState() {
        viewModelScope.launch {
            InGripManager.connectionState.collectLatest { connectionState ->
                intent {
                    when (connectionState) {
                        is InGripManager.BluetoothConnectionState.CONNECTED -> {
                            reduce {
                                state.copy(
                                    screenState = DynamometerConnectionScreenState.AwaitingStart,
                                    isConnecting = false,
                                    isBatteryFetching = true
                                )
                            }
                            TTS.speechTTS(
                                StringProvider.getString(R.string.dynamometer_connected_tts),
                                TextToSpeech.QUEUE_ADD
                            )
                            delay(1000)
                            InGripManager.sendStatusCommand()
                            reduce { state.copy(isBatteryFetching = false) }
                        }
                        is InGripManager.BluetoothConnectionState.DISCONNECTED -> {
                            if (state.screenState != DynamometerConnectionScreenState.Standby) {
                                reduce {
                                    state.copy(
                                        screenState = DynamometerConnectionScreenState.DeviceSelection,
                                        isConnecting = false
                                    )
                                }
                                TTS.speechTTS(
                                    StringProvider.getString(R.string.dynamometer_disconnected_tts),
                                    TextToSpeech.QUEUE_ADD
                                )
                                startBluetoothScan()
                            }
                        }
                        is InGripManager.BluetoothConnectionState.ERROR -> {
                            reduce {
                                state.copy(
                                    screenState = DynamometerConnectionScreenState.ConnectionError,
                                    isConnecting = false
                                )
                            }
                            if (state.screenState != DynamometerConnectionScreenState.Standby) {
                                TTS.speechTTS(
                                    StringProvider.getString(R.string.dynamometer_connection_error_tts),
                                    TextToSpeech.QUEUE_ADD
                                )
                            }
                        }
                        is InGripManager.BluetoothConnectionState.CONNECTING -> {
                            reduce {
                                state.copy(
                                    screenState = DynamometerConnectionScreenState.Connecting,
                                    isConnecting = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeDevices() {
        viewModelScope.launch {
            InGripManager.availableDevices.collectLatest { devices ->
                intent {
                    reduce {
                        state.copy(
                            availableDevices = devices.map { device ->
                                DeviceUi(
                                    name = device.name ?: StringProvider.getString(R.string.dynamometer_unknown_device_name),
                                    address = device.address
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun observeBattery() {
        viewModelScope.launch {
            InGripManager.dataReceived.collectLatest { data ->
                intent {
                    val battery = data?.toInt()
                    reduce { state.copy(batteryPercent = battery) }
                }
            }
        }
    }

    private fun observeDynamometerData() {
        viewModelScope.launch {
            InGripManager.dataReceived.collectLatest { data ->
                intent {
                    val value = data ?: return@intent
                    when (state.testState) {
                        GripStrengthInspectionState.RightHandCompleted -> {
                            reduce { state.copy(rightGripValue = value) }
                        }
                        GripStrengthInspectionState.LeftHandCompleted -> {
                            reduce { state.copy(leftGripValue = value) }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun startBluetoothScan() {
        val context = getApplication<Application>()
        val hasPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (hasPermission) {
            InGripManager.startScan()
        }
    }

    fun handleEvent(event: GripStrengthEvent) = intent {
        when (event) {
            // Start 화면
            GripStrengthEvent.StartScan -> {
                reduce {
                    state.copy(
                        screenState = DynamometerConnectionScreenState.DeviceSelection,
                        isConnecting = true
                    )
                }
                startBluetoothScan()

                viewModelScope.launch {
                    delay(10_000)
                    intent {
                        if (state.screenState == DynamometerConnectionScreenState.DeviceSelection) {
                            reduce {
                                state.copy(
                                    screenState = DynamometerConnectionScreenState.ConnectionError,
                                    isConnecting = false
                                )
                            }
                            TTS.speechTTS(
                                StringProvider.getString(R.string.dynamometer_connection_error_tts),
                                TextToSpeech.QUEUE_ADD
                            )
                        }
                    }
                }
            }
            is GripStrengthEvent.SelectDevice -> {
                val targetDevice = InGripManager.availableDevices.value.firstOrNull {
                    it.address == event.device.address
                }
                if (targetDevice != null) {
                    InGripManager.connect(targetDevice)
                    reduce {
                        state.copy(screenState = DynamometerConnectionScreenState.Connecting)
                    }
                }
            }
            GripStrengthEvent.RetryScan -> {
                startBluetoothScan()
                reduce { state.copy(isConnecting = true) }
            }
            GripStrengthEvent.StartTest -> {
                reduce { state.copy(currentScreen = GripScreen.INSTRUCTIONS) }
                startInstructions()
            }
            GripStrengthEvent.Back -> {
                TTS.tts.stop()
                // TODO: Back navigation 처리
            }

            // Instructions 화면
            GripStrengthEvent.ProceedToTest -> {
                if (!state.ttsSpeaking) {
                    reduce {
                        state.copy(
                            currentScreen = GripScreen.IN_PROGRESS,
                            testState = GripStrengthInspectionState.RightHandReady,
                            rightGripValue = 0.0,
                            leftGripValue = 0.0
                        )
                    }
                    TTS.stopTTS()
                    TTS.speechTTS(
                        StringProvider.getString(R.string.grip_strength_ready_instruction_button_tts_right),
                        TextToSpeech.QUEUE_ADD
                    )
                }
            }

            // InProgress 화면
            GripStrengthEvent.StartPressed -> {
                TTS.stopTTS()
                InGripManager.sendInitializeCommand()

                if (state.testState == GripStrengthInspectionState.RightHandReady) {
                    TTS.speechTTS(
                        StringProvider.getString(R.string.grip_strength_right_hand_instruction_tts),
                        TextToSpeech.QUEUE_ADD
                    )
                    reduce { state.copy(testState = GripStrengthInspectionState.RightHand) }
                } else if (state.testState == GripStrengthInspectionState.LeftHandReady) {
                    TTS.speechTTS(
                        StringProvider.getString(R.string.grip_strength_left_hand_instruction_tts),
                        TextToSpeech.QUEUE_ADD
                    )
                    reduce { state.copy(testState = GripStrengthInspectionState.LeftHand) }
                }
                startGripMeasurementTimer()
            }

            // Error 화면
            GripStrengthEvent.Retry -> {
                reduce {
                    state.copy(
                        currentScreen = GripScreen.INSTRUCTIONS,
                        testState = GripStrengthInspectionState.RightHandReady,
                        rightGripValue = 0.0,
                        leftGripValue = 0.0
                    )
                }
                startInstructions()
            }
            GripStrengthEvent.Return -> {
                // TODO: Return navigation 처리
            }
            GripStrengthEvent.Logout -> {
                // TODO: Logout 처리
            }
        }
    }

    private fun startInstructions() = intent {
        TTS.stopTTS()
        TTS.forceKoreanLanguage()

        viewModelScope.launch {
            suspend fun trySpeak() {
                if (TTS.isInitialized()) {
                    TTS.speechTTS(
                        StringProvider.getString(R.string.tts_ingrip_instructions),
                        TextToSpeech.QUEUE_ADD
                    )
                    intent {
                        reduce { state.copy(ttsSpeaking = true) }
                    }
                    TTS.setOnDoneListener {
                        viewModelScope.launch {
                            intent {
                                reduce { state.copy(ttsSpeaking = false) }
                            }
                        }
                    }
                } else {
                    delay(500)
                    if (TTS.isInitialized()) {
                        TTS.forceKoreanLanguage()
                        TTS.speechTTS(
                            StringProvider.getString(R.string.tts_ingrip_instructions),
                            TextToSpeech.QUEUE_ADD
                        )
                        intent {
                            reduce { state.copy(ttsSpeaking = true) }
                        }
                        TTS.setOnDoneListener {
                            viewModelScope.launch {
                                intent {
                                    reduce { state.copy(ttsSpeaking = false) }
                                }
                            }
                        }
                    }
                }
            }
            trySpeak()
        }
    }

    private fun startGripMeasurementTimer() {
        gripMeasurementTimer?.cancel()
        val timer = object : CountDownTimer(10_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                val count = (millisUntilFinished / 1_000).toInt() + 1
                viewModelScope.launch {
                    intent {
                        reduce { state.copy(countdown = count) }
                    }
                }
            }

            override fun onFinish() {
                viewModelScope.launch {
                    InGripManager.sendResultCommand()
                    intent {
                        val newState = if (state.testState == GripStrengthInspectionState.RightHand) {
                            GripStrengthInspectionState.RightHandCompleted
                        } else {
                            GripStrengthInspectionState.LeftHandCompleted
                        }
                        reduce { state.copy(testState = newState) }
                        handleTestStateChange()
                    }
                }
            }
        }
        gripMeasurementTimer = timer
        timer.start()
    }

    private fun handleTestStateChange() = intent {
        when (state.testState) {
            GripStrengthInspectionState.RightHandCompleted -> {
                TTS.speechTTS(
                    StringProvider.getString(R.string.grip_strength_right_hand_completed_tts),
                    TextToSpeech.QUEUE_ADD
                )
                viewModelScope.launch {
                    delay(5_000)
                    intent {
                        reduce { state.copy(testState = GripStrengthInspectionState.LeftHandReady) }
                        TTS.speechTTS(
                            StringProvider.getString(R.string.grip_strength_ready_instruction_button_tts_left),
                            TextToSpeech.QUEUE_ADD
                        )
                    }
                }
            }
            GripStrengthInspectionState.LeftHandCompleted -> {
                TTS.speechTTS(
                    StringProvider.getString(R.string.grip_strength_left_hand_completed_tts),
                    TextToSpeech.QUEUE_ADD
                )
                viewModelScope.launch {
                    delay(5_000)
                    checkTestResults()
                }
            }
            else -> Unit
        }
    }

    private fun checkTestResults() = intent {
        if (state.rightGripValue < testFailureThreshold || state.leftGripValue < testFailureThreshold) {
            TTS.speechTTS(
                StringProvider.getString(R.string.ingrip_measurement_failed_tts),
                TextToSpeech.QUEUE_ADD
            )
            reduce { state.copy(currentScreen = GripScreen.ERROR) }
        } else {
            delay(2_000)
            navigateToResult()
        }
    }

    private fun navigateToResult() = intent {
        val result = GripStrengthInspectionResultContract(
            rightGrip = state.rightGripValue,
            leftGrip = state.leftGripValue
        )
        // TODO: 결과 전달 방법 구현 필요
    }

    override fun onCleared() {
        super.onCleared()
        gripMeasurementTimer?.cancel()
    }
}
