package com.dronecontrol.viewmodel

import com.dronecontrol.data.ConnectionState
import com.dronecontrol.data.ConnectionType
import com.dronecontrol.data.FlightMode
import com.dronecontrol.data.Telemetry

/**
 * Complete UI state for all DroneControl screens.
 */
data class DroneUiState(
    val telemetry: Telemetry = Telemetry(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val isSimulation: Boolean = true,
    val activeConnectionType: ConnectionType = ConnectionType.SIMULATOR,
    val ipAddress: String = "192.168.4.1",
    val portString: String = "14550",
    val bluetoothAddress: String = "ESP32-MAVLINK-BRIDGE",
    val consoleLogs: List<String> = emptyList(),
    val isArmConfirmDialogOpen: Boolean = false,
    val isTakeoffDialogOpen: Boolean = false,
    val isModeSelectorOpen: Boolean = false,
    val targetTakeoffAlt: Double = 10.0,
    val snackbarMessage: String? = null,
    // Stick positions (-1.0f to 1.0f)
    val leftStickX: Float = 0f,   // Yaw
    val leftStickY: Float = 0f,   // Throttle
    val rightStickX: Float = 0f,  // Roll
    val rightStickY: Float = 0f   // Pitch
)
