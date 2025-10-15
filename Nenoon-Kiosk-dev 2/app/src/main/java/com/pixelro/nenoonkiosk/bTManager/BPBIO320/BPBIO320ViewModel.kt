package com.pixelro.nenoonkiosk.bTManager.BPBIO320

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.test.bloodPressure.BloodPressureTestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BPBIO320ViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val manager = BPBIO320Manager(application)

    val connectionState: StateFlow<Int> = manager.connectionState
    val deviceName: StateFlow<String> = manager.deviceName
    val bloodPressureResult: StateFlow<BloodPressureTestResult?> = manager.bloodPressureResult
    val errorMessage: StateFlow<String?> = manager.errorMessage
    val testInProgress: StateFlow<Boolean> = manager.testInProgress
    val isLastResultComplete: StateFlow<Boolean> = manager.isLastResultComplete
    val batteryLevel: StateFlow<Int?> = manager.batteryLevel

    init {
        initializeBluetoothSDK()
    }

    fun initializeBluetoothSDK() {
        viewModelScope.launch {
            manager.initBluetoothSDK()
        }
    }

    fun selectDevice() {
        viewModelScope.launch {
            manager.selectDevice()
        }
    }

    fun connectDisconnect() {
        viewModelScope.launch {
            manager.connectDisconnect()
        }
    }

    fun removeDevice() {
        viewModelScope.launch {
            manager.removeDevice()
        }
    }

    fun resetTest() {
        viewModelScope.launch {
            manager.resetTest()
        }
    }

    fun removeData() { // optional
        viewModelScope.launch {
            manager.removeData()
        }
    }

    fun startMeasurement() {
        viewModelScope.launch {
            manager.startMeasurement()
        }
    }

    fun setSync() { // optional
        viewModelScope.launch {
            manager.setSync()
        }
    }

    fun setTime() { // optional
        viewModelScope.launch {
            manager.setTime()
        }
    }

    fun getDeviceInfo() { // optional
        viewModelScope.launch {
            manager.getDeviceInfo()
        }
    }
}