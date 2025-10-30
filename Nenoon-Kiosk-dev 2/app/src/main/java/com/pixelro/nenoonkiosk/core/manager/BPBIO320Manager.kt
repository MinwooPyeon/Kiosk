package com.pixelro.nenoonkiosk.core.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.inbody.bpbio.IB_BleManager
import com.inbody.bpbio.IB_SDKConst
import com.pixelro.nenoonkiosk.feature.inspection.bloodPressure.result.BloodPressureInspectionResultContract
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

    private val _bloodPressureResult = MutableStateFlow<BloodPressureInspectionResultContract?>(null)
    val bloodPressureResult = _bloodPressureResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _testInProgress = MutableStateFlow(false)
    val testInProgress = _testInProgress.asStateFlow()

    private val _isLastResultComplete = MutableStateFlow(false)
    val isLastResultComplete = _isLastResultComplete.asStateFlow()

    private val _batteryLevel = MutableStateFlow<Int?>(null)
    val batteryLevel = _batteryLevel.asStateFlow()

    private var scanRetryCount = 0
    private val maxScanRetries = 3
    private var isScanning = false

    private val callbackInBodyBLEManager =
        object : IB_BleManager.BLECallback {
            override fun CallbackInitSDK(retVal: JSONObject) {
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
                    val isSuccess = retVal.getInt("IsSuccess")
                    val deviceNameFromCallback = retVal.getString("Log")
                    val errorCode = retVal.optInt("ErrorCode", 0)
                    val detailLog = retVal.optString("DetailLog", "")
                    
                    isScanning = false
                    
                    if (isSuccess == 1) {
                        _deviceName.value = deviceNameFromCallback
                        scanRetryCount = 0 // Reset retry count on success
                    } else {
                        // Handle specific error codes
                        when (errorCode) {
                            -4 -> { // No device found
                                if (scanRetryCount < maxScanRetries) {
                                    scanRetryCount++
                                    _errorMessage.value = "디바이스를 찾을 수 없습니다. 재시도 중... ($scanRetryCount/$maxScanRetries)"
                                    
                                    // Retry after a short delay
                                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                        retryDeviceSelection()
                                    }, 2000)
                                } else {
                                    _errorMessage.value = "BPBIO320 디바이스를 찾을 수 없습니다. 디바이스가 켜져있고 가까운 거리에 있는지 확인해주세요."
                                }
                            }
                            else -> {
                                _errorMessage.value = "디바이스 선택 실패: $detailLog (에러 코드: $errorCode)"
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    _errorMessage.value = "JSON Error in CallbackSelectDevice: ${e.message}"
                }
            }

            override fun CallbackRemoveDevice(retVal: JSONObject) {
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
                                _errorMessage.value = "Connection failed. Error Code: $errorCode - $detailLog"
                            } else {
                                _errorMessage.value = null
                            }
                        }
                        else -> {
                            if (errorCode != IB_SDKConst.NONE) {
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
                try {
                    val jsonDataString = retVal.optString("JsonData", "")
                    if (jsonDataString.isNotBlank()) {
                        val jsonObj = JSONObject(jsonDataString)
                        if (_deviceName.value.contains("BSM")) {
                            val height = jsonObj.optDouble("Height")
                            val weight = jsonObj.optDouble("Weight")
                            _errorMessage.value = "BSM Sync: Height: $height, Weight: $weight"
                        }
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
                try {
                    val log = retVal.get("Log").toString()
                    val jsonDataString = retVal.optString("JsonData", "")
                    if (jsonDataString.isNotBlank()) {
                        val jsonObj = JSONObject(jsonDataString)
                        if (jsonObj.optInt("IsComplete", IB_SDKConst.FAIL) == IB_SDKConst.SUCCESS) {
                            inBodyBLEManager.SetSyncWithCallback()
                        } else {
                            _errorMessage.value = "Time setup failed: $log"
                        }
                    } else {
                        _errorMessage.value = "Time setup failed: No JsonData found or empty."
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    _errorMessage.value = "Error setting time: ${e.message}"
                }
            }

            override fun CallbackRemoveData(retVal: JSONObject) {
                try {
                    val jsonDataString = retVal.optString("JsonData", "")
                    if (jsonDataString.isNotBlank()) {
                        val jsonObj = JSONObject(jsonDataString)
                        _errorMessage.value = "Data Removed: $jsonObj"
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    _errorMessage.value = "Error removing data: ${e.message}"
                }
            }

            @Override
            override fun CallbackStartTest(retVal: JSONObject) {
                try {
                    val isSuccess = retVal.optInt("IsSuccess", IB_SDKConst.FAIL)
                    val errorCode = retVal.optInt("ErrorCode", IB_SDKConst.NONE)
                    val detailLog = retVal.optString("DetailLog", "No DetailLog provided.")

                    if (isSuccess == IB_SDKConst.SUCCESS) {
                        val jsonDataString = retVal.optString("JsonData", "")

                        if (jsonDataString.isNotBlank()) {
                            val jsonObj = JSONObject(jsonDataString)
                            val sbp = jsonObj.optInt("SBP", 0)
                            val dbp = jsonObj.optInt("DBP", 0)
                            val hr = jsonObj.optInt("HR", 0)
                            val sdkIsComplete = jsonObj.optInt("IsComplete", 0) == IB_SDKConst.SUCCESS

                            _bloodPressureResult.value = BloodPressureInspectionResultContract(sbp, dbp, hr)
                            _isLastResultComplete.value = sdkIsComplete

                            if (sdkIsComplete) {
                                _testInProgress.value = false
                                _errorMessage.value = null
                            } else {
                                _testInProgress.value = true
                                _errorMessage.value = null
                            }
                        } else {
                            _testInProgress.value = true
                            _bloodPressureResult.value = BloodPressureInspectionResultContract(0, 0, 0)
                            _isLastResultComplete.value = false
                            _errorMessage.value = null
                        }
                    } else {
                        _testInProgress.value = false
                        _isLastResultComplete.value = false
                        resetMeasurementState()
                        _errorMessage.value = "Measurement Error: Code $errorCode - $detailLog"
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    _errorMessage.value = "JSON Error parsing test data: ${e.message}"
                    _testInProgress.value = false
                    _isLastResultComplete.value = false
                } catch (e: Exception) {
                    e.printStackTrace()
                    _errorMessage.value = "Unexpected error during test data processing: ${e.message}"
                    _testInProgress.value = false
                    _isLastResultComplete.value = false
                }
            }

            override fun CallbackGetDeviceInfo(retVal: JSONObject) {
                try {
                    val jsonDataString = retVal.optString("JsonData", "")
                    if (jsonDataString.isNotBlank()) {
                        val jsonObj = JSONObject(jsonDataString)
                        if (jsonObj.has("BatteryLevel")) {
                            val battery = jsonObj.optInt("BatteryLevel")
                            _batteryLevel.value = battery
                        }
                    }
                } catch (e: JSONException) {
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
        } else {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot initialize SDK."
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
    }

    fun selectDevice() {
        val name = "BPBIO320"
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot select device."
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            return
        }
        
        if (isScanning) {
            return
        }
        
        scanRetryCount = 0
        isScanning = true
        _errorMessage.value = "BPBIO320 디바이스를 검색 중입니다..."
        
        inBodyBLEManager.SelectDeviceWithCallback(name, false)
        resetMeasurementState()
        _batteryLevel.value = null
    }
    
    private fun retryDeviceSelection() {
        if (isScanning) {
            return
        }
        
        val name = "BPBIO320"
        isScanning = true
        _errorMessage.value = "BPBIO320 디바이스를 재검색 중입니다... ($scanRetryCount/$maxScanRetries)"
        
        inBodyBLEManager.SelectDeviceWithCallback(name, false)
    }

    fun connectDisconnect() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot connect/disconnect."
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            return
        }
        inBodyBLEManager.ConnectDisconnectWithCallback()
    }

    fun removeDevice() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot remove device."
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            return
        }
        inBodyBLEManager.RemoveDeviceWithCallback()
    }

    fun resetTest() {
        resetMeasurementState()
    }

    fun removeData() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot remove data."
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            return
        }
        inBodyBLEManager.RemoveDataWithCallback()
    }

    fun startMeasurement() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot start measurement."
            _testInProgress.value = false
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            _testInProgress.value = false
            return
        }
        if (_connectionState.value == IB_SDKConst.CONNECTED) {
            inBodyBLEManager.StartTestWithCallback()
        } else {
            _errorMessage.value = "Device not connected. Please connect before starting a test."
            _testInProgress.value = false
        }
    }

    fun setSync() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot set sync."
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            return
        }
        inBodyBLEManager.SetSyncWithCallback()
    }

    fun setTime() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot set time."
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            return
        }
        inBodyBLEManager.SetTimeWithCallback()
    }

    fun getDeviceInfo() {
        if (!hasBluetoothPermissions()) {
            _errorMessage.value = "Bluetooth permissions are not granted. Cannot get device info."
            return
        }
        if (!inBodyBLEManager.CheckBluetoothState()) {
            _errorMessage.value = "Bluetooth is not enabled. Please enable Bluetooth."
            return
        }
        if (_connectionState.value == IB_SDKConst.CONNECTED) {
            inBodyBLEManager.GetDeviceInfoWithCallback()
        } else {
            _errorMessage.value = "Cannot get device info: not connected."
        }
    }

    fun getConnectionState() {
        // Get current SDK connection state
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }
    
    fun cancelDeviceSelection() {
        if (isScanning) {
            isScanning = false
            scanRetryCount = 0
            _errorMessage.value = "디바이스 검색이 취소되었습니다."
        }
    }
    
    fun resetScanState() {
        isScanning = false
        scanRetryCount = 0
        _errorMessage.value = null
    }

    companion object {
        private const val TAG = "BloodPressureManager"
    }
}
