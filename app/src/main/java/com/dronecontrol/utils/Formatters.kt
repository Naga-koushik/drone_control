package com.dronecontrol.utils

import java.util.Locale

object Formatters {

    fun formatAltitude(meters: Float): String = String.format(Locale.US, "%.1f m", meters)

    fun formatSpeed(metersPerSec: Float): String = String.format(Locale.US, "%.1f m/s", metersPerSec)

    fun formatVoltage(volts: Float): String = String.format(Locale.US, "%.1f V", volts)

    fun formatBattery(pct: Int): String = "$pct%"

    fun formatHeading(degrees: Float): String = String.format(Locale.US, "%03d°", degrees.toInt() % 360)

    fun formatCoordinate(latOrLon: Double): String = String.format(Locale.US, "%.6f", latOrLon)

    fun formatDistance(meters: Float): String {
        return if (meters >= 1000f) {
            String.format(Locale.US, "%.2f km", meters / 1000f)
        } else {
            String.format(Locale.US, "%.0f m", meters)
        }
    }

    fun getCardinalDirection(heading: Float): String {
        val normalized = ((heading % 360) + 360) % 360
        return when {
            normalized >= 337.5 || normalized < 22.5 -> "N"
            normalized < 67.5 -> "NE"
            normalized < 112.5 -> "E"
            normalized < 157.5 -> "SE"
            normalized < 202.5 -> "S"
            normalized < 247.5 -> "SW"
            normalized < 292.5 -> "W"
            else -> "NW"
        }
    }
}
