package com.dronecontrol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dronecontrol.data.ConnectionType
import com.dronecontrol.data.DroneRepository
import com.dronecontrol.data.FlightMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing GCS presentation logic and mediating between Compose UI and DroneRepository.
 */
class DroneViewModel(
    private val repository: DroneRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DroneUiState())
    val uiState: StateFlow<DroneUiState> = _uiState.asStateFlow()

    init {
        // Collect telemetry stream
        viewModelScope.launch {
            repository.telemetry.collect { telemetry ->
                _uiState.update { it.copy(telemetry = telemetry) }
            }
        }

        // Collect connection state stream
        viewModelScope.launch {
            repository.connectionState.collect { connState ->
                _uiState.update { it.copy(connectionState = connState) }
            }
        }

        // Collect simulation mode indicator
        viewModelScope.launch {
            repository.isSimulation.collect { isSim ->
                _uiState.update { it.copy(isSimulation = isSim) }
            }
        }

        // Collect transport logs
        viewModelScope.launch {
            repository.consoleLogs.collect { logLine ->
                _uiState.update { current ->
                    val updated = (current.consoleLogs + logLine).takeLast(60)
                    current.copy(consoleLogs = updated)
                }
            }
        }
    }

    fun toggleConnect() {
        viewModelScope.launch {
            if (_uiState.value.connectionState.isConnected) {
                repository.disconnect()
            } else {
                repository.connect()
            }
        }
    }

    fun setConnectionType(type: ConnectionType) {
        viewModelScope.launch {
            val port = _uiState.value.portString.toIntOrNull() ?: 14550
            repository.switchConnection(
                type = type,
                ipAddress = _uiState.value.ipAddress,
                port = port,
                bluetoothAddress = _uiState.value.bluetoothAddress
            )
            _uiState.update { it.copy(activeConnectionType = type) }
        }
    }

    fun updateIpAddress(ip: String) {
        _uiState.update { it.copy(ipAddress = ip) }
    }

    fun updatePort(port: String) {
        _uiState.update { it.copy(portString = port) }
    }

    fun updateBluetoothAddress(address: String) {
        _uiState.update { it.copy(bluetoothAddress = address) }
    }

    fun toggleArm() {
        val isArmed = _uiState.value.telemetry.isArmed
        if (!isArmed) {
            // Open confirmation dialog before arming spinning props
            _uiState.update { it.copy(isArmConfirmDialogOpen = true) }
        } else {
            // Immediate disarm
            viewModelScope.launch {
                repository.disarm()
            }
        }
    }

    fun confirmArm() {
        _uiState.update { it.copy(isArmConfirmDialogOpen = false) }
        viewModelScope.launch {
            repository.arm()
        }
    }

    fun dismissArmDialog() {
        _uiState.update { it.copy(isArmConfirmDialogOpen = false) }
    }

    fun openTakeoffDialog() {
        _uiState.update { it.copy(isTakeoffDialogOpen = true) }
    }

    fun dismissTakeoffDialog() {
        _uiState.update { it.copy(isTakeoffDialogOpen = false) }
    }

    fun confirmTakeoff(altitude: Double) {
        _uiState.update { it.copy(isTakeoffDialogOpen = false, targetTakeoffAlt = altitude) }
        viewModelScope.launch {
            repository.takeoff(altitude)
        }
    }

    fun land() {
        viewModelScope.launch {
            repository.land()
        }
    }

    fun returnToLaunch() {
        viewModelScope.launch {
            repository.returnToLaunch()
        }
    }

    fun hold() {
        viewModelScope.launch {
            repository.hold()
        }
    }

    fun openModeSelector() {
        _uiState.update { it.copy(isModeSelectorOpen = true) }
    }

    fun dismissModeSelector() {
        _uiState.update { it.copy(isModeSelectorOpen = false) }
    }

    fun selectFlightMode(mode: FlightMode) {
        _uiState.update { it.copy(isModeSelectorOpen = false) }
        viewModelScope.launch {
            repository.setFlightMode(mode)
        }
    }

    fun onLeftJoystickMoved(x: Float, y: Float) {
        // x: Yaw (-1 left to 1 right)
        // y: Throttle (-1 down to 1 up)
        _uiState.update { it.copy(leftStickX = x, leftStickY = y) }
        dispatchJoystickInputs()
    }

    fun onRightJoystickMoved(x: Float, y: Float) {
        // x: Roll (-1 left to 1 right)
        // y: Pitch (-1 back to 1 forward)
        _uiState.update { it.copy(rightStickX = x, rightStickY = y) }
        dispatchJoystickInputs()
    }

    private fun dispatchJoystickInputs() {
        viewModelScope.launch {
            val state = _uiState.value
            repository.sendManualControl(
                roll = state.rightStickX,
                pitch = state.rightStickY,
                yaw = state.leftStickX,
                throttle = state.leftStickY
            )
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
