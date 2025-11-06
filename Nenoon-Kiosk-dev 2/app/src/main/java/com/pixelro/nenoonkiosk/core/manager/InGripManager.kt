package com.pixelro.nenoonkiosk.core.manager

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.util.UUID

object InGripManager {
    private const val TAG = "InGripManager"
    private const val SCAN_DURATION = 15000L
    private const val SCAN_CHECK_INTERVAL = 200L

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val WRITE_CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    private val READ_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var readCharacteristic: BluetoothGattCharacteristic? = null

    private val _connectionState = MutableStateFlow<BluetoothConnectionState>(BluetoothConnectionState.DISCONNECTED)
    val connectionState: StateFlow<BluetoothConnectionState> = _connectionState.asStateFlow()

    private val _dataReceived = MutableStateFlow<Double?>(null)
    val dataReceived: StateFlow<Double?> = _dataReceived.asStateFlow()

    private lateinit var appContext: Context

    private val _availableDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val availableDevices: StateFlow<List<BluetoothDevice>> = _availableDevices.asStateFlow()

    private var isScanning = false
    private var connectedDevice: BluetoothDevice? = null

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    fun init(context: Context) {
        appContext = context.applicationContext
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        bluetoothAdapter = bluetoothManager?.adapter
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        _isInitialized.value = true
        Log.d(TAG, "InGripManager initialized")
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        val scanner = bluetoothLeScanner
        if (isScanning || scanner == null) {
            Log.w(TAG, "Cannot start scan: isScanning=$isScanning, scanner=${scanner != null}")
            return
        }

        _availableDevices.value = emptyList()
        Log.d(TAG, "Starting Bluetooth LE scan for InBodyHGS devices")
        isScanning = true

        val scanCallback = object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                handleScanResult(result.device)
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e(TAG, "Bluetooth LE scan failed with error code: $errorCode")
                isScanning = false
            }
        }

        scanner.startScan(scanCallback)

        managerScope.launch {
            while (isScanning) {
                delay(SCAN_CHECK_INTERVAL)
                if (isScanning) {
                    filterConnectedDevices()
                }
            }
        }

        managerScope.launch {
            delay(SCAN_DURATION)
            stopScan(scanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleScanResult(device: BluetoothDevice) {
        val deviceName = device.name
        val deviceAddress = device.address

        if (connectedDevice?.address == deviceAddress) {
            return
        }

        if (deviceName?.startsWith("InBodyHGS") == true && !_availableDevices.value.any { it.address == deviceAddress }) {
            val connectionState = bluetoothManager?.getConnectionState(device, BluetoothProfile.GATT)
            val isAlreadyConnected = connectionState == BluetoothProfile.STATE_CONNECTED ||
                    connectionState == BluetoothProfile.STATE_CONNECTING

            if (!isAlreadyConnected) {
                Log.d(TAG, "Found InBodyHGS device: $deviceName - $deviceAddress")
                _availableDevices.value = _availableDevices.value + device
            } else {
                Log.d(TAG, "Skipping already connected device: $deviceName - $deviceAddress (state: $connectionState)")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun filterConnectedDevices() {
        _availableDevices.value = _availableDevices.value.filter { scanDevice ->
            if (connectedDevice?.address == scanDevice.address) {
                return@filter false
            }
            val state = bluetoothManager?.getConnectionState(scanDevice, BluetoothProfile.GATT)
                ?: BluetoothProfile.STATE_DISCONNECTED
            val isConnected = state == BluetoothProfile.STATE_CONNECTED || state == BluetoothProfile.STATE_CONNECTING
            if (isConnected) {
                Log.d(TAG, "Filtering out connected device: ${scanDevice.name} - ${scanDevice.address} (state: $state)")
            }
            !isConnected
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan(callback: ScanCallback) {
        if (!isScanning || bluetoothLeScanner == null) return
        Log.d(TAG, "Stopping Bluetooth LE scan")
        bluetoothLeScanner?.stopScan(callback)
        isScanning = false
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice) {
        Log.d(TAG, "Attempting to connect to device: ${device.name} - ${device.address}")
        if (bluetoothAdapter == null) {
            managerScope.launch {
                _connectionState.value = BluetoothConnectionState.ERROR("Bluetooth not supported")
            }
            return
        }

        _connectionState.value = BluetoothConnectionState.CONNECTING
        _availableDevices.value = _availableDevices.value.filter { it.address != device.address }
        connectedDevice = device
        bluetoothGatt = device.connectGatt(appContext, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.d(TAG, "onConnectionStateChange: newState=$newState, status=$status")
            managerScope.launch {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> handleConnectionSuccess(gatt, status)
                    BluetoothProfile.STATE_DISCONNECTED -> handleDisconnection(status)
                    else -> handleConnectionError(status)
                }
            }
        }

        @SuppressLint("MissingPermission")
        private fun handleConnectionSuccess(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Connected to GATT server successfully")
                _connectionState.value = BluetoothConnectionState.CONNECTED
                connectedDevice?.let { device ->
                    _availableDevices.value = _availableDevices.value.filter { it.address != device.address }
                }
                gatt?.discoverServices()
            } else {
                Log.e(TAG, "Connection failed with status: $status")
                _connectionState.value = BluetoothConnectionState.ERROR("Connection failed with status: $status")
                closeGatt()
                connectedDevice = null
            }
        }

        private fun handleDisconnection(status: Int) {
            Log.d(TAG, "Disconnected from GATT server. Status: $status")
            _connectionState.value = BluetoothConnectionState.DISCONNECTED
            closeGatt()
            connectedDevice = null
        }

        private fun handleConnectionError(status: Int) {
            Log.w(TAG, "Connection state changed with status $status")
            _connectionState.value = BluetoothConnectionState.ERROR("GATT connection error with status $status")
            connectedDevice?.let { failedDevice ->
                if (!_availableDevices.value.any { it.address == failedDevice.address }) {
                    _availableDevices.value = _availableDevices.value + failedDevice
                    Log.d(TAG, "Re-added failed device to available list: ${failedDevice.address}")
                }
            }
            closeGatt()
            connectedDevice = null
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            managerScope.launch {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Log.i(TAG, "GATT services discovered")
                        val service = gatt?.getService(SERVICE_UUID)
                        if (service != null) {
                            writeCharacteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_UUID)
                            readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC_UUID)
                            enableNotifications(readCharacteristic)
                        } else {
                            Log.e(TAG, "Nordic UART Service not found")
                            _connectionState.value = BluetoothConnectionState.ERROR("Nordic UART Service not found")
                        }
                    }
                    else -> {
                        Log.w(TAG, "GATT service discovery failed with status $status")
                        _connectionState.value = BluetoothConnectionState.ERROR("GATT service discovery failed with status $status")
                        closeGatt()
                    }
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            managerScope.launch {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        val gripStrength = parseInBodyHGSData(value, "read")
                        _dataReceived.value = gripStrength
                    }
                    else -> {
                        Log.w(TAG, "Characteristic read failed with status $status")
                        _connectionState.value = BluetoothConnectionState.ERROR("Characteristic read failed with status $status")
                    }
                }
            }
        }

        // API 33+ callback
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            managerScope.launch {
                val data = parseInBodyHGSData(value, "change")
                _dataReceived.value = data
            }
        }

        // Legacy callback for API < 33
        @Deprecated("Deprecated in API 33")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val value = characteristic.value
            managerScope.launch {
                val data = parseInBodyHGSData(value, "change")
                _dataReceived.value = data
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            managerScope.launch {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "Descriptor write success")
                } else {
                    Log.e(TAG, "Descriptor write failed, status: $status")
                    _connectionState.value = BluetoothConnectionState.ERROR("Descriptor write failed, status: $status")
                }
            }
        }
    }

    private fun parseInBodyHGSData(receivedBytes: ByteArray?, source: String): Double? {
        if (receivedBytes == null || receivedBytes.size < 4) {
            Log.w(TAG, "Received $source value is null or too short")
            return null
        }

        if (receivedBytes[0].toUByte().toInt() != 0x02 ||
            receivedBytes[receivedBytes.size - 1].toUByte().toInt() != 0x03
        ) {
            Log.w(TAG, "Malformed InBodyHGS response ($source): Missing STX or ETX")
            return null
        }

        val commandByte = receivedBytes[1].toUByte().toInt()
        val dataBytes = receivedBytes.copyOfRange(2, receivedBytes.size - 1)

        if (dataBytes.isEmpty()) {
            Log.w(TAG, "DATA section is empty for $source")
            return null
        }

        return when (commandByte) {
            0x62 -> parseMeasurementData(dataBytes, source)
            0x60 -> parseBatteryStatus(dataBytes, source)
            else -> {
                Log.w(TAG, "Unknown command byte received: 0x${commandByte.toString(16).uppercase()} from $source")
                null
            }
        }
    }

    private fun parseMeasurementData(dataBytes: ByteArray, source: String): Double? {
        val statusByte = dataBytes[0].toUByte().toInt()
        val measurementResult = extractMeasurementResult(dataBytes, statusByte, source)

        val resultString = "Status: ${getStatusString(statusByte)}, Result: $measurementResult"
        Log.d(TAG, "Parsed InBodyHGS data ($source): $resultString")

        return if (statusByte == 0x31 || statusByte == 0x30) {
            parseGripStrength(measurementResult, source)
        } else {
            null
        }
    }

    private fun extractMeasurementResult(dataBytes: ByteArray, statusByte: Int, source: String): String {
        return when (statusByte) {
            0x30, 0x31 -> {
                if (dataBytes.size > 3 && dataBytes[1].toUByte().toInt() == 0x1B &&
                    dataBytes[dataBytes.size - 1].toUByte().toInt() == 0x1B
                ) {
                    val gripValueBytes = dataBytes.copyOfRange(2, dataBytes.size - 1)
                    String(gripValueBytes, StandardCharsets.UTF_8)
                } else {
                    Log.w(TAG, "Unexpected format for '${getStatusString(statusByte)}' status ($source)")
                    "Unknown (${getStatusString(statusByte)})"
                }
            }
            0x32 -> {
                if (dataBytes.size >= 6 && dataBytes[1].toUByte().toInt() == 0x1B &&
                    dataBytes[dataBytes.size - 1].toUByte().toInt() == 0x1B
                ) {
                    val errorCodeBytes = dataBytes.copyOfRange(2, dataBytes.size - 1)
                    "Error Code: ${String(errorCodeBytes, StandardCharsets.UTF_8)}"
                } else {
                    Log.w(TAG, "Unexpected format for 'Error' status ($source)")
                    "Unknown (Error)"
                }
            }
            else -> "N/A"
        }
    }

    private fun getStatusString(statusByte: Int): String {
        return when (statusByte) {
            0x30 -> "Measuring"
            0x31 -> "Measurement completed"
            0x32 -> "Error"
            else -> "Unknown Status (0x${statusByte.toString(16).uppercase()})"
        }
    }

    private fun parseGripStrength(measurementResult: String, source: String): Double? {
        return try {
            val rawGripValue = measurementResult.toDoubleOrNull()
            if (rawGripValue != null) {
                val gripStrengthKg = rawGripValue / 10.0
                Log.d(TAG, "Grip Strength ($source): $gripStrengthKg kg")
                gripStrengthKg
            } else {
                Log.w(TAG, "Could not parse grip strength from $source: $measurementResult")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing grip strength from $source: ${e.message}")
            null
        }
    }

    private fun parseBatteryStatus(dataBytes: ByteArray, source: String): Double? {
        if (dataBytes.isEmpty()) {
            Log.w(TAG, "STATUS DATA section is empty for $source")
            return null
        }

        val batteryLevelByte = dataBytes[0].toUByte().toInt()
        val (batteryStatus, batteryValue) = when (batteryLevelByte) {
            0x30 -> "Battery needs to be replaced" to 0.0
            0x31 -> "Enough battery" to 100.0
            else -> "Unknown Battery Status (0x${batteryLevelByte.toString(16).uppercase()})" to null
        }

        Log.d(TAG, "Parsed InBodyHGS STATUS data ($source): Battery Level: $batteryStatus")
        return batteryValue
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(characteristic: BluetoothGattCharacteristic?) {
        characteristic ?: return

        val descriptor = characteristic.getDescriptor(CCCD_UUID)
        if (descriptor != null) {
            bluetoothGatt?.setCharacteristicNotification(characteristic, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bluetoothGatt?.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            } else {
                @Suppress("DEPRECATION")
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                @Suppress("DEPRECATION")
                bluetoothGatt?.writeDescriptor(descriptor)
            }
        } else {
            Log.e(TAG, "CCCD not found")
            managerScope.launch {
                _connectionState.value = BluetoothConnectionState.ERROR("CCCD not found")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun writeCommand(command: ByteArray) {
        if (bluetoothGatt == null || writeCharacteristic == null) {
            Log.e(TAG, "Not connected to device")
            managerScope.launch {
                _connectionState.value = BluetoothConnectionState.ERROR("Not connected to device")
            }
            return
        }

        writeCharacteristic?.let { characteristic ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bluetoothGatt?.writeCharacteristic(
                    characteristic,
                    command,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                ) ?: Log.e(TAG, "GATT writeCharacteristic failed")
            } else {
                @Suppress("DEPRECATION")
                characteristic.value = command
                @Suppress("DEPRECATION")
                bluetoothGatt?.writeCharacteristic(characteristic) ?: Log.e(TAG, "GATT writeCharacteristic failed")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt ?: return
        bluetoothGatt?.disconnect()
        closeGatt()
        connectedDevice = null
        _connectionState.value = BluetoothConnectionState.DISCONNECTED
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        writeCharacteristic = null
        readCharacteristic = null
    }

    fun sendStatusCommand() {
        Log.d(TAG, "Sending STATUS command")
        writeCommand(byteArrayOf(0x02, 0x60.toByte(), 0x03))
    }

    fun sendDeviceSetupCommand(unit: Byte, sound: Byte) {
        Log.d(TAG, "Sending SETUP command")
        writeCommand(byteArrayOf(0x02, 0x61.toByte(), unit, 0x1B, sound, 0x1B, 0x03))
    }

    fun sendResultCommand() {
        Log.d(TAG, "Sending RESULT command")
        writeCommand(byteArrayOf(0x02, 0x62.toByte(), 0x03))
    }

    fun sendInitializeCommand() {
        Log.d(TAG, "Sending INITIALIZE command")
        writeCommand(byteArrayOf(0x02, 0x63.toByte(), 0x03))
    }

    fun sendPowerOffCommand() {
        Log.d(TAG, "Sending POWER OFF command")
        writeCommand(byteArrayOf(0x02, 0x70.toByte(), 0x03))
    }

    sealed class BluetoothConnectionState {
        data object DISCONNECTED : BluetoothConnectionState()
        data object CONNECTING : BluetoothConnectionState()
        data object CONNECTED : BluetoothConnectionState()
        data class ERROR(val message: String) : BluetoothConnectionState()
    }
}
