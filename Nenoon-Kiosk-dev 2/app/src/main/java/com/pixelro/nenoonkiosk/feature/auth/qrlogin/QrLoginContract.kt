package com.pixelro.nenoonkiosk.feature.auth.qrlogin

data class QrLoginState(
    val qrScanStatus: String = "",
    val isProcessingQr: Boolean = false
)

sealed interface QrLoginSideEffect {
    data class ShowToast(val message: String) : QrLoginSideEffect
    data object LoginSuccess : QrLoginSideEffect
}
