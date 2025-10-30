package com.veepoo.flutter_veepoo_sdk

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.inuker.bluetooth.library.Code
import com.inuker.bluetooth.library.model.BleGattProfile
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.*
import com.veepoo.protocol.listener.data.*
import com.veepoo.protocol.model.datas.*
import com.veepoo.protocol.model.enums.*
import com.veepoo.protocol.model.settings.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** FlutterVeepooSdkPlugin */
class FlutterVeepooSdkPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var context: Context
    private lateinit var methodChannel: MethodChannel
    private lateinit var scanEventChannel: EventChannel
    private lateinit var connectionEventChannel: EventChannel
    private lateinit var heartRateEventChannel: EventChannel
    private lateinit var bloodPressureEventChannel: EventChannel
    private lateinit var bloodOxygenEventChannel: EventChannel
    private lateinit var stepEventChannel: EventChannel
    private lateinit var notificationEventChannel: EventChannel

    private var scanEventSink: EventChannel.EventSink? = null
    private var connectionEventSink: EventChannel.EventSink? = null
    private var heartRateEventSink: EventChannel.EventSink? = null
    private var bloodPressureEventSink: EventChannel.EventSink? = null
    private var bloodOxygenEventSink: EventChannel.EventSink? = null
    private var stepEventSink: EventChannel.EventSink? = null
    private var notificationEventSink: EventChannel.EventSink? = null

    private val vpOperateManager: VPOperateManager by lazy {
        VPOperateManager.getInstance()
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var currentMacAddress: String? = null

    // BLE Scanning improvements
    private val discoveredDevices = mutableMapOf<String, Map<String, Any?>>()
    private var lastScanTime = 0L
    private var scanTimeoutRunnable: Runnable? = null
    private val SCAN_TIMEOUT_MS = 60000L // 60 seconds
    private val SCAN_THROTTLE_MS = 5000L // 5 seconds between scans (for testing, increase to 60000L in production)
    private val RSSI_UPDATE_THRESHOLD = 10 // Only update if RSSI changes by >10 dBm

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext

        // Setup method channel
        methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_veepoo_sdk/methods")
        methodChannel.setMethodCallHandler(this)

        // Setup event channels
        scanEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_veepoo_sdk/scan_events")
        scanEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                scanEventSink = events
            }

            override fun onCancel(arguments: Any?) {
                scanEventSink = null
            }
        })

        connectionEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_veepoo_sdk/connection_events")
        connectionEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                connectionEventSink = events
            }

            override fun onCancel(arguments: Any?) {
                connectionEventSink = null
            }
        })

        heartRateEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_veepoo_sdk/heart_rate_events")
        heartRateEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                heartRateEventSink = events
            }

            override fun onCancel(arguments: Any?) {
                heartRateEventSink = null
            }
        })

        bloodPressureEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_veepoo_sdk/blood_pressure_events")
        bloodPressureEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                bloodPressureEventSink = events
            }

            override fun onCancel(arguments: Any?) {
                bloodPressureEventSink = null
            }
        })

        bloodOxygenEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_veepoo_sdk/blood_oxygen_events")
        bloodOxygenEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                bloodOxygenEventSink = events
            }

            override fun onCancel(arguments: Any?) {
                bloodOxygenEventSink = null
            }
        })

        stepEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_veepoo_sdk/step_events")
        stepEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                stepEventSink = events
            }

            override fun onCancel(arguments: Any?) {
                stepEventSink = null
            }
        })

        notificationEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_veepoo_sdk/notification_events")
        notificationEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                notificationEventSink = events
            }

            override fun onCancel(arguments: Any?) {
                notificationEventSink = null
            }
        })
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        android.util.Log.d("VeepooSDK", "Method called: ${call.method}")
        when (call.method) {
            "initialize" -> initialize(result)
            "startScan" -> startScan(result)
            "stopScan" -> stopScan(result)
            "connect" -> connect(call, result)
            "disconnect" -> disconnect(result)
            "syncPersonInfo" -> syncPersonInfo(call, result)
            "getDeviceFunctions" -> getDeviceFunctions(result)
            "startHeartRateDetection" -> startHeartRateDetection(result)
            "stopHeartRateDetection" -> stopHeartRateDetection(result)
            "readHeartRateData" -> readHeartRateData(result)
            "startBloodPressureDetection" -> startBloodPressureDetection(result)
            "stopBloodPressureDetection" -> stopBloodPressureDetection(result)
            "startBloodOxygenDetection" -> startBloodOxygenDetection(result)
            "stopBloodOxygenDetection" -> stopBloodOxygenDetection(result)
            "readStepData" -> readStepData(result)
            "readSleepData" -> readSleepData(result)
            "setAlarm" -> setAlarm(call, result)
            "readAlarms" -> readAlarms(result)
            "deleteAlarm" -> deleteAlarm(call, result)
            "sendNotification" -> sendNotification(call, result)
            "findDevice" -> findDevice(result)
            "setScreenBrightness" -> setScreenBrightness(call, result)
            "readBattery" -> readBattery(result)
            "getDeviceVersion" -> getDeviceVersion(result)
            "openCameraControl" -> openCameraControl(result)
            "closeCameraControl" -> closeCameraControl(result)
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel.setMethodCallHandler(null)
        scanEventChannel.setStreamHandler(null)
        connectionEventChannel.setStreamHandler(null)
        heartRateEventChannel.setStreamHandler(null)
        bloodPressureEventChannel.setStreamHandler(null)
        bloodOxygenEventChannel.setStreamHandler(null)
        stepEventChannel.setStreamHandler(null)
        notificationEventChannel.setStreamHandler(null)
    }

    // ==================== SDK Methods ====================

    private fun initialize(result: Result) {
        try {
            android.util.Log.d("VeepooSDK", "Initializing VeepooSDK...")
            vpOperateManager.init(context)
            android.util.Log.d("VeepooSDK", "VeepooSDK initialized successfully")
            result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("VeepooSDK", "Initialization error: ${e.message}", e)
            result.error("INIT_ERROR", "Failed to initialize SDK: ${e.message}", null)
        }
    }

    private fun startScan(result: Result) {
        try {
            android.util.Log.d("VeepooSDK", "Starting BLE scan...")

            // 1. Check Bluetooth state
            if (!isBluetoothEnabled()) {
                android.util.Log.w("VeepooSDK", "Bluetooth is disabled")
                result.error("BLUETOOTH_DISABLED", "Bluetooth is disabled. Please enable Bluetooth.", null)
                return
            }

            // 2. Check scan throttling
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastScanTime < SCAN_THROTTLE_MS) {
                val remainingTime = (SCAN_THROTTLE_MS - (currentTime - lastScanTime)) / 1000
                android.util.Log.w("VeepooSDK", "Scanning too frequently, please wait ${remainingTime}s")
                result.error("SCAN_THROTTLED", "Please wait ${remainingTime} seconds before scanning again", null)
                return
            }

            // 3. Clear previous scan data
            discoveredDevices.clear()
            lastScanTime = currentTime

            // 4. Set up automatic timeout
            scanTimeoutRunnable = Runnable {
                android.util.Log.d("VeepooSDK", "Scan timeout reached, stopping scan")
                stopScanInternal()
            }
            mainHandler.postDelayed(scanTimeoutRunnable!!, SCAN_TIMEOUT_MS)

            // 5. Start scanning
            vpOperateManager.startScanDevice(object : SearchResponse {
                override fun onSearchStarted() {
                    android.util.Log.d("VeepooSDK", "Scan started successfully")
                    mainHandler.post {
                        scanEventSink?.success(
                            mapOf(
                                "status" to "started"
                            )
                        )
                    }
                }

                override fun onDeviceFounded(device: SearchResult?) {
                    device?.let { searchResult ->
                        val address = searchResult.getAddress()
                        val name = searchResult.getName() ?: "Unknown"
                        val rssi = searchResult.rssi

                        android.util.Log.d("VeepooSDK", "Device found: $name - $address (RSSI: $rssi)")

                        val deviceMap = mapOf(
                            "macAddress" to address,
                            "name" to name,
                            "rssi" to rssi,
                            "isBonded" to false
                        )

                        // Device caching with RSSI filtering
                        val existingDevice = discoveredDevices[address]
                        if (existingDevice == null) {
                            // New device - add it
                            discoveredDevices[address] = deviceMap
                            mainHandler.post {
                                scanEventSink?.success(deviceMap)
                            }
                            android.util.Log.d("VeepooSDK", "New device added: $name")
                        } else {
                            // Existing device - only update if RSSI changed significantly
                            val oldRssi = existingDevice["rssi"] as? Int ?: 0
                            if (kotlin.math.abs(oldRssi - rssi) > RSSI_UPDATE_THRESHOLD) {
                                discoveredDevices[address] = deviceMap
                                mainHandler.post {
                                    scanEventSink?.success(deviceMap)
                                }
                                android.util.Log.d("VeepooSDK", "Device RSSI updated: $name (old: $oldRssi, new: $rssi)")
                            }
                        }
                    }
                }

                override fun onSearchStopped() {
                    android.util.Log.d("VeepooSDK", "Scan stopped")
                    cleanupScan()
                    mainHandler.post {
                        scanEventSink?.success(
                            mapOf(
                                "status" to "stopped"
                            )
                        )
                    }
                }

                override fun onSearchCanceled() {
                    android.util.Log.d("VeepooSDK", "Scan canceled")
                    cleanupScan()
                    mainHandler.post {
                        scanEventSink?.success(
                            mapOf(
                                "status" to "canceled"
                            )
                        )
                    }
                }
            })
            result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("VeepooSDK", "Scan error: ${e.message}", e)
            cleanupScan()
            result.error("SCAN_ERROR", "Failed to start scan: ${e.message}", null)
        }
    }

    private fun stopScanInternal() {
        try {
            vpOperateManager.stopScanDevice()
        } catch (e: Exception) {
            android.util.Log.e("VeepooSDK", "Error stopping scan: ${e.message}", e)
        }
    }

    private fun cleanupScan() {
        scanTimeoutRunnable?.let {
            mainHandler.removeCallbacks(it)
            scanTimeoutRunnable = null
        }
    }

    private fun isBluetoothEnabled(): Boolean {
        return try {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
            bluetoothManager?.adapter?.isEnabled == true
        } catch (e: Exception) {
            android.util.Log.e("VeepooSDK", "Error checking Bluetooth state: ${e.message}", e)
            true // Assume enabled if we can't check
        }
    }

    private fun stopScan(result: Result) {
        try {
            android.util.Log.d("VeepooSDK", "Stopping scan manually")
            vpOperateManager.stopScanDevice()
            cleanupScan()
            result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("VeepooSDK", "Failed to stop scan: ${e.message}", e)
            result.error("SCAN_ERROR", "Failed to stop scan: ${e.message}", null)
        }
    }

    private fun connect(call: MethodCall, result: Result) {
        val macAddress = call.argument<String>("macAddress")
        val password = call.argument<String>("password") ?: "0000"
        val is24Hour = call.argument<Boolean>("is24Hour") ?: true

        if (macAddress == null) {
            result.error("INVALID_ARGS", "MAC address is required", null)
            return
        }

        currentMacAddress = macAddress

        try {
            vpOperateManager.connectDevice(
                macAddress,
                "",  // Device name (can be empty)
                object : IConnectResponse {
                    override fun connectState(code: Int, profile: BleGattProfile?, isoadModel: Boolean) {
                        mainHandler.post {
                            connectionEventSink?.success(
                                mapOf(
                                    "status" to when (code) {
                                        Code.REQUEST_SUCCESS -> 2 // connected
                                        else -> 0 // disconnected or error
                                    },
                                    "macAddress" to macAddress,
                                    "errorMessage" to null
                                )
                            )
                        }

                        if (code == Code.REQUEST_SUCCESS) {
                            // Device connected, now confirm password
                            vpOperateManager.confirmDevicePwd(
                                IBleWriteResponse { writeCode ->
                                    // Write response callback
                                },
                                object : IPwdDataListener {
                                    override fun onPwdDataChange(pwdData: PwdData?) {
                                        if (pwdData?.getmStatus() == EPwdStatus.CHECK_SUCCESS) {
                                            result.success(true)
                                        } else {
                                            result.error("AUTH_ERROR", "Password authentication failed", null)
                                        }
                                    }
                                },
                                deviceFunctionDataListener,
                                null,
                                password,
                                is24Hour
                            )
                        } else {
                            result.success(false)
                        }
                    }
                },
                object : INotifyResponse {
                    override fun notifyState(state: Int) {
                        // Notify state callback
                    }
                }
            )
        } catch (e: Exception) {
            result.error("CONNECT_ERROR", "Failed to connect: ${e.message}", null)
        }
    }

    private fun disconnect(result: Result) {
        try {
            vpOperateManager.disconnectWatch(IBleWriteResponse { aBoolean ->
                currentMacAddress = null
                result.success(true)
            })
        } catch (e: Exception) {
            result.error("DISCONNECT_ERROR", "Failed to disconnect: ${e.message}", null)
        }
    }

    private fun syncPersonInfo(call: MethodCall, result: Result) {
        try {
            val height = call.argument<Int>("height") ?: 170
            val weight = call.argument<Double>("weight")?.toInt() ?: 70
            val age = call.argument<Int>("age") ?: 25
            val sex = call.argument<Int>("sex") ?: 1
            val targetSteps = call.argument<Int>("targetSteps") ?: 10000

            val eSex = if (sex == 0) ESex.WOMEN else ESex.MAN
            val personInfo = PersonInfoData(eSex, height, weight, age, targetSteps)

            vpOperateManager.syncPersonInfo(
                IBleWriteResponse { aBoolean ->
                    // Write response
                },
                object : IPersonInfoDataListener {
                    override fun OnPersoninfoDataChange(oprateStatus: EOprateStauts?) {
                        result.success(oprateStatus == EOprateStauts.OPRATE_SUCCESS)
                    }
                },
                personInfo
            )
        } catch (e: Exception) {
            result.error("SYNC_ERROR", "Failed to sync person info: ${e.message}", null)
        }
    }

    private var deviceFunctions: FunctionDeviceSupportData? = null

    private val deviceFunctionDataListener = object : IDeviceFuctionDataListener {
        override fun onFunctionSupportDataChange(functionData: FunctionDeviceSupportData?) {
            deviceFunctions = functionData
        }
    }

    private fun getDeviceFunctions(result: Result) {
        val functions = deviceFunctions
        if (functions != null) {
            result.success(
                mapOf(
                    "supportHeartRate" to (functions.getHeartDetect() == EFunctionStatus.SUPPORT),
                    "supportBloodPressure" to (functions.getBp() == EFunctionStatus.SUPPORT),
                    "supportBloodOxygen" to (functions.getSpo2H() == EFunctionStatus.SUPPORT),
                    "supportTemperature" to (functions.getTemperatureFunction() == EFunctionStatus.SUPPORT),
                    "supportSleep" to (functions.getPrecisionSleep() == EFunctionStatus.SUPPORT),
                    "supportSteps" to true,
                    "supportAlarm" to (functions.getAlarm2() == EFunctionStatus.SUPPORT),
                    "supportCamera" to (functions.getCamera() == EFunctionStatus.SUPPORT),
                    "supportFindPhone" to (functions.getFindDeviceByPhone() == EFunctionStatus.SUPPORT),
                    "supportWeather" to (functions.getWeatherFunction() == EFunctionStatus.SUPPORT),
                    "supportECG" to (functions.getEcg() == EFunctionStatus.SUPPORT),
                    "supportHRV" to (functions.getHrvFunction() == EFunctionStatus.SUPPORT),
                    "supportSedentary" to (functions.getLongseat() == EFunctionStatus.SUPPORT),
                    "supportDrink" to (functions.getDrink() == EFunctionStatus.SUPPORT),
                    "supportWashHand" to false
                )
            )
        } else {
            result.success(
                mapOf(
                    "supportHeartRate" to true,
                    "supportBloodPressure" to true,
                    "supportBloodOxygen" to true,
                    "supportTemperature" to true,
                    "supportSleep" to true,
                    "supportSteps" to true,
                    "supportAlarm" to true,
                    "supportCamera" to true,
                    "supportFindPhone" to true,
                    "supportWeather" to true,
                    "supportECG" to false,
                    "supportHRV" to false,
                    "supportSedentary" to true,
                    "supportDrink" to true,
                    "supportWashHand" to false
                )
            )
        }
    }

    // ==================== Heart Rate ====================

    private fun startHeartRateDetection(result: Result) {
        try {
            android.util.Log.d("VeepooSDK", "Starting heart rate detection...")
            vpOperateManager.startDetectHeart(
                IBleWriteResponse { writeSuccess ->
                    android.util.Log.d("VeepooSDK", "Heart rate write response: $writeSuccess")
                },
                object : IHeartDataListener {
                    override fun onDataChange(heartData: HeartData?) {
                        android.util.Log.d("VeepooSDK", "Heart rate data: ${heartData?.getData()}, status: ${heartData?.getHeartStatus()}")
                        heartData?.let {
                            mainHandler.post {
                                heartRateEventSink?.success(
                                    mapOf(
                                        "heartRate" to it.getData(),
                                        "timestamp" to System.currentTimeMillis(),
                                        "status" to "normal",
                                        "isMeasuring" to (it.getHeartStatus() == EHeartStatus.STATE_HEART_DETECT)
                                    )
                                )
                            }
                        }
                    }
                }
            )
            android.util.Log.d("VeepooSDK", "Heart rate detection started successfully")
            result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("VeepooSDK", "Failed to start heart rate: ${e.message}", e)
            result.error("HR_ERROR", "Failed to start heart rate detection: ${e.message}", null)
        }
    }

    private fun stopHeartRateDetection(result: Result) {
        try {
            vpOperateManager.stopDetectHeart(IBleWriteResponse { aBoolean ->
                result.success(true)
            })
        } catch (e: Exception) {
            result.error("HR_ERROR", "Failed to stop heart rate detection: ${e.message}", null)
        }
    }

    private fun readHeartRateData(result: Result) {
        // VeepooSDK doesn't have a readHeart method - only real-time detection
        // Return empty list as historical data is not available via this method
        result.success(emptyList<Map<String, Any>>())
    }

    // ==================== Blood Pressure ====================

    private fun startBloodPressureDetection(result: Result) {
        try {
            vpOperateManager.startDetectBP(
                IBleWriteResponse { aBoolean -> },
                object : IBPDetectDataListener {
                    override fun onDataChange(bpData: BpData?) {
                        bpData?.let {
                            mainHandler.post {
                                bloodPressureEventSink?.success(
                                    mapOf(
                                        "systolic" to it.getHighPressure(),
                                        "diastolic" to it.getLowPressure(),
                                        "timestamp" to System.currentTimeMillis(),
                                        "status" to "normal",
                                        "isMeasuring" to (it.getStatus() == EBPDetectStatus.STATE_BP_BUSY)
                                    )
                                )
                            }
                        }
                    }
                },
                EBPDetectModel.DETECT_MODEL_PUBLIC
            )
            result.success(true)
        } catch (e: Exception) {
            result.error("BP_ERROR", "Failed to start blood pressure detection: ${e.message}", null)
        }
    }

    private fun stopBloodPressureDetection(result: Result) {
        try {
            vpOperateManager.stopDetectBP(
                IBleWriteResponse { aBoolean ->
                    result.success(true)
                },
                EBPDetectModel.DETECT_MODEL_PUBLIC
            )
        } catch (e: Exception) {
            result.error("BP_ERROR", "Failed to stop blood pressure detection: ${e.message}", null)
        }
    }

    // ==================== Blood Oxygen ====================

    private fun startBloodOxygenDetection(result: Result) {
        try {
            android.util.Log.d("VeepooSDK", "Starting blood oxygen detection...")
            vpOperateManager.startDetectSPO2H(
                IBleWriteResponse { writeSuccess ->
                    android.util.Log.d("VeepooSDK", "Blood oxygen write response: $writeSuccess")
                },
                object : ISpo2hDataListener {
                    override fun onSpO2HADataChange(spo2hData: Spo2hData?) {
                        android.util.Log.d("VeepooSDK", "Blood oxygen data: ${spo2hData?.getValue()}, checking: ${spo2hData?.isChecking()}")
                        spo2hData?.let {
                            mainHandler.post {
                                bloodOxygenEventSink?.success(
                                    mapOf(
                                        "oxygenLevel" to it.getValue(),
                                        "timestamp" to System.currentTimeMillis(),
                                        "status" to "normal",
                                        "isMeasuring" to (it.isChecking())
                                    )
                                )
                            }
                        }
                    }
                }
            )
            android.util.Log.d("VeepooSDK", "Blood oxygen detection started successfully")
            result.success(true)
        } catch (e: Exception) {
            android.util.Log.e("VeepooSDK", "Failed to start blood oxygen: ${e.message}", e)
            result.error("SPO2_ERROR", "Failed to start blood oxygen detection: ${e.message}", null)
        }
    }

    private fun stopBloodOxygenDetection(result: Result) {
        try {
            vpOperateManager.stopDetectSPO2H(
                IBleWriteResponse { aBoolean ->
                    result.success(true)
                },
                object : ISpo2hDataListener {
                    override fun onSpO2HADataChange(spo2hData: Spo2hData?) {
                        // Not used when stopping
                    }
                }
            )
        } catch (e: Exception) {
            result.error("SPO2_ERROR", "Failed to stop blood oxygen detection: ${e.message}", null)
        }
    }

    // ==================== Step Data ====================

    private fun readStepData(result: Result) {
        try {
            vpOperateManager.readOriginData(
                IBleWriteResponse { aBoolean -> },
                object : IOriginDataListener {
                    override fun onOringinFiveMinuteDataChange(originData: OriginData?) {
                        originData?.let {
                            result.success(
                                mapOf(
                                    "steps" to it.getStepValue(),
                                    "distance" to it.getDisValue(),
                                    "calories" to it.getCalValue(),
                                    "timestamp" to System.currentTimeMillis()
                                )
                            )
                        } ?: result.success(null)
                    }

                    override fun onOringinHalfHourDataChange(originHalfHourData: OriginHalfHourData?) {
                        // Not used for step data
                    }

                    override fun onReadOriginProgressDetail(day: Int, date: String?, allPackage: Int, currentPackage: Int) {
                        // Progress callback
                    }

                    override fun onReadOriginProgress(progress: Float) {
                        // Progress callback
                    }

                    override fun onReadOriginComplete() {
                        // Complete callback
                    }
                },
                3
            )
        } catch (e: Exception) {
            result.error("STEP_ERROR", "Failed to read step data: ${e.message}", null)
        }
    }

    // ==================== Sleep Data ====================

    private fun readSleepData(result: Result) {
        try {
            // Read sleep data for the last 3 days
            val watchDataDay = 3

            vpOperateManager.readSleepData(
                IBleWriteResponse { aBoolean -> },
                object : ISleepDataListener {
                    override fun onSleepDataChange(day: String?, sleepData: SleepData?) {
                        sleepData?.let {
                            val sleepList = mutableListOf<Map<String, Any>>()
                            // Parse sleep data and add to list
                            // This would require detailed parsing of SleepData object
                            result.success(sleepList)
                        } ?: result.success(emptyList<Map<String, Any>>())
                    }

                    override fun onSleepProgress(progress: Float) {
                        // Progress callback
                    }

                    override fun onSleepProgressDetail(day: String?, packagenumber: Int) {
                        // Progress detail callback
                    }

                    override fun onReadSleepComplete() {
                        // Complete callback
                    }
                },
                watchDataDay
            )
        } catch (e: Exception) {
            result.error("SLEEP_ERROR", "Failed to read sleep data: ${e.message}", null)
        }
    }

    // ==================== Alarms ====================

    private fun setAlarm(call: MethodCall, result: Result) {
        try {
            val alarmId = call.argument<Int>("alarmId") ?: 0
            val hour = call.argument<Int>("hour") ?: 0
            val minute = call.argument<Int>("minute") ?: 0
            val repeatDays = call.argument<Int>("repeatDays") ?: 0
            val isEnabled = call.argument<Boolean>("isEnabled") ?: true

            val alarmSetting = AlarmSetting(hour, minute, isEnabled)
            // Note: AlarmSetting doesn't have setRepeatDate method
            // Repeat functionality may need to be handled differently

            vpOperateManager.settingAlarm(
                IBleWriteResponse { aBoolean -> },
                object : IAlarmDataListener {
                    override fun onAlarmDataChangeListener(alarmData: AlarmData?) {
                        result.success(alarmData?.getStatus() == EAalarmStatus.SETTING_SUCCESS)
                    }
                },
                listOf(alarmSetting)
            )
        } catch (e: Exception) {
            result.error("ALARM_ERROR", "Failed to set alarm: ${e.message}", null)
        }
    }

    private fun readAlarms(result: Result) {
        try {
            vpOperateManager.readAlarm(
                IBleWriteResponse { aBoolean -> },
                object : IAlarmDataListener {
                    override fun onAlarmDataChangeListener(alarmData: AlarmData?) {
                        alarmData?.let {
                            // AlarmData.toString() contains all alarm info
                            // For now return empty list - proper parsing would need AlarmData analysis
                            result.success(emptyList<Map<String, Any>>())
                        } ?: result.success(emptyList<Map<String, Any>>())
                    }
                }
            )
        } catch (e: Exception) {
            result.error("ALARM_ERROR", "Failed to read alarms: ${e.message}", null)
        }
    }

    private fun deleteAlarm(call: MethodCall, result: Result) {
        val alarmId = call.argument<Int>("alarmId")
        if (alarmId == null) {
            result.error("INVALID_ARGS", "Alarm ID is required", null)
            return
        }

        try {
            val alarmSetting = AlarmSetting(0, 0, false)

            vpOperateManager.settingAlarm(
                IBleWriteResponse { aBoolean -> },
                object : IAlarmDataListener {
                    override fun onAlarmDataChangeListener(alarmData: AlarmData?) {
                        result.success(alarmData?.getStatus() == EAalarmStatus.SETTING_SUCCESS)
                    }
                },
                listOf(alarmSetting)
            )
        } catch (e: Exception) {
            result.error("ALARM_ERROR", "Failed to delete alarm: ${e.message}", null)
        }
    }

    // ==================== Notifications ====================

    private fun sendNotification(call: MethodCall, result: Result) {
        val type = call.argument<Int>("type") ?: 0
        val title = call.argument<String>("title") ?: ""
        val content = call.argument<String>("content") ?: ""

        try {
            // Send notification to device
            // Implementation depends on specific notification type
            // Would need to use appropriate VPOperateManager notification method
            result.success(true)
        } catch (e: Exception) {
            result.error("NOTIF_ERROR", "Failed to send notification: ${e.message}", null)
        }
    }

    // ==================== Device Settings ====================

    private fun findDevice(result: Result) {
        try {
            android.util.Log.d("VeepooSDK", "Triggering find device...")
            vpOperateManager.settingFindDevice(
                IBleWriteResponse { writeSuccess ->
                    android.util.Log.d("VeepooSDK", "Find device write response: $writeSuccess")
                    if (!writeSuccess) {
                        result.error("FIND_ERROR", "Device does not support find feature or write failed", null)
                    }
                },
                object : IFindDeviceDatalistener {
                    override fun onFindDevice(findDeviceData: FindDeviceData?) {
                        android.util.Log.d("VeepooSDK", "Find device callback received: $findDeviceData")
                        result.success(true)
                    }
                },
                true
            )
        } catch (e: Exception) {
            android.util.Log.e("VeepooSDK", "Failed to find device: ${e.message}", e)
            result.error("FIND_ERROR", "Failed to find device: ${e.message}", null)
        }
    }

    private fun setScreenBrightness(call: MethodCall, result: Result) {
        val brightness = call.argument<Int>("brightness")
        if (brightness == null || brightness < 0 || brightness > 100) {
            result.error("INVALID_ARGS", "Brightness must be between 0 and 100", null)
            return
        }

        try {
            // ScreenSetting requires specific parameters
            // This is a placeholder - actual implementation would need device-specific values
            result.success(false)
        } catch (e: Exception) {
            result.error("SCREEN_ERROR", "Failed to set screen brightness: ${e.message}", null)
        }
    }

    private fun readBattery(result: Result) {
        try {
            android.util.Log.d("VeepooSDK", "Reading battery level...")
            vpOperateManager.readBattery(
                IBleWriteResponse { writeSuccess ->
                    android.util.Log.d("VeepooSDK", "Battery read write response: $writeSuccess")
                },
                object : IBatteryDataListener {
                    override fun onDataChange(batteryData: BatteryData?) {
                        val level = batteryData?.getBatteryLevel() ?: 0
                        android.util.Log.d("VeepooSDK", "Battery level: $level%")
                        batteryData?.let {
                            result.success(it.getBatteryLevel())
                        } ?: result.success(null)
                    }
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("VeepooSDK", "Failed to read battery: ${e.message}", e)
            result.error("BATTERY_ERROR", "Failed to read battery: ${e.message}", null)
        }
    }

    private fun getDeviceVersion(result: Result) {
        try {
            // Device version is typically obtained during password confirmation
            // Return placeholder for now
            result.success(
                mapOf(
                    "hardwareVersion" to "",
                    "softwareVersion" to "",
                    "deviceModel" to "",
                    "testVersion" to ""
                )
            )
        } catch (e: Exception) {
            result.error("VERSION_ERROR", "Failed to get device version: ${e.message}", null)
        }
    }

    // ==================== Camera Control ====================

    private fun openCameraControl(result: Result) {
        try {
            vpOperateManager.startCamera(
                IBleWriteResponse { aBoolean -> },
                object : ICameraDataListener {
                    override fun OnCameraDataChange(cameraStatus: ECameraStatus?) {
                        result.success(cameraStatus == ECameraStatus.OPEN_SUCCESS || cameraStatus == ECameraStatus.DEVICE_OPEN_SUCCESS)
                    }
                }
            )
        } catch (e: Exception) {
            result.error("CAMERA_ERROR", "Failed to open camera control: ${e.message}", null)
        }
    }

    private fun closeCameraControl(result: Result) {
        try {
            vpOperateManager.stopCamera(
                IBleWriteResponse { aBoolean -> },
                object : ICameraDataListener {
                    override fun OnCameraDataChange(cameraStatus: ECameraStatus?) {
                        result.success(cameraStatus == ECameraStatus.CLOSE_SUCCESS)
                    }
                }
            )
        } catch (e: Exception) {
            result.error("CAMERA_ERROR", "Failed to close camera control: ${e.message}", null)
        }
    }
}
