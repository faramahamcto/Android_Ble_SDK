/// Notification data
class NotificationData {
  /// Notification type
  final NotificationType type;

  /// Notification title
  final String title;

  /// Notification content
  final String? content;

  /// Timestamp
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
