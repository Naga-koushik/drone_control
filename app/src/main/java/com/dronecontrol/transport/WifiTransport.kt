package com.dronecontrol.transport

import com.dronecontrol.data.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Placeholder implementation for Wi-Fi (UDP/TCP) communication with the ESP32 MAVLink bridge.
 * Once ready, DatagramSocket / Socket handling will be plugged in here without affecting the UI or Repository.
 */
class WifiTransport(
    var ipAddress: String = "192.168.4.1",
    var port: Int = 14550
) : Transport {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<String>(extraBufferCapacity = 64)

    override suspend fun connect(): Result<Unit> {
        _connectionState.value = ConnectionState.CONNECTING
        // Placeholder: Will open DatagramSocket/Socket to ipAddress:port
        _connectionState.value = ConnectionState.CONNECTED
        _incomingMessages.emit("[WIFI] Connected to ESP32 bridge at $ipAddress:$port (STUB)")
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTING
        _connectionState.value = ConnectionState.DISCONNECTED
        _incomingMessages.emit("[WIFI] Disconnected from ESP32 bridge")
    }

    override suspend fun send(message: String): Result<Unit> {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Wi-Fi transport not connected"))
        }
        // Placeholder: send packet to UDP/TCP socket
        return Result.success(Unit)
    }

    override fun incomingMessages(): Flow<String> = _incomingMessages.asSharedFlow()
}
