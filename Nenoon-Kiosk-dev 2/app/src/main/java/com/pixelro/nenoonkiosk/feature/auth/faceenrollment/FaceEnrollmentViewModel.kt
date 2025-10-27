package com.pixelro.nenoonkiosk.feature.auth.faceenrollment

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.harang.data.repository.SignInRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
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
    private val faceRecognizer: FaceRecognizer,
    private val signInRepository: SignInRepository
) : ViewModel(), ContainerHost<FaceEnrollmentState, FaceEnrollmentSideEffect> {

    override val container: Container<FaceEnrollmentState, FaceEnrollmentSideEffect> =
        container(
            FaceEnrollmentState(
                faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_detection_status_looking)
            )
        )

    private var tempFaceEmbedding: FloatArray? = null
    private var registeredFaces: MutableMap<String, FloatArray> = mutableMapOf()

    init {
        faceRecognizer.initialize(application)
        loadRegisteredFacesFromPrefs()
    }

    private fun loadRegisteredFacesFromPrefs() {
        registeredFaces = SharedPreferencesManager.getRegisteredFaceEmbeddings().toMutableMap()
        Log.d("FaceEnrollmentVM", "Loaded ${registeredFaces.size} registered faces")
    }

    fun resetFaceEnrollmentData() = intent {
        state.lastDetectedFaceBitmap?.recycle()
        tempFaceEmbedding = null

        reduce {
            state.copy(
                faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_detection_status_looking),
                isProcessingFace = false,
                lastDetectedFaceBitmap = null,
                isFaceEnrollmentDataReady = false
            )
        }
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

        runCatching {
            withContext(Dispatchers.Default) {
                faceRecognizer.getFaceEmbedding(faceBitmap)
            }
        }.onSuccess { embedding ->
            if (embedding != null && !faceBitmap.isRecycled) {
                tempFaceEmbedding = embedding

                val oldBitmap = state.lastDetectedFaceBitmap
                oldBitmap?.recycle()

                val copiedBitmap = faceBitmap.config?.let { config ->
                    faceBitmap.copy(config, true)
                }

                reduce {
                    state.copy(
                        lastDetectedFaceBitmap = copiedBitmap,
                        isFaceEnrollmentDataReady = true,
                        isProcessingFace = false,
                        faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_enrollment_ready)
                    )
                }
                Log.d("FaceEnrollmentVM", "Face embedding stored successfully")
            } else {
                state.lastDetectedFaceBitmap?.recycle()

                reduce {
                    state.copy(
                        faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_extraction_failed_retry),
                        lastDetectedFaceBitmap = null,
                        isFaceEnrollmentDataReady = false,
                        isProcessingFace = false
                    )
                }
            }
            faceBitmap.recycle()
        }.onFailure { e ->
            Log.e("FaceEnrollmentVM", "Error processing face: ${e.message}", e)

            state.lastDetectedFaceBitmap?.recycle()

            reduce {
                state.copy(
                    faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_processing_error),
                    isFaceEnrollmentDataReady = false,
                    isProcessingFace = false,
                    lastDetectedFaceBitmap = null
                )
            }
            faceBitmap.recycle()
            postSideEffect(FaceEnrollmentSideEffect.ShowToast("얼굴 처리 중 오류가 발생했습니다"))
        }
    }

    fun enrollFace(userId: String, accessToken: String? = null) = intent {
        if (tempFaceEmbedding == null) {
            postSideEffect(FaceEnrollmentSideEffect.ShowToast("등록할 얼굴 정보가 없습니다"))
            postSideEffect(FaceEnrollmentSideEffect.EnrollmentFailed)
            return@intent
        }

        runCatching {
            if (AppConstants.MANAGE_USERS_INTERNALLY) {
                registeredFaces[userId] = tempFaceEmbedding!!
                SharedPreferencesManager.putRegisteredFaceEmbeddings(registeredFaces)
                true
            } else {
                if (accessToken != null) {
                    signInRepository.userUpdateFace(accessToken, tempFaceEmbedding.contentToString())
                    true
                } else {
                    false
                }
            }
        }.onSuccess { success ->
            if (success) {
                tempFaceEmbedding = null
                postSideEffect(FaceEnrollmentSideEffect.ShowToast("얼굴 등록이 완료되었습니다"))
                postSideEffect(FaceEnrollmentSideEffect.EnrollmentSuccess)
            } else {
                postSideEffect(FaceEnrollmentSideEffect.ShowToast("얼굴 등록에 실패했습니다"))
                postSideEffect(FaceEnrollmentSideEffect.EnrollmentFailed)
            }
        }.onFailure { e ->
            Log.e("FaceEnrollmentVM", "Error enrolling face: ${e.message}", e)
            postSideEffect(FaceEnrollmentSideEffect.ShowToast("얼굴 등록 중 오류가 발생했습니다"))
            postSideEffect(FaceEnrollmentSideEffect.EnrollmentFailed)
        }
    }

    fun navigateBack() = intent {
        postSideEffect(FaceEnrollmentSideEffect.NavigateBack)
    }

    override fun onCleared() {
        super.onCleared()
        container.stateFlow.value.lastDetectedFaceBitmap?.recycle()
    }
}
