package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BP170BViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(BP170BUiState())
    val uiState: StateFlow<BP170BUiState> = _uiState.asStateFlow()

    init {
        BP170BManager.init(application)

        // Manager의 상태를 Flow로 수집해 UiState로 통합 업데이트
        viewModelScope.launch {
            launch {
                BP170BManager.connectionState.collect { newState ->
                    _uiState.update { it.copy(connectionState = newState) }
                }
            }
            launch {
                BP170BManager.dataReceived.collect { data ->
                    _uiState.update { it.copy(dataReceived = data) }
                }
            }
            launch {
                BP170BManager.availableDevices.collect { devices ->
                    _uiState.update { it.copy(availableDevices = devices) }
                }
            }
            launch {
                BP170BManager.isInitialized.collect { initialized ->
                    _uiState.update { it.copy(isInitialized = initialized) }
                }
            }
            launch {
                BP170BManager.bloodPressureResult.collect { result ->
                    _uiState.update { it.copy(bloodPressureResult = result) }
                }
            }
        }
    }

    // ---- Bluetooth Actions ----
    fun startScan() {
        viewModelScope.launch {
            BP170BManager.startScan()
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            BP170BManager.connect(device)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            BP170BManager.disconnect()
        }
    }

    // ---- Device Commands ----
    fun sendDeviceStatusCheckCommand() {
        viewModelScope.launch {
            BP170BManager.sendDeviceStatusCheckCommand()
        }
    }

    fun sendErrorCodeCheckCommand() {
        viewModelScope.launch {
            BP170BManager.sendErrorCodeCheckCommand()
        }
    }

    fun sendTimeSetupCommand(
        year: Byte,
        month: Byte,
        day: Byte,
        hour: Byte,
        minute: Byte,
        second: Byte,
    ) {
        viewModelScope.launch {
            BP170BManager.sendTimeSetupCommand(year, month, day, hour, minute, second)
        }
    }

    fun sendSerialNumberRequestCommand() {
        viewModelScope.launch {
            BP170BManager.sendSerialNumberRequestCommand()
        }
    }
}
