package com.dronecontrol.utils

object Constants {
    const val DEFAULT_IP_ADDRESS = "192.168.4.1"
    const val DEFAULT_MAVLINK_PORT = 14550
    const val DEFAULT_TAKEOFF_ALTITUDE_METERS = 10.0
    const val MIN_TAKEOFF_ALTITUDE_METERS = 1.0
    const val MAX_TAKEOFF_ALTITUDE_METERS = 120.0 // Regulatory 400ft ceiling
    const val LOW_BATTERY_THRESHOLD_PCT = 20
    const val CRITICAL_BATTERY_THRESHOLD_PCT = 10
}
