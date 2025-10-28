# Flutter Veepoo SDK

A Flutter plugin for integrating VeepooSDK - a Bluetooth Low Energy (BLE) toolkit for wearable devices such as smartwatches and fitness bands.

## 📚 Documentation

- **[Quick Reference](https://github.com/faramahamcto/Android_Ble_SDK/blob/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/QUICK_REFERENCE.md)** - Fast lookup for parameter types
- **[Data Types Guide](https://github.com/faramahamcto/Android_Ble_SDK/blob/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/DATA_TYPES_GUIDE.md)** - Complete guide for all data types (English & Persian)
- **[Example App](https://github.com/faramahamcto/Android_Ble_SDK/tree/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/example)** - Full working example with UI
- **[VeepooSDK Original Documentation](https://github.com/HBandSDK/Android_Ble_SDK/wiki)** - Official Android SDK documentation

## Features

This plugin provides comprehensive support for:

### Device Management
- 🔍 **Device Scanning** - Discover nearby BLE devices
- 🔗 **Connection Management** - Connect/disconnect from devices
- 🔋 **Battery Monitoring** - Read device battery level
- 📱 **Device Information** - Get hardware/software versions

### Health Monitoring
- ❤️ **Heart Rate** - Real-time heart rate monitoring
- 🩸 **Blood Pressure** - Blood pressure measurement
- 💨 **Blood Oxygen (SpO2)** - Oxygen saturation monitoring
- 👣 **Step Tracking** - Steps, distance, and calories
- 😴 **Sleep Data** - Sleep analysis and tracking
- 🌡️ **Temperature** - Body temperature monitoring (device dependent)

### Device Features
- ⏰ **Alarms** - Set and manage device alarms
- 📢 **Notifications** - Send notifications to device
- 📸 **Camera Control** - Remote camera trigger
- 🔍 **Find Device** - Make device vibrate
- 💡 **Screen Settings** - Adjust brightness
- 🌍 **World Clock** - Timezone configuration

## Requirements

### Android
- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.0+

### Permissions
Add these permissions to your `android/app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## Installation

### 1. Add to pubspec.yaml

```yaml
dependencies:
  flutter_veepoo_sdk:
    git:
      url: https://github.com/faramahamcto/Android_Ble_SDK.git
      ref: claude/session-011CUZJBhvwUBYxEk1G6wYkc
      path: flutter_veepoo_sdk
```

### 2. Download VeepooSDK Libraries

Download the required AAR files from the official VeepooSDK repository:

**Required files:**
- [vpprotocol-2.3.28.15.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core)
- [vpbluetooth-1.18.aar](https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base)

Place them in your project:
```
your_flutter_project/
├── android/
│   └── app/
│       └── libs/
│           ├── vpprotocol-2.3.28.15.aar
│           └── vpbluetooth-1.18.aar
```

### 3. Update android/app/build.gradle

Add the following to your app's build.gradle:

```gradle
android {
    // ...

    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}

dependencies {
    // VeepooSDK dependencies
    implementation(name: 'vpprotocol-2.3.28.15', ext: 'aar')
    implementation(name: 'vpbluetooth-1.18', ext: 'aar')
    implementation 'com.google.code.gson:gson:2.8.9'
}
```

## Usage

### Initialize SDK

```dart
import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk.dart';

final sdk = VeepooSDK.instance;

// Initialize SDK (call once at app startup)
await sdk.initialize();
```

### Scan for Devices

```dart
// Start scanning
final scanStream = sdk.startScan();

scanStream.listen((device) {
  print('Found device: ${device.name} (${device.macAddress})');
  print('RSSI: ${device.rssi}');
});

// Stop scanning after 10 seconds
await Future.delayed(Duration(seconds: 10));
await sdk.stopScan();
```

### Connect to Device

```dart
// Connect to device
final connected = await sdk.connect(
  macAddress: 'AA:BB:CC:DD:EE:FF',
  password: '0000',  // Default password
  is24Hour: true,
);

if (connected) {
  print('Connected successfully');

  // Sync personal information
  await sdk.syncPersonInfo(PersonInfo(
    height: 170,      // cm
    weight: 70.0,     // kg
    age: 25,
    sex: 1,           // 0: female, 1: male
    targetSteps: 10000,
  ));
}

// Listen to connection state changes
sdk.connectionStateStream.listen((state) {
  print('Connection status: ${state.status}');
});
```

### Heart Rate Monitoring

```dart
// Start real-time heart rate monitoring
final heartRateStream = sdk.startHeartRateDetection();

heartRateStream.listen((hrData) {
  print('Heart Rate: ${hrData.heartRate} BPM');
  print('Status: ${hrData.status}');
  print('Measuring: ${hrData.isMeasuring}');
});

// Stop monitoring
await sdk.stopHeartRateDetection();

// Read historical heart rate data
final historicalData = await sdk.readHeartRateData();
for (var data in historicalData) {
  print('HR: ${data.heartRate} at ${data.timestamp}');
}
```

### Blood Pressure Measurement

```dart
// Start blood pressure measurement
final bpStream = sdk.startBloodPressureDetection();

bpStream.listen((bpData) {
  if (!bpData.isMeasuring) {
    print('BP: ${bpData.systolic}/${bpData.diastolic} mmHg');
    sdk.stopBloodPressureDetection();
  }
});
```

### Blood Oxygen (SpO2) Measurement

```dart
// Start blood oxygen measurement
final spo2Stream = sdk.startBloodOxygenDetection();

spo2Stream.listen((spo2Data) {
  if (!spo2Data.isMeasuring) {
    print('SpO2: ${spo2Data.oxygenLevel}%');
    sdk.stopBloodOxygenDetection();
  }
});
```

### Step Data

```dart
// Read current step data
final stepData = await sdk.readStepData();
if (stepData != null) {
  print('Steps: ${stepData.steps}');
  print('Distance: ${stepData.distance}m');
  print('Calories: ${stepData.calories} kcal');
}

// Listen to step data changes
sdk.stepDataStream.listen((stepData) {
  print('Steps updated: ${stepData.steps}');
});
```

### Sleep Data

```dart
// Read sleep data
final sleepDataList = await sdk.readSleepData();
for (var sleep in sleepDataList) {
  print('Sleep: ${sleep.startTime} to ${sleep.endTime}');
  print('Deep: ${sleep.deepSleep}min, Light: ${sleep.lightSleep}min');
  print('Total: ${sleep.totalSleep}min');
}
```

### Alarms

```dart
// Set an alarm
final alarm = AlarmData(
  alarmId: 1,
  hour: 7,
  minute: 30,
  repeatDays: 0x1F,  // Monday to Friday (bit mask)
  isEnabled: true,
  title: 'Wake up',
);

await sdk.setAlarm(alarm);

// Read all alarms
final alarms = await sdk.readAlarms();
for (var alarm in alarms) {
  print('Alarm ${alarm.alarmId}: ${alarm.hour}:${alarm.minute}');
}

// Delete an alarm
await sdk.deleteAlarm(1);
```

### Notifications

```dart
// Send notification to device
await sdk.sendNotification(
  type: NotificationType.call,
  title: 'Incoming Call',
  content: 'John Doe',
);

// Listen to notifications from device
sdk.notificationStream.listen((notification) {
  print('Device notification: ${notification.title}');
});
```

### Device Settings

```dart
// Find device (make it vibrate)
await sdk.findDevice();

// Set screen brightness (0-100)
await sdk.setScreenBrightness(80);

// Read battery level
final battery = await sdk.readBattery();
print('Battery: $battery%');

// Get device version
final version = await sdk.getDeviceVersion();
print('Hardware: ${version?.hardwareVersion}');
print('Software: ${version?.softwareVersion}');
print('Model: ${version?.deviceModel}');
```

### Camera Control

```dart
// Open camera control on device
await sdk.openCameraControl();

// Close camera control
await sdk.closeCameraControl();
```

### Disconnect

```dart
// Disconnect from device
await sdk.disconnect();
```

## Data Models

### VeepooDevice
```dart
class VeepooDevice {
  final String macAddress;
  final String name;
  final int rssi;
  final bool isBonded;
}
```

### ConnectionState
```dart
class ConnectionState {
  final ConnectionStatus status;  // disconnected, connecting, connected, disconnecting, error
  final String? errorMessage;
  final String? macAddress;
}
```

### PersonInfo
```dart
class PersonInfo {
  final int height;           // cm
  final double weight;        // kg
  final int age;
  final int sex;              // 0: female, 1: male
  final int stepLength;       // cm
  final int targetSteps;
  final int targetDistance;   // meters
  final int targetCalories;   // kcal
}
```

### HeartRateData
```dart
class HeartRateData {
  final int heartRate;        // BPM
  final DateTime timestamp;
  final String status;
  final bool isMeasuring;
}
```

### BloodPressureData
```dart
class BloodPressureData {
  final int systolic;         // High pressure
  final int diastolic;        // Low pressure
  final DateTime timestamp;
  final String status;
  final bool isMeasuring;
}
```

### BloodOxygenData
```dart
class BloodOxygenData {
  final int oxygenLevel;      // 0-100%
  final DateTime timestamp;
  final String status;
  final bool isMeasuring;
}
```

### StepData
```dart
class StepData {
  final int steps;
  final double distance;      // meters
  final double calories;      // kcal
  final DateTime timestamp;
}
```

### SleepData
```dart
class SleepData {
  final DateTime startTime;
  final DateTime endTime;
  final int deepSleep;        // minutes
  final int lightSleep;       // minutes
  final int awake;            // minutes
  int get totalSleep;         // deepSleep + lightSleep
}
```

### AlarmData
```dart
class AlarmData {
  final int alarmId;
  final int hour;             // 0-23
  final int minute;           // 0-59
  final int repeatDays;       // Bit mask: Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
  final bool isEnabled;
  final String? title;
}
```

## Error Handling

All SDK methods that can fail will throw a `VeepooException`:

```dart
try {
  await sdk.connect(macAddress: 'AA:BB:CC:DD:EE:FF');
} on VeepooException catch (e) {
  print('SDK Error: ${e.message}');
} catch (e) {
  print('Unknown error: $e');
}
```

## Example App

See the [example](example/) directory for a complete Flutter app demonstrating all plugin features.

To run the example:

```bash
cd example
flutter pub get
flutter run
```

## Supported Devices

This plugin supports all devices compatible with VeepooSDK, including but not limited to:
- Veepoo smartwatches
- Various fitness bands and trackers
- Compatible third-party devices

## Platform Support

| Platform | Supported |
|----------|-----------|
| Android  | ✅ Yes    |
| iOS      | ❌ No     |
| Web      | ❌ No     |
| Desktop  | ❌ No     |

## Known Limitations

1. **Android Only** - Currently only Android is supported. iOS support may be added in the future.
2. **Single Connection** - The SDK supports connecting to one device at a time.
3. **Bluetooth Required** - Device must have Bluetooth enabled.
4. **Location Permission** - Android requires location permission for BLE scanning.

## Troubleshooting

### Connection Issues
- Ensure Bluetooth is enabled
- Check that location services are enabled (Android requirement)
- Verify device is in range
- Try forgetting and re-pairing the device

### Permission Errors
- Request runtime permissions for Bluetooth and Location
- For Android 12+, ensure BLUETOOTH_SCAN and BLUETOOTH_CONNECT are granted

### Build Errors
- Verify VeepooSDK AAR files are in the correct location
- Check that build.gradle has correct dependencies
- Clean and rebuild: `flutter clean && flutter pub get`

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the same license as the underlying VeepooSDK.

## Support

For issues and questions:
1. Check the [example app](example/)
2. Review VeepooSDK documentation
3. Open an issue on GitHub

## Changelog

### Version 1.0.0
- Initial release
- Support for device scanning and connection
- Heart rate, blood pressure, and SpO2 monitoring
- Step and sleep data tracking
- Alarm management
- Notification support
- Device settings and control

## Credits

Built on top of VeepooSDK by Shenzhen Weituo Science Co., Ltd.
