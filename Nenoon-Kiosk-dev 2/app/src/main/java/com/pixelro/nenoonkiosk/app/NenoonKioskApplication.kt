package com.pixelro.nenoonkiosk.app

import android.app.Application
import android.content.Context
import com.pixelro.nenoonkiosk.core.manager.PrinterManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NenoonKioskApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the PrinterManager when the app starts
        PrinterManager.initialize(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        // Disconnect the printer when the app is terminated
        PrinterManager.disconnectPrinter()
    }
    init {
        instance = this
    }

    companion object {
        lateinit var instance: NenoonKioskApplication
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
}