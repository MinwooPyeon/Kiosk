package com.pixelro.nenoonkiosk.core.receiver

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.pixelro.nenoonkiosk.feature.main.MainActivity

class NenoonDeviceAdminReceiver : DeviceAdminReceiver() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onEnabled(
        context: Context,
        intent: Intent,
    ) {
        super.onEnabled(context, intent)
        Log.d("DeviceAdmin", "Device Admin enabled")
        Toast.makeText(context, "Device Admin enabled", Toast.LENGTH_SHORT).show()

        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponentName = ComponentName(context, NenoonDeviceAdminReceiver::class.java)

        if (dpm.isDeviceOwnerApp(context.packageName)) {
            Log.d("DeviceAdmin", "Applying initial Kiosk Policies for Device Owner.")
            dpm.setLockTaskPackages(adminComponentName, arrayOf(context.packageName))

            val featuresToEnable =
                DevicePolicyManager.LOCK_TASK_FEATURE_NONE
                    .or(DevicePolicyManager.LOCK_TASK_FEATURE_SYSTEM_INFO)

            dpm.setLockTaskFeatures(adminComponentName, featuresToEnable)

            dpm.setKeyguardDisabled(adminComponentName, true)

            dpm.setScreenCaptureDisabled(adminComponentName, true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onDisabled(
        context: Context,
        intent: Intent,
    ) {
        super.onDisabled(context, intent)
        Log.d("DeviceAdmin", "Device Admin disabled")
        Toast.makeText(context, "Device Admin disabled", Toast.LENGTH_SHORT).show()

        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponentName = ComponentName(context, NenoonDeviceAdminReceiver::class.java)

        if (dpm.isDeviceOwnerApp(context.packageName)) {
            Log.d("DeviceAdmin", "Removing Kiosk Policies for Device Owner.")
            dpm.setLockTaskPackages(adminComponentName, emptyArray())

            dpm.setLockTaskFeatures(
                adminComponentName,
                DevicePolicyManager.LOCK_TASK_FEATURE_HOME or
                    DevicePolicyManager.LOCK_TASK_FEATURE_OVERVIEW or
                    DevicePolicyManager.LOCK_TASK_FEATURE_NOTIFICATIONS or
                    DevicePolicyManager.LOCK_TASK_FEATURE_SYSTEM_INFO or
                    DevicePolicyManager.LOCK_TASK_FEATURE_GLOBAL_ACTIONS,
            )

            dpm.setKeyguardDisabled(adminComponentName, false)

            dpm.setScreenCaptureDisabled(adminComponentName, false)
        }
    }

    override fun onLockTaskModeEntering(
        context: Context,
        intent: Intent,
        pkg: String,
    ) {
        super.onLockTaskModeEntering(context, intent, pkg)
        Log.d("DeviceAdmin", "Entering lock task mode for: $pkg")
        Toast.makeText(context, "Kiosk Mode Activated", Toast.LENGTH_SHORT).show()

        val window = (context as? MainActivity)?.window
        window?.let {
            WindowCompat.getInsetsController(it, it.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }

    override fun onLockTaskModeExiting(
        context: Context,
        intent: Intent,
    ) {
        super.onLockTaskModeExiting(context, intent)
        Log.d("DeviceAdmin", "Exiting lock task mode")
        Toast.makeText(context, "Kiosk Mode Deactivated", Toast.LENGTH_SHORT).show()

        val window = (context as? MainActivity)?.window
        window?.let {
            WindowCompat.getInsetsController(it, it.decorView).apply {
                show(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }

    override fun onProfileProvisioningComplete(
        context: Context,
        intent: Intent,
    ) {
        super.onProfileProvisioningComplete(context, intent)
        Log.d("DeviceAdmin", "Profile provisioning complete!")
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(launchIntent)

        Toast.makeText(context, "Device Owner Provisioning Complete! Launching Kiosk.", Toast.LENGTH_LONG).show()
    }
}
