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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.permission.components.PermissionDialog
import com.pixelro.nenoonkiosk.feature.permission.components.PermissionItemRow
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

@Composable
fun PermissionScreen(
    state: PermissionState,
    onPermissionItemClick: (PermissionItem) -> Unit,
    onConfirmClick: () -> Unit,
    onDismissDialog: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 40.dp, end = 40.dp)
        ) {
            Spacer(modifier = Modifier.height((GlobalValue.statusBarPadding + 152).dp))

            Text(
                text = "앱 사용을 위해\n접근 권한 허용이 필요해요",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier.padding(top = 80.dp),
                text = "권한이 모두 설정되어있어야 다음 화면으로 넘어갑니다\n체크되지 않은 항목을 선택하면 설정 페이지로 이동합니다",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xff878787)
            )

            Spacer(modifier = Modifier.height(80.dp))

            state.permissionItems.forEach { item ->
                PermissionItemRow(
                    item = item,
                    onClick = { onPermissionItemClick(item) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Button(
            onClick = onConfirmClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(40.dp)
                .height(60.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.main),
                contentColor = colorResource(R.color.white)
            )
        ) {
            Text(
                text = StringProvider.getStringComposable(R.string.confirm),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (state.showRequireAllDialog) {
        PermissionDialog(
            onDismiss = onDismissDialog
        )
    }
}
