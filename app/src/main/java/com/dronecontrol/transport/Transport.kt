package com.dronecontrol.transport

import com.dronecontrol.data.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Low-level bidirectional transport abstraction.
 * Decouples physical medium (Wi-Fi sockets, Bluetooth RFCOMM, USB OTG, or Mock)
 * from high-level drone protocols (MAVLink).
 */
interface Transport {
    val connectionState: StateFlow<ConnectionState>

    /**
     * Initiates connection to the remote device/simulator.
     */
    suspend fun connect(): Result<Unit>

    /**
     * Closes the active transport link.
     */
    suspend fun disconnect()

    /**
     * Transmits raw string or formatted packet through the transport.
     */
    suspend fun send(message: String): Result<Unit>

    /**
     * Transmits raw bytes through the transport.
     */
    suspend fun send(data: ByteArray): Result<Unit> = send(String(data))

    /**
     * Stream of raw incoming string/packet data from the transport medium.
     */
    fun incomingMessages(): Flow<String>
}
