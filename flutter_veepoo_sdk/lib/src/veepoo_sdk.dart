import 'dart:async';
import 'package:flutter/services.dart';
import 'models/models.dart';

/// Main entry point for VeepooSDK Flutter plugin
///
/// This class provides access to all VeepooSDK functionality for
/// communicating with BLE wearable devices (smartwatches, fitness bands)
class VeepooSDK {
  static const MethodChannel _methodChannel = MethodChannel('flutter_veepoo_sdk/methods');
  static const EventChannel _scanEventChannel = EventChannel('flutter_veepoo_sdk/scan_events');
  static const EventChannel _connectionEventChannel = EventChannel('flutter_veepoo_sdk/connection_events');
  static const EventChannel _heartRateEventChannel = EventChannel('flutter_veepoo_sdk/heart_rate_events');
  static const EventChannel _bloodPressureEventChannel = EventChannel('flutter_veepoo_sdk/blood_pressure_events');
  static const EventChannel _bloodOxygenEventChannel = EventChannel('flutter_veepoo_sdk/blood_oxygen_events');
  static const EventChannel _stepEventChannel = EventChannel('flutter_veepoo_sdk/step_events');
  static const EventChannel _notificationEventChannel = EventChannel('flutter_veepoo_sdk/notification_events');

  Stream<VeepooDevice>? _scanStream;
  Stream<ConnectionState>? _connectionStream;
  Stream<HeartRateData>? _heartRateStream;
  Stream<BloodPressureData>? _bloodPressureStream;
  Stream<BloodOxygenData>? _bloodOxygenStream;
  Stream<StepData>? _stepStream;
  Stream<NotificationData>? _notificationStream;

  /// Singleton instance
  static final VeepooSDK instance = VeepooSDK._();
  VeepooSDK._();

