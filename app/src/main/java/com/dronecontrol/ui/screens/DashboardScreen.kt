package com.dronecontrol.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.data.ConnectionState
import com.dronecontrol.data.ConnectionType
import com.dronecontrol.ui.components.ModeSelectorDialog
import com.dronecontrol.ui.components.StatusHeader
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsEmerald
import com.dronecontrol.ui.theme.GcsTheme
import com.dronecontrol.utils.Formatters
import com.dronecontrol.viewmodel.DroneUiState
import com.dronecontrol.viewmodel.DroneViewModel

/**
 * Re-architected Dashboard:
 * 1. Basic drone information & telemetry status.
 * 2. Integrated Connection controls & pre-flight checklist.
 * 3. Prominent action launcher for the Full Landscape Flight Cockpit.
 * 4. Compact SITL / Transport communications log at the bottom.
 */
@Composable
fun DashboardScreen(
    uiState: DroneUiState,
    viewModel: DroneViewModel,
    modifier: Modifier = Modifier
) {
    val telemetry = uiState.telemetry
    val colors = GcsTheme.colors
    val isConnected = uiState.connectionState.isConnected
    val listState = rememberLazyListState()

    // Auto-scroll terminal when new logs arrive
    LaunchedEffect(uiState.consoleLogs.size) {
        if (uiState.consoleLogs.isNotEmpty()) {
            listState.animateScrollToItem(uiState.consoleLogs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Status Header
        StatusHeader(
            telemetry = telemetry,
            connectionState = uiState.connectionState,
            isSimulation = uiState.isSimulation,
            onModeClick = { viewModel.openModeSelector() }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // 2. PRIMARY ACTION: LAUNCH FULL FLIGHT COCKPIT
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.primary.copy(alpha = if (colors.isDark) 0.15f else 0.10f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, colors.primary, RoundedCornerShape(12.dp))
                    .clickable { viewModel.openCockpit() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Flight,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ENTER FLIGHT COCKPIT",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = colors.primary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Opens full landscape HUD with large borderless buttons, attitude horizon & compass",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = "Open Cockpit",
                        tint = colors.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Basic Drone Status Overview Card
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DRONE SYSTEM STATUS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BasicInfoCell("ALTITUDE (AGL)", Formatters.formatAltitude(telemetry.relativeAltitude), colors.textPrimary)
                        BasicInfoCell("GROUND SPEED", Formatters.formatSpeed(telemetry.groundSpeed), colors.textPrimary)
                        BasicInfoCell("BATTERY", "${telemetry.batteryPercentage}%", if (telemetry.batteryPercentage > 20) colors.success else colors.error)
                        BasicInfoCell("GPS FIX", "${telemetry.satelliteCount} SATS", if (telemetry.satelliteCount >= 8) colors.success else colors.warning)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pre-flight check badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PreflightChip("SENSORS: OK", colors.success)
                        PreflightChip("GPS: 3D LOCK", if (telemetry.satelliteCount >= 8) colors.success else colors.warning)
                        PreflightChip("BATTERY: HEALTHY", if (telemetry.batteryPercentage > 20) colors.success else colors.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Integrated Connection & Link Setup (Merged)
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "COMMUNICATION LINK & BRIDGE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode tabs (Simulator / Wi-Fi / Bluetooth)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DashboardLinkOption(
                            label = "SIMULATOR",
                            icon = Icons.Default.Computer,
                            isSelected = uiState.activeConnectionType == ConnectionType.SIMULATOR,
                            onClick = { viewModel.setConnectionType(ConnectionType.SIMULATOR) },
                            modifier = Modifier.weight(1f)
                        )
                        DashboardLinkOption(
                            label = "WI-FI",
                            icon = Icons.Default.Wifi,
                            isSelected = uiState.activeConnectionType == ConnectionType.WIFI,
                            onClick = { viewModel.setConnectionType(ConnectionType.WIFI) },
                            modifier = Modifier.weight(1f)
                        )
                        DashboardLinkOption(
                            label = "BLUETOOTH",
                            icon = Icons.Default.Bluetooth,
                            isSelected = uiState.activeConnectionType == ConnectionType.BLUETOOTH,
                            onClick = { viewModel.setConnectionType(ConnectionType.BLUETOOTH) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic fields for Wi-Fi
                    AnimatedVisibility(visible = uiState.activeConnectionType == ConnectionType.WIFI) {
                        Column {
                            OutlinedTextField(
                                value = uiState.ipAddress,
                                onValueChange = { viewModel.updateIpAddress(it) },
                                label = { Text("ESP32 IP Address") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.primary,
                                    unfocusedBorderColor = colors.cardBorder,
                                    focusedTextColor = colors.textPrimary,
                                    unfocusedTextColor = colors.textPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = uiState.portString,
                                onValueChange = { viewModel.updatePort(it) },
                                label = { Text("MAVLink Port") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.primary,
                                    unfocusedBorderColor = colors.cardBorder,
                                    focusedTextColor = colors.textPrimary,
                                    unfocusedTextColor = colors.textPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Dynamic fields for Bluetooth
                    AnimatedVisibility(visible = uiState.activeConnectionType == ConnectionType.BLUETOOTH) {
                        Column {
                            OutlinedTextField(
                                value = uiState.bluetoothAddress,
                                onValueChange = { viewModel.updateBluetoothAddress(it) },
                                label = { Text("ESP32 Bluetooth Device") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colors.primary,
                                    unfocusedBorderColor = colors.cardBorder,
                                    focusedTextColor = colors.textPrimary,
                                    unfocusedTextColor = colors.textPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Connect / Disconnect button
                    Button(
                        onClick = { viewModel.toggleConnect() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isConnected) colors.error else colors.success,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isConnected) "DISCONNECT LINK" else "CONNECT LINK",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Compact SITL / Transport Communication Console (At bottom, after scrolling)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SITL / TRANSPORT LOG (COMPACT)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textMuted
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp) // Reduced compact size
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (colors.isDark) Color(0xFF070B14) else Color(0xFFE2E8F0))
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                if (uiState.consoleLogs.isEmpty()) {
                    Text(
                        text = "Awaiting transport telemetry frames...",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textMuted,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(state = listState) {
                        items(uiState.consoleLogs) { line ->
                            val logColor = when {
                                line.startsWith("[TX]") -> colors.primary
                                line.startsWith("[REPO_ERR]") -> colors.error
                                line.startsWith("[MAVLINK") -> colors.success
                                else -> colors.textSecondary
                            }
                            Text(
                                text = line,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = logColor,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isModeSelectorOpen) {
        ModeSelectorDialog(
            currentMode = telemetry.flightMode,
            onSelectMode = { mode -> viewModel.selectFlightMode(mode) },
            onDismiss = { viewModel.dismissModeSelector() }
        )
    }
}

@Composable
fun BasicInfoCell(title: String, value: String, valueColor: Color) {
    val colors = GcsTheme.colors
    Column {
        Text(
            text = title,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = colors.textMuted
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = valueColor
        )
    }
}

@Composable
fun PreflightChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun DashboardLinkOption(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GcsTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) colors.primary.copy(alpha = 0.2f) else colors.surfaceVariant)
            .border(
                1.dp,
                if (isSelected) colors.primary else colors.cardBorder,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) colors.primary else colors.textMuted,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isSelected) colors.primary else colors.textPrimary
            )
        }
    }
}
