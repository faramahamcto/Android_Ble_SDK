/// Alarm configuration
///
/// Example:
/// ```dart
/// // Simple one-time alarm at 7:30 AM
/// final alarm = AlarmData(
///   alarmId: 1,         // int - Unique alarm ID (1-10)
///   hour: 7,            // int - Hour in 24h format (0-23)
///   minute: 30,         // int - Minute (0-59)
/// );
///
/// // Repeating alarm for weekdays (Monday to Friday)
/// final weekdayAlarm = AlarmData(
///   alarmId: 2,
///   hour: 6,
///   minute: 0,
///   repeatDays: 0x1F,   // int - Bit mask: Mon+Tue+Wed+Thu+Fri
///   isEnabled: true,    // bool - Alarm is active
///   title: 'Work',      // String - Optional alarm name
/// );
/// ```
class AlarmData {
  /// Alarm unique identifier
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Example**: `1` for alarm ID 1
  ///
  /// **Range**: Usually 1-10 (depends on device)
  final int alarmId;

  /// Hour of the alarm (24-hour format)
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Example**:
  /// - `7` = 7:00 AM
  /// - `14` = 2:00 PM
  /// - `23` = 11:00 PM
  ///
  /// **Range**: `0-23` (0 = midnight, 23 = 11 PM)
  final int hour;

  /// Minute of the alarm
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Example**: `30` for 30 minutes past the hour
  ///
  /// **Range**: `0-59`
  final int minute;

  /// Which days the alarm repeats (bit mask)
  ///
  /// **Type**: `int` (Integer number as bit mask)
  ///
  /// **Bit Values**:
  /// - Bit 0 (1): Monday
  /// - Bit 1 (2): Tuesday
  /// - Bit 2 (4): Wednesday
  /// - Bit 3 (8): Thursday
  /// - Bit 4 (16): Friday
  /// - Bit 5 (32): Saturday
  /// - Bit 6 (64): Sunday
  ///
  /// **Examples**:
  /// - `0` = No repeat (one-time alarm)
  /// - `1` = Monday only
  /// - `0x1F` or `31` = Weekdays (Mon-Fri)
  /// - `0x60` or `96` = Weekend (Sat-Sun)
  /// - `0x7F` or `127` = Every day
  ///
  /// **Default**: `0` (no repeat)
  final int repeatDays;

  /// Whether the alarm is enabled
  ///
  /// **Type**: `bool` (Boolean - true/false)
  ///
  /// **Values**:
  /// - `true` = Alarm is active
  /// - `false` = Alarm is disabled
  ///
  /// **Default**: `true`
  final bool isEnabled;

  /// Optional alarm name/title
  ///
  /// **Type**: `String?` (Text string, nullable)
  ///
  /// **Example**: `"Wake up"`, `"Medicine"`, `"Workout"`
  ///
  /// **Default**: `null` (no title)
  final String? title;

  AlarmData({
    required this.alarmId,
    required this.hour,
    required this.minute,
    this.repeatDays = 0,
    this.isEnabled = true,
    this.title,
  });

  factory AlarmData.fromMap(Map<String, dynamic> map) {
    return AlarmData(
      alarmId: map['alarmId'] as int,
      hour: map['hour'] as int,
      minute: map['minute'] as int,
      repeatDays: map['repeatDays'] as int? ?? 0,
      isEnabled: map['isEnabled'] as bool? ?? true,
      title: map['title'] as String?,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'alarmId': alarmId,
      'hour': hour,
      'minute': minute,
      'repeatDays': repeatDays,
      'isEnabled': isEnabled,
      'title': title,
    };
  }

  /// Check if alarm repeats on a specific day
  /// [day] - Day of week (1=Monday, 7=Sunday)
  bool repeatsOnDay(int day) {
    if (day < 1 || day > 7) return false;
    final mask = 1 << (day - 1);
    return (repeatDays & mask) != 0;
  }

  @override
  String toString() => 'AlarmData(id: $alarmId, time: $hour:${minute.toString().padLeft(2, '0')}, enabled: $isEnabled)';
}
