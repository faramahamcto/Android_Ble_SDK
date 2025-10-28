/// Connection state with device
class ConnectionState {
  /// Connection status
  final ConnectionStatus status;

  /// Optional error message
  final String? errorMessage;

  /// Device MAC address
  final String? macAddress;

  ConnectionState({
    required this.status,
    this.errorMessage,
    this.macAddress,
  });

  factory ConnectionState.fromMap(Map<String, dynamic> map) {
    return ConnectionState(
      status: ConnectionStatus.values[map['status'] as int? ?? 0],
      errorMessage: map['errorMessage'] as String?,
      macAddress: map['macAddress'] as String?,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'status': status.index,
      'errorMessage': errorMessage,
      'macAddress': macAddress,
    };
  }

  @override
  String toString() => 'ConnectionState(status: $status, mac: $macAddress)';
}

/// Connection status enum
enum ConnectionStatus {
  disconnected,
  connecting,
  connected,
  disconnecting,
  error,
}
