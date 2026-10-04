package com.dronecontrol.mavlink

/**
 * Common interface for all MAVLink message types (Heartbeat, Attitude, GlobalPositionInt, etc.).
 */
interface MavlinkMessage {
    val messageId: Int
    val name: String
    fun serialize(): ByteArray
}

/**
 * Placeholder message representing a standard MAVLink HEARTBEAT (#0).
 */
data class MavlinkHeartbeatMessage(
    val customMode: Long = 0,
    val type: Int = 2, // MAV_TYPE_QUADROTOR
    val autopilot: Int = 3, // MAV_AUTOPILOT_ARDUPILOTMEGA
    val baseMode: Short = 0,
    val systemStatus: Short = 4, // MAV_STATE_ACTIVE
    val mavlinkVersion: Short = 3
) : MavlinkMessage {
    override val messageId: Int = 0
    override val name: String = "HEARTBEAT"
    override fun serialize(): ByteArray = ByteArray(9)
}

/**
 * Placeholder message representing MAVLink ATTITUDE (#30).
 */
data class MavlinkAttitudeMessage(
    val timeBootMs: Long = 0,
    val roll: Float = 0f,
    val pitch: Float = 0f,
    val yaw: Float = 0f,
    val rollSpeed: Float = 0f,
    val pitchSpeed: Float = 0f,
    val yawSpeed: Float = 0f
) : MavlinkMessage {
    override val messageId: Int = 30
    override val name: String = "ATTITUDE"
    override fun serialize(): ByteArray = ByteArray(28)
}

/**
 * Placeholder message representing MAVLink GLOBAL_POSITION_INT (#33).
 */
data class MavlinkGlobalPositionMessage(
    val timeBootMs: Long = 0,
    val lat: Int = 0, // degE7
    val lon: Int = 0, // degE7
    val alt: Int = 0, // mm
    val relativeAlt: Int = 0, // mm
    val vx: Short = 0, // cm/s
    val vy: Short = 0,
    val vz: Short = 0,
    val hdg: Int = 0 // cdeg
) : MavlinkMessage {
    override val messageId: Int = 33
    override val name: String = "GLOBAL_POSITION_INT"
    override fun serialize(): ByteArray = ByteArray(28)
}
