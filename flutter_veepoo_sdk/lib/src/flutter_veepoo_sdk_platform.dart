import 'package:plugin_platform_interface/plugin_platform_interface.dart';

/// Platform interface for flutter_veepoo_sdk
abstract class FlutterVeepooSdkPlatform extends PlatformInterface {
  FlutterVeepooSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterVeepooSdkPlatform? _instance;

  static FlutterVeepooSdkPlatform get instance => _instance!;

  static set instance(FlutterVeepooSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }
}
