package com.pixelro.nenoonkiosk.feature.iotdevice.BP170B

import android.bluetooth.BluetoothDevice
import com.pixelro.nenoonkiosk.core.manager.BP170BManager
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult

data class BP170BUiState(
    val connectionState: BP170BManager.BluetoothConnectionState = BP170BManager.BluetoothConnectionState.DISCONNECTED,
    val dataReceived: String? = null,
    val availableDevices: List<BluetoothDevice> = emptyList(),
    val isInitialized: Boolean = false,
    val bloodPressureResult: BloodPressureTestResult? = null,
)
