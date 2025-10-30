package com.pixelro.nenoonkiosk.feature.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.SizeF
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.exerciseglasses.concentration_exercise.ConcentrationExerciseResult
import com.pixelro.nenoonkiosk.feature.inspection.exerciseglasses.presbyopia_exercise.PresbyopiaExerciseResult
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.dementia.DementiaViewModel
import com.pixelro.nenoonkiosk.feature.inspection.gripStrength.result.GripStrengthInspectionResultContract
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridTestResult
import com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.MChartTestResult
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaInspectionResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.children.ChildrenVisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.longdistance.LongVisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.shortdistance.ShortVisualAcuityTestResult
import com.pixelro.nenoonkiosk.feature.survey.model.SurveyGlass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("HardwareIds")
@HiltViewModel
class NenoonViewModel
    @Inject
    constructor(
        application: Application,
    ) : AndroidViewModel(application) {
        /**
         * 화면 보호기 타이머
         */

        @RequiresApi(Build.VERSION_CODES.S)
        private fun checkBackgroundStatus() {
            viewModelScope.launch(CoroutineName("checkBackgroundStatus")) {
                while (true) {
                    /**
                     * 0 = 타이머 작동 X, ScreenSaver X
                     * 1 = 타이머 작동 O, ScreenSaver O
                     */
                    if (_isResumed.value) {
                        _screenSaverTimer.update { _screenSaverTimer.value - 1 }
//                    _screenSaverTimer.update { _screenSaverTimer.value - 0  }

                        if (_screenSaverTimer.value < 0) {
                            _isScreenSaving.update { true }
                        }
                        /**
                         * Check permissions
                         */
                        checkPermissions()
                    }
                    delay(1000)
                }
            }
        }

        private val _isScreenSaving = MutableStateFlow(false)
        val isScreenSaving: StateFlow<Boolean> = _isScreenSaving.asStateFlow()

        /**
         * signIn
         */
        private val _isSignedIn = MutableStateFlow(false)
        val isSignedIn: StateFlow<Boolean> = _isSignedIn

        fun updateIsSignedIn(isSignedIn: Boolean) {
            _isSignedIn.update { isSignedIn }
        }

        /**
         * LocationID
         * */
        private val _locationId = MutableStateFlow(DebugConstants.PLACEHOLDER_PID)
        val locationId: StateFlow<Int> = _locationId

        fun updateLocationId(locId: Int) {
            _locationId.update { locId }
        }

        /**
         * Settings
         */
        enum class SettingsDialogState {
            None,
            Language,
            BloodPressureMonitorType,
        }

        private val _settingsDialogState = MutableStateFlow(SettingsDialogState.None)
        val settingsDialogState: StateFlow<SettingsDialogState> = _settingsDialogState

        private val _isSenior = MutableStateFlow(false)
        val isSenior: StateFlow<Boolean> = _isSenior

        fun updateIsSenior(isSenior: Boolean) {
            _isSenior.update { isSenior }
        }

        fun setSettingsDialogState(state: SettingsDialogState) {
            _settingsDialogState.update { state }
        }

        fun updateLanguage(language: String) {
            SharedPreferencesManager.putString("language", language)
            val locale = Locale.forLanguageTag(language)
            getApplication<Application>().resources.configuration.setLocale(locale)
            TTS.setLanguage(language) 
            _settingsDialogState.update { SettingsDialogState.None }
        }

        /**
         * Screen Saver
         */
        private val _isResumed = MutableStateFlow(false)
        private val _isPaused = MutableStateFlow(false)
        val exoPlayer: ExoPlayer

        /**
         * 타이머 시간 설정
         */
        private val _screenSaverTimer = MutableStateFlow(120)
        private val _timeValue = MutableStateFlow(120)

        fun resetScreenSaverTimer() {
            _screenSaverTimer.update { _timeValue.value }
            _isScreenSaving.update { false }
        }

        fun updateScreenSaverTimerValue(time: Int) {
            _timeValue.update { time }
            resetScreenSaverTimer()
        }

        fun updateLocalConfigurationValues(
            pixelDensity: Float,
            screenWidthDp: Int,
            screenHeightDp: Int,
            focalLength: Float,
            lensSize: SizeF,
        ) {
            GlobalValue.pixelDensity = pixelDensity
            GlobalValue.screenWidthDp = screenWidthDp
            GlobalValue.screenHeightDp = screenHeightDp
            GlobalValue.focalLength = focalLength
            GlobalValue.lensSize = lensSize
        }

        /**
         * Checking permission, location, bluetooth
         */

        private val _isWriteSettingsPermissionGranted = MutableStateFlow(false)
        val isWriteSettingsPermissionGranted: StateFlow<Boolean> = _isWriteSettingsPermissionGranted
        private val _isBluetoothPermissionsGranted = MutableStateFlow(false)
        val isBluetoothPermissionsGranted: StateFlow<Boolean> = _isBluetoothPermissionsGranted
        private val _isCameraPermissionGranted = MutableStateFlow(false)
        val isCameraPermissionGranted: StateFlow<Boolean> = _isCameraPermissionGranted
        private val _isAllPermissionsGranted = MutableStateFlow(false)
        val isAllPermissionsGranted: StateFlow<Boolean> = _isAllPermissionsGranted
        private val _isLocationOn = MutableStateFlow(false)
        val isLocationOn: StateFlow<Boolean> = _isLocationOn
        private val _isBluetoothOn = MutableStateFlow(false)
        val isBlueToothOn: StateFlow<Boolean> = _isBluetoothOn
        private val _resolvableApiException =
            MutableStateFlow(ResolvableApiException(Status.RESULT_CANCELED))
        val resolvableApiException: StateFlow<ResolvableApiException> = _resolvableApiException

        // 권한 체크
        @RequiresApi(Build.VERSION_CODES.S)
        fun checkPermissions() {
            val isBluetoothScanGranted =
                ContextCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.BLUETOOTH_SCAN,
                ) == PackageManager.PERMISSION_GRANTED

            val isBluetoothConnectGranted =
                ContextCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.BLUETOOTH_CONNECT,
                ) == PackageManager.PERMISSION_GRANTED

