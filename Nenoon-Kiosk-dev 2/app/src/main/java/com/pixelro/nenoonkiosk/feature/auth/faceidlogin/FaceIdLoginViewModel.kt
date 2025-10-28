package com.pixelro.nenoonkiosk.feature.auth.faceidlogin

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.harang.data.repository.SignInRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.recognizer.FaceRecognizer
import com.pixelro.nenoonkiosk.core.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FaceIdLoginViewModel @Inject constructor(
    application: Application,
    private val faceRecognizer: FaceRecognizer,
    private val signInRepository: SignInRepository,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<FaceIdLoginState, FaceIdLoginSideEffect> {

    override val container: Container<FaceIdLoginState, FaceIdLoginSideEffect> =
        container(
            FaceIdLoginState(
                attemptsLeft = AppConstants.FACE_ID_MAX_ATTEMPTS,
                faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_detection_status_looking)
            )
        )

    private var registeredFaces: Map<String, FloatArray> = emptyMap()
    private var previousAttemptTime: Long = 0L

    init {
        faceRecognizer.initialize(application)
        loadRegisteredFaces()
    }

    private fun loadRegisteredFaces() {
        registeredFaces = SharedPreferencesManager.getRegisteredFaceEmbeddings()
        Log.d("FaceIdLoginVM", "Loaded ${registeredFaces.size} registered faces")
    }

    fun updateLiveFaceDetectionStatus(status: String) = intent {
        reduce { state.copy(liveFaceDetectionStatus = status) }
    }

    fun canAttemptSignIn(): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime - previousAttemptTime >= AppConstants.FACE_ID_INTERVAL
    }

    fun signInWithFace(faceBitmap: Bitmap) = intent {
        if (state.isProcessingFace || state.attemptsLeft <= 0) {
            faceBitmap.recycle()
            return@intent
        }

        if (!canAttemptSignIn()) {
            faceBitmap.recycle()
            return@intent
        }

        previousAttemptTime = System.currentTimeMillis()

        reduce {
            state.copy(
                isProcessingFace = true,
                faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_recognizing)
            )
        }

        runCatching {
            withContext(Dispatchers.Default) {
                faceRecognizer.getFaceEmbedding(faceBitmap)
            }
        }.onSuccess { embedding ->
            faceBitmap.recycle()

            if (embedding == null) {
                reduce {
                    state.copy(
                        faceDetectionStatus = StringProvider.getString(
                            R.string.signin_vm_face_info_extraction_failed
                        ),
                        attemptsLeft = state.attemptsLeft - 1,
                        isProcessingFace = false
                    )
                }

                if (state.attemptsLeft <= 0) {
                    delay(3000)
                    navigator.navigate(SignInRoute.UserSignIn)
                }
                return@intent
            }

            val success = if (!AppConstants.MANAGE_USERS_INTERNALLY) {
                authenticateWithServer(embedding)
            } else {
                authenticateLocally(embedding)
            }

            if (success) {
                reduce {
                    state.copy(
                        faceDetectionStatus = StringProvider.getString(R.string.signin_vm_login_success),
                        isProcessingFace = false
                    )
                }
                delay(1500)
                postSideEffect(FaceIdLoginSideEffect.LoginSuccess)
            } else {
                reduce {
                    state.copy(
                        faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_recognizing),
                        attemptsLeft = state.attemptsLeft - 1,
                        isProcessingFace = false
                    )
                }

                if (state.attemptsLeft <= 0) {
                    delay(3000)
                    navigator.navigate(SignInRoute.UserSignIn)
                }
            }
        }.onFailure { e ->
            Log.e("FaceIdLoginVM", "Error during face sign in: ${e.message}", e)
            faceBitmap.recycle()

            reduce {
                state.copy(
                    faceDetectionStatus = StringProvider.getString(R.string.signin_vm_face_processing_error),
                    attemptsLeft = state.attemptsLeft - 1,
                    isProcessingFace = false
                )
            }

            if (state.attemptsLeft <= 0) {
                delay(3000)
                navigator.navigate(SignInRoute.UserSignIn)
            }
        }
    }

    private suspend fun authenticateWithServer(embedding: FloatArray): Boolean {
        return runCatching {
            val signedInUserData = signInRepository.userSignInWithFace(
                embedding.contentToString(),
                AppConstants.FACE_ID_THRESHOLD
            )

            if (signedInUserData?.accessToken != null) {
                val newUserData = signInRepository.getUserProfile(signedInUserData.accessToken!!)
                newUserData != null
            } else {
                false
            }
        }.getOrElse { e ->
            Log.e("FaceIdLoginVM", "Server authentication failed: ${e.message}", e)
            false
        }
    }

    private fun authenticateLocally(embedding: FloatArray): Boolean {
        var highestSimilarity = 0.0f
        val recognitionThreshold = AppConstants.FACE_ID_THRESHOLD

        for ((userId, storedEmbedding) in registeredFaces) {
            val similarity = faceRecognizer.compareEmbeddings(embedding, storedEmbedding)
            Log.d("FaceIdLoginVM", "Comparing with $userId: Similarity = $similarity")

            if (similarity > highestSimilarity) {
                highestSimilarity = similarity
            }
        }

        return highestSimilarity >= recognitionThreshold
    }

    fun navigateBack() = intent {
        navigator.navigate(SignInRoute.UserSignIn)
    }
}
