# نصب و راه‌اندازی Flutter Veepoo SDK

## گام 1: اضافه کردن پلاگین به پروژه

در فایل `pubspec.yaml` پروژه Flutter خود، پلاگین را اضافه کنید:

```yaml
dependencies:
  flutter:
    sdk: flutter

  flutter_veepoo_sdk:
    git:
      url: https://github.com/faramahamcto/Android_Ble_SDK.git
      ref: claude/session-011CUZJBhvwUBYxEk1G6wYkc
      path: flutter_veepoo_sdk
```

سپس دستور زیر را اجرا کنید:
```bash
flutter pub get
```

> **نکته مهم:** پلاگین شامل تمام کتابخانه‌های لازم VeepooSDK است. نیازی به دانلود یا کپی دستی فایل‌های AAR نیست!

## گام 2: اضافه کردن مجوزها

فایل `android/app/src/main/AndroidManifest.xml` را باز کنید و مجوزهای زیر را اضافه کنید:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- مجوزهای بلوتوث -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />

    <!-- مجوزهای مکان (برای اسکن BLE ضروری است) -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    <application ...>
        ...
    </application>
</manifest>
```

## گام 3: تست نصب

یک تست ساده برای اطمینان از نصب صحیح:

```dart
import 'package:flutter/material.dart';
import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    initSDK();
  }

  Future<void> initSDK() async {
    try {
      final sdk = VeepooSDK.instance;
      final result = await sdk.initialize();
      print('SDK initialized: $result');
    } catch (e) {
      print('SDK initialization error: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text('Veepoo SDK Test')),
        body: Center(child: Text('SDK Test')),
      ),
    );
  }
}
```

سپس اپلیکیشن را اجرا کنید:
```bash
flutter run
```

## عیب‌یابی

### خطای "Unresolved reference VPOperateManager"

**راه حل:**
1. `flutter clean` را اجرا کنید
2. دوباره `flutter pub get` بزنید
3. اگر مشکل حل نشد، cache را پاک کنید:
```bash
flutter clean
rm -rf ~/.pub-cache/git/
flutter pub get
```

### خطای مجوز بلوتوث

**راه حل:**
1. مجوزها را در AndroidManifest.xml بررسی کنید
2. برای Android 12+، حتماً `BLUETOOTH_SCAN` و `BLUETOOTH_CONNECT` را اضافه کنید
3. در runtime نیز باید مجوزها را درخواست کنید (از permission_handler استفاده کنید)

### خطای Build

**راه حل:**
```bash
cd android
./gradlew clean
cd ..
flutter clean
flutter pub get
flutter run
```

## مستندات بیشتر

- [راهنمای کامل (فارسی)](https://github.com/faramahamcto/Android_Ble_SDK/blob/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/README_FA.md)
- [راهنمای نوع داده‌ها](https://github.com/faramahamcto/Android_Ble_SDK/blob/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/DATA_TYPES_GUIDE.md)
- [مرجع سریع](https://github.com/faramahamcto/Android_Ble_SDK/blob/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/QUICK_REFERENCE.md)
- [برنامه مثال](https://github.com/faramahamcto/Android_Ble_SDK/tree/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/example)

## پشتیبانی

برای مشکلات و سوالات:
- مشاهده issue های GitHub
- مراجعه به [مستندات VeepooSDK اصلی](https://github.com/HBandSDK/Android_Ble_SDK/wiki)
