# راهنمای سریع - نوع داده‌های ورودی

## 🚀 مرجع فوری برای توسعه‌دهندگان

### اتصال به دستگاه
```dart
await sdk.connect(
  macAddress: String,     // "AA:BB:CC:DD:EE:FF"
  password: String,       // "0000" (پیش‌فرض)
  is24Hour: bool,         // true/false (پیش‌فرض: true)
);
```

### اطلاعات شخصی
```dart
PersonInfo(
  height: int,            // 175 (سانتی‌متر)
  weight: double,         // 70.5 (کیلوگرم)
  age: int,               // 28 (سال)
  sex: int,               // 0=زن، 1=مرد
  stepLength: int,        // 70 (سانتی‌متر، اختیاری)
  targetSteps: int,       // 10000 (اختیاری)
);
```

### هشدار
```dart
AlarmData(
  alarmId: int,           // 1-10
  hour: int,              // 0-23 (24 ساعته)
  minute: int,            // 0-59
  repeatDays: int,        // 0=بدون تکرار، 31=روزهای کاری، 127=هر روز
  isEnabled: bool,        // true/false (پیش‌فرض: true)
  title: String?,         // "بیدار شو" (اختیاری)
);
```

### اعلان
```dart
await sdk.sendNotification(
  type: NotificationType,        // .call, .sms, .whatsapp, ...
  title: String,                 // "علی احمدی"
  content: String?,              // "تماس ورودی" (اختیاری)
);
```

### تنظیمات دستگاه
```dart
await sdk.setScreenBrightness(int);  // 0-100 (درصد روشنایی)
await sdk.deleteAlarm(int);          // 1-10 (شناسه هشدار)
```

---

## 📊 جدول سریع نوع‌ها

| پارامتر | نوع | مثال | توضیح |
|---------|-----|------|--------|
| MAC Address | `String` | `"AA:BB:CC:DD:EE:FF"` | آدرس مک |
| Password | `String` | `"0000"` | رمز عبور |
| Height | `int` | `175` | قد (cm) |
| Weight | `double` | `70.5` | وزن (kg) |
| Age | `int` | `28` | سن |
| Sex | `int` | `0` یا `1` | جنسیت |
| Hour | `int` | `0-23` | ساعت |
| Minute | `int` | `0-59` | دقیقه |
| Alarm ID | `int` | `1-10` | شناسه هشدار |
| Repeat Days | `int` | `0-127` | روزهای تکرار |
| Is Enabled | `bool` | `true`/`false` | فعال/غیرفعال |
| Title | `String?` | `"متن"` | عنوان اختیاری |
| Brightness | `int` | `0-100` | روشنایی |

---

## ⚡ نکات سریع

### ✅ درست
```dart
height: 175          // int بدون اعشار
weight: 70.5         // double با اعشار
macAddress: "AA:BB"  // String با کوتیشن
isEnabled: true      // bool
```

### ❌ اشتباه
```dart
height: 175.5        // باید int باشد
weight: 70           // باید double باشد (70.0)
macAddress: AA:BB    // باید String باشد ("AA:BB")
isEnabled: 1         // باید bool باشد (true)
```

---

## 🔢 repeatDays (روزهای تکرار هشدار)

| مقدار | معنی |
|-------|------|
| `0` | بدون تکرار |
| `1` | دوشنبه |
| `2` | سه‌شنبه |
| `4` | چهارشنبه |
| `8` | پنج‌شنبه |
| `16` | جمعه |
| `32` | شنبه |
| `64` | یکشنبه |
| `31` | روزهای کاری (دوشنبه-جمعه) |
| `96` | آخر هفته |
| `127` | هر روز |

---

## 📱 NotificationType

```dart
NotificationType.call       // تماس
NotificationType.sms        // پیامک
NotificationType.whatsapp   // WhatsApp
NotificationType.telegram   // Telegram
NotificationType.instagram  // Instagram
NotificationType.facebook   // Facebook
NotificationType.other      // سایر
```

---

## 📖 برای اطلاعات بیشتر

- راهنمای کامل نوع داده‌ها: `DATA_TYPES_GUIDE.md`
- مستندات اصلی: `README.md` یا `README_FA.md`
