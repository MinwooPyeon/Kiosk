package com.pixelro.nenoonkiosk.feature.auth.accountmanagement

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.harang.data.repository.SignInRepository
import com.mangoslab.nemonicsdk.NPrintInfo
import com.mangoslab.nemonicsdk.NPrinter
import com.mangoslab.nemonicsdk.constants.NPrinterType
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.PrinterManager
import com.pixelro.nenoonkiosk.feature.inspection.result.TestResultUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import androidx.core.graphics.set
import androidx.core.graphics.createBitmap
import com.pixelro.nenoonkiosk.core.navigation.Navigator

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
        userData: com.harang.data.model.dto.User?,
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
        try {
            val qrData = "ID:$userId,PW:$password"
            val writer = QRCodeWriter()
            val bitMatrix = withContext(Dispatchers.IO) {
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

            reduce {
                state.copy(
                    qrCodeBitmap = bitmap,
                    showProgressIndicator = false
                )
            }
        } catch (e: Exception) {
            Log.e("AccountManagementVM", "Error generating QR code: ${e.message}")
            reduce {
                state.copy(
                    qrCodeBitmap = null,
                    showProgressIndicator = false
                )
            }
        }
    }

    suspend fun getQrCodeFromServer(accessToken: String): Bitmap? {
        return signInRepository.getQrUrl(accessToken)?.let { url ->
            signInRepository.getQrCode(url.substringAfter("api/v1/users/qr-image/"))
        }
    }

    fun loadQrCode(qrCode: Bitmap?) = intent {
        reduce {
            state.copy(
                qrCodeBitmap = qrCode,
                showProgressIndicator = false
            )
        }
    }

    fun printQrCode(userId: String, password: String) = intent {
        try {
            val qrData = "ID:$userId,PW:$password"
            val writer = QRCodeWriter()
            val bitMatrix = withContext(Dispatchers.IO) {
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

            printQrCodeInternal(bitmap)

            reduce { state.copy(isQrPrintButtonEnabled = false) }
            delay(10000)
            reduce { state.copy(isQrPrintButtonEnabled = true) }
        } catch (e: Exception) {
            Log.e("AccountManagementVM", "Error printing QR code: ${e.message}")
        }
    }

    fun printExistingQrCode(qrCode: Bitmap?) = intent {
        if (qrCode == null) return@intent

        printQrCodeInternal(qrCode)

        reduce { state.copy(isQrPrintButtonEnabled = false) }
        delay(10000)
        reduce { state.copy(isQrPrintButtonEnabled = true) }
    }

    fun navigateToFaceEnrollment() = intent {
        postSideEffect(AccountManagementSideEffect.NavigateToFaceEnrollment)
    }

    fun signOut() = intent {
        postSideEffect(AccountManagementSideEffect.SignOut)
    }

    fun navigateBack() = intent {
        postSideEffect(AccountManagementSideEffect.NavigateBack)
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
            Log.e("AccountManagementVM", "Error during print: ${e.message}")
        }
    }
}
