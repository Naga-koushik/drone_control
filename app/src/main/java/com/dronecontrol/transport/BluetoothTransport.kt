package com.dronecontrol.transport

import com.dronecontrol.data.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Placeholder implementation for Bluetooth (BLE GATT / Classic SPP) communication
 * with the ESP32 MAVLink bridge.
 */
class BluetoothTransport(
    var targetDeviceAddress: String = "00:00:00:00:00:00"
) : Transport {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<String>(extraBufferCapacity = 64)

    override suspend fun connect(): Result<Unit> {
        _connectionState.value = ConnectionState.CONNECTING
        // Placeholder: Will initiate BluetoothSocket / BluetoothGatt connection
        _connectionState.value = ConnectionState.CONNECTED
        _incomingMessages.emit("[BT] Connected to ESP32 BLE device $targetDeviceAddress (STUB)")
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTING
        _connectionState.value = ConnectionState.DISCONNECTED
        _incomingMessages.emit("[BT] Disconnected from ESP32 BLE device")
    }

    override suspend fun send(message: String): Result<Unit> {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Bluetooth transport not connected"))
        }
        // Placeholder: write to RFCOMM stream or GATT characteristic
        return Result.success(Unit)
    }

    override fun incomingMessages(): Flow<String> = _incomingMessages.asSharedFlow()
}
