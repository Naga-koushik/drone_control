package com.dronecontrol.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.ui.components.SimulationBadge
import com.dronecontrol.ui.theme.GcsTheme
import com.dronecontrol.utils.Formatters
import com.dronecontrol.viewmodel.DroneUiState

data class TrailPoint(val lat: Double, val lon: Double)

@Composable
fun MapScreen(
    uiState: DroneUiState,
    modifier: Modifier = Modifier
) {
    val telemetry = uiState.telemetry
    val colors = GcsTheme.colors
    val homeLat = 37.774929
    val homeLon = -122.419416

    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    val breadcrumbTrail = remember { mutableStateListOf<TrailPoint>() }

    // Record trail positions periodically
    LaunchedEffect(telemetry.latitude, telemetry.longitude) {
        if (telemetry.isArmed && telemetry.relativeAltitude > 0.5f) {
            breadcrumbTrail.add(TrailPoint(telemetry.latitude, telemetry.longitude))
            if (breadcrumbTrail.size > 200) {
                breadcrumbTrail.removeAt(0)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // 1. Tactical Radar / Map Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // Draw radar range rings (e.g. 50m, 100m, 200m)
            val ringRadii = listOf(80f * zoomLevel, 160f * zoomLevel, 260f * zoomLevel, 380f * zoomLevel)
            ringRadii.forEach { radius ->
                drawCircle(
                    color = colors.cardBorder.copy(alpha = 0.6f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Radar cardinal cross lines
            drawLine(
                color = colors.cardBorder.copy(alpha = 0.4f),
                start = Offset(center.x, 0f),
                end = Offset(center.x, size.height),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = colors.cardBorder.copy(alpha = 0.4f),
                start = Offset(0f, center.y),
                end = Offset(size.width, center.y),
                strokeWidth = 1.dp.toPx()
            )

            // Convert Lat/Lon offsets into screen pixels relative to Home
            val scale = 2.5f * zoomLevel
            val metersPerDegLat = 111132.95
            val metersPerDegLon = 111132.95 * Math.cos(homeLat * Math.PI / 180.0)

            // Draw Home Marker (Takeoff location)
            drawCircle(
                color = colors.secondary,
                radius = 7.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = center
            )

            // Draw Breadcrumb trail
            if (breadcrumbTrail.size > 1) {
                val trailPath = Path()
                breadcrumbTrail.forEachIndexed { i, pt ->
                    val dMetersLat = (pt.lat - homeLat) * metersPerDegLat
                    val dMetersLon = (pt.lon - homeLon) * metersPerDegLon
                    val ptX = (center.x + (dMetersLon * scale)).toFloat()
                    val ptY = (center.y - (dMetersLat * scale)).toFloat()

                    if (i == 0) trailPath.moveTo(ptX, ptY) else trailPath.lineTo(ptX, ptY)
                }
                drawPath(
                    path = trailPath,
                    color = colors.primary.copy(alpha = 0.6f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Compute current drone position on canvas
            val droneDistLat = (telemetry.latitude - homeLat) * metersPerDegLat
            val droneDistLon = (telemetry.longitude - homeLon) * metersPerDegLon
            val droneX = (center.x + (droneDistLon * scale)).toFloat()
            val droneY = (center.y - (droneDistLat * scale)).toFloat()
            val dronePos = Offset(droneX, droneY)

            // Heading dashed projection line
            val headingRad = Math.toRadians(telemetry.heading.toDouble())
            val projLen = 35.dp.toPx()
            val projEnd = Offset(
                (droneX + projLen * Math.sin(headingRad)).toFloat(),
                (droneY - projLen * Math.cos(headingRad)).toFloat()
            )
            drawLine(
                color = colors.primary,
                start = dronePos,
                end = projEnd,
                strokeWidth = 2.dp.toPx()
            )

            // Draw Drone Icon (triangle rotated with heading)
            rotate(degrees = telemetry.heading, pivot = dronePos) {
                val triPath = Path().apply {
                    moveTo(droneX, droneY - 14.dp.toPx())
                    lineTo(droneX + 10.dp.toPx(), droneY + 12.dp.toPx())
                    lineTo(droneX, droneY + 7.dp.toPx())
                    lineTo(droneX - 10.dp.toPx(), droneY + 12.dp.toPx())
                    close()
                }
                drawPath(
                    path = triPath,
                    color = if (telemetry.isArmed) colors.error else colors.success
                )
                drawPath(
                    path = triPath,
                    color = Color.White,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        // 2. Top Status Overlay Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground.copy(alpha = 0.92f)),
                modifier = Modifier.border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "TACTICAL RADAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colors.primary
                    )
                    Text(
                        text = "LAT: ${Formatters.formatCoordinate(telemetry.latitude)}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "LON: ${Formatters.formatCoordinate(telemetry.longitude)}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "ALT: ${Formatters.formatAltitude(telemetry.relativeAltitude)} | HDG: ${telemetry.heading.toInt()}°",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textPrimary
                    )
                }
            }

            if (uiState.isSimulation) {
                SimulationBadge()
            }
        }

        // 3. Floating Map Controls (Zoom in/out, Clear trail, Center)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapControlFab(
                icon = Icons.Default.Add,
                onClick = { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(3.0f) }
            )
            MapControlFab(
                icon = Icons.Default.Remove,
                onClick = { zoomLevel = (zoomLevel * 0.8f).coerceAtLeast(0.4f) }
            )
            MapControlFab(
                icon = Icons.Default.Clear,
                onClick = { breadcrumbTrail.clear() }
            )
            MapControlFab(
                icon = Icons.Default.MyLocation,
                onClick = { zoomLevel = 1.0f }
            )
        }

        // 4. Bottom Legend Card
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground.copy(alpha = 0.92f)),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colors.secondary)
                )
                Text("HOME", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = colors.textMuted)

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (telemetry.isArmed) colors.error else colors.success)
                )
                Text(
                    if (telemetry.isArmed) "UAV ARMED" else "UAV SAFE",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textMuted
                )
            }
        }
    }
}

@Composable
fun MapControlFab(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val colors = GcsTheme.colors
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(colors.cardBackground.copy(alpha = 0.92f))
            .border(1.dp, colors.cardBorder, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}
