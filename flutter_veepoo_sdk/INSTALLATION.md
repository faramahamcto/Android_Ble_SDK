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

## گام 2: دانلود کتابخانه‌های VeepooSDK

### فایل‌های مورد نیاز:

دو فایل AAR زیر را از مخزن رسمی VeepooSDK دانلود کنید:

1. **vpprotocol-2.3.28.15.aar**
   - لینک: https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_core
   - این فایل را از پوشه `com2.3.28.15` دانلود کنید

2. **vpbluetooth-1.18.aar**
   - لینک: https://github.com/HBandSDK/Android_Ble_SDK/tree/master/android_sdk_source/jar_base
   - این فایل را از پوشه مربوطه دانلود کنید

### محل قرارگیری فایل‌ها:

فایل‌های دانلود شده را در پوشه زیر قرار دهید:

```
your_flutter_project/
├── android/
│   └── app/
│       └── libs/              ⬅️ این پوشه را ایجاد کنید
│           ├── vpprotocol-2.3.28.15.aar
│           └── vpbluetooth-1.18.aar
```

اگر پوشه `libs` وجود ندارد، آن را ایجاد کنید:
```bash
mkdir -p android/app/libs
```

## گام 3: پیکربندی Gradle

فایل `android/app/build.gradle` را باز کنید و تغییرات زیر را اعمال کنید:

### الف) اضافه کردن repository:

در بخش `android`، repositories را اضافه کنید:

```gradle
android {
    compileSdk 34

    // ... تنظیمات دیگر

    // این بخش را اضافه کنید
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}
```

### ب) اضافه کردن dependencies:

در بخش `dependencies`:

```gradle
dependencies {
    // ... وابستگی‌های دیگر Flutter

    // کتابخانه‌های VeepooSDK
    implementation(name: 'vpprotocol-2.3.28.15', ext: 'aar')
    implementation(name: 'vpbluetooth-1.18', ext: 'aar')
    implementation 'com.google.code.gson:gson:2.8.9'
}
```

## گام 4: اضافه کردن مجوزها

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

## گام 5: تست نصب

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

### خطای "Could not find vpprotocol-2.3.28.15.aar"

**راه حل:**
1. مطمئن شوید فایل‌های AAR در `android/app/libs/` قرار دارند
2. نام فایل‌ها را دقیق بررسی کنید
3. `flutter clean` را اجرا کنید
4. دوباره `flutter pub get` بزنید

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