//        val isBluetoothGranted = ContextCompat.checkSelfPermission(
//            getApplication(),
//            Manifest.permission.BLUETOOTH
//        ) == PackageManager.PERMISSION_GRANTED
//
//        val isBluetoothAdminGranted = ContextCompat.checkSelfPermission(
//            getApplication(),
//            Manifest.permission.BLUETOOTH_ADMIN
//        ) == PackageManager.PERMISSION_GRANTED

            val isCameraGranted =
                ContextCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.CAMERA,
                ) == PackageManager.PERMISSION_GRANTED

            val isWriteSettingsGranted = Settings.System.canWrite(getApplication())

            // 업데이트 각 권한 상태
            _isBluetoothPermissionsGranted.update { isBluetoothScanGranted && isBluetoothConnectGranted }
//        _isBluetoothPermissionsGranted.update { isBluetoothScanGranted && isBluetoothConnectGranted && isBluetoothGranted && isBluetoothAdminGranted }
            _isCameraPermissionGranted.update { isCameraGranted }
            _isWriteSettingsPermissionGranted.update { isWriteSettingsGranted }

            // 위치와 블루투스 상태 확인
            checkIsLocationOn()
            checkIsBluetoothOn()
//        Log.d("BluetoothCheck","1$isBluetoothScanGranted.toString()")
//        Log.d("BluetoothCheck","2$isBluetoothConnectGranted.toString()")
//        Log.d("BluetoothCheck","3$isBluetoothGranted.toString()")
//        Log.d("BluetoothCheck","4$isCameraGranted.toString()")
//        Log.d("BluetoothCheck","5$isWriteSettingsGranted.toString()")

            if (
                isBluetoothScanGranted &&
                isBluetoothConnectGranted &&
//            isBluetoothGranted &&
//            isBluetoothAdminGranted &&
                isCameraGranted &&
                isWriteSettingsGranted &&
                (_isLocationOn.value || DebugConstants.EMULATOR_MODE) &&
                _isBluetoothOn.value
            ) {
                _isAllPermissionsGranted.update { true }
            }
            // 모든 권한이 부여되었는지 확인
        }

        private fun checkIsLocationOn() {
            val locationManager =
                ContextCompat.getSystemService(
                    getApplication(),
                    LocationManager::class.java,
                ) as LocationManager

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val locationRequest: LocationRequest = LocationRequest.Builder(10000).build()
                val client: SettingsClient =
                    LocationServices.getSettingsClient(getApplication() as Context)
                val builder: LocationSettingsRequest.Builder =
                    LocationSettingsRequest
                        .Builder()
                        .addLocationRequest(locationRequest)
                val gpsSettingTask: Task<LocationSettingsResponse> =
                    client.checkLocationSettings(builder.build())

                gpsSettingTask.addOnSuccessListener {
                }
                gpsSettingTask.addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        try {
                            _resolvableApiException.update { exception }
                            _isLocationOn.update { false }
                        } catch (sendEx: IntentSender.SendIntentException) {
                        }
                    }
                }
            } else {
                _isLocationOn.update { true }
            }
        }

        private fun checkIsBluetoothOn() {
            val bluetoothAdapter =
                (getApplication<Application>().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            if (bluetoothAdapter.isEnabled) {
                _isBluetoothOn.update { true }
            } else {
                _isBluetoothOn.update { false }
            }
        }

        private val _selectedTestType = MutableStateFlow(InspectionType.None)
        val selectedTestType: StateFlow<InspectionType> = _selectedTestType

        fun updateToResumed() {
            _isResumed.update { true }
            _isPaused.update { false }
        }

        fun updateToPaused() {
            _isResumed.update { false }
            _isPaused.update { true }
        }

        fun updateSelectedTestType(testType: InspectionType) {
            _selectedTestType.update { testType }
        }

        /**
         * Survey Data
         */

        private val _surveyId = MutableStateFlow(0L)
        val surveyId: StateFlow<Long> = _surveyId

        fun updateSurveyData(surveyId: Long) {
            _surveyId.update { surveyId }
        }

        private val _surveyGlass = MutableStateFlow(SurveyGlass.None)
        val surveyGlass: StateFlow<SurveyGlass> = _surveyGlass

        fun updateSurveyGlass(surveyGlass: SurveyGlass) {
            _surveyGlass.update { surveyGlass }
        }

        /**
         * Test Result
         */

        private val _isPhoriaTestDone = MutableStateFlow(false)
        val isPhoriaTestDone: StateFlow<Boolean> = _isPhoriaTestDone
        private val _isAniseikoniaTestDone = MutableStateFlow(false)
        val isAniseikoniaTestDone: StateFlow<Boolean> = _isAniseikoniaTestDone

        private val _isPresbyopiaTestDone = MutableStateFlow(false)
        val isPresbyopiaTestDone: StateFlow<Boolean> = _isPresbyopiaTestDone
        private val _isShortVisualAcuityTestDone = MutableStateFlow(false)
        val isShortVisualAcuityTestDone: StateFlow<Boolean> = _isShortVisualAcuityTestDone
        private val _isAmslerGridTestDone = MutableStateFlow(false)
        val isAmslerGridTestDone: StateFlow<Boolean> = _isAmslerGridTestDone
        private val _isMChartTestDone = MutableStateFlow(false)
        val isMChartTestDone: StateFlow<Boolean> = _isMChartTestDone
        private val _isBloodPressureTestDone = MutableStateFlow(false)
        val isBloodPressureTestDone: StateFlow<Boolean> = _isBloodPressureTestDone
        private val _isGripStrengthTestDone = MutableStateFlow(false)
        val isGripStrengthTestDone: StateFlow<Boolean> = _isGripStrengthTestDone
        private val _isPulmonaryFunctionTestDone = MutableStateFlow(false)
        val isPulmonaryFunctionTestDone: StateFlow<Boolean> = _isPulmonaryFunctionTestDone

        fun updateIsPhoriaTestDone(isDone: Boolean) {
            _isPhoriaTestDone.update { isDone }
        }

        fun updateIsAniseikoniaTestDone(isDone: Boolean) {
            _isAniseikoniaTestDone.update { isDone }
        }

        fun updateIsPresbyopiaTestDone(isDone: Boolean) {
            _isPresbyopiaTestDone.update { isDone }
        }

        fun updateIsShortVisualAcuityTestDone(isDone: Boolean) {
            _isShortVisualAcuityTestDone.update { isDone }
        }

        fun updateIsAmslerGridTestDone(isDone: Boolean) {
            _isAmslerGridTestDone.update { isDone }
        }

        fun updateIsMChartTestDone(isDone: Boolean) {
            _isMChartTestDone.update { isDone }
        }

        fun updateIsBloodPressureTestDone(isDone: Boolean) {
            _isBloodPressureTestDone.update { isDone }
        }

        fun updateIsGripStrengthTestDone(isDone: Boolean) {
            _isGripStrengthTestDone.update { isDone }
        }

        fun updateIsPulmonaryFunctionTestDone(isDone: Boolean) {
            _isPulmonaryFunctionTestDone.update { isDone }
        }

        fun initializeTestDoneStatus() {
            _isPhoriaTestDone.update { false }
            _isAniseikoniaTestDone.update { false }
            _isPresbyopiaTestDone.update { false }
            _isShortVisualAcuityTestDone.update { false }
            _isAmslerGridTestDone.update { false }
            _isMChartTestDone.update { false }
            _isBloodPressureTestDone.update { false }
            _isGripStrengthTestDone.update { false }
            _isPulmonaryFunctionTestDone.update { false }
            _isPhoriaTestDone.update { false }
            _isAniseikoniaTestDone.update { false }
        }

        fun checkIsTestDone(testType: InspectionType): Boolean {
            when (testType) {
                InspectionType.Phoria -> {
                    return _isPhoriaTestDone.value
                }

                InspectionType.Aniseikonia -> {
                    return _isAniseikoniaTestDone.value
                }

                InspectionType.Presbyopia -> {
                    return _isPresbyopiaTestDone.value
                }

                InspectionType.ShortDistanceVisualAcuity -> {
                    return _isShortVisualAcuityTestDone.value
                }

                InspectionType.AmslerGrid -> {
                    return _isAmslerGridTestDone.value
                }

                InspectionType.MChart -> {
                    return _isMChartTestDone.value
                }

                InspectionType.BloodPressure -> {
                    return _isBloodPressureTestDone.value
                }

                InspectionType.GripStrength -> {
                    return _isGripStrengthTestDone.value
                }

                else -> {
                    return false
                }
            }
        }

        var presbyopiaInspectionResult = PresbyopiaInspectionResult()
        var shortVisualAcuityTestResult = ShortVisualAcuityTestResult()
        var longVisualAcuityTestResult = LongVisualAcuityTestResult()
        var childrenVisualAcuityTestResult = ChildrenVisualAcuityTestResult()
        var amslerGridTestResult = AmslerGridTestResult()
        var mChartTestResult = MChartTestResult()
        var dementiaTestResult = DementiaInspectionResult(scores = List(14) { DementiaViewModel.DementiaAnswer.None })
        var presbyopiaExerciseResult = PresbyopiaExerciseResult()
        var concentrationExerciseResult = ConcentrationExerciseResult()
        var bloodPressureTestResult = BloodPressureInspectionResult(systolic = 0, diastolic = 0, pulseRate = 0)
        var gripStrengthTestResult = GripStrengthInspectionResultContract(leftGrip = 0.0, rightGrip = 0.0)

        init {
            checkBackgroundStatus()
            exoPlayer = ExoPlayer.Builder(getApplication()).build()
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
            exoPlayer.volume = 0f
        }
    }
