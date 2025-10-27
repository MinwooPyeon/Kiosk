package com.pixelro.nenoonkiosk.feature.auth.faceenrollment

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.recognizer.FaceRecognizer
import com.pixelro.nenoonkiosk.core.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FaceEnrollmentViewModel @Inject constructor(
    application: Application,
    private val faceRecognizer: FaceRecognizer
) : ViewModel(), ContainerHost<FaceEnrollmentState, FaceEnrollmentSideEffect> {

    override val container: Container<FaceEnrollmentState, FaceEnrollmentSideEffect> =
        container(
            FaceEnrollmentState(
                faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_detection_status_looking)
            )
        )

    private var tempFaceEmbedding: FloatArray? = null

    init {
        faceRecognizer.initialize(application)
    }

    fun resetFaceEnrollmentData() = intent {
        reduce {
            state.copy(
                faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_detection_status_looking),
                isProcessingFace = false,
                lastDetectedFaceBitmap = null,
                isFaceEnrollmentDataReady = false
            )
        }
        tempFaceEmbedding = null
    }

    fun updateFaceDetectionStatus(status: String) = intent {
        reduce { state.copy(faceDetectionStatus = status) }
    }

    fun processFaceForEmbedding(faceBitmap: Bitmap) = intent {
        if (state.isProcessingFace) {
            faceBitmap.recycle()
            return@intent
        }

        reduce {
            state.copy(
                isProcessingFace = true,
                faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_processing)
            )
        }

        try {
            val embedding = withContext(Dispatchers.Default) {
                faceRecognizer.getFaceEmbedding(faceBitmap)
            }

            if (embedding != null && !faceBitmap.isRecycled) {
                tempFaceEmbedding = embedding

                // 기존 비트맵 recycle
                val oldBitmap = state.lastDetectedFaceBitmap
                oldBitmap?.recycle()

                reduce {
                    state.copy(
                        lastDetectedFaceBitmap = faceBitmap.config?.let {
                            faceBitmap.copy(it, true)
                        },
                        isFaceEnrollmentDataReady = true
                    )
                }
                Log.d("FaceEnrollmentVM", "Face embedding stored")
            } else {
                // 기존 비트맵 recycle
                val oldBitmap = state.lastDetectedFaceBitmap
                oldBitmap?.recycle()

                reduce {
                    state.copy(
                        faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_processing),
                        lastDetectedFaceBitmap = null,
                        isFaceEnrollmentDataReady = false
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("FaceEnrollmentVM", "Error processing face: ${e.message}", e)
            reduce {
                state.copy(
                    faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_processing_error),
                    isFaceEnrollmentDataReady = false
                )
            }
        } finally {
            reduce { state.copy(isProcessingFace = false) }
            faceBitmap.recycle()
        }
    }

    fun getTempFaceEmbedding(): FloatArray? = tempFaceEmbedding

    fun navigateBack() = intent {
        postSideEffect(FaceEnrollmentSideEffect.NavigateBack)
    }

    override fun onCleared() {
        super.onCleared()
        container.stateFlow.value.lastDetectedFaceBitmap?.recycle()
    }
}
