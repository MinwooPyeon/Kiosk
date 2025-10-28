package com.pixelro.nenoonkiosk.feature.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.util.SizeF
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.manager.PrinterManager
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.receiver.NenoonDeviceAdminReceiver
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel: NenoonViewModel by lazy {
        ViewModelProvider(this)[NenoonViewModel::class.java]
    }

    private lateinit var dpm: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName

    private var tapCount = 0
    private var lastTapTime: Long = 0
    private val TAP_THRESHOLD_MS = 3000L
    private val REQUIRED_TAPS = 10
    private val TAP_AREA_SIZE_DP = 50

    private var showPasswordDialog by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        viewModel.resetScreenSaverTimer()

        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                val currentTime = System.currentTimeMillis()
                val dpToPx = resources.displayMetrics.density

                val tapAreaPx = TAP_AREA_SIZE_DP * dpToPx

                if (it.x <= tapAreaPx && it.y <= tapAreaPx) {
                    if (currentTime - lastTapTime < TAP_THRESHOLD_MS) {
                        tapCount++
                        Log.d("KioskExit", "Tap count: $tapCount")
                        if (tapCount >= REQUIRED_TAPS) {
                            Log.d("KioskExit", "Hidden shutdown sequence activated! Showing password dialog.")
                            showPasswordDialog = true
                            tapCount = 0
                            lastTapTime = 0
                        }
                    } else {
                        tapCount = 1
                    }
                    lastTapTime = currentTime
                } else {
                    tapCount = 0
                    lastTapTime = 0
                }
            }
        }

        return super.onTouchEvent(event)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val ADMIN_PASSWORD = AppConstants.KIOSK_MODE_EXIT_PASSWORD
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        viewModel.updateToResumed()
        viewModel.resetScreenSaverTimer()

        dpm = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = ComponentName(this, NenoonDeviceAdminReceiver::class.java)

        if (dpm.isDeviceOwnerApp(packageName)) {
            setKioskModePolicies(true)
            startLockTask()
        } else if (dpm.isLockTaskPermitted(packageName)) {
            startLockTask()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE,
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dpm = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = ComponentName(this, NenoonDeviceAdminReceiver::class.java)

        if (dpm.isDeviceOwnerApp(packageName)) {
            dpm.setLockTaskPackages(adminComponentName, arrayOf(packageName))
            dpm.setLockTaskFeatures(adminComponentName, DevicePolicyManager.LOCK_TASK_FEATURE_NONE)
        }

        checkLocationPermission()
        val locale = SharedPreferencesManager.getString("language")

        if (locale.isBlank()) {
            TTS.initTTS("en")
            viewModel.updateLanguage("en")
        } else {
            TTS.initTTS(locale)
            viewModel.updateLanguage(locale)
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )

        val statusBarResourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        GlobalValue.statusBarPadding = resources.getDimension(statusBarResourceId)
        GlobalValue.navigationBarPadding = 0f

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }

        connectPrinter()

        setContent {
            NenoonKioskTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    val systemUiController = rememberSystemUiController()
                    systemUiController.setStatusBarColor(
                        color = Color(0x00000000),
                    )
                    systemUiController.isNavigationBarVisible = false
                    val context = LocalContext.current
                    val configuration = LocalConfiguration.current
                    LaunchedEffect(true) {
                        val cameraManager =
                            context.getSystemService(CAMERA_SERVICE) as CameraManager
                        val cameraCharacteristics =
                            (context.getSystemService(CAMERA_SERVICE) as CameraManager).getCameraCharacteristics(
                                cameraManager.cameraIdList[if (DebugConstants.EMULATOR_MODE) 0 else 1],
                            )
                        viewModel.updateLocalConfigurationValues(
                            pixelDensity = context.resources.displayMetrics.density,
                            screenWidthDp = configuration.screenWidthDp,
                            screenHeightDp = configuration.screenHeightDp,
                            focalLength =
                                cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                                    ?.get(0) ?: 0f,
                            lensSize =
                                cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
                                    ?: SizeF(0f, 0f),
                        )
                    }
                    val sharedPreferences =
                        getSharedPreferences(
                            NavConstants.PREFERENCE_NAME,
                            MODE_PRIVATE,
                        )

                    nenoonApp()

                    if (showPasswordDialog) {
                        PasswordDialog(
                            onDismiss = { showPasswordDialog = false },
                            onPasswordEntered = { password ->
                                if (password == ADMIN_PASSWORD) {
                                    Toast.makeText(context, "Password correct! Shutting down application...", Toast.LENGTH_SHORT).show()
                                    showPasswordDialog = false

                                    Process.killProcess(Process.myPid())
                                } else {
                                    Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    private fun connectPrinter() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED -> {
                PrinterManager.startBluetoothScan(this)
            }
            else -> {
                requestPermissionsLauncher.launch(
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT),
                )
            }
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            if (permissions[Manifest.permission.BLUETOOTH_SCAN] == true &&
                permissions[Manifest.permission.BLUETOOTH_CONNECT] == true
            ) {
                PrinterManager.startBluetoothScan(this)
            } else {
                Log.e("MainActivity", "Bluetooth permissions were denied.")
            }
        }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onPause() {
        super.onPause()
        viewModel.updateToPaused()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBackPressed() {
        if (dpm.isDeviceOwnerApp(packageName) && dpm.getLockTaskFeatures(adminComponentName) == DevicePolicyManager.LOCK_TASK_FEATURE_NONE) {
            viewModel.resetScreenSaverTimer()
            Log.d("MainActivity", "Back button pressed in restricted kiosk mode.")
        } else {
            viewModel.resetScreenSaverTimer()
            super.onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onDestroy() {
        TTS.tts.stop()
        TTS.destroyTTS()
        viewModel.exoPlayer.release()
        PrinterManager.disconnectPrinter()
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setKioskModePolicies(enable: Boolean) {
        if (!dpm.isDeviceOwnerApp(packageName)) {
            Log.w("KioskMode", "Not a Device Owner app. Cannot apply full kiosk policies.")
            return
        }

        val features =
            if (enable) {
                DevicePolicyManager.LOCK_TASK_FEATURE_NONE
            } else {
                DevicePolicyManager.LOCK_TASK_FEATURE_HOME or
                    DevicePolicyManager.LOCK_TASK_FEATURE_OVERVIEW or
                    DevicePolicyManager.LOCK_TASK_FEATURE_NOTIFICATIONS or
                    DevicePolicyManager.LOCK_TASK_FEATURE_SYSTEM_INFO or
                    DevicePolicyManager.LOCK_TASK_FEATURE_GLOBAL_ACTIONS
            }
        dpm.setLockTaskFeatures(adminComponentName, features)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dpm.setStatusBarDisabled(adminComponentName, enable)
        }

        dpm.setKeyguardDisabled(adminComponentName, enable)
        dpm.setScreenCaptureDisabled(adminComponentName, enable)
    }
}

@Composable
fun PasswordDialog(
    onDismiss: () -> Unit,
    onPasswordEntered: (String) -> Unit,
) {
    var passwordInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("키오스크 모드 해제")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = {
                        Text(
                            StringProvider.getString(
                                R.string.password,
                            ),
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors =
                        TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = colorResource(R.color.main),
                            focusedLabelColor = colorResource(R.color.main),
                            cursorColor = colorResource(R.color.main),
                            backgroundColor = Color.White,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        buttons = {
            Row(
                modifier =
                    Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    onClick = {
                        onPasswordEntered(passwordInput)
                        passwordInput = ""
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(R.color.error),
                            contentColor = Color.White,
                        ),
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(60.dp),
                ) {
                    Text(StringProvider.getString(R.string.enter))
                }
                Spacer(modifier = Modifier.width(24.dp))
                Button(
                    onClick = onDismiss,
                    colors =
                        ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(R.color.gray1),
                        ),
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(60.dp),
                ) {
                    Text(StringProvider.getString(R.string.cancel))
                }
            }
        },
        properties = DialogProperties(dismissOnClickOutside = false),
    )
}
