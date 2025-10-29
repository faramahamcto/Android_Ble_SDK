import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Veepoo SDK Example',
      theme: ThemeData(primarySwatch: Colors.blue, useMaterial3: true),
      home: const HomePage(),
    );
  }
}

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final VeepooSDK _sdk = VeepooSDK.instance;
  final List<VeepooDevice> _devices = [];
  bool _isScanning = false;
  bool _isConnected = false;
  String? _connectedDevice;
  StreamSubscription? _scanSubscription;
  StreamSubscription? _connectionSubscription;
  StreamSubscription? _heartRateSubscription;

  int? _currentHeartRate;
  BloodPressureData? _currentBP;
  BloodOxygenData? _currentSpO2;
  StepData? _stepData;
  int? _battery;

  @override
  void initState() {
    super.initState();
    _initSDK();
  }

  @override
  void dispose() {
    _scanSubscription?.cancel();
    _connectionSubscription?.cancel();
    _heartRateSubscription?.cancel();
    super.dispose();
  }

  Future<void> _initSDK() async {
    try {
      // Request permissions first
      final permissionGranted = await _requestPermissions();
      if (!permissionGranted) {
        _showMessage('Permissions required for Bluetooth functionality');
        return;
      }

      final initialized = await _sdk.initialize();
      if (initialized) {
        _showMessage('SDK initialized successfully');
        _listenToConnectionState();
      }
    } catch (e) {
      _showMessage('Failed to initialize SDK: $e');
    }
  }

  /// Request all necessary permissions for BLE
  Future<bool> _requestPermissions() async {
    if (Platform.isAndroid) {
      Map<Permission, PermissionStatus> statuses;

      // Check Android version and request appropriate permissions
      if (await _isAndroid12OrHigher()) {
        // Android 12+ (API 31+) requires BLUETOOTH_SCAN and BLUETOOTH_CONNECT
        statuses = await [
          Permission.bluetoothScan,
          Permission.bluetoothConnect,
          Permission.location, // Still needed for some devices
        ].request();
      } else {
        // Android 11 and below require Location permission
        statuses = await [
          Permission.bluetooth,
          Permission.location,
          Permission.locationWhenInUse,
        ].request();
      }

      // Check if all permissions are granted
      bool allGranted = statuses.values.every((status) => status.isGranted);

      if (!allGranted) {
        // Show dialog to explain why permissions are needed
        if (mounted) {
          _showPermissionDialog();
        }
        return false;
      }

      return true;
    }

    // iOS permissions (if needed in future)
    return true;
  }

  Future<bool> _isAndroid12OrHigher() async {
    // Android 12 = API 31
    if (Platform.isAndroid) {
      // Check if bluetoothScan permission exists (only on Android 12+)
      final status = await Permission.bluetoothScan.status;
      return status != PermissionStatus.undetermined ||
             await Permission.bluetoothScan.isGranted ||
             await Permission.bluetoothScan.isDenied;
    }
    return false;
  }

  void _showPermissionDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Permissions Required'),
        content: const Text(
          'This app needs Bluetooth and Location permissions to scan for and connect to your smartwatch.\n\n'
          'Please grant the permissions in Settings.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              openAppSettings();
            },
            child: const Text('Open Settings'),
          ),
        ],
      ),
    );
  }

  void _listenToConnectionState() {
    _connectionSubscription = _sdk.connectionStateStream.listen((state) {
      setState(() {
        _isConnected = state.status == ConnectionStatus.connected;
        if (_isConnected) {
          _connectedDevice = state.macAddress;
          _showMessage('Connected to ${state.macAddress}');
          _readDeviceInfo();
        } else {
          _connectedDevice = null;
          _showMessage('Disconnected');
        }
      });
    });
  }

  Future<void> _startScan() async {
    if (_isScanning) return;

    // Check permissions before scanning
    final hasPermissions = await _checkPermissions();
    if (!hasPermissions) {
      _showMessage('Please grant Bluetooth permissions');
      await _requestPermissions();
      return;
    }

    setState(() {
      _devices.clear();
      _isScanning = true;
    });

    try {
      _scanSubscription = _sdk.startScan().listen((device) {
        setState(() {
          if (!_devices.any((d) => d.macAddress == device.macAddress)) {
            _devices.add(device);
          }
        });
      });

      // Stop scan after 10 seconds
      Future.delayed(const Duration(seconds: 10), () {
        _stopScan();
      });
    } catch (e) {
      _showMessage('Failed to start scan: $e');
      setState(() {
        _isScanning = false;
      });
    }
  }

  Future<bool> _checkPermissions() async {
    if (Platform.isAndroid) {
      if (await _isAndroid12OrHigher()) {
        return await Permission.bluetoothScan.isGranted &&
               await Permission.bluetoothConnect.isGranted;
      } else {
        return await Permission.bluetooth.isGranted &&
               await Permission.location.isGranted;
      }
    }
    return true;
  }

  Future<void> _stopScan() async {
    try {
      await _sdk.stopScan();
      _scanSubscription?.cancel();
      setState(() {
        _isScanning = false;
      });
    } catch (e) {
      _showMessage('Failed to stop scan: $e');
    }
  }

  Future<void> _connectToDevice(VeepooDevice device) async {
    try {
      _showMessage('Connecting to ${device.name}...');
      final connected = await _sdk.connect(macAddress: device.macAddress);
      if (connected) {
        // Sync personal info
        await _sdk.syncPersonInfo(PersonInfo(height: 170, weight: 70.0, age: 25, sex: 1));
      }
    } catch (e) {
      _showMessage('Failed to connect: $e');
    }
  }

  Future<void> _disconnect() async {
    try {
      await _sdk.disconnect();
    } catch (e) {
      _showMessage('Failed to disconnect: $e');
    }
  }

  Future<void> _readDeviceInfo() async {
    try {
      // Read battery
      final battery = await _sdk.readBattery();
      setState(() {
        _battery = battery;
      });

      // Read step data
      final stepData = await _sdk.readStepData();
      setState(() {
        _stepData = stepData;
      });
    } catch (e) {
      _showMessage('Failed to read device info: $e');
    }
  }

  Future<void> _startHeartRateMonitoring() async {
    try {
      _heartRateSubscription?.cancel();
      _heartRateSubscription = _sdk.startHeartRateDetection().listen((hrData) {
        setState(() {
          _currentHeartRate = hrData.heartRate;
        });
      });
      _showMessage('Heart rate monitoring started');
    } catch (e) {
      _showMessage('Failed to start heart rate monitoring: $e');
    }
  }

  Future<void> _stopHeartRateMonitoring() async {
    try {
      await _sdk.stopHeartRateDetection();
      _heartRateSubscription?.cancel();
      setState(() {
        _currentHeartRate = null;
      });
      _showMessage('Heart rate monitoring stopped');
    } catch (e) {
      _showMessage('Failed to stop heart rate monitoring: $e');
    }
  }

  Future<void> _measureBloodPressure() async {
    try {
      final subscription = _sdk.startBloodPressureDetection().listen((bpData) {
        if (!bpData.isMeasuring && bpData.systolic > 0) {
          setState(() {
            _currentBP = bpData;
          });
          _sdk.stopBloodPressureDetection();
        }
      });
      _showMessage('Measuring blood pressure...');
    } catch (e) {
      _showMessage('Failed to measure blood pressure: $e');
    }
  }

  Future<void> _measureBloodOxygen() async {
    try {
      final subscription = _sdk.startBloodOxygenDetection().listen((spo2Data) {
        if (!spo2Data.isMeasuring && spo2Data.oxygenLevel > 0) {
          setState(() {
            _currentSpO2 = spo2Data;
          });
          _sdk.stopBloodOxygenDetection();
        }
      });
      _showMessage('Measuring blood oxygen...');
    } catch (e) {
      _showMessage('Failed to measure blood oxygen: $e');
    }
  }

  Future<void> _findDevice() async {
    try {
      await _sdk.findDevice();
      _showMessage('Device should vibrate now');
    } catch (e) {
      _showMessage('Failed to find device: $e');
    }
  }

  void _showMessage(String message) {
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(message), duration: const Duration(seconds: 2)),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Veepoo SDK Example'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: Column(
        children: [
          // Connection Status
          Container(
            padding: const EdgeInsets.all(16),
            color: _isConnected ? Colors.green[100] : Colors.red[100],
            child: Row(
              children: [
                Icon(
                  _isConnected ? Icons.bluetooth_connected : Icons.bluetooth_disabled,
                  color: _isConnected ? Colors.green : Colors.red,
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    _isConnected ? 'Connected to $_connectedDevice' : 'Disconnected',
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                ),
                if (_isConnected) IconButton(icon: const Icon(Icons.close), onPressed: _disconnect),
              ],
            ),
          ),

          // Device List or Health Data
          Expanded(child: _isConnected ? _buildHealthDataView() : _buildDeviceList()),
        ],
      ),
      floatingActionButton: !_isConnected
          ? FloatingActionButton(
              onPressed: _isScanning ? _stopScan : _startScan,
              child: Icon(_isScanning ? Icons.stop : Icons.search),
            )
          : null,
    );
  }

  Widget _buildDeviceList() {
    if (_devices.isEmpty && !_isScanning) {
      return const Center(
        child: Padding(
          padding: EdgeInsets.all(32.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.bluetooth_searching, size: 64, color: Colors.grey),
              SizedBox(height: 16),
              Text(
                'Tap the search button to scan for devices',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16),
              ),
            ],
          ),
        ),
      );
    }

    if (_devices.isEmpty && _isScanning) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: 16),
            Text('Scanning for devices...'),
          ],
        ),
      );
    }

    return ListView.builder(
      itemCount: _devices.length,
      itemBuilder: (context, index) {
        final device = _devices[index];
        return ListTile(
          leading: const Icon(Icons.watch),
          title: Text(device.name),
          subtitle: Text('${device.macAddress}\nRSSI: ${device.rssi}'),
          isThreeLine: true,
          trailing: const Icon(Icons.chevron_right),
          onTap: () => _connectToDevice(device),
        );
      },
    );
  }

  Widget _buildHealthDataView() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Device Info Card
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Device Info', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  if (_battery != null) Text('Battery: $_battery%'),
                  if (_stepData != null) ...[
                    Text('Steps: ${_stepData!.steps}'),
                    Text('Distance: ${_stepData!.distance.toStringAsFixed(0)}m'),
                    Text('Calories: ${_stepData!.calories.toStringAsFixed(0)} kcal'),
                  ],
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Heart Rate Card
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Heart Rate', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  if (_currentHeartRate != null)
                    Text('$_currentHeartRate BPM', style: const TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      ElevatedButton(onPressed: _startHeartRateMonitoring, child: const Text('Start')),
                      const SizedBox(width: 8),
                      ElevatedButton(onPressed: _stopHeartRateMonitoring, child: const Text('Stop')),
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Blood Pressure Card
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Blood Pressure', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  if (_currentBP != null)
                    Text(
                      '${_currentBP!.systolic}/${_currentBP!.diastolic} mmHg',
                      style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                    ),
                  const SizedBox(height: 8),
                  ElevatedButton(onPressed: _measureBloodPressure, child: const Text('Measure')),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Blood Oxygen Card
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Blood Oxygen (SpO2)', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  if (_currentSpO2 != null)
                    Text(
                      '${_currentSpO2!.oxygenLevel}%',
                      style: const TextStyle(fontSize: 32, fontWeight: FontWeight.bold),
                    ),
                  const SizedBox(height: 8),
                  ElevatedButton(onPressed: _measureBloodOxygen, child: const Text('Measure')),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Device Actions
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  const Text('Device Actions', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  ElevatedButton.icon(
                    onPressed: _findDevice,
                    icon: const Icon(Icons.vibration),
                    label: const Text('Find Device'),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
