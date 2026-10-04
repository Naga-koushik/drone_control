package com.dronecontrol.data

/**
 * Types of physical or simulated transport channels available.
 */
enum class ConnectionType(val title: String, val subtitle: String) {
    SIMULATOR("Simulator", "Built-in offline drone physics & telemetry simulation"),
    WIFI("Wi-Fi (UDP / TCP)", "Direct Wi-Fi link to ESP32 MAVLink bridge"),
    BLUETOOTH("Bluetooth (BLE / SPP)", "Wireless short-range link to ESP32 serial bridge")
}
