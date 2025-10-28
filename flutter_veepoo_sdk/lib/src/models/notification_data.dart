/// Notification data for sending to or receiving from device
///
/// Example:
/// ```dart
/// NotificationData(
///   type: NotificationType.call,      // enum - Type of notification
///   title: 'John Doe',                 // String - Main text
///   content: 'Incoming call...',       // String? - Optional details
///   timestamp: DateTime.now(),         // DateTime - When it occurred
/// );
/// ```
class NotificationData {
  /// Type of notification
  ///
  /// **Type**: `NotificationType` (Enum)
  ///
  /// **Values**:
  /// - `NotificationType.call` - Phone call
  /// - `NotificationType.sms` - Text message
  /// - `NotificationType.wechat` - WeChat message
  /// - `NotificationType.qq` - QQ message
  /// - `NotificationType.whatsapp` - WhatsApp
  /// - `NotificationType.facebook` - Facebook
  /// - `NotificationType.twitter` - Twitter/X
  /// - `NotificationType.instagram` - Instagram
  /// - `NotificationType.linkedin` - LinkedIn
  /// - `NotificationType.telegram` - Telegram
  /// - `NotificationType.line` - LINE
  /// - `NotificationType.viber` - Viber
  /// - `NotificationType.other` - Other apps
  ///
  /// **Example**: `type: NotificationType.call`
  final NotificationType type;

  /// Main notification text (caller name, sender, etc.)
  ///
  /// **Type**: `String` (Text string)
  ///
  /// **Example**: `"John Doe"`, `"Mom"`, `"Boss"`
  final String title;

  /// Optional notification content/body
  ///
  /// **Type**: `String?` (Text string, nullable)
  ///
  /// **Example**: `"Incoming call..."`, `"Hello, how are you?"`
  ///
  /// **Default**: `null`
  final String? content;

  /// When the notification occurred
  ///
  /// **Type**: `DateTime` (Date and time object)
  ///
  /// **Example**: `DateTime.now()`, `DateTime(2024, 1, 15, 14, 30)`
  final DateTime timestamp;

  NotificationData({
    required this.type,
    required this.title,
    this.content,
    required this.timestamp,
  });

  factory NotificationData.fromMap(Map<String, dynamic> map) {
    return NotificationData(
      type: NotificationType.values[map['type'] as int? ?? 0],
      title: map['title'] as String,
      content: map['content'] as String?,
      timestamp: DateTime.fromMillisecondsSinceEpoch(map['timestamp'] as int),
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'type': type.index,
      'title': title,
      'content': content,
      'timestamp': timestamp.millisecondsSinceEpoch,
    };
  }

  @override
  String toString() => 'NotificationData(type: $type, title: $title)';
}

/// Notification types
enum NotificationType {
  call,
  sms,
  wechat,
  qq,
  whatsapp,
  facebook,
  twitter,
  instagram,
  linkedin,
  telegram,
  line,
  viber,
  other,
}
