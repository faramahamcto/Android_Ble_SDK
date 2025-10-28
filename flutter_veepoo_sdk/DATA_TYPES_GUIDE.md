# راهنمای نوع داده‌ها - Flutter Veepoo SDK

این راهنما تمام نوع داده‌های پارامترهای ورودی و خروجی را به صورت واضح توضیح می‌دهد.

## 📋 فهرست سریع

| نام | نوع در Dart | نمونه مقدار | توضیح فارسی |
|-----|-------------|-------------|-------------|
| MAC Address | `String` | `"AA:BB:CC:DD:EE:FF"` | آدرس مک بلوتوث |
| Password | `String` | `"0000"` | رمز عبور دستگاه |
| Height | `int` | `175` | قد (سانتی‌متر) |
| Weight | `double` | `70.5` | وزن (کیلوگرم) |
| Age | `int` | `28` | سن (سال) |
| Sex | `int` | `0` یا `1` | جنسیت (0=زن، 1=مرد) |
| Hour | `int` | `0-23` | ساعت (24 ساعته) |
| Minute | `int` | `0-59` | دقیقه |
| Heart Rate | `int` | `75` | ضربان قلب (BPM) |
| Blood Pressure | `int` | `120`, `80` | فشار خون (mmHg) |
| SpO2 | `int` | `98` | اکسیژن خون (درصد) |
| Steps | `int` | `5420` | تعداد قدم |
| Distance | `double` | `4250.5` | مسافت (متر) |
| Calories | `double` | `320.5` | کالری (kcal) |
| RSSI | `int` | `-65` | قدرت سیگنال (منفی) |
| Brightness | `int` | `0-100` | روشنایی صفحه (درصد) |
| Battery | `int` | `85` | باتری (درصد) |
| Is Enabled | `bool` | `true` یا `false` | فعال/غیرفعال |

---

## 1️⃣ اطلاعات شخصی (PersonInfo)

### استفاده:
```dart
final personInfo = PersonInfo(
  height: 175,        // ⬅️ int
  weight: 70.5,       // ⬅️ double
  age: 28,            // ⬅️ int
  sex: 1,             // ⬅️ int
  stepLength: 75,     // ⬅️ int (اختیاری)
  targetSteps: 10000, // ⬅️ int (اختیاری)
);
```

### جدول پارامترها:

| پارامتر | نوع | مقدار نمونه | توضیح | محدوده |
|---------|-----|-------------|--------|---------|
| `height` | **int** | `175` | قد به سانتی‌متر | 100-250 |
| `weight` | **double** | `70.5` | وزن به کیلوگرم | 30.0-200.0 |
| `age` | **int** | `28` | سن به سال | 1-120 |
| `sex` | **int** | `0` یا `1` | جنسیت: 0=زن، 1=مرد | 0 یا 1 |
| `stepLength` | **int** | `75` | طول قدم به سانتی‌متر | 40-100 |
| `targetSteps` | **int** | `10000` | هدف قدم روزانه | 1000-50000 |
| `targetDistance` | **int** | `8000` | هدف مسافت روزانه (متر) | 1000-50000 |
| `targetCalories` | **int** | `2000` | هدف کالری روزانه | 500-5000 |

---

## 2️⃣ هشدار (AlarmData)

### استفاده:
```dart
// هشدار ساده یکبار مصرف
final alarm = AlarmData(
  alarmId: 1,         // ⬅️ int
  hour: 7,            // ⬅️ int
  minute: 30,         // ⬅️ int
);

// هشدار تکراری برای روزهای هفته
final weekdayAlarm = AlarmData(
  alarmId: 2,         // ⬅️ int
  hour: 6,            // ⬅️ int
  minute: 0,          // ⬅️ int
  repeatDays: 0x1F,   // ⬅️ int (bit mask)
  isEnabled: true,    // ⬅️ bool
  title: 'کار',       // ⬅️ String
);
```

### جدول پارامترها:

| پارامتر | نوع | مقدار نمونه | توضیح | محدوده/مقادیر |
|---------|-----|-------------|--------|---------------|
| `alarmId` | **int** | `1` | شناسه یکتا هشدار | 1-10 |
| `hour` | **int** | `7` | ساعت (24 ساعته) | 0-23 |
| `minute` | **int** | `30` | دقیقه | 0-59 |
| `repeatDays` | **int** | `0x1F` یا `31` | روزهای تکرار (bit mask) | ببینید جدول زیر |
| `isEnabled` | **bool** | `true` | فعال/غیرفعال | true/false |
| `title` | **String?** | `"بیدار شو"` | عنوان هشدار (اختیاری) | متن دلخواه |

### repeatDays (روزهای تکرار):

این پارامتر یک **bit mask** است که به صورت عدد صحیح نوشته می‌شود:

