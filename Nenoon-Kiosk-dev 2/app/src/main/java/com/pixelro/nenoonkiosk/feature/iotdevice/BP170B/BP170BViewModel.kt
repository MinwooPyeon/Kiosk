package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class BP170BViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application),
    ContainerHost<State, SideEffect> {

    override val container =
        container<State, SideEffect>(State())

    init {
        BP170BManager.init(application)

        viewModelScope.launch {
            launch {
                BP170BManager.connectionState.collect { newState ->
                    intent { reduce { state.copy(connectionState = newState) } }
                }
            }
            launch {
                BP170BManager.dataReceived.collect { data ->
                    intent { reduce { state.copy(dataReceived = data) } }
                }
            }
            launch {
                BP170BManager.availableDevices.collect { devices ->
                    intent { reduce { state.copy(availableDevices = devices) } }
                }
            }
            launch {
                BP170BManager.isInitialized.collect { initialized ->
                    intent { reduce { state.copy(isInitialized = initialized) } }
                }
            }
            launch {
                BP170BManager.bloodPressureResult.collect { result ->
                    intent { reduce { state.copy(bloodPressureResult = result) } }
                }
            }
        }
    }

    fun onEvent(event: Event) = intent {
        when (event) {
            Event.StartScan -> {
                reduce { state.copy(screenState = ScreenState.Scanning) }
                BP170BManager.startScan()
            }

            is Event.SelectDevice -> {
                reduce { state.copy(screenState = ScreenState.Connecting) }
                BP170BManager.connect(event.device)
            }

            Event.Retry -> {
                reduce { state.copy(screenState = ScreenState.Scanning) }
                BP170BManager.startScan()
            }

            Event.Disconnect -> {
                BP170BManager.disconnect()
                reduce { state.copy(screenState = ScreenState.Standby) }
                postSideEffect(SideEffect.ShowMessage("장치 연결을 해제합니다"))
            }

            Event.SendDeviceStatusCheck -> {
                BP170BManager.sendDeviceStatusCheckCommand()
            }

            Event.SendErrorCodeCheck -> {
                BP170BManager.sendErrorCodeCheckCommand()
            }

            is Event.SendTimeSetup -> {
                with(event) {
                    BP170BManager.sendTimeSetupCommand(year, month, day, hour, minute, second)
                }
            }

            Event.SendSerialNumberRequest -> {
                BP170BManager.sendSerialNumberRequestCommand()
            }
        }
    }
}
