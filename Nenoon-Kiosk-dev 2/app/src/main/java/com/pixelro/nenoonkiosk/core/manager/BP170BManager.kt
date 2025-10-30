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
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.util.UUID

object BP170BManager {
    private const val TAG = "com.pixelro.nenoonkiosk.bTManager.BP170B.BP170BManager"
    const val SCAN_DURATION = 60000 // In milliseconds
    private const val STATUS_POLLING_INTERVAL_MS: Long = 1000 // Poll every 1 second

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Nordic UART Service UUIDs
    private val SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val WRITE_CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    private val READ_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") // Client Characteristic Configuration Descriptor

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

    private val _dataReceived =
        MutableStateFlow<String?>(null) // Raw parsed data string (e.g., status messages)
    val dataReceived: StateFlow<String?> = _dataReceived

    // New StateFlow for parsed blood pressure results
    private val _bloodPressureResult = MutableStateFlow<BloodPressureInspectionResult?>(null)
    val bloodPressureResult: StateFlow<BloodPressureInspectionResult?> = _bloodPressureResult

    // New StateFlow to signal test completion via 0xBA command
    private val _testCompletionTrigger = MutableStateFlow(false)

    private lateinit var appContext: Context

    private val _availableDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val availableDevices: StateFlow<List<BluetoothDevice>> = _availableDevices

    private var isScanning = false
    private var device: BluetoothDevice? = null // store connected device

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    private var pollingJob: Job? = null
    private var testCompletionJob: Job? = null // Job for handling 0xBA trigger

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
        Log.d(TAG, "Starting Bluetooth LE scan for BP170/BPBIO250 devices.")
        isScanning = true
        val deviceListener =
            object : BluetoothAdapter.LeScanCallback {
                override fun onLeScan(
                    device: BluetoothDevice,
                    rssi: Int,
                    scanRecord: ByteArray?,
                ) {
                    if (device.name?.startsWith("BP170B") == true && !_availableDevices.value.contains(device)) {
                        Log.d(TAG, "Found BP device: ${device.name} - ${device.address}")
                        managerScope.launch {
                            _availableDevices.value += device
                        }
                    }
                }
            }
        bluetoothAdapter?.startLeScan(deviceListener)
        managerScope.launch {
            delay(SCAN_DURATION.toLong())
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
            Log.e(TAG, "Bluetooth Adapter not found")
            managerScope.launch {
                _connectionState.value = BluetoothConnectionState.ERROR("Bluetooth not supported")
            }
            return
        }
        
        if (bluetoothAdapter?.isEnabled != true) {
            Log.e(TAG, "Bluetooth is not enabled")
            managerScope.launch {
                _connectionState.value = BluetoothConnectionState.ERROR("Bluetooth is not enabled")
            }
            return
        }

        managerScope.launch {
            _connectionState.value = BluetoothConnectionState.CONNECTING
        }
        BP170BManager.device = device
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
                managerScope.launch {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            if (status == BluetoothGatt.GATT_SUCCESS) {
                                Log.d(TAG, "Connected to GATT server successfully.")
                                _connectionState.value = BluetoothConnectionState.CONNECTED
                                gatt?.discoverServices()
                            } else {
                                Log.e(TAG, "Connection failed with status: $status")
                                _connectionState.value = BluetoothConnectionState.ERROR("Connection failed with status: $status")
                                closeGatt()
                                device = null
                            }
                        }

                        BluetoothProfile.STATE_DISCONNECTED -> {
                            Log.d(TAG, "Disconnected from GATT server.")
                            _connectionState.value = BluetoothConnectionState.DISCONNECTED
                            closeGatt()
                            device = null
                            pollingJob?.cancel() // Stop polling when disconnected
                            testCompletionJob?.cancel() // Stop test completion observer
                            _bloodPressureResult.value = null // Clear previous result
                            _testCompletionTrigger.value = false // Reset trigger
                        }

