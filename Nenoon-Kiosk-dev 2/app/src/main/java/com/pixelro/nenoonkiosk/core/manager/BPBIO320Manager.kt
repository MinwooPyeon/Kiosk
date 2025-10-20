package com.pixelro.nenoonkiosk.core.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.inbody.bpbio.IB_BleManager
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.BloodPressureTestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONException
import org.json.JSONObject

class BPBIO320Manager(private val context: Context) {

    private val inBodyBLEManager = IB_BleManager.getInstance()

    private val _connectionState = MutableStateFlow(IB_SDKConst.IDLE)
    val connectionState = _connectionState.asStateFlow()

    private val _deviceName = MutableStateFlow("N/A")
    val deviceName = _deviceName.asStateFlow()

    private val _bloodPressureResult = MutableStateFlow<BloodPressureTestResult?>(null)
    val bloodPressureResult = _bloodPressureResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _testInProgress = MutableStateFlow(false)
    val testInProgress = _testInProgress.asStateFlow()

    private val _isLastResultComplete = MutableStateFlow(false)
    val isLastResultComplete = _isLastResultComplete.asStateFlow()

    private val _batteryLevel = MutableStateFlow<Int?>(null)
    val batteryLevel = _batteryLevel.asStateFlow()

    private val callbackInBodyBLEManager = object : IB_BleManager.BLECallback {
        override fun CallbackInitSDK(retVal: JSONObject) {
            Log.d(TAG, "CallbackInitSDK was called: $retVal")
            try {
                val isSuccess = retVal.getInt("IsSuccess")
                if (isSuccess == 0) {
                    _errorMessage.value = retVal.getString("DetailLog")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                _errorMessage.value = "JSON Error in CallbackInitSDK: ${e.message}"
            }
        }

        override fun CallbackSelectDevice(retVal: JSONObject) {
            try {
                Log.d(TAG, "CallbackSelectDevice was called : $retVal")
                val isSuccess = retVal.getInt("IsSuccess")
                val deviceNameFromCallback = retVal.getString("Log")
                if (isSuccess == 1) {
                    _deviceName.value = deviceNameFromCallback
                } else {
                    _errorMessage.value = retVal.getString("DetailLog")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                _errorMessage.value = "JSON Error in CallbackSelectDevice: ${e.message}"
            }
        }

        override fun CallbackRemoveDevice(retVal: JSONObject) {
            Log.d(TAG, "CallbackRemoveDevice was called : $retVal")
            try {
                val detailLog = retVal.get("DetailLog").toString()
                _errorMessage.value = detailLog
                _connectionState.value = IB_SDKConst.IDLE
                _deviceName.value = "N/A"
                _batteryLevel.value = null
                resetMeasurementState()
            } catch (e: JSONException) {
                e.printStackTrace()
                _errorMessage.value = "JSON Error in CallbackRemoveDevice: ${e.message}"
            }
        }

        override fun CallbackConnectDisconnect(retVal: JSONObject) {
            try {
                val state = retVal.getInt("BleState")
                val log = retVal.get("Log").toString()
                val detailLog = retVal.get("DetailLog").toString()
                val errorCode = retVal.getInt("ErrorCode")

                _connectionState.value = state
                _deviceName.value = log

                when (state) {
                    IB_SDKConst.CONNECTED -> {
                        _errorMessage.value = null
                        resetMeasurementState()
                        _batteryLevel.value = null
                        Log.d(TAG, "Device connected: $log")

                        if (!IB_SDKConst.isANDPtotocol(_deviceName.value)) {
                            if (_deviceName.value.contains("BSM")) {
                                inBodyBLEManager.SetSyncWithCallback()
                            } else {
                                inBodyBLEManager.SetTimeWithCallback()
                            }
                        }
                        getDeviceInfo()
                    }
                    IB_SDKConst.IDLE, IB_SDKConst.STOPSCANN, IB_SDKConst.DISCONNECTED -> {
                        resetMeasurementState()
                        _batteryLevel.value = null
                        if (errorCode != IB_SDKConst.NONE) {
                            Log.e(TAG, "Connection Failed with ErrorCode: $errorCode, Detail: $detailLog")
                            _errorMessage.value = "Connection failed. Error Code: $errorCode - $detailLog"
                        } else {
                            _errorMessage.value = null
                        }
                    }
                    else -> {
                        if (errorCode != IB_SDKConst.NONE) {
                            Log.e(TAG, "Operation Failed with ErrorCode: $errorCode, Detail: $detailLog during state: $state")
                            _errorMessage.value = "Operation failed. Error Code: $errorCode - $detailLog"
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                _errorMessage.value = "JSON Error in CallbackConnectDisconnect: ${e.message}"
            }
        }

        override fun CallbackSetSync(retVal: JSONObject) {
            Log.d(TAG, "CallbackSetSync : $retVal")
            try {
                val jsonDataString = retVal.optString("JsonData", "")
                if (jsonDataString.isNotBlank()) {
                    val jsonObj = JSONObject(jsonDataString)
                    if (_deviceName.value.contains("BSM")) {
                        val height = jsonObj.optDouble("Height")
                        val weight = jsonObj.optDouble("Weight")
                        _errorMessage.value = "BSM Sync: Height: $height, Weight: $weight"
                    } else {
                        Log.d(TAG, "BP Sync completed. Ready for measurement. Raw: $jsonObj")
                    }
                } else {
                    Log.d(TAG, "CallbackSetSync: No JsonData found.")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                _errorMessage.value = "Error parsing sync data: ${e.message}"
            } finally {
                if (_deviceName.value.contains("BSM")) {
                    inBodyBLEManager.RemoveDataWithCallback()
                }
            }
        }

        override fun CallbackTimeSetup(retVal: JSONObject) {
            Log.d(TAG, "CallbackTimeSetup : $retVal")
            try {
                val log = retVal.get("Log").toString()
                val jsonDataString = retVal.optString("JsonData", "")
                if (jsonDataString.isNotBlank()) {
                    val jsonObj = JSONObject(jsonDataString)
                    if (jsonObj.optInt("IsComplete", IB_SDKConst.FAIL) == IB_SDKConst.SUCCESS) {
                        Log.d(TAG, "Time setup complete. Attempting to SetSync.")
                        inBodyBLEManager.SetSyncWithCallback()
                    } else {
                        _errorMessage.value = "Time setup failed: ${log}"
                        Log.e(TAG, "Time setup failed. Log: $log, JsonData: $jsonObj")
                    }
                } else {
                    _errorMessage.value = "Time setup failed: No JsonData found or empty."
                    Log.e(TAG, "CallbackTimeSetup: No JsonData found. Log: $log")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                _errorMessage.value = "Error setting time: ${e.message}"
                Log.e(TAG, "JSON Error in CallbackTimeSetup: ${e.message}, Raw retVal: $retVal")
            }
        }

        override fun CallbackRemoveData(retVal: JSONObject) {
            Log.d(TAG, "CallbackRemoveData : $retVal")
            try {
                val jsonDataString = retVal.optString("JsonData", "")
                if (jsonDataString.isNotBlank()) {
                    val jsonObj = JSONObject(jsonDataString)
                    _errorMessage.value = "Data Removed: ${jsonObj.toString()}"
                    Log.d(TAG, "Data removed successfully: $jsonObj")
                } else {
                    Log.d(TAG, "CallbackRemoveData: No JsonData found.")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                _errorMessage.value = "Error removing data: ${e.message}"
                Log.e(TAG, "JSON Error in CallbackRemoveData: ${e.message}, Raw retVal: $retVal")
            }
        }

        @Override
        override fun CallbackStartTest(retVal: JSONObject) {
            try {
                val isSuccess = retVal.optInt("IsSuccess", IB_SDKConst.FAIL)
                val errorCode = retVal.optInt("ErrorCode", IB_SDKConst.NONE)
                val detailLog = retVal.optString("DetailLog", "No DetailLog provided.")

                Log.d(TAG, "CallbackStartTest - Parsed: IsSuccess=$isSuccess, ErrorCode=$errorCode, DetailLog='$detailLog'")

                if (isSuccess == IB_SDKConst.SUCCESS) {
                    val jsonDataString = retVal.optString("JsonData", "")

                    if (jsonDataString.isNotBlank()) {
                        val jsonObj = JSONObject(jsonDataString)
                        val sbp = jsonObj.optInt("SBP", 0)
                        val dbp = jsonObj.optInt("DBP", 0)
                        val hr = jsonObj.optInt("HR", 0)
                        val sdkIsComplete = jsonObj.optInt("IsComplete", 0) == IB_SDKConst.SUCCESS

                        Log.d(TAG, "CallbackStartTest - JsonData content: SBP=$sbp, DBP=$dbp, HR=$hr, IsComplete (from SDK JsonData)=$sdkIsComplete")
                        Log.d(TAG, "CallbackStartTest - Raw JsonData Object: $jsonObj")

                        _bloodPressureResult.value = BloodPressureTestResult(sbp, dbp, hr)
                        _isLastResultComplete.value = sdkIsComplete

                        if (sdkIsComplete) {
                            _testInProgress.value = false
                            _errorMessage.value = null
                            Log.d(TAG, "CallbackStartTest - FINAL RESULT: Test completed successfully!")
                        } else {
                            _testInProgress.value = true
                            _errorMessage.value = null
                            Log.d(TAG, "CallbackStartTest - MEASUREMENT IN PROGRESS: Received partial data.")
                        }
                    } else {
                        _testInProgress.value = true
                        _bloodPressureResult.value = BloodPressureTestResult(0, 0, 0)
                        _isLastResultComplete.value = false
                        _errorMessage.value = null
                        Log.d(TAG, "CallbackStartTest - INITIAL START SIGNAL: Test started, no JsonData with results yet.")
                    }
                } else {
                    _testInProgress.value = false
                    _isLastResultComplete.value = false
                    resetMeasurementState()
                    _errorMessage.value = "Measurement Error: Code $errorCode - $detailLog"
                    Log.e(TAG, "CallbackStartTest - MEASUREMENT FAILED! ErrorCode: $errorCode, Detail: '$detailLog', Raw RetVal: $retVal")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                _errorMessage.value = "JSON Error parsing test data: ${e.message}"
                _testInProgress.value = false
                _isLastResultComplete.value = false
                Log.e(TAG, "CallbackStartTest - JSON EXCEPTION! Message: ${e.message}, Raw RetVal: $retVal", e)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Unexpected error during test data processing: ${e.message}"
                _testInProgress.value = false
                _isLastResultComplete.value = false
                Log.e(TAG, "CallbackStartTest - UNEXPECTED EXCEPTION! Message: ${e.message}, Raw RetVal: $retVal", e)
            }
        }

        override fun CallbackGetDeviceInfo(retVal: JSONObject) {
            Log.d(TAG, "CallbackGetDeviceInfo : $retVal")
            try {
                val jsonDataString = retVal.optString("JsonData", "")
                if (jsonDataString.isNotBlank()) {
                    val jsonObj = JSONObject(jsonDataString)
                    if (jsonObj.has("BatteryLevel")) {
                        val battery = jsonObj.optInt("BatteryLevel")
                        _batteryLevel.value = battery
                        Log.d(TAG, "Battery Level Received: $battery%")
                    } else {
                        Log.d(TAG, "CallbackGetDeviceInfo: JsonData exists but 'BatteryLevel' key is missing. Raw: $jsonObj")
                    }
                } else {
                    Log.d(TAG, "CallbackGetDeviceInfo: No JsonData found.")
                }
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing battery info in CallbackGetDeviceInfo: ${e.message}, Raw retVal: $retVal", e)
                _errorMessage.value = "Error getting device info: ${e.message}"
            }
        }
    }

    init {
        inBodyBLEManager.SetCallback(callbackInBodyBLEManager)
        // Do NOT call InitSDKWithCallback here directly. Call it via a public function.
    }

    fun initBluetoothSDK() {
        if (hasBluetoothPermissions()) {
            inBodyBLEManager.InitSDKWithCallback("BP170B", false, context)
            Log.d(TAG, "Bluetooth SDK initialized after permissions check.")
        } else {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot initialize SDK."
            Log.e(TAG, "Attempted to initialize SDK without Bluetooth permissions.")
        }
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun resetMeasurementState() {
        _bloodPressureResult.value = null
        _errorMessage.value = null
        _testInProgress.value = false
        _isLastResultComplete.value = false
        Log.d(TAG, "resetMeasurementState() called. All measurement states cleared.")
    }

    fun selectDevice() {
        val name = "BPBIO320"
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot select device."
            Log.e(TAG, "Bluetooth permissions not granted for selectDevice() call.")
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            Log.e(TAG, "Bluetooth not enabled for selectDevice() call.")
            return
        }
        inBodyBLEManager.SelectDeviceWithCallback(name, false)
        resetMeasurementState()
        _batteryLevel.value = null
        Log.d(TAG, "selectDevice() called for: $name")
    }

    fun connectDisconnect() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot connect/disconnect."
            Log.e(TAG, "Bluetooth permissions not granted for connectDisconnect() call.")
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            Log.e(TAG, "Bluetooth not enabled for connectDisconnect() call.")
            return
        }
        Log.d(TAG, "connectDisconnect() called. Current connection state: ${_connectionState.value}")
        inBodyBLEManager.ConnectDisconnectWithCallback()
    }

    fun removeDevice() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot remove device."
            Log.e(TAG, "Bluetooth permissions not granted for removeDevice() call.")
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            Log.e(TAG, "Bluetooth not enabled for removeDevice() call.")
            return
        }
        Log.d(TAG, "removeDevice() called.")
        inBodyBLEManager.RemoveDeviceWithCallback()
    }

    fun resetTest() {
        resetMeasurementState()
        Log.d(TAG, "resetTest() called from ViewModel.")
    }

    fun removeData() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot remove data."
            Log.e(TAG, "Bluetooth permissions not granted for removeData() call.")
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            Log.e(TAG, "Bluetooth not enabled for removeData() call.")
            return
        }
        Log.d(TAG, "removeData() called.")
        inBodyBLEManager.RemoveDataWithCallback()
    }

    fun startMeasurement() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot start measurement."
            _testInProgress.value = false
            Log.e(TAG, "Bluetooth permissions not granted for startMeasurement() call.")
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            _testInProgress.value = false
            Log.e(TAG, "Bluetooth not enabled for startMeasurement() call.")
            return
        }
        Log.d(TAG, "startMeasurement() called.")
        if (_connectionState.value == IB_SDKConst.CONNECTED) {
            Log.d(TAG, "Calling StartTestWithCallback().")
            inBodyBLEManager.StartTestWithCallback()
        } else {
            _errorMessage.value = "Device not connected. Please connect before starting a test."
            _testInProgress.value = false
            Log.e(TAG, "Attempted to start test when not connected. Current state: ${_connectionState.value}")
        }
    }

    fun setSync() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot set sync."
            Log.e(TAG, "Bluetooth permissions not granted for setSync() call.")
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            Log.e(TAG, "Bluetooth not enabled for setSync() call.")
            return
        }
        Log.d(TAG, "setSync() called.")
        inBodyBLEManager.SetSyncWithCallback()
    }

    fun setTime() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot set time."
            Log.e(TAG, "Bluetooth permissions not granted for setTime() call.")
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            Log.e(TAG, "Bluetooth not enabled for setTime() call.")
            return
        }
        Log.d(TAG, "setTime() called.")
        inBodyBLEManager.SetTimeWithCallback()
    }

    fun getDeviceInfo() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot get device info."
            Log.e(TAG, "Bluetooth permissions not granted for getDeviceInfo() call.")
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            Log.e(TAG, "Bluetooth not enabled for getDeviceInfo() call.")
            return
        }
        if (_connectionState.value == IB_SDKConst.CONNECTED) {
            Log.d(TAG, "getDeviceInfo() called.")
            inBodyBLEManager.GetDeviceInfoWithCallback()
        } else {
            Log.w(TAG, "Cannot get device info: not connected. Current state: ${_connectionState.value}")
            _errorMessage.value = "Cannot get device info: not connected."
        }
    }

    fun getConnectionState() {
        Log.d(TAG, "getConnectionState() called. Current SDK connection state: ${inBodyBLEManager.GetConnectionState()}")
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
        Log.d(TAG, "setErrorMessage() called with: '$message'")
    }

    companion object {
        private const val TAG = "BloodPressureManager"
    }
}