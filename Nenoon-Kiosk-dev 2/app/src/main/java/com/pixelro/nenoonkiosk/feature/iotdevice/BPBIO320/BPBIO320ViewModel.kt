package com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.core.manager.BPBIO320Manager
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class BPBIO320ViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application),
    ContainerHost<State, SideEffect> {

    private val manager = BPBIO320Manager(application)

    override val container =
        container<State, SideEffect>(State())

    init {
        initializeBluetooth()
        observeManagerFlows()
    }

    private fun initializeBluetooth() = intent {
        manager.initBluetoothSDK()
    }

    private fun observeManagerFlows() {
        viewModelScope.launch {
            launch {
                combine(
                    *arrayOf(
                        manager.connectionState,
                        manager.deviceName,
                        manager.bloodPressureResult,
                        manager.errorMessage,
                        manager.testInProgress,
                        manager.isLastResultComplete,
                        manager.batteryLevel
                    )
                ) { values: Array<Any?> ->
                    State(
                        connectionState = values[0] as Int,
                        deviceName = values[1] as String,
                        bloodPressureResult = values[2] as? BloodPressureTestResult,
                        errorMessage = values[3] as? String,
                        testInProgress = values[4] as Boolean,
                        isLastResultComplete = values[5] as Boolean,
                        batteryLevel = values[6] as? Int,
                        screenState = resolveScreenState(values[0] as Int, values[3] as? String)
                    )
                }.onEach { newState ->
                    intent { reduce { newState } }
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun resolveScreenState(connectionState: Int, errorMessage: String?): ScreenState {
        return when (connectionState) {
            com.inbody.bpbio.IB_SDKConst.CONNECTED -> ScreenState.AwaitingStart
            com.inbody.bpbio.IB_SDKConst.CONNECTING -> ScreenState.Connecting
            com.inbody.bpbio.IB_SDKConst.DISCONNECTED,
            com.inbody.bpbio.IB_SDKConst.IDLE -> ScreenState.SearchingOrIdle
            else -> if (!errorMessage.isNullOrBlank()) ScreenState.ConnectionError
            else ScreenState.Standby
        }
    }

    fun onEvent(event: Event) = intent {
        when (event) {
            Event.InitializeBluetooth -> manager.initBluetoothSDK()
            Event.Start -> {
                manager.removeDevice()
                manager.selectDevice()
                manager.connectDisconnect()
                reduce { state.copy(screenState = ScreenState.Connecting) }
            }
            Event.Retry -> {
                manager.removeDevice()
                manager.selectDevice()
                manager.connectDisconnect()
                reduce { state.copy(screenState = ScreenState.Connecting) }
            }
            Event.Disconnect -> {
                manager.connectDisconnect()
                reduce { state.copy(screenState = ScreenState.Standby) }
                postSideEffect(SideEffect.ShowMessage("장치 연결을 해제합니다."))
            }
            Event.RemoveDevice -> manager.removeDevice()
            Event.SelectDevice -> manager.selectDevice()
            Event.ConnectOrDisconnect -> manager.connectDisconnect()
        }
    }
}