| مقدار (دهدهی) | مقدار (هگز) | معنی |
|---------------|-------------|------|
| `0` | `0x00` | بدون تکرار (یکبار) |
| `1` | `0x01` | دوشنبه |
| `2` | `0x02` | سه‌شنبه |
| `4` | `0x04` | چهارشنبه |
| `8` | `0x08` | پنج‌شنبه |
| `16` | `0x10` | جمعه |
| `32` | `0x20` | شنبه |
| `64` | `0x40` | یکشنبه |
| `31` | `0x1F` | روزهای کاری (دوشنبه-جمعه) |
| `96` | `0x60` | آخر هفته (شنبه-یکشنبه) |
| `127` | `0x7F` | هر روز |

**مثال ترکیبی:**
```dart
// دوشنبه (1) + چهارشنبه (4) + جمعه (16) = 21
repeatDays: 21  // یا 0x15
```

---

## 3️⃣ اتصال به دستگاه (connect)

### استفاده:
```dart
await sdk.connect(
  macAddress: 'AA:BB:CC:DD:EE:FF',  // ⬅️ String (الزامی)
  password: '0000',                  // ⬅️ String (اختیاری)
  is24Hour: true,                    // ⬅️ bool (اختیاری)
);
```

### جدول پارامترها:

| پارامتر | نوع | مقدار نمونه | توضیح | مقدار پیش‌فرض |
|---------|-----|-------------|--------|----------------|
| `macAddress` | **String** | `"AA:BB:CC:DD:EE:FF"` | آدرس مک بلوتوث دستگاه | - (الزامی) |
| `password` | **String** | `"0000"` | رمز عبور دستگاه | `"0000"` |
| `is24Hour` | **bool** | `true` | فرمت 24 ساعته | `true` |

---

## 4️⃣ ارسال اعلان (sendNotification)

### استفاده:
```dart
await sdk.sendNotification(
  type: NotificationType.call,     // ⬅️ enum
  title: 'علی احمدی',              // ⬅️ String (الزامی)
  content: 'تماس ورودی...',         // ⬅️ String? (اختیاری)
);
```

### جدول پارامترها:

| پارامتر | نوع | مقدار نمونه | توضیح |
|---------|-----|-------------|--------|
| `type` | **NotificationType** | `NotificationType.call` | نوع اعلان (enum) |
| `title` | **String** | `"علی احمدی"` | عنوان اصلی اعلان |
| `content` | **String?** | `"تماس ورودی"` | محتوای اعلان (اختیاری) |

### مقادیر NotificationType:

```dart
NotificationType.call       // تماس تلفنی
NotificationType.sms        // پیامک
NotificationType.wechat     // WeChat
NotificationType.qq         // QQ
NotificationType.whatsapp   // WhatsApp
NotificationType.facebook   // Facebook
NotificationType.twitter    // Twitter/X
NotificationType.instagram  // Instagram
NotificationType.telegram   // Telegram
NotificationType.other      // سایر
```

---

## 5️⃣ تنظیم روشنایی (setScreenBrightness)

### استفاده:
```dart
await sdk.setScreenBrightness(80);  // ⬅️ int
```

### جدول پارامترها:

| پارامتر | نوع | مقدار نمونه | توضیح | محدوده |
|---------|-----|-------------|--------|---------|
| `brightness` | **int** | `80` | روشنایی صفحه به درصد | 0-100 |

**توضیح:**
- `0` = کاملاً خاموش
- `50` = روشنایی متوسط
- `100` = حداکثر روشنایی

---

## 6️⃣ حذف هشدار (deleteAlarm)

### استفاده:
```dart
await sdk.deleteAlarm(1);  // ⬅️ int
```

### جدول پارامترها:

| پارامتر | نوع | مقدار نمونه | توضیح | محدوده |
|---------|-----|-------------|--------|---------|
| `alarmId` | **int** | `1` | شناسه هشدار برای حذف | 1-10 |

---

## 7️⃣ داده‌های خروجی

### VeepooDevice (دستگاه یافت شده)

```dart
VeepooDevice {
  macAddress: String,    // "AA:BB:CC:DD:EE:FF"
  name: String,          // "Veepoo Watch"
  rssi: int,             // -65 (قدرت سیگنال)
  isBonded: bool,        // true/false
}
```

### HeartRateData (ضربان قلب)

```dart
HeartRateData {
  heartRate: int,        // 75 (BPM)
  timestamp: DateTime,   // زمان اندازه‌گیری
  status: String,        // "normal"
  isMeasuring: bool,     // true/false
}
```

### BloodPressureData (فشار خون)

```dart
BloodPressureData {
  systolic: int,         // 120 (فشار بالا)
  diastolic: int,        // 80 (فشار پایین)
  timestamp: DateTime,   // زمان اندازه‌گیری
  status: String,        // "normal"
  isMeasuring: bool,     // true/false
}
```

### BloodOxygenData (اکسیژن خون)

```dart
BloodOxygenData {
  oxygenLevel: int,      // 98 (درصد)
  timestamp: DateTime,   // زمان اندازه‌گیری
  status: String,        // "normal"
  isMeasuring: bool,     // true/false
}
```

