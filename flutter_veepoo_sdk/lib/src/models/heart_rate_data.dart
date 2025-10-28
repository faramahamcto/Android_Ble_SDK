/// Heart rate measurement data
class HeartRateData {
  /// Heart rate value in BPM
  final int heartRate;

  /// Timestamp of measurement
  final DateTime timestamp;

  /// Measurement status
  final String status;

  /// Is measuring
  final bool isMeasuring;

  HeartRateData({
    required this.heartRate,
    required this.timestamp,
    this.status = 'normal',
    this.isMeasuring = false,
  });

  factory HeartRateData.fromMap(Map<String, dynamic> map) {
    return HeartRateData(
      heartRate: map['heartRate'] as int,
      timestamp: DateTime.fromMillisecondsSinceEpoch(map['timestamp'] as int),
      status: map['status'] as String? ?? 'normal',
      isMeasuring: map['isMeasuring'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'heartRate': heartRate,
      'timestamp': timestamp.millisecondsSinceEpoch,
      'status': status,
      'isMeasuring': isMeasuring,
    };
  }

  @override
  String toString() => 'HeartRateData(hr: $heartRate BPM, time: $timestamp)';
}
