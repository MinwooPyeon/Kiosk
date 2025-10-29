package com.pixelro.nenoonkiosk.feature.setting

import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager

data class SettingsUiState(
    val isLocationSignedIn: Boolean = false,
    val isUserSignedIn: Boolean = false,
    val currentLanguage: String = "ko",
    val currentBloodPressureMonitorType: SharedPreferencesManager.BloodPressureMonitorType = SharedPreferencesManager.BloodPressureMonitorType.BPBIO320,
    val showDialog: DialogType = DialogType.None
) {
    val isSignedIn: Boolean
        get() = isLocationSignedIn && isUserSignedIn

    sealed interface DialogType {
        data object None : DialogType
        data object Language : DialogType
        data object BloodPressureMonitor : DialogType
    }
}

sealed interface SettingsSideEffect {
    data class ShowToast(val message: String) : SettingsSideEffect
}
