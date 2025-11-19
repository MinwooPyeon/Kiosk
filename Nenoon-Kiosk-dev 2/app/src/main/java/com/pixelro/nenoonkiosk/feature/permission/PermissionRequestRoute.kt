package com.pixelro.nenoonkiosk.feature.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.main.NenoonViewModel

@SuppressLint("ServiceCast")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun PermissionRequestRoute(
    viewModel: NenoonViewModel,
    toLoginScreen: () -> Unit
) {
    val context = LocalContext.current

    // 권한 배열
    val bluetoothPermissions = remember {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }
    val cameraPermissions = remember { arrayOf(Manifest.permission.CAMERA) }

    // 작성 권한(WRITE_SETTINGS) 런처
    val writeSettingPermissionRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.System.canWrite(context)) {
                Toast.makeText(
                    context,
                    R.string.allow_system,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    R.string.not_allow_system,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // 다중 권한 런처
    val permissionRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            // Bluetooth 권한 처리
            if (result.keys.contains(Manifest.permission.BLUETOOTH_SCAN)) {
                val scanGranted = result[Manifest.permission.BLUETOOTH_SCAN] == true
                val connectGranted = result[Manifest.permission.BLUETOOTH_CONNECT] == true
                if (scanGranted && connectGranted) {
                    Toast.makeText(
                        context,
                        R.string.allow_bluetooth,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                       R.string.not_allow_bluetooth,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            // Camera 권한 처리
            if (result.keys.contains(Manifest.permission.CAMERA)) {
                if (result[Manifest.permission.CAMERA] == true) {
                    Toast.makeText(
                        context,
                        R.string.allow_camera,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        R.string.not_allow_camera,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    val bluetoothServiceRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                Log.d("bluetoothServiceRequest", "bluetooth service accepted")
            } else {
                Log.d("bluetoothServiceRequest", "bluetooth service denied")
            }
        }
    // VM State 수집
    val isWriteGranted by viewModel.isWriteSettingsPermissionGranted.collectAsState()
    val isCameraGranted by viewModel.isCameraPermissionGranted.collectAsState()
    val isBtPermsGranted by viewModel.isBluetoothPermissionsGranted.collectAsState()
    val isBtOn by viewModel.isBlueToothOn.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val state = PermissionRequestState(
        isWriteSettingsGranted = isWriteGranted,
        isCameraGranted = isCameraGranted,
        isBluetoothPermsGranted = isBtPermsGranted,
        isBluetoothOn = isBtOn,
        showRequireAllDialog = showDialog
    )

    // 항목 리스트 (onClick 은 이 래퍼에서만 수행)
    val items = remember(isWriteGranted, isCameraGranted, isBtPermsGranted, isBtOn) {
        listOf(
            PermissionItemUi(
                title = "시스템 설정 변경",
                description = "화면 보호기 영상 재생에 이용",
                iconRes = R.drawable.icon_settings,
                checked = isWriteGranted,
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        "package:${context.packageName}".toUri()
                    )
                    writeSettingPermissionRequestLauncher.launch(intent)
                }
            ),
            PermissionItemUi(
                title = "카메라 권한",
                description = "거리 측정을 위해 이용",
                iconRes = R.drawable.icon_camera,
                checked = isCameraGranted,
                onClick = {
                    permissionRequestLauncher.launch(cameraPermissions)
                }
            ),
            PermissionItemUi(
                title = "블루투스 권한",
                description = "프린터 연결을 위한 용도",
                iconRes = R.drawable.icon_bluetooth,
                checked = isBtPermsGranted,
                onClick = {
                    permissionRequestLauncher.launch(bluetoothPermissions)
                }
            ),
            PermissionItemUi(
                title = "블루투스 서비스",
                description = "프린터를 연결하기 위한 블루투스 확인 용도",
                iconRes = R.drawable.icon_bluetoothon,
                checked = isBtOn,
                onClick = {
                    val bluetoothAdapter =
                        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
                    if (
                        ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_SCAN
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionRequestLauncher.launch(bluetoothPermissions)
                        return@PermissionItemUi
                    }
                    if (bluetoothAdapter?.isEnabled == true) {
                        // 이미 ON이면 무시하거나 토스트
                    } else {
                        bluetoothServiceRequestLauncher.launch(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        )
                    }
                }
            )
        )
    }

    fun handleConfirm() {
        val allGranted =
            state.isWriteSettingsGranted &&
                    state.isCameraGranted &&
                    state.isBluetoothPermsGranted &&
                    state.isBluetoothOn

        if (allGranted) {
            toLoginScreen()
        } else {
            showDialog = true
        }
    }

    PermissionRequestScreen(
        state = state,
        items = items,
        onConfirmClick = ::handleConfirm,
        onDismissDialog = { showDialog = false }
    )
}