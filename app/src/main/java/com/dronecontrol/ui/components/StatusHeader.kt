package com.dronecontrol.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.data.ConnectionState
import com.dronecontrol.data.Telemetry
import com.dronecontrol.ui.theme.GcsTheme
import com.dronecontrol.utils.Formatters

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusHeader(
    telemetry: Telemetry,
    connectionState: ConnectionState,
    isSimulation: Boolean,
    onModeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = GcsTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.cardBackground)
            .border(width = 1.dp, color = colors.cardBorder)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Top row: App title & SIMULATION MODE alert badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Flight,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DRONE CONTROL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = colors.textPrimary
                )
            }

            if (isSimulation) {
                SimulationBadge()
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom row / FlowRow: Status telemetry badges
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Connection Chip
            val connColor = when (connectionState) {
                ConnectionState.CONNECTED -> colors.success
                ConnectionState.CONNECTING -> colors.warning
                else -> colors.error
            }
            StatusPill(
                label = "LINK",
                value = connectionState.name,
                accentColor = connColor,
                icon = Icons.Default.Wifi
            )

            // Armed/Disarmed Chip
            val armColor = if (telemetry.isArmed) colors.error else colors.textMuted
            val armText = if (telemetry.isArmed) "ARMED" else "DISARMED"
            StatusPill(
                label = "SAFETY",
                value = armText,
                accentColor = armColor,
                icon = Icons.Default.PowerSettingsNew
            )

            // Flight Mode Chip (Clickable)
            StatusPill(
                label = "MODE",
                value = telemetry.flightMode.displayName,
                accentColor = colors.primary,
                modifier = Modifier.clickable { onModeClick() }
            )

            // Battery Chip
            val battColor = when {
                telemetry.batteryPercentage < 20 -> colors.error
                telemetry.batteryPercentage < 40 -> colors.warning
                else -> colors.success
            }
            StatusPill(
                label = "BATT",
                value = "${telemetry.batteryPercentage}% (${Formatters.formatVoltage(telemetry.batteryVoltage)})",
                accentColor = battColor,
                icon = Icons.Default.BatteryChargingFull
            )

            // GPS Chip
            StatusPill(
                label = "GPS",
                value = "${telemetry.gpsFix.label} (${telemetry.satelliteCount} SAT)",
                accentColor = if (telemetry.satelliteCount >= 8) colors.success else colors.warning,
                icon = Icons.Default.GpsFixed
            )
        }
    }
}

@Composable
fun SimulationBadge(modifier: Modifier = Modifier) {
    val colors = GcsTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "sim_pulse")
    val pulseColor by infiniteTransition.animateColor(
        initialValue = colors.warning,
        targetValue = colors.warning.copy(alpha = 0.4f),
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_color"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(pulseColor.copy(alpha = 0.2f))
            .border(1.dp, pulseColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(pulseColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SIMULATION MODE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = colors.warning,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun StatusPill(
    label: String,
    value: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val colors = GcsTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colors.surfaceVariant)
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = colors.textMuted
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = accentColor
            )
        }
    }
}
