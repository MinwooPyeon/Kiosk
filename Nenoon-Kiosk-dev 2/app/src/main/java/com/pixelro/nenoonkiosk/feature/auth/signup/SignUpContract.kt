package com.pixelro.nenoonkiosk.feature.auth.signup

import android.graphics.Bitmap

data class SignUpState(
    val id: String = "",
    val password: String = "",
    val name: String = "",
    val email: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val emailError: String? = null,
    val isSigningUp: Boolean = false,
    val signupSuccess: Boolean = false,
    val errorMessage: String? = null,
    val generatedQrBitmap: Bitmap? = null,
    val isFaceEnrollmentReady: Boolean = false
)

sealed interface SignUpSideEffect {
    data class ShowToast(val message: String) : SignUpSideEffect
    data object SignUpSuccess : SignUpSideEffect
    data object SignUpFailed : SignUpSideEffect
    data object NavigateToFaceEnrollment : SignUpSideEffect
    data object NavigateBack : SignUpSideEffect
}
