package com.pixelro.nenoonkiosk.feature.auth.signup

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Patterns
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harang.data.repository.SignInRepository
import com.mangoslab.nemonicsdk.NPrintInfo
import com.mangoslab.nemonicsdk.NPrinter
import com.mangoslab.nemonicsdk.constants.NPrinterType
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.PrinterManager
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.bitmapToFile
import com.pixelro.nenoonkiosk.core.util.qr.QRCodeGenerator
import com.pixelro.nenoonkiosk.feature.inspection.result.TestResultUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val application: Application,
    private val signInRepository: SignInRepository,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<SignUpState, SignUpSideEffect> {

    override val container: Container<SignUpState, SignUpSideEffect> =
        container(SignUpState())

    private var tempFaceEmbedding: FloatArray? = null
    private var tempAccessToken: String? = null

    fun updateId(id: String) = intent {
        reduce { state.copy(id = id) }
    }

    fun updatePassword(password: String) = intent {
        val passwordError = validatePassword(password)
        val confirmPasswordError = if (state.confirmPassword.isNotBlank()) {
            validateConfirmPassword(password, state.confirmPassword)
        } else null

        reduce {
            state.copy(
                password = password,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }
    }

    fun updateName(name: String) = intent {
        reduce { state.copy(name = name) }
    }

    fun updateEmail(email: String) = intent {
        val emailError = if (email.isNotBlank()) validateEmail(email) else null
        reduce {
            state.copy(
                email = email,
                emailError = emailError
            )
        }
    }

    fun updateConfirmPassword(confirmPassword: String) = intent {
        val confirmPasswordError = validateConfirmPassword(state.password, confirmPassword)
        reduce {
            state.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = confirmPasswordError
            )
        }
    }

    fun togglePasswordVisibility() = intent {
        reduce { state.copy(passwordVisible = !state.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() = intent {
        reduce { state.copy(confirmPasswordVisible = !state.confirmPasswordVisible) }
    }

    fun updateFaceEnrollmentReady(ready: Boolean) = intent {
        reduce { state.copy(isFaceEnrollmentReady = ready) }
    }

    fun updateTempFaceEmbedding(embedding: FloatArray?) {
        tempFaceEmbedding = embedding
    }

    fun signUp() = intent {
        if (state.id.isBlank() || state.password.isBlank() || state.name.isBlank() ||
            state.passwordError != null || state.confirmPasswordError != null ||
            (state.email.isNotBlank() && state.emailError != null)
        ) {
            reduce {
                state.copy(errorMessage = StringProvider.getString(R.string.signin_vm_signup_validation_fill_all))
            }
            return@intent
        }

        reduce {
            state.copy(isSigningUp = true, errorMessage = null)
        }

        val success = runCatching {
            val isFaceEnrolled = state.isFaceEnrollmentReady && tempFaceEmbedding != null

            if (AppConstants.MANAGE_USERS_INTERNALLY) {
                SharedPreferencesManager.putUserAccount(
                    state.id,
                    state.password,
                    state.name,
                    state.email.ifBlank { null }
                )

                if (isFaceEnrolled) {
                    tempFaceEmbedding?.let { embedding ->
                        val registeredFaces = SharedPreferencesManager.getRegisteredFaceEmbeddings().toMutableMap()
                        registeredFaces[state.id] = embedding
                        SharedPreferencesManager.putRegisteredFaceEmbeddings(registeredFaces)
                    }
                }

                val qrBitmap = generateQrCodeBitmap(state.id, state.password)
                reduce { state.copy(generatedQrBitmap = qrBitmap) }
                true
            } else {
                val qrCode =
                    generateQrCodeBitmap(state.id, state.password) ?: return@runCatching false

                reduce { state.copy(generatedQrBitmap = qrCode) }

                val qrUrl = signInRepository.updateQrCode(
                    bitmapToFile(application, qrCode, "qr-image.jpg")
                ) ?: return@runCatching false

                val token = signInRepository.userSignUp(
                    id = state.id,
                    pw = state.password,
                    name = state.name,
                    email = state.email.ifBlank { AppConstants.DEFAULT_EMAIL },
                    pid = 0L,
                    vector = if (isFaceEnrolled) tempFaceEmbedding.contentToString() else "",
                    qrUrl = qrUrl
                )

                if (token != null) {
                    tempAccessToken = token
                    true
                } else {
                    false
                }
            }
        }.getOrElse { e ->
            Log.e("SignUpVM", "Sign up error: ${e.message}", e)
            false
        }

        if (success) {
            reduce {
                state.copy(
                    isSigningUp = false,
                    signupSuccess = true
                )
            }
            viewModelScope.launch {
                printQrCode()
            }
        } else {
            reduce {
                state.copy(
                    isSigningUp = false,
                    errorMessage = ""
                )
            }
        }
    }

    private fun validatePassword(password: String): String? {
        return if (password.length < 8 || password.length > 32) {
            StringProvider.getString(R.string.signin_vm_validation_password_length)
        } else {
            null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return if (password != confirmPassword) {
            StringProvider.getString(R.string.signin_vm_validation_password_mismatch)
        } else {
            null
        }
    }

    private fun validateEmail(email: String): String? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            StringProvider.getString(R.string.signin_vm_validation_email_invalid)
        } else {
            null
        }
    }

    private suspend fun generateQrCodeBitmap(id: String, password: String): Bitmap? {
        return runCatching {
            val userDataJson = JSONObject().apply {
                put("id", id)
                put("pw", password)
            }.toString()

            withContext(Dispatchers.IO) {
                QRCodeGenerator.generateQrCode(userDataJson, 400, 400)
            }
        }.getOrNull()
    }

    private fun printQrCode() {
        val qrCode = container.stateFlow.value.generatedQrBitmap ?: return

        val printerInfo = PrinterManager.getPrinterInfo()
        val printerType = printerInfo.first
        val printerMacAddress = printerInfo.second
        val nPrinterController = PrinterManager.getPrinterController()

        if (printerMacAddress.isEmpty() || nPrinterController == null) {
            Log.e("SignUpVM", "Printer not configured")
            return
        }

        try {
            nPrinterController.connectDelay = 2000

            val logoImg = BitmapFactory.decodeResource(
                application.resources,
                R.drawable.pixelro_logo_black
            ).scale(240, 80, false)

            val qrImg = qrCode.scale(80, 80, false)
            val bm = TestResultUtil.formatQrCode(qrImg = qrImg, logoImg = logoImg)

            NPrintInfo(
                NPrinter(
                    printerType ?: NPrinterType.NEMONIC_MIP201,
                    "Printer",
                    printerMacAddress
                ), bm
            ).apply {
                copies = 1
                isEnableDither = true
            }

            Log.d("SignUpVM", "Print command sent successfully")
        } catch (e: Exception) {
            Log.e("SignUpVM", "Error during print: ${e.message}", e)
        }
    }

    fun navigateToFaceEnrollment() = intent {
        navigator.navigate(SignInRoute.FaceEnrollment)
    }

    fun navigateBack() = intent {
        navigator.navigate(SignInRoute.UserSignIn)
    }

    override fun onCleared() {
        super.onCleared()
        container.stateFlow.value.generatedQrBitmap?.recycle()
    }
}