### StepData (قدم و فعالیت)

```dart
StepData {
  steps: int,            // 5420 (تعداد قدم)
  distance: double,      // 4250.5 (متر)
  calories: double,      // 320.5 (kcal)
  timestamp: DateTime,   // زمان
}
```

### SleepData (داده خواب)

```dart
SleepData {
  startTime: DateTime,   // زمان شروع خواب
  endTime: DateTime,     // زمان پایان خواب
  deepSleep: int,        // 120 (دقیقه)
  lightSleep: int,       // 240 (دقیقه)
  awake: int,            // 15 (دقیقه)
  totalSleep: int,       // 360 (دقیقه) - محاسبه شده
}
```

### DeviceVersion (نسخه دستگاه)

```dart
DeviceVersion {
  hardwareVersion: String,   // "V2.1.5"
  softwareVersion: String,   // "2.3.28"
  deviceModel: String,       // "VP-WATCH-PRO"
  testVersion: String,       // "TEST_001"
}
```

---

## 📌 نکات مهم

### 1. تفاوت int و double:

```dart
// ❌ اشتباه
height: 175.5,  // height باید int باشد نه double

// ✅ درست
height: 175,    // عدد صحیح بدون اعشار
weight: 70.5,   // عدد اعشاری
```

### 2. تفاوت String و int:

```dart
// ❌ اشتباه
macAddress: 112233445566,  // باید String باشد

// ✅ درست
macAddress: "AA:BB:CC:DD:EE:FF",  // متن با کوتیشن
```

### 3. تفاوت bool و int:

```dart
// ❌ اشتباه
isEnabled: 1,  // باید bool باشد نه عدد

// ✅ درست
isEnabled: true,  // یا false
```

### 4. مقادیر null (اختیاری):

```dart
// ✅ پارامترهای اختیاری می‌توانند null باشند
title: null,           // یا
title: "متن دلخواه",  // یا
// title را اصلاً ننویسید
```

---

## 🔢 خلاصه نوع داده‌ها

| نوع Dart | نام فارسی | مثال | توضیح |
|----------|-----------|------|--------|
| `int` | عدد صحیح | `10`, `175`, `-65` | بدون اعشار |
| `double` | عدد اعشاری | `70.5`, `3.14` | با نقطه اعشار |
| `String` | متن | `"سلام"`, `"AA:BB:CC"` | با کوتیشن |
| `bool` | درست/غلط | `true`, `false` | فقط دو مقدار |
| `DateTime` | تاریخ و زمان | `DateTime.now()` | شیء زمان |
| `enum` | شمارش | `NotificationType.call` | مقادیر از پیش تعریف شده |

---

## ✅ چک‌لیست سریع

قبل از اجرای کد، این موارد را بررسی کنید:

- [ ] MAC Address با فرمت `"XX:XX:XX:XX:XX:XX"` است (String)
- [ ] قد و سن و ساعت و دقیقه عدد صحیح (`int`) هستند
- [ ] وزن و مسافت و کالری عدد اعشاری (`double`) هستند
- [ ] فعال/غیرفعال با `true` یا `false` نوشته شده (نه 0 یا 1)
- [ ] متن‌ها داخل کوتیشن (`"..."`) نوشته شده‌اند
- [ ] مقادیر enum با نقطه نوشته شده (`NotificationType.call`)

---

## 💡 مثال کامل

```dart
import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk.dart';

void example() async {
  final sdk = VeepooSDK.instance;

  // 1. اتصال
  await sdk.connect(
    macAddress: 'AA:BB:CC:DD:EE:FF',  // String ⬅️
    password: '0000',                  // String ⬅️
    is24Hour: true,                    // bool ⬅️
  );

  // 2. همگام‌سازی اطلاعات
  await sdk.syncPersonInfo(PersonInfo(
    height: 175,       // int ⬅️
    weight: 70.5,      // double ⬅️
    age: 28,           // int ⬅️
    sex: 1,            // int ⬅️ (0 یا 1)
  ));

  // 3. تنظیم هشدار
  await sdk.setAlarm(AlarmData(
    alarmId: 1,        // int ⬅️
    hour: 7,           // int ⬅️ (0-23)
    minute: 30,        // int ⬅️ (0-59)
    repeatDays: 31,    // int ⬅️ (bit mask)
    isEnabled: true,   // bool ⬅️
  ));

  // 4. ارسال اعلان
  await sdk.sendNotification(
    type: NotificationType.call,  // enum ⬅️
    title: 'علی',                 // String ⬅️
    content: 'تماس',              // String ⬅️
  );

  // 5. تنظیم روشنایی
  await sdk.setScreenBrightness(80);  // int ⬅️ (0-100)

  // 6. خواندن باتری
  final battery = await sdk.readBattery();  // ➡️ int? (0-100)
  print('Battery: $battery%');
}
```

---

این راهنما به شما کمک می‌کند تا دقیقاً بدانید هر پارامتر چه نوعی است و چگونه باید استفاده شود. 🎯
