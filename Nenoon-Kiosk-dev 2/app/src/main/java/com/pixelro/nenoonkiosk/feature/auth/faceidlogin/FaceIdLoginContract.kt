package com.pixelro.nenoonkiosk.feature.auth.faceidlogin

data class FaceIdLoginState(
    val faceDetectionStatus: String = "",
    val isProcessingFace: Boolean = false,
    val attemptsLeft: Int = 3,
    val liveFaceDetectionStatus: String = ""
)

sealed interface FaceIdLoginSideEffect {
    data class ShowToast(val message: String) : FaceIdLoginSideEffect
    data object LoginSuccess : FaceIdLoginSideEffect
    data object LoginFailed : FaceIdLoginSideEffect
    data object MaxAttemptsReached : FaceIdLoginSideEffect
    data object NavigateBack : FaceIdLoginSideEffect
}