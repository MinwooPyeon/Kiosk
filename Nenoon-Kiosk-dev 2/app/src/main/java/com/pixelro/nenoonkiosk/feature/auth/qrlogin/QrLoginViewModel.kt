package com.pixelro.nenoonkiosk.feature.auth.qrlogin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.harang.data.repository.SignInRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class QrLoginViewModel @Inject constructor(
    private val signInRepository: SignInRepository,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<QrLoginState, QrLoginSideEffect> {

    override val container: Container<QrLoginState, QrLoginSideEffect> =
        container(
            QrLoginState(
                qrScanStatus = StringProvider.getString(R.string.qr_sign_in_scan_instruction)
            )
        )

    fun signInWithQrCode(qrData: String) = intent {
        if (state.isProcessingQr) return@intent

        reduce {
            state.copy(
                isProcessingQr = true,
                qrScanStatus = StringProvider.getString(R.string.qr_sign_in_login_processing)
            )
        }

        runCatching {
            parseQrData(qrData)
        }.onSuccess { (userId, password) ->
            val success = if (AppConstants.MANAGE_USERS_INTERNALLY) {
                authenticateLocally(userId, password)
            } else {
                authenticateWithServer(userId, password)
            }

            if (success) {
                reduce {
                    state.copy(
                        qrScanStatus = "",
                        isProcessingQr = false
                    )
                }
                postSideEffect(QrLoginSideEffect.LoginSuccess)
            } else {
                reduce {
                    state.copy(
                        qrScanStatus = StringProvider.getString(R.string.qr_sign_in_invalid_qr),
                        isProcessingQr = false
                    )
                }
                postSideEffect(QrLoginSideEffect.ShowToast(StringProvider.getString(R.string.qr_sign_in_invalid_qr_toast)))
            }
        }.onFailure { e ->
            Log.e("QrLoginVM", "QR sign in error: ${e.message}", e)
            reduce {
                state.copy(
                    qrScanStatus = StringProvider.getString(R.string.qr_sign_in_invalid_qr),
                    isProcessingQr = false
                )
            }
            postSideEffect(QrLoginSideEffect.ShowToast(StringProvider.getString(R.string.qr_sign_in_invalid_qr_toast)))
        }
    }

    private fun parseQrData(qrData: String): Pair<String, String> {
        val parts = qrData.split(",")
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid QR format")
        }

        val userId = parts[0].substringAfter("ID:").trim()
        val password = parts[1].substringAfter("PW:").trim()

        if (userId.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Empty ID or password")
        }

        return userId to password
    }

    private fun authenticateLocally(userId: String, password: String): Boolean {
        val user = SharedPreferencesManager.checkUserAccount(userId, password)
        return user != null
    }

    private suspend fun authenticateWithServer(userId: String, password: String): Boolean {
        val signedInUserData = signInRepository.userSignIn(userId, password)

        return if (signedInUserData?.accessToken != null) {
            val newUserData = signInRepository.getUserProfile(signedInUserData.accessToken!!)
            newUserData != null
        } else {
            false
        }
    }

    fun navigateBack() = intent {
        navigator.navigate(SignInRoute.UserSignIn)
    }
}
