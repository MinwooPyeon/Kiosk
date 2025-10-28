package com.pixelro.nenoonkiosk.feature.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data class Loaded(val appVersion: String) : SplashUiState
}

sealed interface SplashSideEffect {
    data object NavigateToPermission : SplashSideEffect
    data object NavigateToSignIn : SplashSideEffect
}
