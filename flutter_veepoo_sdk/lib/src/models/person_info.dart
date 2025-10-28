/// Personal information to sync with device
class PersonInfo {
  /// Height in cm
  final int height;

  /// Weight in kg
  final double weight;

  /// Age
  final int age;

  /// Gender (0: female, 1: male)
  final int sex;

  /// Step length in cm
  final int stepLength;

  /// Target steps per day
  final int targetSteps;

  /// Target distance in meters
  final int targetDistance;

  /// Target calories in kcal
  final int targetCalories;

  PersonInfo({
    required this.height,
    required this.weight,
    required this.age,
    required this.sex,
    this.stepLength = 70,
    this.targetSteps = 10000,
    this.targetDistance = 8000,
    this.targetCalories = 2000,
  });

  Map<String, dynamic> toMap() {
    return {
      'height': height,
      'weight': weight,
      'age': age,
      'sex': sex,
      'stepLength': stepLength,
      'targetSteps': targetSteps,
      'targetDistance': targetDistance,
      'targetCalories': targetCalories,
    };
  }

  factory PersonInfo.fromMap(Map<String, dynamic> map) {
    return PersonInfo(
      height: map['height'] as int,
      weight: (map['weight'] as num).toDouble(),
      age: map['age'] as int,
      sex: map['sex'] as int,
      stepLength: map['stepLength'] as int? ?? 70,
      targetSteps: map['targetSteps'] as int? ?? 10000,
      targetDistance: map['targetDistance'] as int? ?? 8000,
      targetCalories: map['targetCalories'] as int? ?? 2000,
    );
  }
}
