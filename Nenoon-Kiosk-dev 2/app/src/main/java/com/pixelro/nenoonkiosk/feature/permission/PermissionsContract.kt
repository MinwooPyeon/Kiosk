package com.pixelro.nenoonkiosk.feature.permission

import androidx.annotation.DrawableRes

data class PermissionState(
    val isWriteSettingsGranted: Boolean = false,
    val isCameraGranted: Boolean = false,
    val isBluetoothPermsGranted: Boolean = false,
    val isBluetoothOn: Boolean = false,
    val showRequireAllDialog: Boolean = false,
    val permissionItems: List<PermissionItem> = emptyList()
)

data class PermissionItem(
    val title: String,
    val description: String,
    @DrawableRes val iconRes: Int,
    val type: PermissionType,
    val checked: Boolean
)

enum class PermissionType {
    WRITE_SETTINGS,
    CAMERA,
    BLUETOOTH_PERMISSIONS,
    BLUETOOTH_SERVICE
}

sealed interface PermissionSideEffect {
    data class ShowToast(val message: String) : PermissionSideEffect
    data class RequestWriteSettings(val packageName: String) : PermissionSideEffect
    data class RequestCameraPermission(val permissions: Array<String>) : PermissionSideEffect
    data class RequestBluetoothPermissions(val permissions: Array<String>) : PermissionSideEffect
    data object RequestBluetoothEnable : PermissionSideEffect
}
