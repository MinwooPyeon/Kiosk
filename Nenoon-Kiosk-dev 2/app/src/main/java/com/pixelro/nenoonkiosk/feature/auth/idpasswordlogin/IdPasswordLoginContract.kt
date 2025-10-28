package com.pixelro.nenoonkiosk.feature.auth.idpasswordlogin

data class IdPasswordLoginState(
    val userId: String = "",
    val userPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginEnabled: Boolean = false,
    val passwordVisible: Boolean = false
)

sealed interface IdPasswordLoginSideEffect {
    data class ShowToast(val message: String) : IdPasswordLoginSideEffect
    data object LoginSuccess : IdPasswordLoginSideEffect
}
