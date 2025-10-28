/// Device version information
class DeviceVersion {
  /// Hardware version
  final String hardwareVersion;

  /// Software version
  final String softwareVersion;

  /// Device model
  final String deviceModel;

  /// Device test version
  final String testVersion;

  DeviceVersion({
    required this.hardwareVersion,
    required this.softwareVersion,
    required this.deviceModel,
    this.testVersion = '',
  });

  factory DeviceVersion.fromMap(Map<String, dynamic> map) {
    return DeviceVersion(
      hardwareVersion: map['hardwareVersion'] as String? ?? '',
      softwareVersion: map['softwareVersion'] as String? ?? '',
      deviceModel: map['deviceModel'] as String? ?? '',
      testVersion: map['testVersion'] as String? ?? '',
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'hardwareVersion': hardwareVersion,
      'softwareVersion': softwareVersion,
      'deviceModel': deviceModel,
      'testVersion': testVersion,
    };
  }

  @override
  String toString() => 'DeviceVersion(hw: $hardwareVersion, sw: $softwareVersion, model: $deviceModel)';
}
