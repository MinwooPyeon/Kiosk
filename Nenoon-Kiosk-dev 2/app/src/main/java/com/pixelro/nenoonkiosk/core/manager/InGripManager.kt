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
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.util.UUID

object InGripManager {
    private const val TAG = "DynamometerManager"
    private const val SCAN_DURATION = 15000L

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val WRITE_CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    private val READ_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var readCharacteristic: BluetoothGattCharacteristic? = null

    private val _connectionState =
        MutableStateFlow<BluetoothConnectionState>(
            BluetoothConnectionState.DISCONNECTED,
        )
    val connectionState: StateFlow<BluetoothConnectionState> = _connectionState

    private val _dataReceived = MutableStateFlow<Double?>(null)
    val dataReceived: StateFlow<Double?> = _dataReceived

    private lateinit var appContext: Context

    private val _availableDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val availableDevices: StateFlow<List<BluetoothDevice>> = _availableDevices

    private var isScanning = false
    private var device: BluetoothDevice? = null // store connected device

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    fun init(context: Context) {
        appContext = context.applicationContext
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager?.adapter
        _isInitialized.value = true
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (isScanning || bluetoothAdapter == null) return
        _availableDevices.value = emptyList() // clear previous results
        Log.d(TAG, "Starting Bluetooth LE scan for InBodyHGS devices.")
        isScanning = true
        val deviceListener =
            object : BluetoothAdapter.LeScanCallback {
                @SuppressLint("MissingPermission")
                override fun onLeScan(
                    device: BluetoothDevice,
                    rssi: Int,
                    scanRecord: ByteArray?,
                ) {
                    if (InGripManager.device?.address == device.address) {
                        return
                    }
                    
                    if (device.name?.startsWith("InBodyHGS") == true && !_availableDevices.value.contains(device)) {
                        val connectionState = bluetoothManager?.getConnectionState(device, BluetoothProfile.GATT)
                        val isAlreadyConnected = connectionState == BluetoothProfile.STATE_CONNECTED || 
                                                 connectionState == BluetoothProfile.STATE_CONNECTING
                        
                        if (!isAlreadyConnected) {
                            Log.d(TAG, "Found InBodyHGS device: ${device.name} - ${device.address}")
                            _availableDevices.value = _availableDevices.value + device
                        } else {
                            Log.d(TAG, "Skipping already connected device: ${device.name} - ${device.address} (state: $connectionState)")
                        }
                    }
                }
            }
        bluetoothAdapter?.startLeScan(deviceListener)
        
        managerScope.launch {
            while (isScanning) {
                delay(200)
                if (isScanning) {
                    _availableDevices.value = _availableDevices.value.filter { scanDevice ->
                        if (InGripManager.device?.address == scanDevice.address) {
                            return@filter false
                        }
                        val state = bluetoothManager?.getConnectionState(scanDevice, BluetoothProfile.GATT) ?: BluetoothProfile.STATE_DISCONNECTED
                        val isConnected = state == BluetoothProfile.STATE_CONNECTED || state == BluetoothProfile.STATE_CONNECTING
                        if (isConnected) {
                            Log.d(TAG, "Filtering out connected device during scan: ${scanDevice.name} - ${scanDevice.address} (state: $state)")
                        }
                        !isConnected
                    }
                }
            }
        }
        
        managerScope.launch {
            delay(SCAN_DURATION)
            stopScan(deviceListener)
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan(callback: BluetoothAdapter.LeScanCallback) {
        if (!isScanning || bluetoothAdapter == null) return
        Log.d(TAG, "Stopping Bluetooth LE scan.")
        bluetoothAdapter?.stopLeScan(callback)
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
        InGripManager.device = device
        bluetoothGatt = device.connectGatt(appContext, false, gattCallback)
    }

    private val gattCallback =
        object : BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            override fun onConnectionStateChange(
                gatt: BluetoothGatt?,
                status: Int,
                newState: Int,
            ) {
                super.onConnectionStateChange(gatt, status, newState)
                Log.d(TAG, "onConnectionStateChange: newState=$newState, status=$status")
                managerScope.launch {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            if (status == BluetoothGatt.GATT_SUCCESS) {
                                Log.d(TAG, "Connected to GATT server successfully.")
                                _connectionState.value = BluetoothConnectionState.CONNECTED
                                // 연결 디바이스 리스트 삭제
                                device?.let { connectedDevice ->
                                    _availableDevices.value = _availableDevices.value.filter { it.address != connectedDevice.address }
                                }
                                gatt?.discoverServices()
                            } else {
                                Log.e(TAG, "Connection failed with status: $status")
                                _connectionState.value = BluetoothConnectionState.ERROR("Connection failed with status: $status")
                                closeGatt()
                                device = null
                            }
                        }

                        BluetoothProfile.STATE_DISCONNECTED -> {
                            Log.d(TAG, "Disconnected from GATT server. Status: $status")
                            _connectionState.value = BluetoothConnectionState.DISCONNECTED
                            closeGatt()
                            device = null
                        }

                        else -> {
                            Log.w(TAG, "Connection state changed with status $status")
                            _connectionState.value =
                                BluetoothConnectionState.ERROR("GATT connection error with status $status")
                            // 연결 실패 시 리스트에 다시 추가 
                            device?.let { failedDevice ->
                                if (!_availableDevices.value.any { it.address == failedDevice.address }) {
                                    _availableDevices.value = _availableDevices.value + failedDevice
                                    Log.d(TAG, "Re-added failed device to available list: ${failedDevice.address}")
                                }
                            }
                            closeGatt()
                            device = null
                        }
                    }
                }
            }

