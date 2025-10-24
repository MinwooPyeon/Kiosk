package com.pixelro.nenoonkiosk.feature.auth.login

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Patterns
import androidx.annotation.OptIn
import androidx.core.graphics.scale
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import com.harang.data.model.User
import com.harang.data.repository.SignInRepository
import com.mangoslab.nemonicsdk.NPrintInfo
import com.mangoslab.nemonicsdk.NPrinter
import com.mangoslab.nemonicsdk.constants.NPrinterType
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.PrinterManager
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.bitmapToFile
import com.pixelro.nenoonkiosk.core.util.qr.QRCodeGenerator
import com.pixelro.nenoonkiosk.feature.auth.FaceRecognizer
import com.pixelro.nenoonkiosk.feature.inspection.result.TestResultUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    application: Application,
    private val signInRepository: SignInRepository,
    private val navigator: Navigator,
    val faceRecognizer: FaceRecognizer,
) : AndroidViewModel(application) {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    private val _locationId = MutableStateFlow<Int?>(null)
    val locationId: StateFlow<Int?> = _locationId.asStateFlow()

    private val _isLocationSignedIn = MutableStateFlow(false)
    val isLocationSignedIn: StateFlow<Boolean> = _isLocationSignedIn.asStateFlow()

    private val _isUserSignedIn = MutableStateFlow(false)
    val isUserSignedIn: StateFlow<Boolean> = _isUserSignedIn.asStateFlow()

    private val _faceDetectionStatus =
        MutableStateFlow(StringProvider.getString(R.string.signin_vm_face_detection_status_looking))
    val faceDetectionStatus: StateFlow<String> = _faceDetectionStatus.asStateFlow()

    private val _isProcessingFace = MutableStateFlow(false)
    val isProcessingFace: StateFlow<Boolean> = _isProcessingFace.asStateFlow()

    private val _lastDetectedFaceBitmap = MutableStateFlow<Bitmap?>(null)
    val lastDetectedFaceBitmap: StateFlow<Bitmap?> = _lastDetectedFaceBitmap.asStateFlow()

    private val _enrollmentMessage = MutableStateFlow<String?>(null)
    val enrollmentMessage: StateFlow<String?> = _enrollmentMessage.asStateFlow()

    private val _enrollmentSuccess = MutableStateFlow(false)
    val enrollmentSuccess: StateFlow<Boolean> = _enrollmentSuccess.asStateFlow()

    private val _isFaceEnrollmentDataReady = MutableStateFlow(false)
    val isFaceEnrollmentDataReady: StateFlow<Boolean> = _isFaceEnrollmentDataReady.asStateFlow()

    private val _accountQrCode = MutableStateFlow<Bitmap?>(null)
    val accountQrCode: StateFlow<Bitmap?> = _accountQrCode.asStateFlow()

    private var tempFaceEmbedding: FloatArray? = null
    private var registeredFaces: MutableMap<String, FloatArray> = mutableMapOf()
    private var tempAccessToken: String? = null

    init {
        faceRecognizer.initialize(application)
        loadRegisteredFacesFromPrefs()
    }

    private fun loadRegisteredFacesFromPrefs() {
        registeredFaces = SharedPreferencesManager.getRegisteredFaceEmbeddings().toMutableMap()
        Log.d(
            "SignInViewModel",
            "Loaded ${registeredFaces.size} registered faces from SharedPreferences."
        )
    }

    fun isUserSignInSkipped(): Boolean {
        return _userId.value == AppConstants.DEFAULT_USER_ID && _isUserSignedIn.value
    }

    override fun onCleared() {
        super.onCleared()
        _lastDetectedFaceBitmap.value?.recycle()
        _lastDetectedFaceBitmap.value = null
    }

    fun resetFaceEnrollmentData() {
        _faceDetectionStatus.update { StringProvider.getString(R.string.signin_vm_face_detection_status_looking) }
        _isProcessingFace.update { false }
        _lastDetectedFaceBitmap.update { null }
        _enrollmentMessage.update { null }
        _enrollmentSuccess.update { false }
        _isFaceEnrollmentDataReady.update { false }
        tempFaceEmbedding = null
    }

    fun resetAllViewModelData() {
        _userId.update { null }
        _isLocationSignedIn.update { false }
        _isUserSignedIn.update { false }
        resetFaceEnrollmentData()
        _accountQrCode.update { null }
        tempAccessToken = null
    }

    fun resetUserData() {
        _userId.update { null }
        _isUserSignedIn.update { false }
        resetFaceEnrollmentData()
        clearQrCode()
        tempAccessToken = null
    }

    fun clearQrCode() {
        _accountQrCode.update { null }
    }

    fun validatePassword(password: String): String? {
        return if (password.length < 8 || password.length > 32) {
            StringProvider.getString(R.string.signin_vm_validation_password_length)
        } else {
            null
        }
    }

    fun validateConfirmPassword(
        password: String,
        confirmPassword: String,
    ): String? {
        return if (password != confirmPassword) {
            StringProvider.getString(R.string.signin_vm_validation_password_mismatch)
        } else {
            null
        }
    }

    fun validateEmail(email: String): String? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            StringProvider.getString(R.string.signin_vm_validation_email_invalid)
        } else {
            null
        }
    }

    suspend fun userSignIn(
        id: String,
        password: String,
        updateIsSignedIn: (Boolean) -> Unit,
    ): Boolean {
        if (!validateUserSignIn(id, password)) {
            _isUserSignedIn.update { false }
            updateIsSignedIn(false)
            return false
        }

        val signedInUserData =
            if (AppConstants.MANAGE_USERS_INTERNALLY) {
                SharedPreferencesManager.checkUserAccount(id, password)
            } else {
                signInRepository.userSignIn(id, password)
            }

        return if (signedInUserData?.accessToken != null) {
            try {
                val newUserData = signInRepository.getUserProfile(signedInUserData.accessToken!!)

                if (newUserData == null) {
                    false
                } else {
                    _isUserSignedIn.update { true }
                    updateIsSignedIn(true)
                    _userId.update { id }
                    _userData.update {
                        newUserData?.copy(
                            password = password,
                            accessToken = signedInUserData.accessToken,
                            refreshToken = signedInUserData.refreshToken,
                        )
                    }

                    if (AppConstants.MANAGE_USERS_INTERNALLY) {
                        _accountQrCode.value = generateQrCode(id, password)
                    } else {
                        _accountQrCode.value = getQrCode()
                    }

                    true
                }
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Sign in failed: ", e)
                false
            }
        } else if (signedInUserData != null) {
            _isUserSignedIn.update { true }
            updateIsSignedIn(true)
            _userId.update { id }
            _userData.update { signedInUserData.copy(id = id, password = password) }
            true
        } else {
            false
        }
    }

    fun userSignInSkip() {
        _isUserSignedIn.update { true }
        _userId.update { AppConstants.DEFAULT_USER_ID }
    }

    suspend fun locationSignIn(
        id: String,
        password: String,
        updateIsSignedIn: (Boolean) -> Unit,
    ): Boolean {
        if (!validateLocationSignIn(id, password)) {
            _isLocationSignedIn.update { false }
            updateIsSignedIn(false)
            return false
        }

        userSignOut()

        val result = signInRepository.locationSignIn(id, password)

        Log.d("111111", "$result")

        if (result != null) {
            if (result.data["success"] as Boolean) {
                signInRepository.updateLocationId((result.data["pid"] as Double).toInt())
                signInRepository.updateScreenSaverVideoURI(result.data["video"] as String)
                _isLocationSignedIn.update { true }
                updateIsSignedIn(true)
                return true
            } else {
                _isLocationSignedIn.update { false }
                updateIsSignedIn(false)
                return false
            }
        } else {
            _isLocationSignedIn.update { false }
            updateIsSignedIn(false)
            return false
        }
    }

    @OptIn(UnstableApi::class)
    fun locationSignInSkip(updateIsSignedIn: (Boolean) -> Unit) {
        _isLocationSignedIn.update { true }
        updateIsSignedIn(true)
        viewModelScope.launch(Dispatchers.IO) {
            signInRepository.updateLocationId(AppConstants.DEFAULT_LOCATION_ID)
            signInRepository.updateScreenSaverVideoURI(
                RawResourceDataSource.buildRawResourceUri(
                    R.raw.ad_sub
                ).toString()
            )
        }
    }

    suspend fun userSignUp(
        id: String,
        password: String,
        name: String,
        email: String?,
    ): String? {
        if (id.isBlank() || password.isBlank() || name.isBlank() || validatePassword(password) != null || (email?.isNotBlank() == true && validateEmail(
                email
            ) != null)
        ) {
            _enrollmentMessage.value =
                StringProvider.getString(R.string.signin_vm_signup_validation_fill_all)
            return null
        }

        val isFaceEnrolled = isFaceEnrollmentDataReady.value && tempFaceEmbedding != null

        val signUpResult =
            if (AppConstants.MANAGE_USERS_INTERNALLY) {
                SharedPreferencesManager.putUserAccount(id, password, name, email)
                if (isFaceEnrolled) {
                    tempFaceEmbedding?.let { embedding ->
                        registeredFaces[id] = embedding
                        SharedPreferencesManager.putRegisteredFaceEmbeddings(registeredFaces)
                    }
                }
                ""
            } else if (_locationId.value != null) {
                val qrCode = generateQrCode(id, password)
                if (qrCode != null) {
                    val qrUrl = signInRepository.updateQrCode(
                        bitmapToFile(
                            getApplication(),
                            qrCode,
                            "qr-image.jpg"
                        )
                    )
                    if (qrUrl != null) {
                        val res =
                            signInRepository.userSignUp(
                                id = id,
                                pw = password,
                                name = name,
                                email = if (email.isNullOrBlank()) AppConstants.DEFAULT_EMAIL else email,
                                pid = 0L,
                                vector = tempFaceEmbedding.contentToString(),
                                qrUrl = qrUrl,
                            )
                        if (res != null) {
                            tempAccessToken = res
                        }
                        res
                    } else {
                        null
                    }
                } else {
                    null
                }
            } else {
                null
            }

        return signUpResult
    }

    fun validateLocationSignIn(
        id: String,
        password: String,
    ): Boolean {
        return !(id.isBlank() || password.isBlank())
    }

    fun validateUserSignIn(
        id: String,
        password: String,
    ): Boolean {
        return !(id.isBlank() || password.isBlank())
    }

    fun userSignOut() {
        viewModelScope.launch {
            SharedPreferencesManager.clearCompoundTestResult(AppConstants.DEFAULT_USER_ID)
            _isUserSignedIn.update { false }
            _userId.update { null }
            _userData.update { null }
            resetFaceEnrollmentData()
        }
    }

    fun locationSignOut() {
        viewModelScope.launch {
            signInRepository.locationSignOut()
            userSignOut()
            _isLocationSignedIn.update { false }
        }
    }

    fun clearEnrollmentMessage() {
        _enrollmentMessage.update { null }
        _enrollmentSuccess.update { false }
    }

    fun updateFaceDetectionStatus(status: String) {
        _faceDetectionStatus.update { status }
    }

    fun processFaceForEmbeddingAndStoreTemporarily(faceBitmap: Bitmap) {
        if (_isProcessingFace.value) {
            faceBitmap.recycle()
            return
        }

        _isProcessingFace.update { true }
        _faceDetectionStatus.update { StringProvider.getString(R.string.signin_vm_face_processing) }

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val embedding = faceRecognizer.getFaceEmbedding(faceBitmap)
                withContext(Dispatchers.Main) {
                    if (embedding != null && !faceBitmap.isRecycled) {
                        tempFaceEmbedding = embedding
                        _lastDetectedFaceBitmap.update { oldBitmap ->
                            oldBitmap?.recycle()
                            faceBitmap.config?.let { faceBitmap.copy(it, true) }
                        }
                        _isFaceEnrollmentDataReady.update { true }
                        Log.d("SignInViewModel", "Face embedding temporarily stored.")
                    } else {
                        _faceDetectionStatus.update { StringProvider.getString(R.string.signin_vm_face_processing) }
                        _lastDetectedFaceBitmap.update { oldBitmap ->
                            oldBitmap?.recycle()
                            null
                        }
                        _isFaceEnrollmentDataReady.update { false }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(
                        "SignInViewModel",
                        "Error processing face for embedding: ${e.message}",
                        e
                    )
                    _faceDetectionStatus.update { StringProvider.getString(R.string.signin_vm_face_processing_error) }
                    _isFaceEnrollmentDataReady.update { false }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isProcessingFace.update { false }
                    faceBitmap.recycle()
                }
            }
        }
    }

    // FaceIdSingin Screen
    suspend fun userSignInWithFace(
        faceBitmap: Bitmap,
        updateIsSignedIn: (Boolean) -> Unit,
    ): Boolean {
        if (_isProcessingFace.value) return false

        _isProcessingFace.update { true }
        _faceDetectionStatus.update { StringProvider.getString(R.string.signin_vm_face_recognizing) }

        val embedding = faceRecognizer.getFaceEmbedding(faceBitmap)

        if (embedding == null) {
            withContext(Dispatchers.Main) {
                _isUserSignedIn.update { false }
                _faceDetectionStatus.update { StringProvider.getString(R.string.signin_vm_face_info_extraction_failed) }
            }
            _isProcessingFace.update { false }
            faceBitmap.recycle()
            return false
        }

        if (!AppConstants.MANAGE_USERS_INTERNALLY) {
            val signedInUserData = signInRepository.userSignInWithFace(
                embedding.contentToString(),
                AppConstants.FACE_ID_THRESHOLD
            )

            return if (signedInUserData?.accessToken != null) {
                try {
                    val newUserData =
                        signInRepository.getUserProfile(signedInUserData.accessToken!!)

                    if (newUserData == null) {
                        false
                    } else {
                        _isUserSignedIn.update { true }
                        _userId.update { newUserData!!.id }
                        _userData.update {
                            newUserData.copy(
                                accessToken = signedInUserData.accessToken,
                                refreshToken = signedInUserData.refreshToken,
                            )
                        }
                        _faceDetectionStatus.update {
                            StringProvider.getString(
                                R.string.signin_vm_login_success,
                                newUserData?.name
                                    ?: StringProvider.getString(R.string.default_user_name),
                            )
                        }

                        delay(1500)
                        updateIsSignedIn(true)
                        _isProcessingFace.update { false }

                        _accountQrCode.value = getQrCode()

                        true
                    }
                } catch (e: Exception) {
                    Log.e("SignInViewModel", "Sign in failed: ", e)
                    _isProcessingFace.update { false }
                    false
                }
            } else {
                _isProcessingFace.update { false }
                false
            }
        } else {
            var recognizedUser: String? = null
            var highestSimilarity = 0.0f
            val recognitionThreshold = AppConstants.FACE_ID_THRESHOLD
            for ((userId, storedEmbedding) in registeredFaces) {
                val similarity =
                    faceRecognizer.compareEmbeddings(embedding, storedEmbedding)
                Log.d("SignInViewModel", "Comparing with $userId: Similarity = $similarity")
                if (similarity > highestSimilarity) {
                    highestSimilarity = similarity
                    if (highestSimilarity > recognitionThreshold) {
                        recognizedUser = userId
                    }
                }
            }

            if (recognizedUser != null) {
                _userId.update { recognizedUser }
                _isUserSignedIn.update { true }

                val signedInUserData =
                    SharedPreferencesManager.checkUserAccount(recognizedUser)

                _userData.update { signedInUserData }
                _isUserSignedIn.update { true }
                _faceDetectionStatus.update {
                    StringProvider.getString(
                        R.string.signin_vm_login_success,
                        signedInUserData?.name
                            ?: StringProvider.getString(R.string.default_user_name),
                    )
                }

                delay(1500)
                updateIsSignedIn(true)

                return true
            } else {
                _isUserSignedIn.update { false }
                _faceDetectionStatus.update { StringProvider.getString(R.string.signin_vm_face_recognizing) }
            }
            _isProcessingFace.update { false }
            faceBitmap.recycle()
            return false
        }
    }


    // Face Enrollment
    suspend fun updateFace(userId: String? = null): Boolean {
        if (tempFaceEmbedding != null) {
            if (AppConstants.MANAGE_USERS_INTERNALLY) {
                if (userId.isNullOrBlank() && _userId.value.isNullOrBlank()) return false
                registeredFaces[userId ?: _userId.value!!] = tempFaceEmbedding!!
                SharedPreferencesManager.putRegisteredFaceEmbeddings(registeredFaces)
                return true
            } else {
                if (_userData.value != null && _userData.value?.accessToken != null) {
                    signInRepository.userUpdateFace(
                        _userData.value?.accessToken!!,
                        tempFaceEmbedding.contentToString()
                    )
                } else if (tempAccessToken != null) {
                    signInRepository.userUpdateFace(
                        tempAccessToken!!,
                        tempFaceEmbedding.contentToString()
                    )
                    tempAccessToken = null
                } else {
                    return false
                }
            }
            _enrollmentMessage.update {
                StringProvider.getString(
                    R.string.signin_vm_face_registration_success,
                    userId
                )
            }
            _enrollmentSuccess.update { true }
            tempFaceEmbedding = null
            _isFaceEnrollmentDataReady.update { false }
            return true
        } else {
            _enrollmentMessage.update { StringProvider.getString(R.string.signin_vm_face_registration_extraction_failed) }
            _enrollmentSuccess.update { false }
            _isFaceEnrollmentDataReady.update { false }
        }
        return false
    }

    suspend fun generateQrCode(
        id: String,
        password: String,
    ): Bitmap? {
        val userDataJson =
            JSONObject().apply {
                put("id", id)
                put("pw", password)
            }.toString()

        val bitmap =
            withContext(Dispatchers.IO) {
                QRCodeGenerator.generateQrCode(userDataJson, 400, 400)
            }
        _accountQrCode.value = bitmap
        return bitmap
    }

    fun generateAndPrintQrCode(
        id: String,
        password: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val qrCode = generateQrCode(id, password)

            if (qrCode != null) {
                printQrCodeInternal(qrCode)
            }
        }
    }

    fun printQrCode(qrCode: Bitmap?) {
        if (qrCode != null) {
            printQrCodeInternal(qrCode)
        }
    }

    suspend fun getQrCode(): Bitmap? {
        return if (_userData.value != null && !_userData.value?.accessToken.isNullOrEmpty()) {
            signInRepository.getQrUrl(_userData.value!!.accessToken!!)?.let { url ->
                val temp = signInRepository.getQrCode(url.substringAfter("api/v1/users/qr-image/"))
                temp
            }
        } else {
            null
        }
    }

    private fun printQrCodeInternal(qrCodeImageBitmap: Bitmap) {
        val printerInfo = PrinterManager.getPrinterInfo()
        val printerType = printerInfo.first
        val printerMacAddress = printerInfo.second
        val nPrinterController = PrinterManager.getPrinterController()

        if (printerMacAddress.isEmpty() || nPrinterController == null) {
            Log.e("SignInViewModel", "Printer not configured or controller is null.")
            return
        }

        val applicationContext = getApplication<Application>().applicationContext
        try {
            nPrinterController.connectDelay = 2000
            Log.d("SignInViewModel", "Custom Connect Delay set to 2000 ms")

            val resources = applicationContext.resources
            val logoImg =
                BitmapFactory.decodeResource(resources, R.drawable.pixelro_logo_black)
                    .scale(240, 80, false)
            val qrImg = qrCodeImageBitmap.scale(80, 80, false)
            val bm = TestResultUtil.formatQrCode(qrImg = qrImg, logoImg = logoImg)

            NPrintInfo(
                NPrinter(
                    printerType ?: NPrinterType.NEMONIC_MIP201,
                    "Printer",
                    printerMacAddress
                ), bm
            ).apply {
                copies = 1  // 인스턴스 메서드로 호출
                isEnableDither = true  // 인스턴스 메서드로 호출
            }


            Log.d("SignInViewModel", "Print command sent successfully.")
        } catch (e: Exception) {
            Log.e("SignInViewModel", "Error during print command: ${e.message}")
        }
    }
}