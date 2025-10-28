package com.pixelro.nenoonkiosk.feature.auth.faceenrollment

import android.graphics.Bitmap

data class FaceEnrollmentState(
    val faceDetectionStatus: String = "",
    val isProcessingFace: Boolean = false,
    val lastDetectedFaceBitmap: Bitmap? = null,
    val isFaceEnrollmentDataReady: Boolean = false
)

sealed interface FaceEnrollmentSideEffect {
    data class ShowToast(val message: String) : FaceEnrollmentSideEffect
}
