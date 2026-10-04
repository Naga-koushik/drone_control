package com.dronecontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.dronecontrol.ui.components.SimulationBadge
import com.dronecontrol.ui.components.StatusPill
import com.dronecontrol.ui.components.TakeoffAltitudeDialog
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsEmerald
import com.dronecontrol.ui.theme.GcsTheme
import com.dronecontrol.utils.Formatters
import com.dronecontrol.viewmodel.DroneUiState
import com.dronecontrol.viewmodel.DroneViewModel

/**
 * Dedicated Landscape / Full Cockpit Flight Control Screen.
 * Provides enlarged ergonomic borderless controls for confident finger operation,
 * with full HUD avionics (ADI, Compass, Flight Actions, Status Readouts).
 */
@Composable
fun FlightCockpitScreen(
    uiState: DroneUiState,
    viewModel: DroneViewModel,
    onCloseCockpit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val telemetry = uiState.telemetry
    val colors = GcsTheme.colors
    val isConnected = uiState.connectionState.isConnected

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // 1. Top Avionics Cockpit Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.cardBackground)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exit Cockpit Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onCloseCockpit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Exit Cockpit",
                            tint = colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "FLIGHT COCKPIT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                        color = colors.textPrimary
                    )
                }

                // Status Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(
                        label = "ALT",
                        value = Formatters.formatAltitude(telemetry.relativeAltitude),
                        accentColor = colors.primary
                    )
                    StatusPill(
                        label = "SPD",
                        value = Formatters.formatSpeed(telemetry.groundSpeed),
                        accentColor = colors.success
                    )
                    StatusPill(
                        label = "SAFETY",
                        value = if (telemetry.isArmed) "ARMED" else "DISARMED",
                        accentColor = if (telemetry.isArmed) colors.error else colors.textMuted
                    )
                    StatusPill(
                        label = "BATT",
                        value = "${telemetry.batteryPercentage}%",
                        accentColor = if (telemetry.batteryPercentage > 20) colors.success else colors.error
                    )
                    if (uiState.isSimulation) {
                        SimulationBadge()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Main 3-Column Flight Operations Layout (Left Controls - Center Avionics - Right Controls)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT SIDE: Extra-large ergonomic Altitude / Steer D-Pad (buttonSize = 64.dp)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.cardBackground)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    DirectionalButtonPad(
                        title = "ALTITUDE & STEERING",
                        accentColor = GcsCyan,
                        upIcon = Icons.Default.ArrowUpward,
                        downIcon = Icons.Default.ArrowDownward,
                        leftIcon = Icons.Default.RotateLeft,
                        rightIcon = Icons.Default.RotateRight,
                        padSize = 210.dp,
                        buttonSize = 60.dp,
                        centerLabel = "HOVER",
                        onUpPressed = { viewModel.setClimb(it) },
                        onDownPressed = { viewModel.setDescend(it) },
                        onLeftPressed = { viewModel.setTurnLeft(it) },
                        onRightPressed = { viewModel.setTurnRight(it) },
                        onCenterClick = { viewModel.emergencyBrake() }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // CENTER: Flight Instruments (ADI + Compass) & Flight Actions
                Column(
                    modifier = Modifier
                        .weight(1.4f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Flight Instruments HUD
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.cardBackground)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ADI Artificial Horizon
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ATTITUDE",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            ArtificialHorizon(
                                roll = telemetry.roll,
                                pitch = telemetry.pitch,
                                size = 110.dp
                            )
                        }

                        // Compass Rose
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "HEADING",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            CompassRose(
                                heading = telemetry.heading,
                                size = 95.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Primary Flight Actions Bar
                    FlightActionControls(
                        isArmed = telemetry.isArmed,
                        isConnected = isConnected,
                        onArmClick = { viewModel.toggleArm() },
                        onTakeoffClick = { viewModel.openTakeoffDialog() },
                        onLandClick = { viewModel.land() },
                        onRtlClick = { viewModel.returnToLaunch() },
                        onHoldClick = { viewModel.hold() }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // RIGHT SIDE: Extra-large ergonomic Directional Movement D-Pad (buttonSize = 64.dp)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.cardBackground)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    DirectionalButtonPad(
                        title = "DIRECTIONAL MOVEMENT",
                        accentColor = GcsEmerald,
                        upIcon = Icons.Default.KeyboardArrowUp,
                        downIcon = Icons.Default.KeyboardArrowDown,
                        leftIcon = Icons.Default.KeyboardArrowLeft,
                        rightIcon = Icons.Default.KeyboardArrowRight,
                        padSize = 210.dp,
                        buttonSize = 60.dp,
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
