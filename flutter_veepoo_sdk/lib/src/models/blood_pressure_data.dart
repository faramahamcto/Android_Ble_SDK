/// Blood pressure measurement data
class BloodPressureData {
  /// Systolic pressure (high pressure)
  final int systolic;

  /// Diastolic pressure (low pressure)
  final int diastolic;

  /// Timestamp of measurement
  final DateTime timestamp;

  /// Measurement status
  final String status;

  /// Is measuring
  final bool isMeasuring;

  BloodPressureData({
    required this.systolic,
    required this.diastolic,
    required this.timestamp,
    this.status = 'normal',
    this.isMeasuring = false,
  });

  factory BloodPressureData.fromMap(Map<String, dynamic> map) {
    return BloodPressureData(
      systolic: map['systolic'] as int,
      diastolic: map['diastolic'] as int,
      timestamp: DateTime.fromMillisecondsSinceEpoch(map['timestamp'] as int),
      status: map['status'] as String? ?? 'normal',
      isMeasuring: map['isMeasuring'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'systolic': systolic,
      'diastolic': diastolic,
      'timestamp': timestamp.millisecondsSinceEpoch,
      'status': status,
      'isMeasuring': isMeasuring,
    };
  }

  @override
  String toString() => 'BloodPressureData(BP: $systolic/$diastolic, time: $timestamp)';
}