  /// Initialize the SDK
  /// Must be called before any other SDK operations
  Future<bool> initialize() async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('initialize');
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to initialize SDK: ${e.message}');
    }
  }

  /// Start scanning for BLE devices
  /// Returns a stream of discovered devices
  Stream<VeepooDevice> startScan() {
    _scanStream ??= _scanEventChannel.receiveBroadcastStream().map((event) {
      return VeepooDevice.fromMap(Map<String, dynamic>.from(event));
    });
    return _scanStream!;
  }

  /// Stop scanning for BLE devices
  Future<void> stopScan() async {
    try {
      await _methodChannel.invokeMethod('stopScan');
    } on PlatformException catch (e) {
      throw VeepooException('Failed to stop scan: ${e.message}');
    }
  }

  /// Connect to a device by MAC address
  /// [macAddress] - The MAC address of the device to connect
  /// [password] - Optional device password (default: "0000")
  /// [is24Hour] - Use 24-hour time format (default: true)
  Future<bool> connect({
    required String macAddress,
    String password = '0000',
    bool is24Hour = true,
  }) async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('connect', {
        'macAddress': macAddress,
        'password': password,
        'is24Hour': is24Hour,
      });
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to connect: ${e.message}');
    }
  }

  /// Disconnect from the currently connected device
  Future<bool> disconnect() async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('disconnect');
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to disconnect: ${e.message}');
    }
  }

  /// Listen to connection state changes
  Stream<ConnectionState> get connectionStateStream {
    _connectionStream ??= _connectionEventChannel.receiveBroadcastStream().map((event) {
      return ConnectionState.fromMap(Map<String, dynamic>.from(event));
    });
    return _connectionStream!;
  }

  /// Sync personal information to the device
  Future<bool> syncPersonInfo(PersonInfo personInfo) async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('syncPersonInfo', personInfo.toMap());
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to sync person info: ${e.message}');
    }
  }

  /// Get device functions/capabilities
  Future<DeviceFunctions?> getDeviceFunctions() async {
    try {
      final result = await _methodChannel.invokeMethod('getDeviceFunctions');
      if (result != null) {
        return DeviceFunctions.fromMap(Map<String, dynamic>.from(result));
      }
      return null;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to get device functions: ${e.message}');
    }
  }

  // ==================== Heart Rate ====================

  /// Start real-time heart rate detection
  /// Returns a stream of heart rate data
  Stream<HeartRateData> startHeartRateDetection() {
    _methodChannel.invokeMethod('startHeartRateDetection');
    _heartRateStream ??= _heartRateEventChannel.receiveBroadcastStream().map((event) {
      return HeartRateData.fromMap(Map<String, dynamic>.from(event));
    });
    return _heartRateStream!;
  }

  /// Stop heart rate detection
  Future<void> stopHeartRateDetection() async {
    try {
      await _methodChannel.invokeMethod('stopHeartRateDetection');
    } on PlatformException catch (e) {
      throw VeepooException('Failed to stop heart rate detection: ${e.message}');
    }
  }

  /// Read heart rate data from device
  Future<List<HeartRateData>> readHeartRateData() async {
    try {
      final result = await _methodChannel.invokeMethod('readHeartRateData');
      if (result != null) {
        return (result as List).map((e) => HeartRateData.fromMap(Map<String, dynamic>.from(e))).toList();
      }
      return [];
    } on PlatformException catch (e) {
      throw VeepooException('Failed to read heart rate data: ${e.message}');
    }
  }

  // ==================== Blood Pressure ====================

  /// Start blood pressure detection
  /// Returns a stream of blood pressure data
  Stream<BloodPressureData> startBloodPressureDetection() {
    _methodChannel.invokeMethod('startBloodPressureDetection');
    _bloodPressureStream ??= _bloodPressureEventChannel.receiveBroadcastStream().map((event) {
      return BloodPressureData.fromMap(Map<String, dynamic>.from(event));
    });
    return _bloodPressureStream!;
  }

  /// Stop blood pressure detection
  Future<void> stopBloodPressureDetection() async {
    try {
      await _methodChannel.invokeMethod('stopBloodPressureDetection');
    } on PlatformException catch (e) {
      throw VeepooException('Failed to stop blood pressure detection: ${e.message}');
    }
  }

  // ==================== Blood Oxygen (SpO2) ====================

  /// Start blood oxygen detection
  /// Returns a stream of SpO2 data
  Stream<BloodOxygenData> startBloodOxygenDetection() {
    _methodChannel.invokeMethod('startBloodOxygenDetection');
    _bloodOxygenStream ??= _bloodOxygenEventChannel.receiveBroadcastStream().map((event) {
      return BloodOxygenData.fromMap(Map<String, dynamic>.from(event));
    });
    return _bloodOxygenStream!;
  }

  /// Stop blood oxygen detection
  Future<void> stopBloodOxygenDetection() async {
    try {
      await _methodChannel.invokeMethod('stopBloodOxygenDetection');
    } on PlatformException catch (e) {
      throw VeepooException('Failed to stop blood oxygen detection: ${e.message}');
    }
  }

  // ==================== Step Data ====================

  /// Listen to step data changes
  Stream<StepData> get stepDataStream {
    _stepStream ??= _stepEventChannel.receiveBroadcastStream().map((event) {
      return StepData.fromMap(Map<String, dynamic>.from(event));
    });
    return _stepStream!;
  }

  /// Read step data from device
  Future<StepData?> readStepData() async {
    try {
      final result = await _methodChannel.invokeMethod('readStepData');
      if (result != null) {
        return StepData.fromMap(Map<String, dynamic>.from(result));
      }
      return null;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to read step data: ${e.message}');
    }
  }

  // ==================== Sleep Data ====================

  /// Read sleep data from device
  Future<List<SleepData>> readSleepData() async {
    try {
      final result = await _methodChannel.invokeMethod('readSleepData');
      if (result != null) {
        return (result as List).map((e) => SleepData.fromMap(Map<String, dynamic>.from(e))).toList();
      }
      return [];
    } on PlatformException catch (e) {
      throw VeepooException('Failed to read sleep data: ${e.message}');
    }
  }

  // ==================== Alarms ====================

  /// Set alarm on device
  Future<bool> setAlarm(AlarmData alarm) async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('setAlarm', alarm.toMap());
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to set alarm: ${e.message}');
    }
  }

  /// Read alarms from device
  Future<List<AlarmData>> readAlarms() async {
    try {
      final result = await _methodChannel.invokeMethod('readAlarms');
      if (result != null) {
        return (result as List).map((e) => AlarmData.fromMap(Map<String, dynamic>.from(e))).toList();
      }
      return [];
    } on PlatformException catch (e) {
      throw VeepooException('Failed to read alarms: ${e.message}');
    }
  }

  /// Delete alarm
  Future<bool> deleteAlarm(int alarmId) async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('deleteAlarm', {'alarmId': alarmId});
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to delete alarm: ${e.message}');
    }
  }

  // ==================== Notifications ====================

  /// Listen to notification events from device
  Stream<NotificationData> get notificationStream {
    _notificationStream ??= _notificationEventChannel.receiveBroadcastStream().map((event) {
      return NotificationData.fromMap(Map<String, dynamic>.from(event));
    });
    return _notificationStream!;
  }

  /// Send notification to device
  Future<bool> sendNotification({
    required NotificationType type,
    required String title,
    String? content,
  }) async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('sendNotification', {
        'type': type.index,
        'title': title,
        'content': content,
      });
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to send notification: ${e.message}');
    }
  }

  // ==================== Device Settings ====================

  /// Find device (make it vibrate)
  Future<bool> findDevice() async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('findDevice');
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to find device: ${e.message}');
    }
  }

  /// Set screen brightness
  /// [brightness] - Value from 0 to 100
  Future<bool> setScreenBrightness(int brightness) async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('setScreenBrightness', {'brightness': brightness});
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to set screen brightness: ${e.message}');
    }
  }

  /// Read device battery level
  Future<int?> readBattery() async {
    try {
      final result = await _methodChannel.invokeMethod<int>('readBattery');
      return result;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to read battery: ${e.message}');
    }
  }

  /// Get device version information
  Future<DeviceVersion?> getDeviceVersion() async {
    try {
      final result = await _methodChannel.invokeMethod('getDeviceVersion');
      if (result != null) {
        return DeviceVersion.fromMap(Map<String, dynamic>.from(result));
      }
      return null;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to get device version: ${e.message}');
    }
  }

  // ==================== Camera Control ====================

  /// Open camera control on device
  Future<bool> openCameraControl() async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('openCameraControl');
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to open camera control: ${e.message}');
    }
  }

  /// Close camera control on device
  Future<bool> closeCameraControl() async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('closeCameraControl');
      return result ?? false;
    } on PlatformException catch (e) {
      throw VeepooException('Failed to close camera control: ${e.message}');
    }
  }
}

/// Custom exception for VeepooSDK errors
class VeepooException implements Exception {
  final String message;
  VeepooException(this.message);

  @override
  String toString() => 'VeepooException: $message';
}
