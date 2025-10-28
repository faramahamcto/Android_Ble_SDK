/// Sleep data
class SleepData {
  /// Sleep start time
  final DateTime startTime;

  /// Sleep end time
  final DateTime endTime;

  /// Deep sleep duration in minutes
  final int deepSleep;

  /// Light sleep duration in minutes
  final int lightSleep;

  /// Awake duration in minutes
  final int awake;

  /// Total sleep duration in minutes
  int get totalSleep => deepSleep + lightSleep;

  SleepData({
    required this.startTime,
    required this.endTime,
    required this.deepSleep,
    required this.lightSleep,
    required this.awake,
  });

  factory SleepData.fromMap(Map<String, dynamic> map) {
    return SleepData(
      startTime: DateTime.fromMillisecondsSinceEpoch(map['startTime'] as int),
      endTime: DateTime.fromMillisecondsSinceEpoch(map['endTime'] as int),
      deepSleep: map['deepSleep'] as int,
      lightSleep: map['lightSleep'] as int,
      awake: map['awake'] as int,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'startTime': startTime.millisecondsSinceEpoch,
      'endTime': endTime.millisecondsSinceEpoch,
      'deepSleep': deepSleep,
      'lightSleep': lightSleep,
      'awake': awake,
    };
  }

  @override
  String toString() => 'SleepData(total: ${totalSleep}min, deep: ${deepSleep}min, light: ${lightSleep}min)';
}
