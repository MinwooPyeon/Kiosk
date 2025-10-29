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
    ContainerHost<BPBIO320Contract.State, BPBIO320Contract.SideEffect> {

    private val manager = BPBIO320Manager(application)

    override val container =
        container<BPBIO320Contract.State, BPBIO320Contract.SideEffect>(BPBIO320Contract.State())

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
                    BPBIO320Contract.State(
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

    private fun resolveScreenState(connectionState: Int, errorMessage: String?): BPBIO320Contract.ScreenState {
        return when (connectionState) {
            com.inbody.bpbio.IB_SDKConst.CONNECTED -> BPBIO320Contract.ScreenState.AwaitingStart
            com.inbody.bpbio.IB_SDKConst.CONNECTING -> BPBIO320Contract.ScreenState.Connecting
            com.inbody.bpbio.IB_SDKConst.DISCONNECTED,
            com.inbody.bpbio.IB_SDKConst.IDLE -> BPBIO320Contract.ScreenState.SearchingOrIdle
            else -> if (!errorMessage.isNullOrBlank()) BPBIO320Contract.ScreenState.ConnectionError
            else BPBIO320Contract.ScreenState.Standby
        }
    }

    fun onEvent(event: BPBIO320Contract.Event) = intent {
        when (event) {
            BPBIO320Contract.Event.InitializeBluetooth -> manager.initBluetoothSDK()
            BPBIO320Contract.Event.Start -> {
                manager.removeDevice()
                manager.selectDevice()
                manager.connectDisconnect()
                reduce { state.copy(screenState = BPBIO320Contract.ScreenState.Connecting) }
            }
            BPBIO320Contract.Event.Retry -> {
                manager.removeDevice()
                manager.selectDevice()
                manager.connectDisconnect()
                reduce { state.copy(screenState = BPBIO320Contract.ScreenState.Connecting) }
            }
            BPBIO320Contract.Event.Disconnect -> {
                manager.connectDisconnect()
                reduce { state.copy(screenState = BPBIO320Contract.ScreenState.Standby) }
                postSideEffect(BPBIO320Contract.SideEffect.ShowMessage("장치 연결을 해제합니다."))
            }
            BPBIO320Contract.Event.RemoveDevice -> manager.removeDevice()
            BPBIO320Contract.Event.SelectDevice -> manager.selectDevice()
            BPBIO320Contract.Event.ConnectOrDisconnect -> manager.connectDisconnect()
        }
    }
}
