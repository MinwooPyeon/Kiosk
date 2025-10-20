package com.pixelro.nenoonkiosk.core.manager

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.mangoslab.nemonicsdk.INPrinterControllerCallback
import com.mangoslab.nemonicsdk.INPrinterScanControllerCallback
import com.mangoslab.nemonicsdk.NPrinter
import com.mangoslab.nemonicsdk.NPrinterController
import com.mangoslab.nemonicsdk.NPrinterScanController
import com.mangoslab.nemonicsdk.constants.NPrinterType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object PrinterManager {
    private var nPrinterController: NPrinterController? = null
    private var printerScanController: NPrinterScanController? = null
    private var printerType: NPrinterType? = null
    private val _printerName = MutableStateFlow("")
    private val _printerMacAddress = MutableStateFlow("")

    // Initialize PrinterManager with the application context
    fun initialize(application: Application) {
        Log.d("PrinterManager", "Initializing PrinterManager...")
        setupBluetoothPrinter(application)
    }

    // 업데이트 프린터 정보
    private fun updatePrinter(name: String, address: String) {
        _printerName.update { name }
        _printerMacAddress.update { address }
        Log.d("PrinterManager", "Printer updated: Name: $name, Address: $address")
    }

    // Get printer information
    fun getPrinterInfo(): Pair<NPrinterType?, String> {
        return printerType to _printerMacAddress.value
    }

    // Get printer controller
    fun getPrinterController(): NPrinterController? {
        return nPrinterController
    }

    // Setup Bluetooth printer connection
    private fun setupBluetoothPrinter(context: Context) {
        val bluetoothManager = ContextCompat.getSystemService(context, BluetoothManager::class.java) as BluetoothManager

        printerScanController = NPrinterScanController(context, object : INPrinterScanControllerCallback {
            override fun deviceFound(printer: NPrinter) {
                Log.d("PrinterManager", "Device found callback triggered.")
                val deviceName = printer.getName()
                val deviceHardwareAddress = printer.getMacAddress()
                val type = printer.getType()
                Log.d("PrinterManager", "Device found: $deviceName, $deviceHardwareAddress, Type: $type")

                if (deviceHardwareAddress != null && deviceName != null && deviceHardwareAddress.contains("74:F0:7D")) {
                    updatePrinter(deviceName, deviceHardwareAddress)
                    printerType = type
                    Log.d("PrinterManager", "Printer matched: $type, $deviceName, $deviceHardwareAddress")

                    nPrinterController = NPrinterController(context, object : INPrinterControllerCallback {
                        override fun disconnected() {
                            Log.d("PrinterManager", "Printer disconnected")
                        }

                        override fun printProgress(index: Int, total: Int, result: Int) {
                            Log.d("PrinterManager", "Print progress: $index/$total, result: $result")
                        }

                        override fun printComplete(result: Int) {
                            Log.d("PrinterManager", "Print complete, result: $result")
                        }
                    })

                    val printer = NPrinter(type, deviceName, deviceHardwareAddress)
                    Log.d("PrinterManager", "Connecting to printer: $deviceHardwareAddress")
                    nPrinterController?.connect(printer)
                } else {
                    Log.d("PrinterManager", "Printer not matched: $deviceName, $deviceHardwareAddress")
                }
            }
        })

        // Bluetooth 권한 확인 및 스캔 시작
        startBluetoothScan(context)
    }

    // Start scanning for Bluetooth printers
    fun startBluetoothScan(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            printerScanController?.startScan()
            Log.d("PrinterManager", "Bluetooth scan started")
        } else {
            Log.e("PrinterManager", "Bluetooth permissions are not granted.")
        }
    }

    // Disconnect the printer when not needed
    fun disconnectPrinter() {
        nPrinterController?.disconnect()
        Log.d("PrinterManager", "Printer disconnected on app exit.")
    }
}
