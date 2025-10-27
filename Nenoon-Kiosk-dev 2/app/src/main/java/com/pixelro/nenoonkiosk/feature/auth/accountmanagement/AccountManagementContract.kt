package com.pixelro.nenoonkiosk.feature.auth.accountmanagement

import android.graphics.Bitmap
import com.harang.data.model.dto.User

data class AccountManagementState(
    val isUserSignedIn: Boolean = false,
    val userData: User? = null,
    val qrCodeBitmap: Bitmap? = null,
    val showProgressIndicator: Boolean = true,
    val isQrPrintButtonEnabled: Boolean = true,
    val isUserSignInSkipped: Boolean = false
)

sealed interface AccountManagementSideEffect {
    data class ShowToast(val message: String) : AccountManagementSideEffect
    data object NavigateToFaceEnrollment : AccountManagementSideEffect
    data object SignOut : AccountManagementSideEffect
    data object NavigateBack : AccountManagementSideEffect
}
