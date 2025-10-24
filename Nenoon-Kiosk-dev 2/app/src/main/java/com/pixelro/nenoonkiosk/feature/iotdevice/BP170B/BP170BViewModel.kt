package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Import the BloodPressureTestResult data class if it's in a different package
// For this setup, we assume BloodPressureTestResult is defined within the BP170BManager's package
// or is a globally accessible data class.

@HiltViewModel
class BP170BViewModel
    @Inject
    constructor(application: Application) : AndroidViewModel(application) {
        init {
            BP170BManager.init(application)
        }

        val connectionState: StateFlow<BP170BManager.BluetoothConnectionState> =
            BP170BManager.connectionState
        val dataReceived: StateFlow<String?> =
            BP170BManager.dataReceived
        val availableDevices: StateFlow<List<BluetoothDevice>> = BP170BManager.availableDevices
        val isInitialized: StateFlow<Boolean> = BP170BManager.isInitialized

        // Add this StateFlow to expose the parsed blood pressure result
        val bloodPressureResult: StateFlow<BloodPressureTestResult?> = BP170BManager.bloodPressureResult

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
