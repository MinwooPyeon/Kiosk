package com.pixelro.nenoonkiosk.feature.auth.faceupdate

import android.graphics.Bitmap

data class FaceUpdateState(
    val faceDetectionStatus: String = "",
    val isProcessingFace: Boolean = false,
    val lastDetectedFaceBitmap: Bitmap? = null,
    val isFaceEnrollmentDataReady: Boolean = false,
    val enrollmentMessage: String? = null,
    val currentScreenStatus: String = ""
)

sealed interface FaceUpdateSideEffect {
    data class ShowToast(val message: String) : FaceUpdateSideEffect
    data object UpdateSuccess : FaceUpdateSideEffect
    data object UpdateFailed : FaceUpdateSideEffect
    data object NavigateBack : FaceUpdateSideEffect
}
