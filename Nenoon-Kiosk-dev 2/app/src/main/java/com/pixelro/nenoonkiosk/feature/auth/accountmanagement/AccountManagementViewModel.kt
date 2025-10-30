package com.pixelro.nenoonkiosk.feature.auth.accountmanagement

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.harang.data.model.dto.User
import com.harang.data.repository.SignInRepository
import com.mangoslab.nemonicsdk.NPrintInfo
import com.mangoslab.nemonicsdk.NPrinter
import com.mangoslab.nemonicsdk.constants.NPrinterType
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.PrinterManager
import com.pixelro.nenoonkiosk.core.navigation.AdminRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.result.TestResultUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AccountManagementViewModel @Inject constructor(
    private val application: Application,
    private val signInRepository: SignInRepository,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<AccountManagementState, AccountManagementSideEffect> {

    override val container: Container<AccountManagementState, AccountManagementSideEffect> =
        container(AccountManagementState())

    fun loadUserData(
        userId: String?,
        userData: User?,
        isUserSignedIn: Boolean
    ) = intent {
        reduce {
            state.copy(
                isUserSignedIn = isUserSignedIn,
                userData = userData,
                isUserSignInSkipped = userId == AppConstants.DEFAULT_USER_ID && isUserSignedIn
            )
        }
    }

    fun generateQrCodeBitmap(userId: String, password: String) = intent {
        runCatching {
            val qrData = "ID:$userId,PW:$password"
            val writer = QRCodeWriter()
            val bitMatrix = withContext(Dispatchers.Default) {
                writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512)
            }

            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap[x, y] = if (bitMatrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
                }
            }

            bitmap
        }.onSuccess { bitmap ->
            reduce {
                state.copy(
                    qrCodeBitmap = bitmap,
                    showProgressIndicator = false,
                    errorMessage = null
                )
            }
        }.onFailure { e ->
            Log.e("AccountManagementVM", "Error generating QR code: ${e.message}", e)
            reduce {
                state.copy(
                    qrCodeBitmap = null,
                    showProgressIndicator = false,
                    errorMessage = "QR 코드 생성에 실패했습니다"
                )
            }
            postSideEffect(AccountManagementSideEffect.ShowToast("QR 코드 생성에 실패했습니다"))
        }
    }

    fun loadQrCodeFromServer(accessToken: String) = intent {
        runCatching {
            signInRepository.getQrUrl(accessToken)?.let { url ->
                signInRepository.getQrCode(url.substringAfter("api/v1/users/qr-image/"))
            }
        }.onSuccess { qrCode ->
            reduce {
                state.copy(
                    qrCodeBitmap = qrCode,
                    showProgressIndicator = false,
                    errorMessage = null
                )
            }
        }.onFailure { e ->
            Log.e("AccountManagementVM", "Error loading QR code from server: ${e.message}", e)
            reduce {
                state.copy(
                    qrCodeBitmap = null,
                    showProgressIndicator = false,
                    errorMessage = "QR 코드 로딩에 실패했습니다"
                )
            }
            postSideEffect(AccountManagementSideEffect.ShowToast("QR 코드 로딩에 실패했습니다"))
        }
    }

    fun printQrCode() = intent {
        val currentUserId = state.userData?.id
        val currentUserPassword = state.userData?.password
        val qrCodeBitmap = state.qrCodeBitmap

        if (!state.isUserSignedIn) {
            postSideEffect(AccountManagementSideEffect.ShowToast("로그인이 필요합니다"))
            return@intent
        }

        runCatching {
            val bitmapToPrint = if (AppConstants.MANAGE_USERS_INTERNALLY &&
                !currentUserId.isNullOrBlank() &&
                !currentUserPassword.isNullOrBlank()
            ) {
                generateQrCodeBitmapSync(currentUserId, currentUserPassword)
            } else if (!AppConstants.MANAGE_USERS_INTERNALLY && qrCodeBitmap != null) {
                qrCodeBitmap
            } else {
                null
            }

            bitmapToPrint?.let {
                printQrCodeInternal(it)
                reduce { state.copy(isQrPrintButtonEnabled = false) }
                delay(10000)
                reduce { state.copy(isQrPrintButtonEnabled = true) }
            } ?: run {
                postSideEffect(AccountManagementSideEffect.ShowToast("인쇄할 QR 코드가 없습니다"))
            }
        }.onFailure { e ->
            Log.e("AccountManagementVM", "Error printing QR code: ${e.message}", e)
            postSideEffect(AccountManagementSideEffect.ShowToast("QR 코드 인쇄에 실패했습니다"))
            reduce { state.copy(isQrPrintButtonEnabled = true) }
        }
    }

    private suspend fun generateQrCodeBitmapSync(userId: String, password: String): Bitmap? {
        return runCatching {
            val qrData = "ID:$userId,PW:$password"
            val writer = QRCodeWriter()
            val bitMatrix = withContext(Dispatchers.Default) {
                writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512)
            }

            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap[x, y] = if (bitMatrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
                }
            }
            bitmap
        }.getOrNull()
    }

    fun navigateToFaceEnrollment() = intent {
        if (!state.isUserSignedIn || state.userData?.id == null) {
            postSideEffect(AccountManagementSideEffect.ShowToast("로그인이 필요합니다"))
            return@intent
        }
        navigator.navigate(AdminRoute.FaceUpdateTermsOfService)
    }

    fun signOut() = intent {
        navigator.navigateAndClearBackStack(SignInRoute.UserSignIn)
        postSideEffect(AccountManagementSideEffect.SignOut)
    }

    fun navigateBack() = intent {
        navigator.navigateBack()
    }

    private fun printQrCodeInternal(qrCodeImageBitmap: Bitmap) {
        val printerInfo = PrinterManager.getPrinterInfo()
        val printerType = printerInfo.first
        val printerMacAddress = printerInfo.second
        val nPrinterController = PrinterManager.getPrinterController()

        if (printerMacAddress.isEmpty() || nPrinterController == null) {
            Log.e("AccountManagementVM", "Printer not configured")
            return
        }

        try {
            nPrinterController.connectDelay = 2000

            val logoImg = BitmapFactory.decodeResource(
                application.resources,
                R.drawable.pixelro_logo_black
            ).scale(240, 80, false)

            val qrImg = qrCodeImageBitmap.scale(80, 80, false)
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

            Log.d("AccountManagementVM", "Print command sent successfully")
        } catch (e: Exception) {
            Log.e("AccountManagementVM", "Error during print: ${e.message}", e)
        }
    }
}
