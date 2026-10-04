package com.dronecontrol.mavlink

/**
 * Representation of a framing packet for MAVLink v1 or v2 protocol.
 */
data class MavlinkPacket(
    val magic: Int = 0xFD,           // 0xFD for MAVLink v2, 0xFE for MAVLink v1
    val payloadLength: Int,
    val incompatibilityFlags: Int = 0,
    val compatibilityFlags: Int = 0,
    val sequence: Int = 0,
    val systemId: Int = 255,         // GCS default sysId
    val componentId: Int = 190,      // GCS default compId
    val messageId: Int,
    val payload: ByteArray,
    val checksum: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MavlinkPacket
        return sequence == other.sequence && messageId == other.messageId && payload.contentEquals(other.payload)
    }

    override fun hashCode(): Int {
        var result = sequence
        result = 31 * result + messageId
        result = 31 * result + payload.contentHashCode()
        return result
    }
}
