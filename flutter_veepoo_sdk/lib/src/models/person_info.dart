/// Personal information to sync with device
///
/// Example:
/// ```dart
/// final personInfo = PersonInfo(
///   height: 175,        // int - Height in centimeters (e.g. 175 cm)
///   weight: 70.5,       // double - Weight in kilograms (e.g. 70.5 kg)
///   age: 28,            // int - Age in years (e.g. 28)
///   sex: 1,             // int - Gender: 0 = female, 1 = male
///   stepLength: 75,     // int - Step length in cm (optional, default: 70)
///   targetSteps: 10000, // int - Daily step goal (optional, default: 10000)
/// );
/// ```
class PersonInfo {
  /// Height in centimeters
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Example**: `175` means 175 cm
  ///
  /// **Range**: Typically 100-250 cm
  final int height;

  /// Weight in kilograms
  ///
  /// **Type**: `double` (Decimal number)
  ///
  /// **Example**: `70.5` means 70.5 kg
  ///
  /// **Range**: Typically 30.0-200.0 kg
  final double weight;

  /// Age in years
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Example**: `28` means 28 years old
  ///
  /// **Range**: Typically 1-120
  final int age;

  /// Gender
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Values**:
  /// - `0` = Female (زن)
  /// - `1` = Male (مرد)
  ///
  /// **Example**: `sex: 1` for male
  final int sex;

  /// Step length in centimeters
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Example**: `75` means 75 cm per step
  ///
  /// **Default**: `70` cm
  ///
  /// **Range**: Typically 40-100 cm
  final int stepLength;

  /// Target steps per day
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Example**: `10000` means 10,000 steps daily goal
  ///
  /// **Default**: `10000` steps
  final int targetSteps;

  /// Target distance in meters
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Example**: `8000` means 8,000 meters (8 km)
  ///
  /// **Default**: `8000` meters
  final int targetDistance;

  /// Target calories in kilocalories
  ///
  /// **Type**: `int` (Integer number)
  ///
  /// **Example**: `2000` means 2,000 kcal daily goal
  ///
  /// **Default**: `2000` kcal
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
