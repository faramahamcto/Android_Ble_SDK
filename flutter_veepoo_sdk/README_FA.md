# Flutter Veepoo SDK - راهنمای فارسی

یک پلاگین Flutter برای یکپارچه‌سازی با VeepooSDK - ابزار Bluetooth Low Energy (BLE) برای دستگاه‌های پوشیدنی مانند ساعت هوشمند و مچ‌بند سلامتی.

## 📚 مستندات

- **[راهنمای نصب گام‌به‌گام](https://github.com/faramahamcto/Android_Ble_SDK/blob/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/INSTALLATION.md)** - نصب کامل با جزئیات (فارسی)
- **[مرجع سریع](https://github.com/faramahamcto/Android_Ble_SDK/blob/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/QUICK_REFERENCE.md)** - جستجوی سریع نوع پارامترها (فارسی)
- **[راهنمای کامل نوع داده‌ها](https://github.com/faramahamcto/Android_Ble_SDK/blob/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/DATA_TYPES_GUIDE.md)** - راهنمای جامع همه نوع داده‌ها (فارسی و انگلیسی)
- **[برنامه مثال](https://github.com/faramahamcto/Android_Ble_SDK/tree/claude/session-011CUZJBhvwUBYxEk1G6wYkc/flutter_veepoo_sdk/example)** - نمونه کامل با رابط کاربری
- **[مستندات اصلی VeepooSDK](https://github.com/HBandSDK/Android_Ble_SDK/wiki)** - مستندات رسمی Android SDK

## قابلیت‌ها

این پلاگین پشتیبانی کامل از موارد زیر را دارد:

### مدیریت دستگاه
- 🔍 **جستجوی دستگاه** - کشف دستگاه‌های BLE در اطراف
- 🔗 **مدیریت اتصال** - اتصال/قطع اتصال از دستگاه‌ها
- 🔋 **مانیتورینگ باتری** - خواندن سطح باتری دستگاه
- 📱 **اطلاعات دستگاه** - دریافت نسخه سخت‌افزار/نرم‌افزار

### مانیتورینگ سلامتی
- ❤️ **ضربان قلب** - مانیتورینگ لحظه‌ای ضربان قلب
- 🩸 **فشار خون** - اندازه‌گیری فشار خون
- 💨 **اکسیژن خون (SpO2)** - مانیتورینگ اشباع اکسیژن
- 👣 **شمارش قدم** - قدم‌ها، مسافت و کالری
- 😴 **داده خواب** - تحلیل و ردیابی خواب
- 🌡️ **دما** - مانیتورینگ دمای بدن (بسته به دستگاه)

### امکانات دستگاه
- ⏰ **هشدارها** - تنظیم و مدیریت هشدارهای دستگاه
- 📢 **اعلان‌ها** - ارسال اعلان به دستگاه
- 📸 **کنترل دوربین** - تریگر دوربین از راه دور
- 🔍 **یافتن دستگاه** - لرزش دستگاه
- 💡 **تنظیمات صفحه** - تنظیم روشنایی
- 🌍 **ساعت جهانی** - پیکربندی منطقه زمانی

## پیش‌نیازها

### اندروید
- **حداقل SDK**: 21 (Android 5.0)
- **هدف SDK**: 34 (Android 14)
- **Kotlin**: 1.9.0+

### مجوزها
این مجوزها را به `android/app/src/main/AndroidManifest.xml` اضافه کنید:

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## نصب

### 1. اضافه کردن به pubspec.yaml

```yaml
dependencies:
  flutter_veepoo_sdk:
    git:
      url: https://github.com/faramahamcto/Android_Ble_SDK.git
      ref: claude/session-011CUZJBhvwUBYxEk1G6wYkc
      path: flutter_veepoo_sdk
```

### 2. اجرای دستور flutter pub get

```bash
flutter pub get
```

**همین!** پلاگین شامل تمام کتابخانه‌های لازم VeepooSDK (فایل‌های AAR برای vpprotocol و vpbluetooth) است.

## استفاده

### راه‌اندازی اولیه SDK

```dart
import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk.dart';

final sdk = VeepooSDK.instance;

// راه‌اندازی SDK (یک بار در شروع برنامه)
await sdk.initialize();
```

### جستجوی دستگاه‌ها

```dart
// شروع اسکن
final scanStream = sdk.startScan();

scanStream.listen((device) {
  print('دستگاه یافت شد: ${device.name} (${device.macAddress})');
  print('قدرت سیگنال: ${device.rssi}');
});

// توقف اسکن بعد از 10 ثانیه
await Future.delayed(Duration(seconds: 10));
await sdk.stopScan();
```

### اتصال به دستگاه

```dart
// اتصال به دستگاه
final connected = await sdk.connect(
  macAddress: 'AA:BB:CC:DD:EE:FF',
  password: '0000',  // رمز پیش‌فرض
  is24Hour: true,
);

if (connected) {
  print('اتصال موفق');

  // همگام‌سازی اطلاعات شخصی
  await sdk.syncPersonInfo(PersonInfo(
    height: 170,      // سانتی‌متر
    weight: 70.0,     // کیلوگرم
    age: 25,
    sex: 1,           // 0: زن, 1: مرد
    targetSteps: 10000,
  ));
}

// گوش دادن به تغییرات وضعیت اتصال
sdk.connectionStateStream.listen((state) {
  print('وضعیت اتصال: ${state.status}');
});
```

### مانیتورینگ ضربان قلب

```dart
// شروع مانیتورینگ لحظه‌ای ضربان قلب
final heartRateStream = sdk.startHeartRateDetection();

heartRateStream.listen((hrData) {
  print('ضربان قلب: ${hrData.heartRate} BPM');
  print('وضعیت: ${hrData.status}');
});

// توقف مانیتورینگ
await sdk.stopHeartRateDetection();

// خواندن داده‌های تاریخی ضربان قلب
final historicalData = await sdk.readHeartRateData();
```

### اندازه‌گیری فشار خون

```dart
// شروع اندازه‌گیری فشار خون
final bpStream = sdk.startBloodPressureDetection();

bpStream.listen((bpData) {
  if (!bpData.isMeasuring) {
    print('فشار خون: ${bpData.systolic}/${bpData.diastolic} mmHg');
    sdk.stopBloodPressureDetection();
  }
});
```

### اندازه‌گیری اکسیژن خون (SpO2)

```dart
// شروع اندازه‌گیری اکسیژن خون
final spo2Stream = sdk.startBloodOxygenDetection();

spo2Stream.listen((spo2Data) {
  if (!spo2Data.isMeasuring) {
    print('اکسیژن خون: ${spo2Data.oxygenLevel}%');
    sdk.stopBloodOxygenDetection();
  }
});
```

### داده قدم

```dart
// خواندن داده فعلی قدم
final stepData = await sdk.readStepData();
if (stepData != null) {
  print('قدم‌ها: ${stepData.steps}');
  print('مسافت: ${stepData.distance} متر');
  print('کالری: ${stepData.calories} کیلوکالری');
}
```

### داده خواب

```dart
// خواندن داده خواب
final sleepDataList = await sdk.readSleepData();
for (var sleep in sleepDataList) {
  print('خواب: ${sleep.startTime} تا ${sleep.endTime}');
  print('خواب عمیق: ${sleep.deepSleep} دقیقه, خواب سبک: ${sleep.lightSleep} دقیقه');
  print('مجموع: ${sleep.totalSleep} دقیقه');
}
```

### هشدارها

```dart
// تنظیم هشدار
final alarm = AlarmData(
  alarmId: 1,
  hour: 7,
  minute: 30,
  repeatDays: 0x1F,  // دوشنبه تا جمعه
  isEnabled: true,
  title: 'بیدار شو',
);

await sdk.setAlarm(alarm);

// خواندن همه هشدارها
final alarms = await sdk.readAlarms();

// حذف هشدار
await sdk.deleteAlarm(1);
```

### اعلان‌ها

```dart
// ارسال اعلان به دستگاه
await sdk.sendNotification(
  type: NotificationType.call,
  title: 'تماس ورودی',
  content: 'علی احمدی',
);
```

### تنظیمات دستگاه

```dart
// یافتن دستگاه (لرزش)
await sdk.findDevice();

// تنظیم روشنایی صفحه (0-100)
await sdk.setScreenBrightness(80);

// خواندن سطح باتری
final battery = await sdk.readBattery();
print('باتری: $battery%');

// دریافت نسخه دستگاه
final version = await sdk.getDeviceVersion();
print('سخت‌افزار: ${version?.hardwareVersion}');
print('نرم‌افزار: ${version?.softwareVersion}');
```

### قطع اتصال

```dart
// قطع اتصال از دستگاه
await sdk.disconnect();
```

## مثال

برنامه مثال کامل را در پوشه [example](example/) ببینید.

برای اجرای مثال:

```bash
cd example
flutter pub get
flutter run
```

## دستگاه‌های پشتیبانی‌شده

این پلاگین از تمام دستگاه‌های سازگار با VeepooSDK پشتیبانی می‌کند.

## پشتیبانی پلتفرم

| پلتفرم | پشتیبانی |
|--------|----------|
| اندروید | ✅ بله   |
| iOS    | ❌ خیر   |
| وب     | ❌ خیر   |
| دسکتاپ | ❌ خیر   |

## محدودیت‌های شناخته‌شده

1. **فقط اندروید** - در حال حاضر فقط اندروید پشتیبانی می‌شود
2. **اتصال تکی** - SDK از اتصال به یک دستگاه در یک زمان پشتیبانی می‌کند
3. **نیاز به بلوتوث** - دستگاه باید بلوتوث فعال داشته باشد
4. **مجوز مکان** - اندروید برای اسکن BLE نیاز به مجوز مکان دارد

## عیب‌یابی

### مشکلات اتصال
- اطمینان حاصل کنید بلوتوث فعال است
- بررسی کنید سرویس‌های مکان فعال هستند
- دستگاه در محدوده باشد
- دستگاه را فراموش کرده و دوباره جفت کنید

### خطاهای مجوز
- مجوزهای بلوتوث و مکان را در زمان اجرا درخواست کنید
- برای اندروید 12+، BLUETOOTH_SCAN و BLUETOOTH_CONNECT را تایید کنید

## مجوز

این پروژه تحت همان مجوز VeepooSDK منتشر شده است.

## اعتبار

ساخته شده بر روی VeepooSDK توسط Shenzhen Weituo Science Co., Ltd.
