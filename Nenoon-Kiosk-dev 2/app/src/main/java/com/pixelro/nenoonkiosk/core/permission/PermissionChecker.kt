package com.pixelro.nenoonkiosk.core.permission

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionChecker @Inject constructor(
    private val application: Application
) {
    @RequiresApi(Build.VERSION_CODES.S)
    fun areAllPermissionsGranted(): Boolean {
        return isWriteSettingsGranted() &&
                isCameraGranted() &&
                isBluetoothScanGranted() &&
                isBluetoothConnectGranted() &&
                isBluetoothEnabled() &&
                (isLocationEnabled() || DebugConstants.EMULATOR_MODE)
    }

    fun isWriteSettingsGranted(): Boolean {
        return Settings.System.canWrite(application)
    }

    fun isCameraGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun isBluetoothScanGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun isBluetoothConnectGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isBluetoothEnabled(): Boolean {
        val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE)
                as? BluetoothManager
        return bluetoothManager?.adapter?.isEnabled == true
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = ContextCompat.getSystemService(
            application,
            LocationManager::class.java
        )
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
    }
}
