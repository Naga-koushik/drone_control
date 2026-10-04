package com.dronecontrol.data

/**
 * Standard UAV flight modes supported by ArduPilot / PX4 flight controllers.
 */
enum class FlightMode(val displayName: String, val description: String) {
    MANUAL("MANUAL", "Direct manual motor/surface control"),
    STABILIZE("STABILIZE", "Self-leveling attitude with manual throttle"),
    ALT_HOLD("ALT HOLD", "Altitude hold with self-leveling attitude"),
    POS_HOLD("POS HOLD", "Position and altitude hold via GPS"),
    HOLD("HOLD", "Loiter and maintain 3D position hold"),
    AUTO("AUTO", "Autonomous waypoint mission execution"),
    GUIDED("GUIDED", "Point-and-click navigation directed by GCS"),
    RTL("RTL", "Return to Launch location and land automatically"),
    LAND("LAND", "Autonomous vertical landing at current position")
}
