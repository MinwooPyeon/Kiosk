package com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.core.manager.BPBIO320Manager
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BPBIO320ViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val manager = BPBIO320Manager(application)

    // 기존 각각의 Flow
    private val connectionState = manager.connectionState
    private val deviceName = manager.deviceName
    private val bloodPressureResult = manager.bloodPressureResult
    private val errorMessage = manager.errorMessage
    private val testInProgress = manager.testInProgress
    private val isLastResultComplete = manager.isLastResultComplete
    private val batteryLevel = manager.batteryLevel

    val uiState: StateFlow<BPBIO320UiState> =
        combine(
            *arrayOf(
                connectionState,
                deviceName,
                bloodPressureResult,
                errorMessage,
                testInProgress,
                isLastResultComplete,
                batteryLevel
            )
        ) { values ->
            BPBIO320UiState(
                connectionState = values[0] as Int,
                deviceName = values[1] as String,
                bloodPressureResult = values[2] as? BloodPressureTestResult,
                errorMessage = values[3] as? String,
                testInProgress = values[4] as Boolean,
                isLastResultComplete = values[5] as Boolean,
                batteryLevel = values[6] as? Int
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            BPBIO320UiState()
        )

    init {
        initializeBluetoothSDK()
    }

    fun initializeBluetoothSDK() {
        viewModelScope.launch { manager.initBluetoothSDK() }
    }

    fun selectDevice() {
        viewModelScope.launch { manager.selectDevice() }
    }

    fun connectDisconnect() {
        viewModelScope.launch { manager.connectDisconnect() }
    }

    fun removeDevice() {
        viewModelScope.launch { manager.removeDevice() }
    }

    fun resetTest() {
        viewModelScope.launch { manager.resetTest() }
    }

    fun removeData() {
        viewModelScope.launch { manager.removeData() }
    }

    fun startMeasurement() {
        viewModelScope.launch { manager.startMeasurement() }
    }

    fun setSync() {
        viewModelScope.launch { manager.setSync() }
    }

    fun setTime() {
        viewModelScope.launch { manager.setTime() }
    }

    fun getDeviceInfo() {
        viewModelScope.launch { manager.getDeviceInfo() }
    }
}
