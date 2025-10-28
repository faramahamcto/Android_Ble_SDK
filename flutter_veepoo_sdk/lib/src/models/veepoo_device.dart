/// Represents a discovered BLE device
///
/// Example:
/// ```dart
/// VeepooDevice(
///   macAddress: 'AA:BB:CC:DD:EE:FF',  // String - MAC address
///   name: 'Veepoo Watch',              // String - Device name
///   rssi: -65,                         // int - Signal strength
///   isBonded: false,                   // bool - Pairing status
/// );
/// ```
class VeepooDevice {
  /// Device MAC address (Bluetooth hardware address)
  ///
  /// **Type**: `String` (Text string)
  ///
  /// **Format**: Six pairs of hexadecimal digits separated by colons
  ///
  /// **Example**: `"AA:BB:CC:DD:EE:FF"` or `"12:34:56:78:9A:BC"`
  final String macAddress;

  /// Device name (Bluetooth advertised name)
  ///
  /// **Type**: `String` (Text string)
  ///
  /// **Example**: `"Veepoo Watch"`, `"Smart Band Pro"`
  final String name;

  /// RSSI (Received Signal Strength Indicator)
  ///
  /// **Type**: `int` (Integer number, negative)
  ///
  /// **Range**: Typically `-100` to `-30` dBm
  ///
  /// **Interpretation**:
  /// - `-30` to `-50` = Excellent signal
  /// - `-50` to `-70` = Good signal
  /// - `-70` to `-90` = Fair signal
  /// - `-90` to `-100` = Weak signal
  ///
  /// **Example**: `-65` means moderate signal strength
  final int rssi;

  /// Whether device is already bonded/paired
  ///
  /// **Type**: `bool` (Boolean - true/false)
  ///
  /// **Values**:
  /// - `true` = Device is already paired with phone
  /// - `false` = Device is not paired yet
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
