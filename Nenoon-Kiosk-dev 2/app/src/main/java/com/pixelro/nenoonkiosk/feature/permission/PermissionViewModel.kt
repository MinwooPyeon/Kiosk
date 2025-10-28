package com.pixelro.nenoonkiosk.feature.permission

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    application: Application,
    private val navigator: Navigator
) : AndroidViewModel(application), ContainerHost<PermissionState, PermissionSideEffect> {

    override val container: Container<PermissionState, PermissionSideEffect> =
        container(PermissionState())

    init {
        checkAllPermissions()
    }

    fun checkAllPermissions() = intent {
        val context = getApplication<Application>()

        val isWriteSettingsGranted = Settings.System.canWrite(context)

        val isCameraGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val isBluetoothScanGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        val isBluetoothConnectGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        val isBluetoothPermsGranted = isBluetoothScanGranted && isBluetoothConnectGranted

        val bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        val isBluetoothOn = bluetoothAdapter.isEnabled

        val items = listOf(
            PermissionItem(
                title = "시스템 설정 변경",
                description = "화면 보호기 영상 재생에 이용",
                iconRes = R.drawable.icon_settings,
                type = PermissionType.WRITE_SETTINGS,
                checked = isWriteSettingsGranted
            ),
            PermissionItem(
                title = "카메라 권한",
                description = "거리 측정을 위해 이용",
                iconRes = R.drawable.icon_camera,
                type = PermissionType.CAMERA,
                checked = isCameraGranted
            ),
            PermissionItem(
                title = "블루투스 권한",
                description = "프린터 연결을 위한 용도",
                iconRes = R.drawable.icon_bluetooth,
                type = PermissionType.BLUETOOTH_PERMISSIONS,
                checked = isBluetoothPermsGranted
            ),
            PermissionItem(
                title = "블루투스 서비스",
                description = "프린터를 연결하기 위한 블루투스 확인 용도",
                iconRes = R.drawable.icon_bluetoothon,
                type = PermissionType.BLUETOOTH_SERVICE,
                checked = isBluetoothOn
            )
        )

        reduce {
            state.copy(
                isWriteSettingsGranted = isWriteSettingsGranted,
                isCameraGranted = isCameraGranted,
                isBluetoothPermsGranted = isBluetoothPermsGranted,
                isBluetoothOn = isBluetoothOn,
                permissionItems = items
            )
        }
    }

    fun onPermissionItemClick(type: PermissionType) = intent {
        when (type) {
            PermissionType.WRITE_SETTINGS -> {
                postSideEffect(
                    PermissionSideEffect.RequestWriteSettings(
                        getApplication<Application>().packageName
                    )
                )
            }
            PermissionType.CAMERA -> {
                postSideEffect(
                    PermissionSideEffect.RequestCameraPermission(
                        arrayOf(Manifest.permission.CAMERA)
                    )
                )
            }
            PermissionType.BLUETOOTH_PERMISSIONS -> {
                postSideEffect(
                    PermissionSideEffect.RequestBluetoothPermissions(
                        arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                        )
                    )
                )
            }
            PermissionType.BLUETOOTH_SERVICE -> {
                if (state.isBluetoothOn) {
                    postSideEffect(PermissionSideEffect.ShowToast("블루투스가 이미 켜져 있습니다"))
                } else if (!state.isBluetoothPermsGranted) {
                    postSideEffect(
                        PermissionSideEffect.RequestBluetoothPermissions(
                            arrayOf(
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT
                            )
                        )
                    )
                } else {
                    postSideEffect(PermissionSideEffect.RequestBluetoothEnable)
                }
            }
        }
    }

    fun onConfirmClick() = intent {
        val allGranted = state.isWriteSettingsGranted &&
                state.isCameraGranted &&
                state.isBluetoothPermsGranted &&
                state.isBluetoothOn

        if (allGranted) {
            navigator.navigate(SignInRoute.LocationSignIn)
        } else {
            reduce {
                state.copy(showRequireAllDialog = true)
            }
        }
    }

    fun dismissDialog() = intent {
        reduce {
            state.copy(showRequireAllDialog = false)
        }
    }

    fun updatePermissionResult(granted: Boolean, message: String) = intent {
        postSideEffect(PermissionSideEffect.ShowToast(message))
        checkAllPermissions()
    }
}
