package com.pixelro.nenoonkiosk.feature.permission

import androidx.annotation.DrawableRes

data class PermissionRequestState(
    val isWriteSettingsGranted: Boolean,
    val isCameraGranted: Boolean,
    val isBluetoothPermsGranted: Boolean,
    val isBluetoothOn: Boolean,
    val showRequireAllDialog: Boolean = false
)

data class PermissionItemUi(
    val title: String,
    val description: String,
    @DrawableRes val iconRes: Int,
    val checked: Boolean,
    val onClick: () -> Unit
)