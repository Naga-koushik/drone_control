package com.dronecontrol.drone

import com.dronecontrol.data.ConnectionState
import com.dronecontrol.data.FlightMode
import com.dronecontrol.data.Telemetry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Primary abstraction interface for controlling a drone.
 * This completely isolates the UI and ViewModel from whether the drone is running in:
 * - Offline Simulator Mode (MockDroneConnection)
 * - Wi-Fi ESP32 Bridge
 * - Bluetooth ESP32 Bridge
 * - USB Serial MAVLink
 */
interface DroneConnection {

    /**
     * Observable connection state of the drone link.
     */
    val connectionState: StateFlow<ConnectionState>

    /**
     * Whether this connection is an internal simulation.
     */
    val isSimulation: Boolean

    /**
     * Initiates connection to the drone / bridge.
     */
    suspend fun connect(): Result<Unit>

    /**
     * Closes the connection and releases background simulation/network jobs.
     */
    suspend fun disconnect()

    /**
     * Arms drone motors (enables propulsion).
     */
    suspend fun arm(): Result<Unit>

    /**
     * Disarms drone motors (disables propulsion).
     */
    suspend fun disarm(): Result<Unit>

    /**
     * Commands drone to takeoff to a specific target altitude in meters.
     */
    suspend fun takeoff(altitude: Double = 10.0): Result<Unit>

    /**
     * Commands drone to land vertically at current position.
     */
    suspend fun land(): Result<Unit>

    /**
     * Commands Return to Launch (RTL) - flies back to takeoff coordinates and lands.
     */
    suspend fun returnToLaunch(): Result<Unit>

    /**
     * Commands the drone to hold current 3D position (Loiter/Hold).
     */
    suspend fun hold(): Result<Unit>

    /**
     * Changes flight controller operating mode.
     */
    suspend fun setFlightMode(mode: FlightMode): Result<Unit>

    /**
     * Sends normalized manual control inputs from virtual or physical joysticks.
     *
     * @param roll -1.0 (full left) to 1.0 (full right)
     * @param pitch -1.0 (full back / pitch up) to 1.0 (full forward / pitch down)
     * @param yaw -1.0 (full CCW yaw) to 1.0 (full CW yaw)
     * @param throttle -1.0 (min thrust / descent) to 1.0 (max thrust / climb)
     */
    suspend fun sendManualControl(
        roll: Float,
        pitch: Float,
        yaw: Float,
        throttle: Float
    ): Result<Unit>

    /**
     * Continuous reactive stream of drone telemetry.
     */
    fun telemetry(): Flow<Telemetry>
}
