/// Alarm configuration
class AlarmData {
  /// Alarm ID
  final int alarmId;

  /// Hour (0-23)
  final int hour;

  /// Minute (0-59)
  final int minute;

  /// Repeat days (bit mask: Monday=1, Tuesday=2, ..., Sunday=64)
  final int repeatDays;

  /// Alarm is enabled
  final bool isEnabled;

  /// Alarm title/name
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
