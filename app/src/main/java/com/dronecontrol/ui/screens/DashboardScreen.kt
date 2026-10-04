package com.dronecontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.ui.components.ArmConfirmationDialog
import com.dronecontrol.ui.components.ArtificialHorizon
import com.dronecontrol.ui.components.CompassRose
import com.dronecontrol.ui.components.DirectionalButtonPad
import com.dronecontrol.ui.components.FlightActionControls
import com.dronecontrol.ui.components.ModeSelectorDialog
import com.dronecontrol.ui.components.StatusHeader
import com.dronecontrol.ui.components.TakeoffAltitudeDialog
import com.dronecontrol.ui.components.TelemetryBadge
import com.dronecontrol.ui.components.VirtualJoystick
import com.dronecontrol.ui.theme.GcsCardBackground
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsDarkBackground
import com.dronecontrol.ui.theme.GcsEmerald
import com.dronecontrol.ui.theme.GcsTextMuted
import com.dronecontrol.ui.theme.GcsTextPrimary
import com.dronecontrol.ui.theme.GcsTextSecondary
import com.dronecontrol.utils.Formatters
import com.dronecontrol.viewmodel.DroneUiState
import com.dronecontrol.viewmodel.DroneViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    uiState: DroneUiState,
    viewModel: DroneViewModel,
    modifier: Modifier = Modifier
) {
    val telemetry = uiState.telemetry
    val isConnected = uiState.connectionState.isConnected

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GcsDarkBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Top Status Bar Header
        StatusHeader(
            telemetry = telemetry,
            connectionState = uiState.connectionState,
            isSimulation = uiState.isSimulation,
            onModeClick = { viewModel.openModeSelector() }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 2. Primary Avionics Telemetry Quick Badges
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelemetryBadge(
                    title = "ALT AGL",
                    value = Formatters.formatAltitude(telemetry.relativeAltitude),
                    icon = Icons.Default.Height,
                    accentColor = GcsCyan
                )
                TelemetryBadge(
                    title = "GND SPEED",
                    value = Formatters.formatSpeed(telemetry.groundSpeed),
                    icon = Icons.Default.Speed,
                    accentColor = GcsEmerald
                )
                TelemetryBadge(
                    title = "HEADING",
                    value = Formatters.formatHeading(telemetry.heading),
                    icon = Icons.Default.Navigation,
                    accentColor = GcsCyan
                )
                TelemetryBadge(
                    title = "HOME DIST",
                    value = Formatters.formatDistance(telemetry.distanceToHome),
                    icon = Icons.Default.NearMe,
                    accentColor = Color(0xFF818CF8)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Center Cockpit HUD (Artificial Horizon + Compass + Tactical Map Placeholder)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(GcsCardBackground)
                    .border(1.dp, GcsCardBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left HUD: Artificial Horizon (Attitude Indicator)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ATTITUDE (ADI)",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GcsTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ArtificialHorizon(
                        roll = telemetry.roll,
                        pitch = telemetry.pitch,
                        size = 125.dp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "R:${telemetry.roll.toInt()}° P:${telemetry.pitch.toInt()}°",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GcsTextSecondary
                    )
                }

                // Middle HUD: Compass Rose
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "COMPASS",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GcsTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CompassRose(
                        heading = telemetry.heading,
                        size = 110.dp
                    )
                }

                // Right HUD: Tactical Mini-Radar / Map Placeholder
                TacticalMiniMap(
                    lat = telemetry.latitude,
                    lon = telemetry.longitude,
                    distanceToHome = telemetry.distanceToHome,
                    heading = telemetry.heading,
                    isArmed = telemetry.isArmed
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Primary Flight Action Buttons (ARM, DISARM, TAKEOFF, LAND, RTL, HOLD)
            FlightActionControls(
                isArmed = telemetry.isArmed,
                isConnected = isConnected,
                onArmClick = { viewModel.toggleArm() },
                onTakeoffClick = { viewModel.openTakeoffDialog() },
                onLandClick = { viewModel.land() },
                onRtlClick = { viewModel.returnToLaunch() },
                onHoldClick = { viewModel.hold() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Tactical Button System: Dual Directional Pads with Auto-Braking Hover
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(GcsCardBackground)
                    .border(1.dp, GcsCardBorder, RoundedCornerShape(10.dp))
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Pad: Safe Altitude (Climb/Descend) & Steering (Yaw Left/Right)
                DirectionalButtonPad(
                    title = "ALTITUDE / STEER",
                    accentColor = GcsCyan,
                    upLabel = "CLIMB",
                    upIcon = Icons.Default.ArrowUpward,
                    downLabel = "DESCEND",
                    downIcon = Icons.Default.ArrowDownward,
                    leftLabel = "YAW L",
                    leftIcon = Icons.Default.RotateLeft,
                    rightLabel = "YAW R",
                    rightIcon = Icons.Default.RotateRight,
                    centerLabel = "HOVER",
                    onUpPressed = { viewModel.setClimb(it) },
                    onDownPressed = { viewModel.setDescend(it) },
                    onLeftPressed = { viewModel.setTurnLeft(it) },
                    onRightPressed = { viewModel.setTurnRight(it) },
                    onCenterClick = { viewModel.emergencyBrake() }
                )

                // Right Pad: Directional Pitch (Forward/Back) & Roll (Left/Right)
                DirectionalButtonPad(
                    title = "DIRECTIONAL MOVE",
                    accentColor = GcsEmerald,
                    upLabel = "FORWARD",
                    upIcon = Icons.Default.KeyboardArrowUp,
                    downLabel = "BACK",
                    downIcon = Icons.Default.KeyboardArrowDown,
                    leftLabel = "LEFT",
                    leftIcon = Icons.Default.KeyboardArrowLeft,
                    rightLabel = "RIGHT",
                    rightIcon = Icons.Default.KeyboardArrowRight,
                    centerLabel = "BRAKE",
                    onUpPressed = { viewModel.setMoveForward(it) },
                    onDownPressed = { viewModel.setMoveBackward(it) },
                    onLeftPressed = { viewModel.setMoveLeft(it) },
                    onRightPressed = { viewModel.setMoveRight(it) },
                    onCenterClick = { viewModel.emergencyBrake() }
                )
            }
        }
    }

    // Modal dialogs
    if (uiState.isArmConfirmDialogOpen) {
        ArmConfirmationDialog(
            onConfirm = { viewModel.confirmArm() },
            onDismiss = { viewModel.dismissArmDialog() }
        )
    }

    if (uiState.isTakeoffDialogOpen) {
        TakeoffAltitudeDialog(
            initialAltitude = uiState.targetTakeoffAlt,
            onConfirm = { alt -> viewModel.confirmTakeoff(alt) },
            onDismiss = { viewModel.dismissTakeoffDialog() }
        )
    }

    if (uiState.isModeSelectorOpen) {
        ModeSelectorDialog(
            currentMode = telemetry.flightMode,
            onSelectMode = { mode -> viewModel.selectFlightMode(mode) },
            onDismiss = { viewModel.dismissModeSelector() }
        )
    }
}

/**
 * Compact radar / tactical map preview placeholder on the Dashboard.
 */
@Composable
fun TacticalMiniMap(
    lat: Double,
    lon: Double,
    distanceToHome: Float,
    heading: Float,
    isArmed: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "MINI RADAR",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = GcsTextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0C1424))
                .border(1.dp, GcsCardBorder, RoundedCornerShape(8.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = "GPS LOCK",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = GcsEmerald
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = String.format(java.util.Locale.US, "%.4f N", lat),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = GcsTextPrimary
                )
                Text(
                    text = String.format(java.util.Locale.US, "%.4f W", kotlin.math.abs(lon)),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = GcsTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isArmed) Color(0x33EF4444) else Color(0x3310B981))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isArmed) "AIRBORNE" else "ON GROUND",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isArmed) Color(0xFFEF4444) else Color(0xFF10B981)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "MAP OVERLAY",
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = GcsTextMuted
        )
    }
}
