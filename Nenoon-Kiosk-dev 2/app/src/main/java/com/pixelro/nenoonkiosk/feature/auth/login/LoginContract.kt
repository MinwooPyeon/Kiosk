package com.pixelro.nenoonkiosk.feature.auth.login

data class LoginState(
    val isLoading: Boolean = false
)

sealed interface LoginSideEffect {
    data class ShowToast(val message: String) : LoginSideEffect
}
