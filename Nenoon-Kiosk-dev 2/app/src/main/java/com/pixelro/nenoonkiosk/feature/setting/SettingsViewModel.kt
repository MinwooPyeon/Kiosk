package com.pixelro.nenoonkiosk.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel(), ContainerHost<SettingsUiState, SettingsSideEffect> {

    override val container: Container<SettingsUiState, SettingsSideEffect> =
        container(
            initialState = SettingsUiState(
                currentLanguage = SharedPreferencesManager.getLanguage(),
                currentBloodPressureMonitorType = SharedPreferencesManager.getBloodPressureMonitorType()
            )
        )

    fun updateSignInStatus(isLocationSignedIn: Boolean, isUserSignedIn: Boolean) = intent {
        reduce {
            state.copy(
                isLocationSignedIn = isLocationSignedIn,
                isUserSignedIn = isUserSignedIn
            )
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            navigator.navigateBack()
        }
    }

    fun onLanguageClick() = intent {
        reduce {
            state.copy(showDialog = SettingsUiState.DialogType.Language)
        }
    }

    fun onBloodPressureMonitorClick() = intent {
        reduce {
            state.copy(showDialog = SettingsUiState.DialogType.BloodPressureMonitor)
        }
    }

    fun onLoginClick() = intent {
        if (state.isSignedIn) {
            handleLogout()
        } else {
            viewModelScope.launch {
                navigator.navigate(SignInRoute.UserSignIn)
            }
        }
    }

    fun onLanguageSelected(languageCode: String) = intent {
        SharedPreferencesManager.putLanguage(languageCode)
        reduce {
            state.copy(
                currentLanguage = languageCode,
                showDialog = SettingsUiState.DialogType.None
            )
        }
        postSideEffect(SettingsSideEffect.ShowToast("언어가 변경되었습니다"))
    }

    fun onBloodPressureMonitorSelected(type: String) = intent {
        val monitorType = SharedPreferencesManager.BloodPressureMonitorType.valueOf(type)
        SharedPreferencesManager.putBloodPressureMonitorType(monitorType)
        reduce {
            state.copy(
                currentBloodPressureMonitorType = monitorType,
                showDialog = SettingsUiState.DialogType.None
            )
        }
        postSideEffect(SettingsSideEffect.ShowToast("혈압계가 변경되었습니다"))
    }

    fun onDialogDismiss() = intent {
        reduce {
            state.copy(showDialog = SettingsUiState.DialogType.None)
        }
    }

    private fun handleLogout() = intent {
        SharedPreferencesManager.putString("current_user_id", "")
        reduce {
            state.copy(
                isLocationSignedIn = false,
                isUserSignedIn = false
            )
        }
        postSideEffect(SettingsSideEffect.ShowToast("로그아웃되었습니다"))
    }
}
