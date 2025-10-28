/// Blood oxygen (SpO2) measurement data
class BloodOxygenData {
  /// Blood oxygen level (0-100%)
  final int oxygenLevel;

  /// Timestamp of measurement
  final DateTime timestamp;

  /// Measurement status
  final String status;

  /// Is measuring
  final bool isMeasuring;

  BloodOxygenData({
    required this.oxygenLevel,
    required this.timestamp,
    this.status = 'normal',
    this.isMeasuring = false,
  });

  factory BloodOxygenData.fromMap(Map<String, dynamic> map) {
    return BloodOxygenData(
      oxygenLevel: map['oxygenLevel'] as int,
      timestamp: DateTime.fromMillisecondsSinceEpoch(map['timestamp'] as int),
      status: map['status'] as String? ?? 'normal',
      isMeasuring: map['isMeasuring'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'oxygenLevel': oxygenLevel,
      'timestamp': timestamp.millisecondsSinceEpoch,
      'status': status,
      'isMeasuring': isMeasuring,
    };
  }

  @override
  String toString() => 'BloodOxygenData(SpO2: $oxygenLevel%, time: $timestamp)';
}
