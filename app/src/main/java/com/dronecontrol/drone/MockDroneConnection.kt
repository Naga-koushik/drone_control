package com.dronecontrol.drone

import com.dronecontrol.data.ConnectionState
import com.dronecontrol.data.FlightMode
import com.dronecontrol.data.GpsFix
import com.dronecontrol.data.Telemetry
import com.dronecontrol.transport.MockTransport
import com.dronecontrol.transport.Transport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * High-fidelity drone simulation that implements [DroneConnection].
 * Communicates internally via a [Transport] (default: [MockTransport]).
 * Runs a continuous physics & telemetry generation loop at 10Hz.
 */
class MockDroneConnection(
    val transport: Transport = MockTransport(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : DroneConnection {

    override val isSimulation: Boolean = true

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // Home coordinates (takeoff origin)
    private val homeLat = 37.774929
    private val homeLon = -122.419416
    private val homeAlt = 15.0f

    // Simulated internal drone state
    private var currentLat = homeLat
    private var currentLon = homeLon
    private var currentAlt = homeAlt
    private var currentRelAlt = 0.0f
    private var currentHeading = 45.0f // degrees
    private var currentRoll = 0.0f
    private var currentPitch = 0.0f
    private var currentYawRate = 0.0f
    private var currentGroundSpeed = 0.0f
    private var currentVerticalSpeed = 0.0f
    private var batteryPct = 98.0f
    private var isArmedState = false
    private var currentMode = FlightMode.STABILIZE
    private var satelliteCount = 16
    private var gpsFixState = GpsFix.FIX_3D

    // Autonomous target states (for TAKEOFF, RTL, LAND)
    private var targetAltitude: Float? = null
    private var isRtlReturning = false

    // Manual stick inputs (-1.0 to 1.0)
    private var manualRollInput = 0.0f
    private var manualPitchInput = 0.0f
    private var manualYawInput = 0.0f
    private var manualThrottleInput = 0.0f

    private val _telemetryFlow = MutableStateFlow(createTelemetrySnapshot())
    private var physicsJob: Job? = null

    override suspend fun connect(): Result<Unit> {
        if (_connectionState.value == ConnectionState.CONNECTED) {
            return Result.success(Unit)
        }
        _connectionState.value = ConnectionState.CONNECTING
        transport.connect()
        delay(500) // Realistic link handshake delay
        _connectionState.value = ConnectionState.CONNECTED
        gpsFixState = GpsFix.FIX_3D
        startSimulationLoop()
        transport.send("MOCK_DRONE: System online, pre-arm checks passed.")
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTING
        physicsJob?.cancel()
        physicsJob = null
        transport.disconnect()
        _connectionState.value = ConnectionState.DISCONNECTED
        isArmedState = false
        _telemetryFlow.value = createTelemetrySnapshot()
    }

    override suspend fun arm(): Result<Unit> {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Cannot arm: Not connected to drone"))
        }
        isArmedState = true
        transport.send("MOCK_DRONE: ARMED - Propulsion enabled")
        _telemetryFlow.value = createTelemetrySnapshot()
        return Result.success(Unit)
    }

    override suspend fun disarm(): Result<Unit> {
        if (!isArmedState) return Result.success(Unit)
        // If in air, warn or cut motors
        isArmedState = false
        targetAltitude = null
        isRtlReturning = false
        currentVerticalSpeed = 0.0f
        transport.send("MOCK_DRONE: DISARMED - Propulsion disabled")
        _telemetryFlow.value = createTelemetrySnapshot()
        return Result.success(Unit)
    }

    override suspend fun takeoff(altitude: Double): Result<Unit> {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        if (!isArmedState) {
            arm()
        }
        currentMode = FlightMode.GUIDED
        targetAltitude = altitude.toFloat()
        isRtlReturning = false
        transport.send("MOCK_DRONE: TAKEOFF commanded to ${altitude}m")
        return Result.success(Unit)
    }

    override suspend fun land(): Result<Unit> {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        currentMode = FlightMode.LAND
        targetAltitude = 0.0f
        isRtlReturning = false
        transport.send("MOCK_DRONE: LAND commanded at current position")
        return Result.success(Unit)
    }

    override suspend fun returnToLaunch(): Result<Unit> {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        currentMode = FlightMode.RTL
        isRtlReturning = true
        // Set safe RTL clearance altitude (at least 15m)
        targetAltitude = maxOf(currentRelAlt, 15.0f)
        transport.send("MOCK_DRONE: RTL commanded - Returning to launch coordinates")
        return Result.success(Unit)
    }

    override suspend fun hold(): Result<Unit> {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        currentMode = FlightMode.HOLD
        targetAltitude = currentRelAlt
        isRtlReturning = false
        manualRollInput = 0f
        manualPitchInput = 0f
        manualYawInput = 0f
        manualThrottleInput = 0f
        currentGroundSpeed = 0f
        currentVerticalSpeed = 0f
        transport.send("MOCK_DRONE: HOLD commanded - Loitering in place")
        return Result.success(Unit)
    }

    override suspend fun setFlightMode(mode: FlightMode): Result<Unit> {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            return Result.failure(IllegalStateException("Not connected"))
        }
        currentMode = mode
        when (mode) {
            FlightMode.LAND -> land()
            FlightMode.RTL -> returnToLaunch()
            FlightMode.HOLD -> hold()
            else -> {
                targetAltitude = null
                isRtlReturning = false
            }
        }
        transport.send("MOCK_DRONE: Flight mode switched to ${mode.name}")
        _telemetryFlow.value = createTelemetrySnapshot()
        return Result.success(Unit)
    }

    override suspend fun sendManualControl(
        roll: Float,
        pitch: Float,
        yaw: Float,
        throttle: Float
    ): Result<Unit> {
        manualRollInput = roll.coerceIn(-1.0f, 1.0f)
        manualPitchInput = pitch.coerceIn(-1.0f, 1.0f)
        manualYawInput = yaw.coerceIn(-1.0f, 1.0f)
        manualThrottleInput = throttle.coerceIn(-1.0f, 1.0f)
        return Result.success(Unit)
    }

    override fun telemetry(): Flow<Telemetry> = _telemetryFlow.asStateFlow()

    private fun startSimulationLoop() {
        physicsJob?.cancel()
        physicsJob = scope.launch {
            val dt = 0.1f // 100ms per step = 10Hz
            while (isActive && _connectionState.value == ConnectionState.CONNECTED) {
                stepPhysics(dt)
                _telemetryFlow.value = createTelemetrySnapshot()
                delay(100)
            }
        }
    }

    /**
     * Executes one tick of physical simulation.
     */
    private fun stepPhysics(dt: Float) {
        // Battery drain simulation
        val drainRate = if (isArmedState) 0.035f else 0.005f
        batteryPct = (batteryPct - drainRate * dt).coerceAtLeast(0.0f)

        if (!isArmedState) {
            // Drone sitting on ground
            currentRoll = 0.0f
            currentPitch = 0.0f
            currentGroundSpeed = 0.0f
            currentVerticalSpeed = 0.0f
            currentRelAlt = 0.0f
            currentAlt = homeAlt
            return
        }

        // 1. Heading simulation from Yaw input
        val yawRateDegPerSec = manualYawInput * 50.0f // up to 50 deg/s
        currentHeading = (currentHeading + yawRateDegPerSec * dt) % 360.0f
        if (currentHeading < 0) currentHeading += 360.0f

        // 2. Altitude simulation based on Mode or Manual Throttle
        when {
            currentMode == FlightMode.LAND -> {
                // Land mode: descend slowly
                currentVerticalSpeed = if (currentRelAlt > 2.0f) -1.8f else -0.6f
                currentRelAlt += currentVerticalSpeed * dt
                if (currentRelAlt <= 0.05f) {
                    currentRelAlt = 0.0f
                    currentVerticalSpeed = 0.0f
                    isArmedState = false
                    currentMode = FlightMode.STABILIZE
                }
            }
            targetAltitude != null -> {
                // Climbing or descending towards target altitude
                val diff = targetAltitude!! - currentRelAlt
                if (kotlin.math.abs(diff) > 0.15f) {
                    currentVerticalSpeed = if (diff > 0) 2.0f else -1.5f
                    currentRelAlt += currentVerticalSpeed * dt
                } else {
                    currentRelAlt = targetAltitude!!
                    currentVerticalSpeed = 0.0f
                    if (currentMode == FlightMode.GUIDED) {
                        targetAltitude = null // Reached takeoff target
                    }
                }
            }
            else -> {
                // Manual throttle control: stick center (0.0) holds alt, positive climbs, negative descends
                currentVerticalSpeed = manualThrottleInput * 3.0f // max 3 m/s climb/descent
                currentRelAlt = (currentRelAlt + currentVerticalSpeed * dt).coerceAtLeast(0.0f)
            }
        }
        currentAlt = homeAlt + currentRelAlt

        // 3. Horizontal motion simulation
        if (isRtlReturning && currentMode == FlightMode.RTL) {
            // Navigate towards home
            val dLat = homeLat - currentLat
            val dLon = homeLon - currentLon
            val dist = calculateDistanceMeters(currentLat, currentLon, homeLat, homeLon)

            if (dist > 2.0) {
                val bearing = atan2(dLon, dLat)
                val targetSpeed = 5.0f // 5 m/s RTL transit speed
                currentGroundSpeed = targetSpeed
                currentPitch = 12.0f // Nose down forward flight
                currentRoll = 0.0f

                // Move coordinates towards home
                val metersPerDegLat = 111132.95
                val metersPerDegLon = 111132.95 * cos(currentLat * PI / 180.0)
                currentLat += (targetSpeed * cos(bearing) * dt) / metersPerDegLat
                currentLon += (targetSpeed * sin(bearing) * dt) / metersPerDegLon
            } else {
                // Reached home, now land
                isRtlReturning = false
                currentMode = FlightMode.LAND
                targetAltitude = 0.0f
            }
        } else {
            // Manual roll/pitch motion
            currentRoll = manualRollInput * 25.0f // up to 25 deg bank angle
            currentPitch = manualPitchInput * 25.0f // up to 25 deg pitch angle

            val horizontalSpeed = sqrt(manualRollInput * manualRollInput + manualPitchInput * manualPitchInput) * 8.0f
            currentGroundSpeed = horizontalSpeed

            if (horizontalSpeed > 0.1f) {
                // Direction of movement relative to drone heading and joystick
                val stickAngle = atan2(manualRollInput.toDouble(), manualPitchInput.toDouble())
                val moveHeadingRad = (currentHeading * PI / 180.0) + stickAngle
                val metersPerDegLat = 111132.95
                val metersPerDegLon = 111132.95 * cos(currentLat * PI / 180.0)

                currentLat += (horizontalSpeed * cos(moveHeadingRad) * dt) / metersPerDegLat
                currentLon += (horizontalSpeed * sin(moveHeadingRad) * dt) / metersPerDegLon
            }
        }
    }

    private fun createTelemetrySnapshot(): Telemetry {
        val distHome = calculateDistanceMeters(currentLat, currentLon, homeLat, homeLon).toFloat()
        // 3S LiPo voltage: ~12.6V down to ~10.8V
        val vLoaded = (10.8f + (batteryPct / 100.0f) * 1.8f) - (if (isArmedState) 0.25f else 0.0f)

        return Telemetry(
            batteryPercentage = batteryPct.toInt(),
            batteryVoltage = ((vLoaded * 10).toInt() / 10.0f),
            altitude = ((currentAlt * 10).toInt() / 10.0f),
            relativeAltitude = ((currentRelAlt * 10).toInt() / 10.0f),
            latitude = currentLat,
            longitude = currentLon,
            heading = currentHeading,
            groundSpeed = ((currentGroundSpeed * 10).toInt() / 10.0f),
            verticalSpeed = ((currentVerticalSpeed * 10).toInt() / 10.0f),
            gpsFix = gpsFixState,
            satelliteCount = satelliteCount,
            isArmed = isArmedState,
            flightMode = currentMode,
            connectionState = _connectionState.value,
            roll = currentRoll,
            pitch = currentPitch,
            yaw = currentHeading,
            distanceToHome = distHome,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun calculateDistanceMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
