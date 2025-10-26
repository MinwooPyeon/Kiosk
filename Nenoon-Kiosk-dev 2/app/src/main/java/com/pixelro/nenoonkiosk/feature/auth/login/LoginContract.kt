package com.pixelro.nenoonkiosk.feature.auth.login

import android.graphics.Bitmap
import com.harang.data.model.User

// UIState 정의
data class LoginUiState(
    val userId: String? = null,
    val userData: User? = null,
    val isLocationSignedIn: Boolean = false,
    val isUserSignedIn: Boolean = false,
    val faceDetectionStatus: String = "",
    val isProcessingFace: Boolean = false,
    val lastDetectedFaceBitmap: Bitmap? = null,
    val enrollmentMessage: String? = null,
    val enrollmentSuccess: Boolean = false,
    val isFaceEnrollmentDataReady: Boolean = false,
    val accountQrCode: Bitmap? = null
)

// SideEffect 정의
sealed interface LoginSideEffect {
    data class ShowToast(val message: String) : LoginSideEffect
    data class UpdateSignInStatus(val isSignedIn: Boolean) : LoginSideEffect
    data object PrintQrCode : LoginSideEffect
    data class ValidationError(val error: String) : LoginSideEffect
}