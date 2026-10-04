package com.dronecontrol.data

/**
 * Standard GPS fix quality indicator.
 */
enum class GpsFix(val label: String) {
    NO_GPS("NO GPS"),
    NO_FIX("NO FIX"),
    FIX_2D("2D FIX"),
    FIX_3D("3D FIX"),
    DGPS("DGPS"),
    RTK_FLOAT("RTK FLOAT"),
    RTK_FIXED("RTK FIX")
}
