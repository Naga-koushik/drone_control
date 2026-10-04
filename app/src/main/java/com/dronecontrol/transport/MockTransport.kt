package com.dronecontrol.transport

import com.dronecontrol.data.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * In-memory simulated transport used when running in SIMULATION MODE.
 * Generates synthetic heartbeat / diagnostic lines and logs sent commands.
 */
class MockTransport(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Transport {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _incoming = MutableSharedFlow<String>(extraBufferCapacity = 64)
    private var heartbeatJob: Job? = null

    override suspend fun connect(): Result<Unit> {
        _connectionState.value = ConnectionState.CONNECTING
        delay(400) // Simulate handshake latency
        _connectionState.value = ConnectionState.CONNECTED

        startMockHeartbeats()
        _incoming.emit("[TRANSPORT_INIT] MockTransport connected to virtual drone channel")
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTING
        heartbeatJob?.cancel()
        heartbeatJob = null
        delay(200)
        _connectionState.value = ConnectionState.DISCONNECTED
        _incoming.emit("[TRANSPORT_SHUTDOWN] MockTransport disconnected")
    }

    override suspend fun send(message: String): Result<Unit> {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Transport is not connected"))
        }
        // Echo command reception for simulated diagnostic stream
        _incoming.emit("[TX] $message")
        return Result.success(Unit)
    }

    override fun incomingMessages(): Flow<String> = _incoming.asSharedFlow()

    private fun startMockHeartbeats() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            var seq = 0L
            while (isActive && _connectionState.value == ConnectionState.CONNECTED) {
                delay(1000)
                _incoming.emit("[MAVLINK_HB] seq=$seq sysid=1 compid=1 type=QUADROTOR autopilot=ARDUPILOT")
                seq++
            }
        }
    }
}
