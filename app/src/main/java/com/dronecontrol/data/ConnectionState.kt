package com.dronecontrol.data

/**
 * Represents the state of the communication link to the drone or ESP32 bridge.
 */
enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR;

    val isConnected: Boolean
        get() = this == CONNECTED

    val isBusy: Boolean
        get() = this == CONNECTING || this == DISCONNECTING
}
