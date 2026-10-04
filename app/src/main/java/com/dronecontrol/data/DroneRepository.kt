package com.dronecontrol.data

import com.dronecontrol.drone.DroneConnection
import com.dronecontrol.drone.MockDroneConnection
import com.dronecontrol.transport.BluetoothTransport
import com.dronecontrol.transport.MockTransport
import com.dronecontrol.transport.Transport
import com.dronecontrol.transport.WifiTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Single source of truth for drone communication, telemetry streaming,
 * and command dispatching.
 * Mediates between ViewModel and the active DroneConnection & Transport.
 */
class DroneRepository(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {

    private val _activeConnectionType = MutableStateFlow(ConnectionType.SIMULATOR)
    val activeConnectionType: StateFlow<ConnectionType> = _activeConnectionType.asStateFlow()

    private val _isSimulation = MutableStateFlow(true)
    val isSimulation: StateFlow<Boolean> = _isSimulation.asStateFlow()

    // Active connection instance
    private var currentConnection: DroneConnection = MockDroneConnection()
    private val _connectionNotifier = MutableStateFlow(currentConnection)

    // Log messages from the transport layer for the connection screen console
    private val _consoleLogs = MutableSharedFlow<String>(extraBufferCapacity = 128)
    val consoleLogs: Flow<String> = _consoleLogs.asSharedFlow()

    private var transportSubscriptionJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val connectionState: StateFlow<ConnectionState> = _connectionNotifier
        .flatMapLatest { it.connectionState }
        .stateIn(scope, SharingStarted.Eagerly, ConnectionState.DISCONNECTED)

    @OptIn(ExperimentalCoroutinesApi::class)
    val telemetry: StateFlow<Telemetry> = _connectionNotifier
        .flatMapLatest { it.telemetry() }
        .stateIn(scope, SharingStarted.Eagerly, Telemetry())

    init {
        // Start in Simulator mode
        setupTransportLogCapture(currentConnection)
    }

    /**
     * Connects using the currently selected connection type.
     */
    suspend fun connect(): Result<Unit> {
        _consoleLogs.emit("[REPO] Initiating connect to ${_activeConnectionType.value.title}...")
        return currentConnection.connect().onSuccess {
            _consoleLogs.emit("[REPO] Connected successfully to ${_activeConnectionType.value.title}")
        }.onFailure {
            _consoleLogs.emit("[REPO_ERR] Connection failed: ${it.message}")
        }
    }

    /**
     * Disconnects the active drone link.
     */
    suspend fun disconnect() {
        _consoleLogs.emit("[REPO] Disconnecting...")
        currentConnection.disconnect()
        _consoleLogs.emit("[REPO] Disconnected.")
    }

    /**
     * Switches the active connection type (Simulator, Wi-Fi ESP32, Bluetooth ESP32).
     */
    suspend fun switchConnection(
        type: ConnectionType,
        ipAddress: String = "192.168.4.1",
        port: Int = 14550,
        bluetoothAddress: String = ""
    ) {
        if (connectionState.value.isConnected) {
            disconnect()
        }

        _activeConnectionType.value = type

        when (type) {
            ConnectionType.SIMULATOR -> {
                val mockTransport = MockTransport(scope)
                val mockConnection = MockDroneConnection(transport = mockTransport, scope = scope)
                currentConnection = mockConnection
                _isSimulation.value = true
                setupTransportLogCapture(mockConnection)
            }
            ConnectionType.WIFI -> {
                val wifiTransport = WifiTransport(ipAddress = ipAddress, port = port)
                // For now, Wi-Fi transport is plugged into a DroneConnection placeholder
                // (which simulates communication handshake while waiting for ESP32 hardware)
                val wifiConnection = MockDroneConnection(transport = wifiTransport, scope = scope)
                currentConnection = wifiConnection
                _isSimulation.value = false
                setupTransportLogCapture(wifiConnection)
            }
            ConnectionType.BLUETOOTH -> {
                val btTransport = BluetoothTransport(targetDeviceAddress = bluetoothAddress)
                val btConnection = MockDroneConnection(transport = btTransport, scope = scope)
                currentConnection = btConnection
                _isSimulation.value = false
                setupTransportLogCapture(btConnection)
            }
        }

        _connectionNotifier.value = currentConnection
        _consoleLogs.emit("[CONFIG] Switched connection mode to ${type.title}")
    }

    private fun setupTransportLogCapture(connection: DroneConnection) {
        transportSubscriptionJob?.cancel()
        if (connection is MockDroneConnection) {
            transportSubscriptionJob = scope.launch {
                connection.transport.incomingMessages().collect { msg ->
                    _consoleLogs.emit(msg)
                }
            }
        }
    }

    suspend fun arm(): Result<Unit> = currentConnection.arm()

    suspend fun disarm(): Result<Unit> = currentConnection.disarm()

    suspend fun takeoff(altitude: Double = 10.0): Result<Unit> = currentConnection.takeoff(altitude)

    suspend fun land(): Result<Unit> = currentConnection.land()

    suspend fun returnToLaunch(): Result<Unit> = currentConnection.returnToLaunch()

    suspend fun hold(): Result<Unit> = currentConnection.hold()

    suspend fun setFlightMode(mode: FlightMode): Result<Unit> = currentConnection.setFlightMode(mode)

    suspend fun sendManualControl(
        roll: Float,
        pitch: Float,
        yaw: Float,
        throttle: Float
    ): Result<Unit> = currentConnection.sendManualControl(roll, pitch, yaw, throttle)
}
