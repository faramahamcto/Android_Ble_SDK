/// Step and activity data
class StepData {
  /// Number of steps
  final int steps;

  /// Distance in meters
  final double distance;

  /// Calories burned in kcal
  final double calories;

  /// Timestamp
  final DateTime timestamp;

  StepData({
    required this.steps,
    required this.distance,
    required this.calories,
    required this.timestamp,
  });

  factory StepData.fromMap(Map<String, dynamic> map) {
    return StepData(
      steps: map['steps'] as int,
      distance: (map['distance'] as num).toDouble(),
      calories: (map['calories'] as num).toDouble(),
      timestamp: DateTime.fromMillisecondsSinceEpoch(map['timestamp'] as int),
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'steps': steps,
      'distance': distance,
      'calories': calories,
      'timestamp': timestamp.millisecondsSinceEpoch,
    };
  }

  @override
  String toString() => 'StepData(steps: $steps, distance: ${distance}m, cal: ${calories}kcal)';
}
