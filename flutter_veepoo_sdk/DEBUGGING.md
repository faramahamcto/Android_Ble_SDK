# راهنمای دیباگ (Debugging Guide)

## مشکل: startScan چیزی پیدا نمی‌کند

اگر وقتی `startScan` را می‌زنید دستگاهی پیدا نمی‌شود و scan به سرعت تمام می‌شود، این مراحل را بررسی کنید:

### 1. بررسی Permissions (مهم‌ترین!)

#### Android 12+ (API 31+):
```xml
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

#### Android 11 و پایین‌تر:
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

**چک کنید:**
```dart
// در Flutter
final status = await Permission.bluetoothScan.status;
print('Bluetooth Scan Permission: $status');

final locationStatus = await Permission.location.status;
print('Location Permission: $locationStatus');
```

### 2. بررسی Bluetooth و Location در سیستم

```bash
# Check از طریق logcat
adb logcat | grep VeepooSDK
```

**باید ببینید:**
```
D/VeepooSDK: Initializing VeepooSDK...
D/VeepooSDK: VeepooSDK initialized successfully
D/VeepooSDK: Starting BLE scan...
D/VeepooSDK: Scan started successfully
D/VeepooSDK: Device found: Device_Name - AA:BB:CC:DD:EE:FF
```

**اگر دیدید:**
```
D/VeepooSDK: Scan stopped
```
یا بلافاصله بعد از "Starting BLE scan" میاد، یعنی:
- Bluetooth خاموش است
- Location خاموش است (Android)
- Permission نداده‌اید

### 3. چک کردن Bluetooth روشن باشد

```dart
// باید Bluetooth روشن باشد
// نمی‌تونید از طریق Flutter Bluetooth را روشن کنید، باید user خودش روشن کنه
```

**راه حل:** یک دیالوگ به user نشان بدید که Bluetooth را روشن کند:
```dart
Future<void> _checkBluetoothEnabled() async {
  // توجه: این فقط یک مثال هست، Flutter plugin خاصی برای چک Bluetooth لازمه
  // مثلاً: flutter_blue_plus یا flutter_bluetooth_serial

  showDialog(
    context: context,
    builder: (context) => AlertDialog(
      title: Text('Bluetooth Required'),
      content: Text('Please enable Bluetooth to scan for devices.'),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text('OK'),
        ),
      ],
    ),
  );
}
```

### 4. چک کردن Location Services (Android)

**Android نیاز دارد که Location Services روشن باشد برای BLE Scanning!**

```dart
import 'package:geolocator/geolocator.dart';

Future<bool> _checkLocationServiceEnabled() async {
  bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
  if (!serviceEnabled) {
    print('Location services are disabled');
    // نشون بده به user که Location را روشن کند
    return false;
  }
  return true;
}
```

### 5. دیباگ با Logcat

**ترمینال 1 - نمایش همه لاگ‌های VeepooSDK:**
```bash
adb logcat -s VeepooSDK:D
```

**ترمینال 2 - نمایش همه لاگ‌های Flutter:**
```bash
flutter logs
```

**ترمینال 3 - نمایش خطاهای BLE سیستم:**
```bash
adb logcat | grep -i bluetooth
```

### 6. تست با دستگاه واقعی

**مهم:** BLE Scanning روی Emulator معمولاً کار نمی‌کنه!

- از دستگاه Android واقعی استفاده کنید
- ساعت هوشمند شما باید:
  - روشن باشد
  - در حالت advertising باشد (معمولاً وقتی به هیچ دستگاهی متصل نیست)
  - نزدیک گوشی باشد (کمتر از 5 متر)

### 7. بررسی AndroidManifest.xml

مطمئن شوید که در `android/app/src/main/AndroidManifest.xml` همه permissions هست:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Bluetooth Permissions -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN"
        android:usesPermissionFlags="neverForLocation" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

    <!-- Location Permissions (required for BLE on Android) -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    <application ...>
        ...
    </application>
</manifest>
```

### 8. کد تست برای دیباگ

```dart
Future<void> _debugScan() async {
  print('=== Starting Debug Scan ===');

  // 1. Check permissions
  print('1. Checking permissions...');
  if (Platform.isAndroid) {
    final btScan = await Permission.bluetoothScan.status;
    final btConnect = await Permission.bluetoothConnect.status;
    final location = await Permission.location.status;

    print('  - Bluetooth Scan: $btScan');
    print('  - Bluetooth Connect: $btConnect');
    print('  - Location: $location');

    if (!btScan.isGranted || !btConnect.isGranted || !location.isGranted) {
      print('  ERROR: Not all permissions granted!');
      return;
    }
  }

  // 2. Check location services
  print('2. Checking location services...');
  bool locationEnabled = await Geolocator.isLocationServiceEnabled();
  print('  - Location Services: ${locationEnabled ? "Enabled" : "DISABLED"}');

  if (!locationEnabled) {
    print('  ERROR: Location services disabled!');
    return;
  }

  // 3. Initialize SDK
  print('3. Initializing SDK...');
  try {
    final initialized = await _sdk.initialize();
    print('  - SDK Initialized: $initialized');
  } catch (e) {
    print('  ERROR: $e');
    return;
  }

  // 4. Start scan
  print('4. Starting scan...');
  print('  Watch logcat with: adb logcat -s VeepooSDK:D');

  await _sdk.startScan();

  print('5. Scan started, listening for devices...');
  print('  If no devices found after 10 seconds:');
  print('  - Check if smartwatch is turned on');
  print('  - Check if smartwatch is NOT connected to another phone');
  print('  - Check if smartwatch is in range (< 5m)');

  // Wait 10 seconds
  await Future.delayed(Duration(seconds: 10));

  await _sdk.stopScan();
  print('=== Debug Scan Complete ===');
}
```

### 9. مشکلات رایج و راه‌حل

| مشکل | علت | راه‌حل |
|------|-----|--------|
| `Scan stopped` بلافاصله | Bluetooth خاموش | Bluetooth را روشن کنید |
| `Scan stopped` بلافاصله | Location خاموش | Location Services را روشن کنید |
| `Scan stopped` بلافاصله | Permission ندارد | دوباره permission بگیرید |
| هیچ device پیدا نمی‌شه | دستگاه متصل به گوشی دیگه‌ای هست | دستگاه را disconnect کنید |
| هیچ device پیدا نمی‌شه | دستگاه خاموش است | دستگاه را روشن کنید |
| هیچ device پیدا نمی‌شه | خیلی دور است | نزدیک‌تر بیارید |
| خطای Emulator | Emulator BLE ندارد | از دستگاه واقعی استفاده کنید |

### 10. دستور دیباگ سریع

```bash
# همه چیز رو یه جا چک کن
adb shell settings get secure bluetooth_on  # باید 1 باشه
adb shell settings get secure location_mode  # باید بزرگتر از 0 باشه
adb logcat -s VeepooSDK:D &  # لاگ‌ها رو نشون بده
flutter run
```

---

## تماس با پشتیبانی

اگر همه این مراحل رو انجام دادید و هنوز کار نمی‌کند:

1. خروجی `adb logcat -s VeepooSDK:D` را کپی کنید
2. خروجی `flutter logs` را کپی کنید
3. مدل گوشی و نسخه Android را بگویید
4. مدل ساعت هوشمند را بگویید

---

**نکته:** این plugin روی emulator کار نمی‌کنه! حتماً با دستگاه واقعی تست کنید.
