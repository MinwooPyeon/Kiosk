package com.pixelro.nenoonkiosk.feature.auth.locationlogin

data class LocationLoginState(
    val locationStatus: String = "",
    val isCheckingLocation: Boolean = false,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null,
    val isLocationPermissionGranted: Boolean = false,
    val isLocationEnabled: Boolean = false
)

sealed interface LocationLoginSideEffect {
    data class ShowToast(val message: String) : LocationLoginSideEffect
    data object LoginSuccess : LocationLoginSideEffect
    data object LoginFailed : LocationLoginSideEffect
    data object RequestLocationPermission : LocationLoginSideEffect
    data object RequestEnableLocation : LocationLoginSideEffect
    data object NavigateBack : LocationLoginSideEffect
}
