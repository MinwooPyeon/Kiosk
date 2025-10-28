package com.pixelro.nenoonkiosk.feature.auth.locationlogin

data class LocationLoginState(
    val id: String = "",
    val password: String = "",
    val loginError: Boolean = false
)

sealed interface LocationLoginSideEffect {
    data object LoginSuccess : LocationLoginSideEffect
}
