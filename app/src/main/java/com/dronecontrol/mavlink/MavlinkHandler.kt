package com.dronecontrol.mavlink

import com.dronecontrol.data.FlightCommand
import com.dronecontrol.data.Telemetry
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining serialization of flight commands into MAVLink packets,
 * and deserialization of incoming byte stream packets into domain Telemetry.
 */
interface MavlinkHandler {
    /**
     * Parses incoming byte chunks into MAVLink packets and emits high-level telemetry updates.
     */
    fun parseStream(rawStream: Flow<String>): Flow<Telemetry>

    /**
     * Encodes a domain flight command into MAVLink byte payload to transmit over Transport.
     */
    fun encodeCommand(command: FlightCommand): ByteArray
}

/**
 * Placeholder implementation of MavlinkHandler to be expanded with full MAVLink microservices
 * (Mission, Parameter, Command protocol, etc.) once the ESP32 hardware bridge is integrated.
 */
class DefaultMavlinkHandler : MavlinkHandler {
    override fun parseStream(rawStream: Flow<String>): Flow<Telemetry> {
        // Placeholder parser pipeline
        throw UnsupportedOperationException("Full MAVLink parser pipeline will be activated with physical transport")
    }

    override fun encodeCommand(command: FlightCommand): ByteArray {
        // Placeholder command encoder
        return ByteArray(0)
    }
}
