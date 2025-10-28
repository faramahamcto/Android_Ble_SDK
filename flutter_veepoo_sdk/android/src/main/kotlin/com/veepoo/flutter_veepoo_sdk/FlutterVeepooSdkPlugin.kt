package com.veepoo.flutter_veepoo_sdk

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
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
            vpOperateManager.init(context)
            result.success(true)
        } catch (e: Exception) {
            result.error("INIT_ERROR", "Failed to initialize SDK: ${e.message}", null)
        }
    }

    private fun startScan(result: Result) {
        try {
            vpOperateManager.startScanDevice(object : ISearchResponse {
                override fun onSearchStarted() {
                    // Scan started
                }

                override fun onDeviceFounded(device: com.inuker.bluetooth.library.search.SearchResult?) {
                    device?.let {
                        mainHandler.post {
                            scanEventSink?.success(
                                mapOf(
                                    "macAddress" to it.address,
                                    "name" to (it.name ?: "Unknown"),
                                    "rssi" to it.rssi,
                                    "isBonded" to false
                                )
                            )
                        }
                    }
                }

                override fun onSearchStopped() {
                    // Scan stopped
                }

                override fun onSearchCanceled() {
                    // Scan canceled
                }
            })
            result.success(true)
        } catch (e: Exception) {
            result.error("SCAN_ERROR", "Failed to start scan: ${e.message}", null)
        }
    }

    private fun stopScan(result: Result) {
        try {
            vpOperateManager.stopScanDevice()
            result.success(true)
        } catch (e: Exception) {
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

        try {
            vpOperateManager.connectDevice(
                macAddress,
                object : IConnectResponse {
                    override fun connectState(code: Int, profile: Int, status: Int) {
                        mainHandler.post {
                            connectionEventSink?.success(
                                mapOf(
                                    "status" to when (code) {
                                        IConnectResponse.CONNECT_STATE -> 2 // connected
                                        IConnectResponse.DISCONNECT_STATE -> 0 // disconnected
                                        else -> 4 // error
                                    },
                                    "macAddress" to macAddress,
                                    "errorMessage" to null
                                )
                            )
                        }

                        if (code == IConnectResponse.CONNECT_STATE) {
                            // Device connected, now confirm password
                            vpOperateManager.confirmDevicePwd(
                                { aBoolean ->
                                    // Write response
                                },
                                object : IPwdDataListener {
                                    override fun onPwdDataChange(pwdData: PwdData?) {
                                        if (pwdData?.pwdState == EPwdStatus.SUCCESS) {
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
                        } else if (code == IConnectResponse.DISCONNECT_STATE) {
                            result.success(false)
                        }
                    }
                },
                null
            )
        } catch (e: Exception) {
            result.error("CONNECT_ERROR", "Failed to connect: ${e.message}", null)
        }
    }

    private fun disconnect(result: Result) {
        try {
            vpOperateManager.disconnectWatch { aBoolean ->
                result.success(aBoolean)
            }
        } catch (e: Exception) {
            result.error("DISCONNECT_ERROR", "Failed to disconnect: ${e.message}", null)
        }
    }

    private fun syncPersonInfo(call: MethodCall, result: Result) {
        try {
            val height = call.argument<Int>("height") ?: 170
            val weight = call.argument<Double>("weight") ?: 70.0
            val age = call.argument<Int>("age") ?: 25
            val sex = call.argument<Int>("sex") ?: 1
            val stepLength = call.argument<Int>("stepLength") ?: 70
            val targetSteps = call.argument<Int>("targetSteps") ?: 10000

            val personInfo = PersonInfoData()
            personInfo.height = height
            personInfo.weight = weight.toFloat()
            personInfo.age = age
            personInfo.sex = sex
            personInfo.stepLength = stepLength
            personInfo.targetStepCount = targetSteps

            vpOperateManager.syncPersonInfo { aBoolean ->
                result.success(aBoolean)
            }, personInfo)
        } catch (e: Exception) {
            result.error("SYNC_ERROR", "Failed to sync person info: ${e.message}", null)
        }
    }

    private var deviceFunctions: DeviceFunctionsData? = null

    private val deviceFunctionDataListener = object : IDeviceFuctionDataListener {
        override fun onFunctionSupportDataChange(functionData: FunctionDeviceSupportData?) {
            // Store device functions
        }
    }

    private fun getDeviceFunctions(result: Result) {
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
                "supportWashHand" to true
            )
        )
    }

    // ==================== Heart Rate ====================

    private fun startHeartRateDetection(result: Result) {
        try {
            vpOperateManager.startHeartDetect(
                { aBoolean -> },
                object : IHeartDataListener {
                    override fun onDataChange(heartData: HeartData?) {
                        heartData?.let {
                            mainHandler.post {
                                heartRateEventSink?.success(
                                    mapOf(
                                        "heartRate" to it.data,
                                        "timestamp" to System.currentTimeMillis(),
                                        "status" to "normal",
                                        "isMeasuring" to (it.heartState == EHeartStatus.HEART_SEARCHING)
                                    )
                                )
                            }
                        }
                    }
                }
            )
            result.success(true)
        } catch (e: Exception) {
            result.error("HR_ERROR", "Failed to start heart rate detection: ${e.message}", null)
        }
    }

    private fun stopHeartRateDetection(result: Result) {
        try {
            vpOperateManager.stopHeartDetect { aBoolean ->
                result.success(aBoolean)
            }
        } catch (e: Exception) {
            result.error("HR_ERROR", "Failed to stop heart rate detection: ${e.message}", null)
        }
    }

    private fun readHeartRateData(result: Result) {
        try {
            vpOperateManager.readHeartRate(
                { aBoolean -> },
                object : IHeartDataListener {
                    override fun onDataChange(heartData: HeartData?) {
                        heartData?.let {
                            val dataList = listOf(
                                mapOf(
                                    "heartRate" to it.data,
                                    "timestamp" to System.currentTimeMillis(),
                                    "status" to "normal",
                                    "isMeasuring" to false
                                )
                            )
                            result.success(dataList)
                        } ?: result.success(emptyList<Map<String, Any>>())
                    }
                }
            )
        } catch (e: Exception) {
            result.error("HR_ERROR", "Failed to read heart rate data: ${e.message}", null)
        }
    }

    // ==================== Blood Pressure ====================

    private fun startBloodPressureDetection(result: Result) {
        try {
            vpOperateManager.startBPDetect(
                { aBoolean -> },
                object : IBPDetectDataListener {
                    override fun onDataChange(bpData: BpData?) {
                        bpData?.let {
                            mainHandler.post {
                                bloodPressureEventSink?.success(
                                    mapOf(
                                        "systolic" to it.highPressure,
                                        "diastolic" to it.lowPressure,
                                        "timestamp" to System.currentTimeMillis(),
                                        "status" to "normal",
                                        "isMeasuring" to (it.bpStatus == EBPStatus.BP_CHECKING)
                                    )
                                )
                            }
                        }
                    }
                }
            )
            result.success(true)
        } catch (e: Exception) {
            result.error("BP_ERROR", "Failed to start blood pressure detection: ${e.message}", null)
        }
    }

    private fun stopBloodPressureDetection(result: Result) {
        try {
            vpOperateManager.stopBPDetect { aBoolean ->
                result.success(aBoolean)
            }
        } catch (e: Exception) {
            result.error("BP_ERROR", "Failed to stop blood pressure detection: ${e.message}", null)
        }
    }

    // ==================== Blood Oxygen ====================

    private fun startBloodOxygenDetection(result: Result) {
        try {
            vpOperateManager.startDetectSPO2H(
                { aBoolean -> },
                object : ISPO2HDataListener {
                    override fun onSpO2HADataChange(spo2hData: Spo2hData?) {
                        spo2hData?.let {
                            mainHandler.post {
                                bloodOxygenEventSink?.success(
                                    mapOf(
                                        "oxygenLevel" to it.value,
                                        "timestamp" to System.currentTimeMillis(),
                                        "status" to "normal",
                                        "isMeasuring" to (it.checkState == ESpo2hStatus.SPO2H_CHECKING)
                                    )
                                )
                            }
                        }
                    }
                }
            )
            result.success(true)
        } catch (e: Exception) {
            result.error("SPO2_ERROR", "Failed to start blood oxygen detection: ${e.message}", null)
        }
    }

    private fun stopBloodOxygenDetection(result: Result) {
        try {
            vpOperateManager.stopDetectSPO2H { aBoolean ->
                result.success(aBoolean)
            }
        } catch (e: Exception) {
            result.error("SPO2_ERROR", "Failed to stop blood oxygen detection: ${e.message}", null)
        }
    }

    // ==================== Step Data ====================

    private fun readStepData(result: Result) {
        try {
            vpOperateManager.readOriginData(
                { aBoolean -> },
                object : IOriginDataListener {
                    override fun onDataChange(originData: OriginData?) {
                        originData?.let {
                            result.success(
                                mapOf(
                                    "steps" to it.step,
                                    "distance" to (it.distance.toDouble()),
                                    "calories" to (it.calories.toDouble()),
                                    "timestamp" to System.currentTimeMillis()
                                )
                            )
                        } ?: result.success(null)
                    }
                }
            )
        } catch (e: Exception) {
            result.error("STEP_ERROR", "Failed to read step data: ${e.message}", null)
        }
    }

    // ==================== Sleep Data ====================

    private fun readSleepData(result: Result) {
        try {
            vpOperateManager.readSleepData(
                { aBoolean -> },
                object : ISleepDataListener {
                    override fun onSleepDataChange(sleepData: SleepData?) {
                        sleepData?.let {
                            val sleepList = mutableListOf<Map<String, Any>>()
                            // Parse sleep data and add to list
                            result.success(sleepList)
                        } ?: result.success(emptyList<Map<String, Any>>())
                    }
                }
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
            val title = call.argument<String>("title")

            val alarmSetting = AlarmSetting()
            alarmSetting.alarmId = alarmId
            alarmSetting.hour = hour
            alarmSetting.minute = minute
            alarmSetting.repeatTimes = repeatDays
            alarmSetting.isOpen = isEnabled
            alarmSetting.scene = EAalarmStatus.OPEN

            vpOperateManager.settingAlarm(
                { aBoolean -> result.success(aBoolean) },
                null,
                alarmSetting
            )
        } catch (e: Exception) {
            result.error("ALARM_ERROR", "Failed to set alarm: ${e.message}", null)
        }
    }

    private fun readAlarms(result: Result) {
        try {
            vpOperateManager.readAlarm(
                { aBoolean -> },
                object : IAlarmDataListener {
                    override fun onAlarmDataChangeListListener(alarmDatas: MutableList<AlarmData>?) {
                        val alarmList = alarmDatas?.map { alarm ->
                            mapOf(
                                "alarmId" to alarm.alarmId,
                                "hour" to alarm.hour,
                                "minute" to alarm.minute,
                                "repeatDays" to alarm.repeatDate,
                                "isEnabled" to (alarm.alarmStatus == EAalarmStatus.OPEN),
                                "title" to ""
                            )
                        } ?: emptyList()
                        result.success(alarmList)
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
            val alarmSetting = AlarmSetting()
            alarmSetting.alarmId = alarmId
            alarmSetting.scene = EAalarmStatus.CLOSE

            vpOperateManager.settingAlarm(
                { aBoolean -> result.success(aBoolean) },
                null,
                alarmSetting
            )
        } catch (e: Exception) {
            result.error("ALARM_ERROR", "Failed to delete alarm: ${e.message}", null)
        }
    }

    // ==================== Notifications ====================

    private fun sendNotification(call: MethodCall, result: Result) {
        val type = call.argument<Int>("type") ?: 0
        val title = call.argument<String>("title") ?: ""
        val content = call.argument<String>("content")

        try {
            // Send notification to device
            // Implementation depends on specific notification type
            result.success(true)
        } catch (e: Exception) {
            result.error("NOTIF_ERROR", "Failed to send notification: ${e.message}", null)
        }
    }

    // ==================== Device Settings ====================

    private fun findDevice(result: Result) {
        try {
            vpOperateManager.findDevice { aBoolean ->
                result.success(aBoolean)
            }
        } catch (e: Exception) {
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
            val screenSetting = ScreenSetting()
            screenSetting.lightLevel = brightness / 20 // Convert to 0-5 range

            vpOperateManager.settingScreen(
                { aBoolean -> result.success(aBoolean) },
                screenSetting
            )
        } catch (e: Exception) {
            result.error("SCREEN_ERROR", "Failed to set screen brightness: ${e.message}", null)
        }
    }

    private fun readBattery(result: Result) {
        try {
            vpOperateManager.readBattery(
                { aBoolean -> },
                object : IBatteryDataListener {
                    override fun onDataChange(batteryData: BatteryData?) {
                        batteryData?.let {
                            result.success(it.batteryLevel)
                        } ?: result.success(null)
                    }
                }
            )
        } catch (e: Exception) {
            result.error("BATTERY_ERROR", "Failed to read battery: ${e.message}", null)
        }
    }

    private fun getDeviceVersion(result: Result) {
        try {
            vpOperateManager.readDeviceVersion(
                { aBoolean -> },
                object : IDeviceVersionDataListener {
                    override fun onVersionDataChange(versionData: VersionData?) {
                        versionData?.let {
                            result.success(
                                mapOf(
                                    "hardwareVersion" to (it.hardwareVersion ?: ""),
                                    "softwareVersion" to (it.softwareVersion ?: ""),
                                    "deviceModel" to (it.deviceNumber ?: ""),
                                    "testVersion" to (it.testVersion ?: "")
                                )
                            )
                        } ?: result.success(null)
                    }
                }
            )
        } catch (e: Exception) {
            result.error("VERSION_ERROR", "Failed to get device version: ${e.message}", null)
        }
    }

    // ==================== Camera Control ====================

    private fun openCameraControl(result: Result) {
        try {
            vpOperateManager.openCamera { aBoolean ->
                result.success(aBoolean)
            }
        } catch (e: Exception) {
            result.error("CAMERA_ERROR", "Failed to open camera control: ${e.message}", null)
        }
    }

    private fun closeCameraControl(result: Result) {
        try {
            vpOperateManager.closeCamera { aBoolean ->
                result.success(aBoolean)
            }
        } catch (e: Exception) {
            result.error("CAMERA_ERROR", "Failed to close camera control: ${e.message}", null)
        }
    }
}
