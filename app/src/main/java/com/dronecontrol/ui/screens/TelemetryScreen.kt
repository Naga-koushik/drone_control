package com.dronecontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.data.Telemetry
import com.dronecontrol.ui.components.SimulationBadge
import com.dronecontrol.ui.theme.GcsAmber
import com.dronecontrol.ui.theme.GcsCardBackground
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCrimson
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsDarkBackground
import com.dronecontrol.ui.theme.GcsEmerald
import com.dronecontrol.ui.theme.GcsTextMuted
import com.dronecontrol.ui.theme.GcsTextPrimary
import com.dronecontrol.ui.theme.GcsTextSecondary
import com.dronecontrol.utils.Formatters
import com.dronecontrol.viewmodel.DroneUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelemetryScreen(
    uiState: DroneUiState,
    modifier: Modifier = Modifier
) {
    val telemetry = uiState.telemetry

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GcsDarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FULL TELEMETRY AUDIT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    color = GcsTextPrimary
                )
                Text(
                    text = "Real-time stream from flight controller sensors",
                    fontSize = 12.sp,
                    color = GcsTextMuted
                )
            }
            if (uiState.isSimulation) {
                SimulationBadge()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Power & Battery Subsystem Card
        TelemetrySectionCard(
            title = "POWER & ELECTRICAL SUBSYSTEM",
            icon = Icons.Default.BatteryChargingFull,
            accentColor = if (telemetry.batteryPercentage > 30) GcsEmerald else GcsCrimson
        ) {
            val battProgress = (telemetry.batteryPercentage / 100f).coerceIn(0f, 1f)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Battery State of Charge", fontSize = 12.sp, color = GcsTextSecondary)
                    Text(
                        "${telemetry.batteryPercentage}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (telemetry.batteryPercentage > 20) GcsEmerald else GcsCrimson
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = battProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (telemetry.batteryPercentage > 20) GcsEmerald else GcsCrimson,
                    trackColor = Color(0xFF1E293B),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(12.dp))
                TelemetryDataRow("Main Bus Voltage", Formatters.formatVoltage(telemetry.batteryVoltage))
                TelemetryDataRow("Estimated Current Draw", if (telemetry.isArmed) "14.2 A" else "0.8 A")
                TelemetryDataRow("Estimated Flight Time Remaining", "${(telemetry.batteryPercentage * 0.22).toInt()} min")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Position & GPS Subsystem Card
        TelemetrySectionCard(
            title = "GPS & SPATIAL NAVIGATION",
            icon = Icons.Default.GpsFixed,
            accentColor = GcsCyan
        ) {
            TelemetryDataRow("GPS Fix Status", telemetry.gpsFix.label)
            TelemetryDataRow("Satellites Locked", "${telemetry.satelliteCount} SATS")
            TelemetryDataRow("Latitude", Formatters.formatCoordinate(telemetry.latitude))
            TelemetryDataRow("Longitude", Formatters.formatCoordinate(telemetry.longitude))
            TelemetryDataRow("Altitude (MSL)", Formatters.formatAltitude(telemetry.altitude))
            TelemetryDataRow("Altitude (AGL / Relative)", Formatters.formatAltitude(telemetry.relativeAltitude))
            TelemetryDataRow("Distance to Launch", Formatters.formatDistance(telemetry.distanceToHome))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Dynamics & Motion Subsystem Card
        TelemetrySectionCard(
            title = "FLIGHT DYNAMICS & ATTITUDE",
            icon = Icons.Default.Navigation,
            accentColor = Color(0xFF818CF8)
        ) {
            TelemetryDataRow("Heading (Compass)", "${Formatters.formatHeading(telemetry.heading)} (${Formatters.getCardinalDirection(telemetry.heading)})")
            TelemetryDataRow("Ground Speed", Formatters.formatSpeed(telemetry.groundSpeed))
            TelemetryDataRow("Vertical Climb Rate", Formatters.formatSpeed(telemetry.verticalSpeed))
            TelemetryDataRow("Attitude Roll", "${telemetry.roll.toInt()}°")
            TelemetryDataRow("Attitude Pitch", "${telemetry.pitch.toInt()}°")
            TelemetryDataRow("Attitude Yaw", "${telemetry.yaw.toInt()}°")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Autopilot System & Status Card
        TelemetrySectionCard(
            title = "AUTOPILOT & SAFETIES",
            icon = Icons.Default.Security,
            accentColor = GcsAmber
        ) {
            TelemetryDataRow("Active Flight Mode", telemetry.flightMode.displayName)
            TelemetryDataRow("Propulsion Arm State", if (telemetry.isArmed) "ARMED (LIVE)" else "DISARMED (SAFE)")
            TelemetryDataRow("Link Transport State", uiState.connectionState.name)
            val timeString = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(telemetry.timestamp))
            TelemetryDataRow("Last Frame Timestamp", timeString)
            TelemetryDataRow("Stream Cadence", "10 Hz")
        }
    }
}

@Composable
fun TelemetrySectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GcsCardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GcsCardBorder, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp,
                    color = accentColor
                )
            }
            content()
        }
    }
}

@Composable
fun TelemetryDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = GcsTextSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = GcsTextPrimary
        )
    }
}
