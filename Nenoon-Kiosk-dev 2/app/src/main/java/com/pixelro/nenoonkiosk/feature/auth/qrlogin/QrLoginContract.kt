package com.pixelro.nenoonkiosk.feature.auth.qrlogin

data class QrLoginState(
    val qrScanStatus: String = "",
    val isProcessingQr: Boolean = false,
    val isCameraPermissionGranted: Boolean = false,
    val scannedData: String? = null
)

sealed interface QrLoginSideEffect {
    data class ShowToast(val message: String) : QrLoginSideEffect
    data object LoginSuccess : QrLoginSideEffect
    data object LoginFailed : QrLoginSideEffect
    data object RequestCameraPermission : QrLoginSideEffect
    data object NavigateBack : QrLoginSideEffect
}