                        else -> {
                            Log.w(TAG, "Connection state changed with status $status")
                            _connectionState.value =
                                BluetoothConnectionState.ERROR("GATT connection error with status $status")
                            closeGatt()
                            device = null
                            pollingJob?.cancel() // Stop polling on error
                            testCompletionJob?.cancel() // Stop test completion observer
                            _bloodPressureResult.value = null // Clear previous result
                            _testCompletionTrigger.value = false // Reset trigger
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
                            Log.i(TAG, "GATT services discovered successfully")
                            gatt?.getService(SERVICE_UUID)?.let { service ->
                                writeCharacteristic =
                                    service.getCharacteristic(
                                        WRITE_CHARACTERISTIC_UUID,
                                    )
                                readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC_UUID)

                                if (writeCharacteristic == null || readCharacteristic == null) {
                                    Log.e(TAG, "Required characteristics not found. Write: $writeCharacteristic, Read: $readCharacteristic")
                                    _connectionState.value = BluetoothConnectionState.ERROR("Required characteristics not found")
                                    closeGatt()
                                    return@launch
                                }
                                
                                enableNotifications(readCharacteristic)

                                // Cancel any existing polling job before starting a new one
                                pollingJob?.cancel()
                                pollingJob =
                                    managerScope.launch {
                                        while (isActive && connectionState.value == BluetoothConnectionState.CONNECTED) {
                                            sendDeviceStatusCheckCommand() // Poll for general status
                                            delay(STATUS_POLLING_INTERVAL_MS)
                                        }
                                    }

                                // Start observing _testCompletionTrigger
                                testCompletionJob?.cancel()
                                testCompletionJob =
                                    managerScope.launch {
                                        _testCompletionTrigger.filter { it }.collect {
                                            Log.d(TAG, "0xBA command received. Sending command to fetch last measured data (0xC4).")
                                            sendLastMeasuredDataCommand()
                                            _testCompletionTrigger.value = false // Reset trigger after sending command
                                        }
                                    }
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

            /**
             * Parses the received byte array according to the BP170/BPBIO250 communication protocol.
             * The protocol defines a frame structure: STX, ID, BOD0, BOD1, CMD0, CMD1, Data, CheckSum, ETX.
             *
             * The function validates the frame structure, calculates the checksum, and interprets the data
             * based on the command bytes (CMD0/CMD1).
             * @param receivedBytes The byte array received from the Bluetooth characteristic.
             * @param source A string indicating the source of the received data (e.g., "read", "change").
             * @return A descriptive string of the parsed data, or null if parsing fails.
             */
            private fun parseBP170Data(
                receivedBytes: ByteArray?,
                source: String = "unknown",
            ): String? {
                if (receivedBytes == null) {
                    Log.w(TAG, "Received $source value is null.")
                    return null
                }
                
                // Handle very short data (4 bytes) - might be direct measurement result
                if (receivedBytes.size == 4) {
                    Log.d(TAG, "Received short data (4 bytes): ${receivedBytes.joinToString(" ") { String.format("%02X", it) }}")
                    // Try to parse as direct measurement result: systolic, diastolic, pulse, checksum
                    val systolic = receivedBytes[0].toUByte().toInt()
                    val diastolic = receivedBytes[1].toUByte().toInt()
                    val pulse = receivedBytes[2].toUByte().toInt()
                    val checksum = receivedBytes[3].toUByte().toInt()
                    
                    // Simple validation
                    if (systolic in 30..300 && diastolic in 30..300 && pulse in 30..240) {
                        _bloodPressureResult.value = BloodPressureInspectionResult(systolic, diastolic, pulse)
                        Log.d(TAG, "Parsed short data as BP result: SBP=$systolic, DBP=$diastolic, Pulse=$pulse")
                        return "Short Data BP Result: SBP=$systolic, DBP=$diastolic, Pulse=$pulse"
                    } else {
                        Log.w(TAG, "Short data values out of range: SBP=$systolic, DBP=$diastolic, Pulse=$pulse")
                        return "Short Data: Invalid values. SBP=$systolic, DBP=$diastolic, Pulse=$pulse"
                    }
                }
                
                // Minimum frame size: STX(1) + ID(1) + BOD0(1) + BOD1(1) + CMD0(1) + CMD1(1) + CheckSum(1) + ETX(1) = 8 bytes.
                if (receivedBytes.size < 8) {
                    Log.w(TAG, "Received $source value is too short: ${receivedBytes.size} bytes. Raw: ${receivedBytes.joinToString(" ") { String.format("%02X", it) }}")
                    return null
                }

                // Validate STX (0x02) and ID ('B' or 0x42)
                if (receivedBytes[0].toUByte().toInt() != 0x02 ||
                    receivedBytes[1].toUByte().toInt() != 0x42
                ) {
                    Log.w(TAG, "Malformed BP170 response ($source): Missing STX or ID.")
                    return null
                }
                
                // Check for ETX (0x03) - be more flexible with position
                val etxPosition = receivedBytes.indexOfLast { it.toUByte().toInt() == 0x03 }
                if (etxPosition == -1) {
                    Log.w(TAG, "Malformed BP170 response ($source): Missing ETX. Raw: ${receivedBytes.joinToString(" ") { String.format("%02X", it) }}")
                    // Try to parse anyway for 0xBA commands which might have different ETX
                    if (receivedBytes.size >= 6) {
                        val cmd0 = receivedBytes[4].toUByte().toInt()
                        if (cmd0 == 0xBA) {
                            Log.d(TAG, "Attempting to parse 0xBA command without proper ETX")
                            return parseBACommand(receivedBytes, source)
                        }
                    }
                    return null
                }

                val bod0 = receivedBytes[2].toUByte().toInt()
                val bod1 = receivedBytes[3].toUByte().toInt()
                val cmd0 = receivedBytes[4].toUByte().toInt()
                val cmd1 = receivedBytes[5].toUByte().toInt()
                // Data bytes are between CMD1 and CheckSum.
                val dataBytes = receivedBytes.copyOfRange(6, receivedBytes.size - 2)
                val receivedChecksum = receivedBytes[receivedBytes.size - 2].toUByte().toInt()

                // Re-calculate checksum to verify. CheckSum = (ID + Length0 + Length1 + Command0 + Command1 + Data(n)) & 0x3F + 0x0A
                val calculatedChecksumData = byteArrayOf(0x42.toByte(), bod0.toByte(), bod1.toByte(), cmd0.toByte(), cmd1.toByte()) + dataBytes
                var sum: Int = 0
                for (byte in calculatedChecksumData) {
                    sum += byte.toUByte().toInt()
                }
                val expectedChecksum = (sum and 0x3F) + 0x0A

                if (receivedChecksum != expectedChecksum) {
                    Log.w(
                        TAG,
                        "Checksum mismatch for $source. Expected: 0x${expectedChecksum.toString(
                            16,
                        ).uppercase()}, Received: 0x${receivedChecksum.toString(16).uppercase()}. Skipping data parsing.",
                    )
                    return "Checksum mismatch - data may be corrupted"
                }

