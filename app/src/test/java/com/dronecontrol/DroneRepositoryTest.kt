package com.dronecontrol

import com.dronecontrol.data.ConnectionState
import com.dronecontrol.data.ConnectionType
import com.dronecontrol.data.DroneRepository
import com.dronecontrol.data.FlightMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DroneRepositoryTest {

    private lateinit var testScope: TestScope
    private lateinit var repository: DroneRepository

    @Before
    fun setUp() {
        testScope = TestScope()
        repository = DroneRepository(scope = testScope)
    }

    @Test
    fun testRepositoryInitializesInSimulationMode() = testScope.runTest {
        assertTrue(repository.isSimulation.value)
        assertEquals(ConnectionType.SIMULATOR, repository.activeConnectionType.value)
    }

    @Test
    fun testConnectAndTelemetryStream() = testScope.runTest {
        val connectResult = repository.connect()
        assertTrue(connectResult.isSuccess)
        assertEquals(ConnectionState.CONNECTED, repository.connectionState.value)

        val telemetry = repository.telemetry.first()
        assertTrue(telemetry.batteryPercentage > 0)
    }

    @Test
    fun testSwitchConnectionToWifi() = testScope.runTest {
        repository.switchConnection(
            type = ConnectionType.WIFI,
            ipAddress = "192.168.4.1",
            port = 14550
        )
        assertEquals(ConnectionType.WIFI, repository.activeConnectionType.value)
        org.junit.Assert.assertFalse(repository.isSimulation.value)
    }

    @Test
    fun testFlightModeDispatch() = testScope.runTest {
        repository.connect()
        val modeResult = repository.setFlightMode(FlightMode.ALT_HOLD)
        assertTrue(modeResult.isSuccess)
    }
}
