package com.dronecontrol.data

/**
 * Commands that can be dispatched to the flight controller through DroneConnection.
 */
sealed class FlightCommand {
    object Arm : FlightCommand()
    object Disarm : FlightCommand()
    data class Takeoff(val altitude: Double = 10.0) : FlightCommand()
    object Land : FlightCommand()
    object ReturnToLaunch : FlightCommand()
    object Hold : FlightCommand()
    data class SetFlightMode(val mode: FlightMode) : FlightCommand()
    data class ManualControl(
        val roll: Float,      // -1.0 to 1.0 (left to right)
        val pitch: Float,     // -1.0 to 1.0 (back to forward)
        val yaw: Float,       // -1.0 to 1.0 (ccw to cw)
        val throttle: Float   // -1.0 to 1.0 (down to up) or 0.0 to 1.0
    ) : FlightCommand()
}
