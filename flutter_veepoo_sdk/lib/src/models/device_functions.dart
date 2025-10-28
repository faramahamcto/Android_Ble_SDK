/// Device functions and capabilities
class DeviceFunctions {
  final bool supportHeartRate;
  final bool supportBloodPressure;
  final bool supportBloodOxygen;
  final bool supportTemperature;
  final bool supportSleep;
  final bool supportSteps;
  final bool supportAlarm;
  final bool supportCamera;
  final bool supportFindPhone;
  final bool supportWeather;
  final bool supportECG;
  final bool supportHRV;
  final bool supportSedentary;
  final bool supportDrink;
  final bool supportWashHand;

  DeviceFunctions({
    this.supportHeartRate = false,
    this.supportBloodPressure = false,
    this.supportBloodOxygen = false,
    this.supportTemperature = false,
    this.supportSleep = false,
    this.supportSteps = false,
    this.supportAlarm = false,
    this.supportCamera = false,
    this.supportFindPhone = false,
    this.supportWeather = false,
    this.supportECG = false,
    this.supportHRV = false,
    this.supportSedentary = false,
    this.supportDrink = false,
    this.supportWashHand = false,
  });

  factory DeviceFunctions.fromMap(Map<String, dynamic> map) {
    return DeviceFunctions(
      supportHeartRate: map['supportHeartRate'] as bool? ?? false,
      supportBloodPressure: map['supportBloodPressure'] as bool? ?? false,
      supportBloodOxygen: map['supportBloodOxygen'] as bool? ?? false,
      supportTemperature: map['supportTemperature'] as bool? ?? false,
      supportSleep: map['supportSleep'] as bool? ?? false,
      supportSteps: map['supportSteps'] as bool? ?? false,
      supportAlarm: map['supportAlarm'] as bool? ?? false,
      supportCamera: map['supportCamera'] as bool? ?? false,
      supportFindPhone: map['supportFindPhone'] as bool? ?? false,
      supportWeather: map['supportWeather'] as bool? ?? false,
      supportECG: map['supportECG'] as bool? ?? false,
      supportHRV: map['supportHRV'] as bool? ?? false,
      supportSedentary: map['supportSedentary'] as bool? ?? false,
      supportDrink: map['supportDrink'] as bool? ?? false,
      supportWashHand: map['supportWashHand'] as bool? ?? false,
    );
  }
}
