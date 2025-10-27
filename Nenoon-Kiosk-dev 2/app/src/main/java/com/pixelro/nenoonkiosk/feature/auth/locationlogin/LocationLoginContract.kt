package com.pixelro.nenoonkiosk.feature.auth.locationlogin

data class LocationLoginState(
    val id: String = "",
    val password: String = "",
    val loginError: Boolean = false,
    val isLoggingIn: Boolean = false
)

sealed interface LocationLoginSideEffect {
    data class ShowToast(val message: String) : LocationLoginSideEffect
    data object LoginSuccess : LocationLoginSideEffect
    data object NavigateToUserSignIn : LocationLoginSideEffect
    data object NavigateToSettings : LocationLoginSideEffect
}