            override fun onServicesDiscovered(
                gatt: BluetoothGatt?,
                status: Int,
            ) {
                super.onServicesDiscovered(gatt, status)
                managerScope.launch {
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> {
                            Log.i(TAG, "GATT services discovered")
                            gatt?.getService(SERVICE_UUID)?.let { service ->
                                writeCharacteristic =
                                    service.getCharacteristic(
                                        WRITE_CHARACTERISTIC_UUID,
                                    )
                                readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC_UUID)
                                enableNotifications(readCharacteristic)
                            } ?: run {
                                Log.e(TAG, "Nordic UART Service not found")
                                _connectionState.value =
                                    BluetoothConnectionState.ERROR("Nordic UART Service not found")
                            }
                        }

                        else -> {
                            Log.w(TAG, "GATT service discovery failed with status $status")
                            _connectionState.value =
                                BluetoothConnectionState.ERROR("GATT service discovery failed with status $status")
                            closeGatt()
                        }
                    }
                }
            }

            private fun parseInBodyHGSData(
                receivedBytes: ByteArray?,
                source: String = "unknown",
            ): Double? {
                if (receivedBytes == null || receivedBytes.size < 4) {
                    Log.w(TAG, "Received $source value is null or too short.")
                    return null
                }

                if (receivedBytes[0].toUByte().toInt() != 0x02 ||
                    receivedBytes[receivedBytes.size - 1].toUByte().toInt() != 0x03
                ) {
                    Log.w(TAG, "Malformed InBodyHGS response ($source): Missing STX or ETX.")
                    return null
                }

                val commandByte = receivedBytes[1].toUByte().toInt()
                val dataBytes = receivedBytes.copyOfRange(2, receivedBytes.size - 1)

                if (dataBytes.isEmpty()) {
                    Log.w(TAG, "DATA section is empty for $source.")
                    return null
                }

                when (commandByte) {
                    0x62 -> {
                        val statusByte = dataBytes[0].toUByte().toInt()
                        var measurementResult = ""
                        var parsedGripStrength: Double? = null

                        when (statusByte) {
                            0x30 -> {
                                if (dataBytes.size > 3 && dataBytes[1].toUByte()
                                        .toInt() == 0x1B && dataBytes[dataBytes.size - 1].toUByte()
                                        .toInt() == 0x1B
                                ) {
                                    val gripValueBytes = dataBytes.copyOfRange(2, dataBytes.size - 1)
                                    measurementResult = String(gripValueBytes, StandardCharsets.UTF_8)
                                } else {
                                    Log.w(TAG, "Unexpected format for 'Measuring' status ($source).")
                                    measurementResult = "Unknown (Measuring)"
                                }
                            }

                            0x31 -> {
                                if (dataBytes.size > 3 && dataBytes[1].toUByte()
                                        .toInt() == 0x1B && dataBytes[dataBytes.size - 1].toUByte()
                                        .toInt() == 0x1B
                                ) {
                                    val gripValueBytes = dataBytes.copyOfRange(2, dataBytes.size - 1)
                                    measurementResult = String(gripValueBytes, StandardCharsets.UTF_8)
                                } else {
                                    Log.w(
                                        TAG,
                                        "Unexpected format for 'Measurement completed' status ($source).",
                                    )
                                    measurementResult = "Unknown (Completed)"
                                }
                            }

                            0x32 -> {
                                if (dataBytes.size >= 6 && dataBytes[1].toUByte()
                                        .toInt() == 0x1B && dataBytes[dataBytes.size - 1].toUByte()
                                        .toInt() == 0x1B
                                ) {
                                    val errorCodeBytes = dataBytes.copyOfRange(2, dataBytes.size - 1)
                                    measurementResult =
                                        "Error Code: ${String(errorCodeBytes, StandardCharsets.UTF_8)}"
                                } else {
                                    Log.w(TAG, "Unexpected format for 'Error' status ($source).")
                                    measurementResult = "Unknown (Error)"
                                }
                            }

                            else -> {
                                measurementResult = "N/A"
                            }
                        }

                        val resultString = "Status: ${
                            when (statusByte) {
                                0x30 -> "Measuring"
                                0x31 -> "Measurement completed"
                                0x32 -> "Error"
                                else -> "Unknown Status (0x${statusByte.toString(16).uppercase()})"
                            }
                        }, Result: $measurementResult"
                        Log.d(TAG, "Parsed InBodyHGS data ($source): $resultString")

                        if (statusByte == 0x31 || statusByte == 0x30) {
                            try {
                                val rawGripValue = measurementResult.toDoubleOrNull()
                                if (rawGripValue != null) {
                                    val gripStrengthKg = rawGripValue / 10.0
                                    Log.d(TAG, "Grip Strength ($source): $gripStrengthKg kg")
                                    parsedGripStrength = gripStrengthKg
                                } else {
                                    Log.w(
                                        TAG,
                                        "Could not parse grip strength from $source: $measurementResult",
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing grip strength from $source: ${e.message}")
                            }
                        }
                        return parsedGripStrength
                    }

                    0x60 -> {
                        if (dataBytes.isEmpty()) {
                            Log.w(TAG, "STATUS DATA section is empty for $source.")
                            return null
                        }
                        val batteryLevelByte = dataBytes[0].toUByte().toInt()
                        val batteryStatus: String
                        val batteryValue: Double?

                        when (batteryLevelByte) {
                            0x30 -> {
                                batteryStatus = "Battery needs to be replaced"
                                batteryValue = 0.0
                            }

                            0x31 -> {
                                batteryStatus = "Enough battery"
                                batteryValue = 100.0
                            }

                            else -> {
                                batteryStatus = "Unknown Battery Status (0x${
                                    batteryLevelByte.toString(16).uppercase()
                                })"
                                batteryValue = null
                            }
                        }
                        Log.d(
                            TAG,
                            "Parsed InBodyHGS STATUS data ($source): Battery Level: $batteryStatus",
                        )

                        return batteryValue
                    }

                    else -> {
                        Log.w(
                            TAG,
                            "Unknown command byte received: 0x${
                                commandByte.toString(16).uppercase()
                            } from $source.",
                        )
                        return null
                    }
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic,
                status: Int,
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                managerScope.launch {
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> {
                            val gripStrength = parseInBodyHGSData(characteristic.value, "read")
                            _dataReceived.value = gripStrength
                        }
                        BluetoothGatt.GATT_FAILURE -> {
                            Log.w(TAG, "Characteristic read failed with status $status")
                            _connectionState.value =
                                BluetoothConnectionState.ERROR("Characteristic read failed with status $status")
                        }
                    }
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic,
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                managerScope.launch {
                    val data = parseInBodyHGSData(characteristic.value, "change")
                    _dataReceived.value = data
                }
            }

            override fun onDescriptorWrite(
                gatt: BluetoothGatt?,
                descriptor: BluetoothGattDescriptor?,
                status: Int,
            ) {
                super.onDescriptorWrite(gatt, descriptor, status)
                managerScope.launch {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.d(TAG, "Descriptor write success")
                    } else {
                        Log.e(TAG, "Descriptor write failed, status: $status")
                        _connectionState.value =
                            BluetoothConnectionState.ERROR("Descriptor write failed, status: $status")
                    }
                }
            }
        }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(characteristic: BluetoothGattCharacteristic?) {
        characteristic?.let {
            val descriptor = it.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")) // CCCD
            if (descriptor != null) {
                bluetoothGatt?.setCharacteristicNotification(it, true)
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                bluetoothGatt?.writeDescriptor(descriptor)
            } else {
                Log.e(TAG, "CCCD not found")
                managerScope.launch {
                    _connectionState.value = BluetoothConnectionState.ERROR("CCCD not found")
                }
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
                bluetoothGatt?.writeCharacteristic(characteristic, command, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
                    ?: Log.e(TAG, "GATT writeCharacteristic failed")
            } else {
                characteristic.value = command
                bluetoothGatt?.writeCharacteristic(characteristic) ?: Log.e(TAG, "GATT writeCharacteristic failed")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (bluetoothGatt == null) return
        bluetoothGatt?.disconnect()
        closeGatt()
        device = null
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
        writeCommand(byteArrayOf(0x02, 0x60.toByte(), 0x03)) // STATUS command
    }

    fun sendDeviceSetupCommand(
        unit: Byte,
        sound: Byte,
    ) {
        Log.d(TAG, "Sending SETUP command")
        writeCommand(byteArrayOf(0x02, 0x61.toByte(), unit, 0x1B, sound, 0x1B, 0x03)) // DEVICE SETUP command
    }

    fun sendResultCommand() {
        Log.d(TAG, "Sending RESULT command")
        writeCommand(byteArrayOf(0x02, 0x62.toByte(), 0x03)) // RESULT command
    }

    fun sendInitializeCommand() {
        Log.d(TAG, "Sending INITIALIZE command")
        writeCommand(byteArrayOf(0x02, 0x63.toByte(), 0x03)) // INITIALIZE command
    }

    fun sendPowerOffCommand() {
        Log.d(TAG, "Sending POWER OFF command")
        writeCommand(byteArrayOf(0x02, 0x70.toByte(), 0x03)) // POWER OFF command
    }

    sealed class BluetoothConnectionState {
        object DISCONNECTED : BluetoothConnectionState()

        object CONNECTING : BluetoothConnectionState()

        object CONNECTED : BluetoothConnectionState()

        data class ERROR(val message: String) : BluetoothConnectionState()
    }
}
