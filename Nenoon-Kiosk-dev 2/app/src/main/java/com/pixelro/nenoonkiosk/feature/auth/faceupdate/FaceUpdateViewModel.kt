package com.pixelro.nenoonkiosk.feature.auth.faceupdate

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.harang.data.model.dto.User
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
class FaceUpdateViewModel @Inject constructor(
    application: Application,
    private val faceRecognizer: FaceRecognizer,
    private val signInRepository: SignInRepository
) : ViewModel(), ContainerHost<FaceUpdateState, FaceUpdateSideEffect> {

    override val container: Container<FaceUpdateState, FaceUpdateSideEffect> =
        container(
            FaceUpdateState(
                faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_detection_status_looking),
                currentScreenStatus = StringProvider.getString(R.string.user_face_update_initial_status)
            )
        )

    private var tempFaceEmbedding: FloatArray? = null
    private var registeredFaces: MutableMap<String, FloatArray> = mutableMapOf()

    init {
        faceRecognizer.initialize(application)
        loadRegisteredFaces()
    }

    private fun loadRegisteredFaces() {
        registeredFaces = SharedPreferencesManager.getRegisteredFaceEmbeddings().toMutableMap()
        Log.d("FaceUpdateVM", "Loaded ${registeredFaces.size} registered faces")
    }

    fun resetFaceData() = intent {
        state.lastDetectedFaceBitmap?.recycle()
        tempFaceEmbedding = null

        reduce {
            state.copy(
                faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_detection_status_looking),
                isProcessingFace = false,
                lastDetectedFaceBitmap = null,
                isFaceEnrollmentDataReady = false,
                enrollmentMessage = null,
                currentScreenStatus = StringProvider.getString(R.string.user_face_update_initial_status)
            )
        }
    }

    fun updateFaceDetectionStatus(status: String) = intent {
        reduce { state.copy(faceDetectionStatus = status) }
        updateScreenStatus()
    }

    fun captureFace(faceBitmap: Bitmap) = intent {
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
                Log.d("FaceUpdateVM", "Face captured successfully")
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
            updateScreenStatus()
        }.onFailure { e ->
            Log.e("FaceUpdateVM", "Error capturing face: ${e.message}", e)

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
            postSideEffect(FaceUpdateSideEffect.ShowToast("얼굴 캡처 중 오류가 발생했습니다"))
            updateScreenStatus()
        }
    }

    private fun updateScreenStatus() = intent {
        val status = when {
            state.isFaceEnrollmentDataReady -> {
                StringProvider.getString(R.string.user_face_update_ready_status)
            }
            state.faceDetectionStatus.isEmpty() -> {
                StringProvider.getString(R.string.user_face_update_scan_prompt)
            }
            state.faceDetectionStatus.isNotEmpty() -> {
                state.faceDetectionStatus
            }
            else -> {
                StringProvider.getString(R.string.user_face_update_retry_scan)
            }
        }

        reduce { state.copy(currentScreenStatus = status) }
    }

    fun saveFaceUpdate(userId: String, accessToken: String? = null) = intent {
        if (tempFaceEmbedding == null) {
            postSideEffect(FaceUpdateSideEffect.ShowToast("저장할 얼굴 정보가 없습니다"))
            postSideEffect(FaceUpdateSideEffect.UpdateFailed)
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
                reduce {
                    state.copy(
                        enrollmentMessage = StringProvider.getString(
                            R.string.signin_vm_face_registration_success,
                            userId
                        ),
                        isFaceEnrollmentDataReady = false
                    )
                }
                postSideEffect(FaceUpdateSideEffect.ShowToast("얼굴 업데이트가 완료되었습니다"))
                postSideEffect(FaceUpdateSideEffect.UpdateSuccess)
            } else {
                reduce {
                    state.copy(
                        enrollmentMessage = StringProvider.getString(R.string.signin_vm_face_registration_extraction_failed)
                    )
                }
                postSideEffect(FaceUpdateSideEffect.ShowToast("얼굴 업데이트에 실패했습니다"))
                postSideEffect(FaceUpdateSideEffect.UpdateFailed)
            }
        }.onFailure { e ->
            Log.e("FaceUpdateVM", "Error saving face update: ${e.message}", e)
            postSideEffect(FaceUpdateSideEffect.ShowToast("얼굴 업데이트 중 오류가 발생했습니다"))
            postSideEffect(FaceUpdateSideEffect.UpdateFailed)
        }
    }

    fun navigateBack() = intent {
        postSideEffect(FaceUpdateSideEffect.NavigateBack)
    }

    override fun onCleared() {
        super.onCleared()
        container.stateFlow.value.lastDetectedFaceBitmap?.recycle()
    }
}
