package com.pixelro.nenoonkiosk.feature.auth.qrlogin

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.harang.data.repository.SignInRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
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
class QrLoginViewModel @Inject constructor(
    private val application: Application,
    private val signInRepository: SignInRepository
) : ViewModel(), ContainerHost<QrLoginState, QrLoginSideEffect> {

    override val container: Container<QrLoginState, QrLoginSideEffect> =
        container(
            QrLoginState(
                qrScanStatus = StringProvider.getString(R.string.qr_signin_scanning)
            )
        )

    private var lastScanTime: Long = 0L

    fun checkCameraPermission() = intent {
        val hasPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        reduce {
            state.copy(isCameraPermissionGranted = hasPermission)
        }

        if (!hasPermission) {
            postSideEffect(QrLoginSideEffect.RequestCameraPermission)
        }
    }

    fun updatePermissionGranted(granted: Boolean) = intent {
        reduce {
            state.copy(isCameraPermissionGranted = granted)
        }
    }

    fun signInWithQrCode(qrCodeData: String) = intent {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastScanTime < AppConstants.QR_SCAN_COOLDOWN_MS) {
            return@intent
        }
        lastScanTime = currentTime

        if (state.isProcessingQr) {
            return@intent
        }

        reduce {
            state.copy(
                isProcessingQr = true,
                scannedData = qrCodeData,
                qrScanStatus = StringProvider.getString(R.string.qr_signin_processing)
            )
        }

        try {
            val (userId, password) = parseQrCodeData(qrCodeData)

            if (userId == null || password == null) {
                reduce {
                    state.copy(
                        qrScanStatus = StringProvider.getString(R.string.qr_signin_invalid_format),
                        isProcessingQr = false
                    )
                }
                postSideEffect(QrLoginSideEffect.LoginFailed)
                return@intent
            }

            val success = if (AppConstants.MANAGE_USERS_INTERNALLY) {
                signInLocally(userId, password)
            } else {
                signInWithServer(userId, password)
            }

            if (success) {
                reduce {
                    state.copy(
                        qrScanStatus = StringProvider.getString(R.string.qr_signin_success),
                        isProcessingQr = false
                    )
                }
                delay(1000)
                postSideEffect(QrLoginSideEffect.LoginSuccess)
            } else {
                reduce {
                    state.copy(
                        qrScanStatus = StringProvider.getString(R.string.qr_signin_authentication_failed),
                        isProcessingQr = false
                    )
                }
                postSideEffect(QrLoginSideEffect.LoginFailed)
            }
        } catch (e: Exception) {
            Log.e("QrLoginVM", "QR sign in error: ${e.message}", e)
            reduce {
                state.copy(
                    qrScanStatus = StringProvider.getString(R.string.qr_signin_error),
                    isProcessingQr = false
                )
            }
            postSideEffect(QrLoginSideEffect.LoginFailed)
        }
    }

    private fun parseQrCodeData(qrData: String): Pair<String?, String?> {
        return try {
            val parts = qrData.split(",")
            if (parts.size == 2) {
                val userId = parts[0].substringAfter("ID:").trim()
                val password = parts[1].substringAfter("PW:").trim()
                Pair(userId, password)
            } else {
                Pair(null, null)
            }
        } catch (e: Exception) {
            Log.e("QrLoginVM", "Error parsing QR data: ${e.message}", e)
            Pair(null, null)
        }
    }

    private fun signInLocally(userId: String, password: String): Boolean {
        val storedUsers = SharedPreferencesManager.getStoredUsers()
        val user = storedUsers.find { it.id == userId && it.password == password }

        if (user != null) {
            SharedPreferencesManager.putCurrentUser(user)
            return true
        }
        return false
    }

    private suspend fun signInWithServer(userId: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val signedInUserData = signInRepository.userSignIn(userId, password)

                if (signedInUserData?.accessToken != null) {
                    val newUserData =
                        signInRepository.getUserProfile(signedInUserData.accessToken!!)
                    if (newUserData != null) {
                        SharedPreferencesManager.putCurrentUser(newUserData)
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("QrLoginVM", "Server sign in failed: ${e.message}", e)
                false
            }
        }
    }

    fun navigateBack() = intent {
        postSideEffect(QrLoginSideEffect.NavigateBack)
    }
}
