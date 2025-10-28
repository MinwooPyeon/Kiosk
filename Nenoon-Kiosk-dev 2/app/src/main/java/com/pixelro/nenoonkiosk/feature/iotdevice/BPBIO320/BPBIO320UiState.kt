package com.pixelro.nenoonkiosk.feature.iotdevice.BPBIO320

import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult

data class BPBIO320UiState(
    val connectionState: Int = 0,
    val deviceName: String = "",
    val bloodPressureResult: BloodPressureTestResult? = null,
    val errorMessage: String? = null,
    val testInProgress: Boolean = false,
    val isLastResultComplete: Boolean = false,
    val batteryLevel: Int? = null,
)