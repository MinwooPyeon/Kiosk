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
import kotlinx.coroutines.delay
import org.json.JSONObject
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
                qrScanStatus = StringProvider.getString(R.string.qr_sign_in_scan_instruction)
            )
        )

    private var lastScanTime: Long = 0L
    private val scanCooldown = 3000L

    fun checkCameraPermission() = intent {
        val hasPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        reduce {
            state.copy(isCameraPermissionGranted = hasPermission)
        }

        if (!hasPermission) {
            reduce {
                state.copy(qrScanStatus = "카메라 권한이 필요합니다")
            }
            postSideEffect(QrLoginSideEffect.RequestCameraPermission)
        }
    }

    fun updatePermissionGranted(granted: Boolean) = intent {
        reduce {
            state.copy(
                isCameraPermissionGranted = granted,
                qrScanStatus = if (granted) {
                    StringProvider.getString(R.string.qr_sign_in_scan_instruction)
                } else {
                    "카메라 권한이 필요합니다"
                }
            )
        }
    }

    fun signInWithQrCode(qrCodeData: String) = intent {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastScanTime < scanCooldown) {
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
                qrScanStatus = StringProvider.getString(R.string.qr_sign_in_login_processing)
            )
        }

        runCatching {
            val (userId, password) = parseQrCodeData(qrCodeData)

            if (userId == null || password == null) {
                reduce {
                    state.copy(
                        qrScanStatus = StringProvider.getString(R.string.qr_sign_in_invalid_qr),
                        isProcessingQr = false
                    )
                }
                delay(1500)
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
                        qrScanStatus = StringProvider.getString(R.string.qr_sign_in_login_success_toast),
                        isProcessingQr = false
                    )
                }
                delay(1500)
                postSideEffect(QrLoginSideEffect.LoginSuccess)
            } else {
                reduce {
                    state.copy(
                        qrScanStatus = StringProvider.getString(R.string.qr_sign_in_invalid_qr),
                        isProcessingQr = false
                    )
                }
                delay(1500)
                postSideEffect(QrLoginSideEffect.LoginFailed)
            }
        }.onFailure { e ->
            Log.e("QrLoginVM", "QR sign in error: ${e.message}", e)
            reduce {
                state.copy(
                    qrScanStatus = StringProvider.getString(R.string.qr_sign_in_invalid_qr),
                    isProcessingQr = false
                )
            }
            postSideEffect(QrLoginSideEffect.LoginFailed)
        }
    }

    private fun parseQrCodeData(qrData: String): Pair<String?, String?> {
        return try {
            val json = JSONObject(qrData)
            val userId = json.getString("id")
            val password = json.getString("pw")
            Pair(userId, password)
        } catch (e: Exception) {
            Log.e("QrLoginVM", "Error parsing QR data: ${e.message}", e)
            Pair(null, null)
        }
    }

    private fun signInLocally(userId: String, password: String): Boolean {
        val user = SharedPreferencesManager.checkUserAccount(userId, password)
        return user != null
    }

    private suspend fun signInWithServer(userId: String, password: String): Boolean {
        return runCatching {
            val signedInUserData = signInRepository.userSignIn(userId, password)

            if (signedInUserData?.accessToken != null) {
                val newUserData = signInRepository.getUserProfile(signedInUserData.accessToken!!)
                newUserData != null
            } else {
                false
            }
        }.getOrElse { e ->
            Log.e("QrLoginVM", "Server sign in failed: ${e.message}", e)
            false
        }
    }

    fun navigateBack() = intent {
        postSideEffect(QrLoginSideEffect.NavigateBack)
    }
}
