/// Represents a discovered BLE device
class VeepooDevice {
  /// Device MAC address
  final String macAddress;

  /// Device name
  final String name;

  /// RSSI signal strength
  final int rssi;

  /// Device is already bonded
  final bool isBonded;

  VeepooDevice({
    required this.macAddress,
    required this.name,
    required this.rssi,
    this.isBonded = false,
  });

  factory VeepooDevice.fromMap(Map<String, dynamic> map) {
    return VeepooDevice(
      macAddress: map['macAddress'] as String,
      name: map['name'] as String? ?? 'Unknown',
      rssi: map['rssi'] as int? ?? 0,
      isBonded: map['isBonded'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'macAddress': macAddress,
      'name': name,
      'rssi': rssi,
      'isBonded': isBonded,
    };
  }

  @override
  String toString() => 'VeepooDevice(name: $name, mac: $macAddress, rssi: $rssi)';
}
