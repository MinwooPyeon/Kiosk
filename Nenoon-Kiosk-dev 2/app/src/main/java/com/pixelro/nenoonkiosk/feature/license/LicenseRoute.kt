package com.pixelro.nenoonkiosk.feature.license

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LicenseRoute(
    onLicenseActivated: () -> Unit,
    viewModel: LicenseViewModel = hiltViewModel()
) {
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    // 인증 성공 시 콜백
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            onLicenseActivated()
        }
    }

    LicenseScreen(
        password = password,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onNumberClick = viewModel::onNumberClick,
        onBackspaceClick = viewModel::onBackspaceClick,
        onClearClick = viewModel::onClearClick,
        onAuthenticateClick = viewModel::onAuthenticateClick
    )
}