package com.pixelro.nenoonkiosk.feature.permission

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun PermissionRoute(
    viewModel: PermissionViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value
    val context = LocalContext.current

    // Write Settings 런처
    val writeSettingPermissionRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val granted = Settings.System.canWrite(context)
        viewModel.updatePermissionResult(
            context.getString(if (granted) R.string.allow_system else R.string.not_allow_system)
        )
    }

    // 다중 권한 런처
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }
        val messageRes = when {
            result.keys.contains(android.Manifest.permission.BLUETOOTH_SCAN) -> {
                if (allGranted) R.string.allow_bluetooth else R.string.not_allow_bluetooth
            }
            result.keys.contains(android.Manifest.permission.CAMERA) -> {
                if (allGranted) R.string.allow_camera else R.string.not_allow_camera
            }
            else -> R.string.confirm
        }
        viewModel.updatePermissionResult(context.getString(messageRes))
    }

    // 블루투스 활성화 런처
    val bluetoothServiceRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        val granted = activityResult.resultCode == Activity.RESULT_OK
        viewModel.updatePermissionResult(
            if (granted) "블루투스가 활성화되었습니다" else "블루투스 활성화를 거부했습니다"
        )
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PermissionSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            is PermissionSideEffect.RequestWriteSettings -> {
                val intent = Intent(
                    Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    "package:${sideEffect.packageName}".toUri()
                )
                writeSettingPermissionRequestLauncher.launch(intent)
            }
            is PermissionSideEffect.RequestCameraPermission -> {
                permissionRequestLauncher.launch(sideEffect.permissions)
            }
            is PermissionSideEffect.RequestBluetoothPermissions -> {
                permissionRequestLauncher.launch(sideEffect.permissions)
            }
            is PermissionSideEffect.RequestBluetoothEnable -> {
                bluetoothServiceRequestLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }
    }

    PermissionScreen(
        state = state,
        onPermissionItemClick = { viewModel.onPermissionItemClick(it.type) },
        onConfirmClick = { viewModel.onConfirmClick() },
        onDismissDialog = { viewModel.dismissDialog() }
    )
}