                // Attempt to decode data bytes as UTF-8 string, trim to remove padding nulls/whitespace.
                val responseDataString =
                    try {
                        String(dataBytes, StandardCharsets.UTF_8).trim()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error decoding data bytes to UTF-8: ${e.message}")
                        dataBytes.joinToString(" ") { String.format("%02X", it) } // Fallback to hex string
                    }

                // Interpret the response based on CMD0.
                return when (cmd0) {
                    0xB0 -> { // Response for APP Device Status check (command 0xC0)
                        when (dataBytes.firstOrNull()?.toUByte()?.toInt()) {
                            0x00 -> "Status: Setting clock"
                            0x01 -> "Status: Checking data stored in M1 (first check)"
                            0x02 -> "Status: Checking data stored in M1 (second check)"
                            0x03 -> "Status: Checking last measured data"
                            0x04 -> "Status: During measurement" // This is the status for "during measurement"
                            0x05 -> "Status: Measurement complete"
                            0x0E -> "Status: Device ready" 
                            0x0F -> "Status: Standby mode" 
                            0x10 -> "Status: Error state"
                            0x11 -> "Status: Calibration mode"
                            0x12 -> "Status: Test mode"
                            0x13 -> "Status: Maintenance mode"
                            0x14 -> "Status: Low battery"
                            0x15 -> "Status: High battery"
                            else -> "Status: Unknown (0x${dataBytes.firstOrNull()?.toUByte()?.toInt()?.toString(16)?.uppercase()})"
                        } + if (responseDataString.isNotEmpty()) ", Raw Data: $responseDataString" else ""
                    }
                    0xB1 -> { // Response for APP Device Error Code Check (command 0xC1)
                        "Error Code: $responseDataString"
                    }
                    0xB2 -> { // Response for APP Device Time Setup (command 0xC2)
                        "Time Setup Response: ${if (dataBytes.firstOrNull()?.toUByte()?.toInt() == 0x00) "Success" else "Failed"}" // Assuming 0x00 indicates success.
                    }
                    0xB4 -> { // Response for APP Device Check Last measured data (command 0xC4)
                        // Data structure for 0xB4: Year(1), Month(1), Day(1), Hour(1), Minute(1), Second(1)
                        // Systolic BP (2 bytes), Diastolic BP (2 bytes), Pulse Rate (2 bytes), Measurement Result Code (1 byte)
                        if (dataBytes.size >= 13) { // 6 bytes for time + 2*3 bytes for BP/Pulse + 1 byte for result code = 13 bytes
                            val systolicLE = dataBytes[6].toUByte().toInt() or (dataBytes[7].toUByte().toInt() shl 8)
                            val diastolicLE = dataBytes[8].toUByte().toInt() or (dataBytes[9].toUByte().toInt() shl 8)
                            val pulseRateLE = dataBytes[10].toUByte().toInt() or (dataBytes[11].toUByte().toInt() shl 8)
                            
                            val systolicBE = (dataBytes[6].toUByte().toInt() shl 8) or dataBytes[7].toUByte().toInt()
                            val diastolicBE = (dataBytes[8].toUByte().toInt() shl 8) or dataBytes[9].toUByte().toInt()
                            val pulseRateBE = (dataBytes[10].toUByte().toInt() shl 8) or dataBytes[11].toUByte().toInt()
                            
                            val measurementResultCode = dataBytes[12].toUByte().toInt()

                            val systolic = if (systolicLE in 30..300) systolicLE else systolicBE
                            val diastolic = if (diastolicLE in 30..300) diastolicLE else diastolicBE
                            val pulseRate = if (pulseRateLE in 30..240) pulseRateLE else pulseRateBE

                            if (systolic in 30..300 && diastolic in 30..300 && pulseRate in 30..240) {
                                _bloodPressureResult.value = BloodPressureInspectionResult(systolic, diastolic, pulseRate)
                                Log.d(
                                    TAG,
                                    "Parsed BP Result from 0xB4: SBP=$systolic, DBP=$diastolic, Pulse=$pulseRate, ResultCode=$measurementResultCode",
                                )
                                "Measurement Data (0xB4): SBP=$systolic, DBP=$diastolic, Pulse=$pulseRate, Result Code: $measurementResultCode"
                            } else {
                                Log.w(TAG, "0xB4 parsed values out of range: SBP=$systolic, DBP=$diastolic, Pulse=$pulseRate")
                                "Measurement Data (0xB4): Invalid values. SBP=$systolic, DBP=$diastolic, Pulse=$pulseRate, Result Code: $measurementResultCode"
                            }
                        } else {
                            Log.w(TAG, "0xB4 response data too short: ${dataBytes.size} bytes. Expected at least 13.")
                            "Measurement Data (0xB4): Insufficient data. Raw: ${dataBytes.joinToString(" ") { String.format("%02X", it) }}"
                        }
                    }
                    0xB5 -> { // Response for APP Device Serial Number Request (command 0xC5)
                        "Serial Number: $responseDataString"
                    }
                    0xBA -> { // New: Response when cmd0 is 0xBA (assuming it means test is over AND carries data)
                        Log.d(TAG, "Received CMD0 0xBA. Attempting to decode data as single bytes with offset.")
                        // User specified: 7th byte (index 6) is systolic, 8th (index 7) is diastolic, 9th (index 8) is pulse.
                        // Each value should be subtracted by 10 after converting to decimal.
                        if (dataBytes.size >= 9) { // At least 6 bytes for time + 3 bytes for SBP, DBP, Pulse
                            val systolicRaw = dataBytes[6].toUByte().toInt()
                            val diastolicRaw = dataBytes[7].toUByte().toInt()
                            val pulseRateRaw = dataBytes[8].toUByte().toInt()

                            val systolic = systolicRaw - 10
                            val diastolic = diastolicRaw - 10
                            val pulseRate = pulseRateRaw - 10

                            if (systolic in 30..300 && diastolic in 30..300 && pulseRate in 30..240) {
                                _bloodPressureResult.value = BloodPressureInspectionResult(systolic, diastolic, pulseRate)
                                Log.d(
                                    TAG,
                                    "Parsed BP Result from 0xBA: SBP=$systolic (raw $systolicRaw), DBP=$diastolic (raw $diastolicRaw), Pulse=$pulseRate (raw $pulseRateRaw)",
                                )

                                managerScope.launch {
                                    _testCompletionTrigger.value = true // Set trigger to true to also send C4
                                }
                                "Test Over (CMD0: 0xBA), Data Decoded: SBP=$systolic, DBP=$diastolic, Pulse=$pulseRate. Also triggering C4 command."
                            } else {
                                Log.w(TAG, "0xBA parsed values out of range: SBP=$systolic (raw $systolicRaw), DBP=$diastolic (raw $diastolicRaw), Pulse=$pulseRate (raw $pulseRateRaw)")
                                // Still trigger C4 command to get more reliable data
                                managerScope.launch {
                                    _testCompletionTrigger.value = true
                                }
                                "Test Over (CMD0: 0xBA), Data: Invalid values. SBP=$systolic, DBP=$diastolic, Pulse=$pulseRate. Triggering C4 command."
                            }
                        } else {
                            Log.w(TAG, "0xBA response data too short for BP results: ${dataBytes.size} bytes. Expected at least 9.")
                            managerScope.launch {
                                _testCompletionTrigger.value = true // Still trigger C4 even if 0xBA data is short
                            }
                            "Test Over (CMD0: 0xBA), Data: Insufficient data for BP results. Raw: ${dataBytes.joinToString(
                                " ",
                            ) { String.format("%02X", it) }}. Triggering C4 command."
                        }
                    }
                    // Add more command responses as needed from the protocol document
                    else -> {
                        "Unknown Command (CMD0: 0x${cmd0.toString(
                            16,
                        ).uppercase()}, CMD1: 0x${cmd1.toString(16).uppercase()}), Data: $responseDataString"
                    }
                }
            }
            
            private fun parseBACommand(receivedBytes: ByteArray, source: String): String? {
                Log.d(TAG, "Parsing 0xBA command: ${receivedBytes.joinToString(" ") { String.format("%02X", it) }}")
                
                if (receivedBytes.size >= 9) {
                    // Try to find blood pressure data in the byte array
                    // Look for reasonable values in different positions
                    for (i in 6 until receivedBytes.size - 1) {
                        val systolicRaw = receivedBytes[i].toUByte().toInt()
                        val diastolicRaw = receivedBytes[i + 1].toUByte().toInt()
                        val pulseRaw = receivedBytes[i + 2].toUByte().toInt()
                        
                        val systolic = systolicRaw - 10
                        val diastolic = diastolicRaw - 10
                        val pulse = pulseRaw - 10
                        
                        if (systolic in 30..300 && diastolic in 30..300 && pulse in 30..240) {
                            _bloodPressureResult.value = BloodPressureInspectionResult(systolic, diastolic, pulse)
                            Log.d(TAG, "Parsed 0xBA BP Result: SBP=$systolic (raw $systolicRaw), DBP=$diastolic (raw $diastolicRaw), Pulse=$pulse (raw $pulseRaw)")
                            
                            managerScope.launch {
                                _testCompletionTrigger.value = true
                            }
                            return "0xBA BP Result: SBP=$systolic, DBP=$diastolic, Pulse=$pulse"
                        }
                    }
                }
                
                // If no valid BP data found, still trigger C4 command
                managerScope.launch {
                    _testCompletionTrigger.value = true
                }
                return "0xBA command received but no valid BP data found"
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
                            val parsedData = parseBP170Data(characteristic.value, "read")
                            _dataReceived.value = parsedData
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
                    val rawBytes = characteristic.value
                    Log.d(TAG, "Characteristic changed - Raw bytes: ${rawBytes?.joinToString(" ") { String.format("%02X", it) }}")
                    val data = parseBP170Data(rawBytes, "change")
                    _dataReceived.value = data
                    Log.d(TAG, "Parsed data: $data")
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
            val descriptor = it.getDescriptor(CCCD_UUID)
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

    /**
     * Constructs a BP170/BPBIO250 command byte array based on the specified protocol.
     * The frame structure is STX, ID, BOD0, BOD1, CMD0, CMD1, Data, CheckSum, ETX.
     *
     * @param cmd0 The first command byte.
     * @param cmd1 The second command byte.
     * @param data The optional data payload for the command.
     * @return A ByteArray representing the complete command frame.
     */
    private fun createBP170Command(
        cmd0: Byte,
        cmd1: Byte,
        data: ByteArray = byteArrayOf(),
    ): ByteArray {
        val id: Byte = 0x42 // 'B'
        val commandAndDataLength = 2 + data.size // CMD0, CMD1 (2 bytes) + Data bytes (n bytes)

        // Length0 = (Number of data to transfer, including command & 0x3F) + 0x0A
        val length0 = ((commandAndDataLength and 0x3F) + 0x0A).toByte()
        // Length1 = ((Number of data to transfer, including command >> 6) & 0x3F) + 0x0A
        val length1 = (((commandAndDataLength shr 6) and 0x3F) + 0x0A).toByte()

        // Payload for checksum calculation: ID + Length0 + Length1 + Command0 + Command1 + Data(n)
        val payloadForChecksum = byteArrayOf(id, length0, length1, cmd0, cmd1) + data

        var checksumSum = 0
        for (byte in payloadForChecksum) {
            checksumSum += byte.toUByte().toInt()
        }
        // CheckSum = (sum of payload bytes & 0x3F) + 0x0A
        val checksum = ((checksumSum and 0x3F) + 0x0A).toByte()

        // Final command frame: STX + ID + BOD0 + BOD1 + CMD0 + CMD1 + Data + CheckSum + ETX
        return byteArrayOf(0x02, id, length0, length1, cmd0, cmd1) + data + checksum + 0x03
    }

    @SuppressLint("MissingPermission")
    private fun writeCommand(command: ByteArray) {
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
        _connectionState.value = BluetoothConnectionState.DISCONNECTED
        pollingJob?.cancel() // Stop polling
        testCompletionJob?.cancel() // Stop test completion observer
        _bloodPressureResult.value = null // Clear previous result
        _testCompletionTrigger.value = false // Reset trigger
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt() {
        bluetoothGatt?.close()
        bluetoothGatt = null
        writeCharacteristic = null
        readCharacteristic = null
    }

    /**
     * Sends the APP Device Status Check command (CMD0: 0xC0, CMD1: 0x00).
     * This command checks the current status of the device (e.g., setting clock, during measurement).
     */
    fun sendDeviceStatusCheckCommand() {
        Log.d(TAG, "Sending APP Device Status Check command (0xC0)")
        writeCommand(createBP170Command(0xC0.toByte(), 0x00.toByte()))
    }

    /**
     * Sends the APP Device Error Code Check command (CMD0: 0xC1, CMD1: 0x00).
     * This command requests the current error code from the device.
     */
    fun sendErrorCodeCheckCommand() {
        Log.d(TAG, "Sending APP Device Error Code Check command (0xC1)")
        writeCommand(createBP170Command(0xC1.toByte(), 0x00.toByte()))
    }

    /**
     * Sends the APP Device Time Setup command (CMD0: 0xC2, CMD1: 0x00).
     * This command sets the current time on the device.
     * The data payload should be 6 bytes: Year, Month, Day, Hour, Minute, Second.
     */
    fun sendTimeSetupCommand(
        year: Byte,
        month: Byte,
        day: Byte,
        hour: Byte,
        minute: Byte,
        second: Byte,
    ) {
        Log.d(TAG, "Sending APP Device Time Setup command (0xC2)")
        val timeData = byteArrayOf(year, month, day, hour, minute, second)
        writeCommand(createBP170Command(0xC2.toByte(), 0x00.toByte(), timeData))
    }

    /**
     * Sends the APP Device Check Last measured data command (CMD0: 0xC4, CMD1: 0x00).
     * This command requests the last measured blood pressure data from the device.
     */
    fun sendLastMeasuredDataCommand() {
        Log.d(TAG, "Sending APP Device Check Last measured data command (0xC4)")
        writeCommand(createBP170Command(0xC4.toByte(), 0x00.toByte()))
    }

    /**
     * Sends the APP Device Serial Number Request command (CMD0: 0xC5, CMD1: 0x00).
     * This command requests the serial number of the device.
     */
    fun sendSerialNumberRequestCommand() {
        Log.d(TAG, "Sending APP Device Serial Number Request command (0xC5)")
        writeCommand(createBP170Command(0xC5.toByte(), 0x00.toByte()))
    }

    sealed class BluetoothConnectionState {
        object DISCONNECTED : BluetoothConnectionState()

        object CONNECTING : BluetoothConnectionState()

        object CONNECTED : BluetoothConnectionState()

        data class ERROR(val message: String) : BluetoothConnectionState()
    }
}
