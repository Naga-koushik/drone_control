package com.dronecontrol

import com.dronecontrol.data.ConnectionState
import com.dronecontrol.data.FlightMode
import com.dronecontrol.drone.MockDroneConnection
import com.dronecontrol.transport.MockTransport
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MockDroneConnectionTest {

    private lateinit var testScope: TestScope
    private lateinit var mockTransport: MockTransport
    private lateinit var connection: MockDroneConnection

    @Before
    fun setUp() {
        testScope = TestScope()
        mockTransport = MockTransport(scope = testScope)
        connection = MockDroneConnection(transport = mockTransport, scope = testScope)
    }

    @Test
    fun testInitialStateIsDisconnectedAndDisarmed() = testScope.runTest {
        assertEquals(ConnectionState.DISCONNECTED, connection.connectionState.value)
        val initialTelemetry = connection.telemetry().first()
        assertFalse(initialTelemetry.isArmed)
        assertEquals(0.0f, initialTelemetry.relativeAltitude, 0.01f)
    }

    @Test
    fun testConnectChangesStateToConnected() = testScope.runTest {
        val result = connection.connect()
        assertTrue(result.isSuccess)
        assertEquals(ConnectionState.CONNECTED, connection.connectionState.value)
    }

    @Test
    fun testArmAndDisarmCommands() = testScope.runTest {
        connection.connect()

        val armResult = connection.arm()
        assertTrue(armResult.isSuccess)
        val armedTelemetry = connection.telemetry().first()
        assertTrue(armedTelemetry.isArmed)

        val disarmResult = connection.disarm()
        assertTrue(disarmResult.isSuccess)
        val disarmedTelemetry = connection.telemetry().first()
        assertFalse(disarmedTelemetry.isArmed)
    }

    @Test
    fun testTakeoffEngagesGuidedModeAndArms() = testScope.runTest {
        connection.connect()
        val takeoffResult = connection.takeoff(15.0)
        assertTrue(takeoffResult.isSuccess)

        val telem = connection.telemetry().first()
        assertTrue(telem.isArmed)
        assertEquals(FlightMode.GUIDED, telem.flightMode)
    }

    @Test
    fun testLandCommand() = testScope.runTest {
        connection.connect()
        connection.arm()
        val landResult = connection.land()
        assertTrue(landResult.isSuccess)

        val telem = connection.telemetry().first()
        assertEquals(FlightMode.LAND, telem.flightMode)
    }

    @Test
    fun testReturnToLaunchCommand() = testScope.runTest {
        connection.connect()
        connection.arm()
        val rtlResult = connection.returnToLaunch()
        assertTrue(rtlResult.isSuccess)

        val telem = connection.telemetry().first()
        assertEquals(FlightMode.RTL, telem.flightMode)
    }

    @Test
    fun testManualControlInputs() = testScope.runTest {
        connection.connect()
        connection.arm()

        val controlResult = connection.sendManualControl(
            roll = 0.5f,
            pitch = -0.5f,
            yaw = 1.0f,
            throttle = 0.8f
        )
        assertTrue(controlResult.isSuccess)
    }
}
