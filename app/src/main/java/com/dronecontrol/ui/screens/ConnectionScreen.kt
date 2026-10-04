package com.dronecontrol.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PowerSettingsNew
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
import com.dronecontrol.ui.components.SimulationBadge
import com.dronecontrol.ui.theme.GcsTheme
import com.dronecontrol.viewmodel.DroneUiState
import com.dronecontrol.viewmodel.DroneViewModel

@Composable
fun ConnectionScreen(
    uiState: DroneUiState,
    viewModel: DroneViewModel,
    modifier: Modifier = Modifier
) {
    val colors = GcsTheme.colors
    val listState = rememberLazyListState()

    // Auto-scroll console when new logs arrive
    LaunchedEffect(uiState.consoleLogs.size) {
        if (uiState.consoleLogs.isNotEmpty()) {
            listState.animateScrollToItem(uiState.consoleLogs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LINK CONFIGURATION",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    color = colors.textPrimary
                )
                Text(
                    text = "Configure ESP32 bridge or virtual simulation link",
                    fontSize = 12.sp,
                    color = colors.textMuted
                )
            }

            if (uiState.isSimulation) {
                SimulationBadge()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Connection Type Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConnectionTypeOption(
                type = ConnectionType.SIMULATOR,
                icon = Icons.Default.Computer,
                isSelected = uiState.activeConnectionType == ConnectionType.SIMULATOR,
                onClick = { viewModel.setConnectionType(ConnectionType.SIMULATOR) },
                modifier = Modifier.weight(1f)
            )
            ConnectionTypeOption(
                type = ConnectionType.WIFI,
                icon = Icons.Default.Wifi,
                isSelected = uiState.activeConnectionType == ConnectionType.WIFI,
                onClick = { viewModel.setConnectionType(ConnectionType.WIFI) },
                modifier = Modifier.weight(1f)
            )
            ConnectionTypeOption(
                type = ConnectionType.BLUETOOTH,
                icon = Icons.Default.Bluetooth,
                isSelected = uiState.activeConnectionType == ConnectionType.BLUETOOTH,
                onClick = { viewModel.setConnectionType(ConnectionType.BLUETOOTH) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Parameter Configuration Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Connection State Status Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LINK STATUS:",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textMuted
                    )

                    val statusColor = when (uiState.connectionState) {
                        ConnectionState.CONNECTED -> colors.success
                        ConnectionState.CONNECTING -> colors.warning
                        ConnectionState.DISCONNECTING -> colors.warning
                        ConnectionState.DISCONNECTED -> colors.textMuted
                        ConnectionState.ERROR -> colors.error
                    }
                    Text(
                        text = uiState.connectionState.name,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wi-Fi fields (IP + Port)
                AnimatedVisibility(visible = uiState.activeConnectionType == ConnectionType.WIFI) {
                    Column {
                        OutlinedTextField(
                            value = uiState.ipAddress,
                            onValueChange = { viewModel.updateIpAddress(it) },
                            label = { Text("ESP32 IP Address") },
                            placeholder = { Text("192.168.4.1") },
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
                        OutlinedTextField(
                            value = uiState.portString,
                            onValueChange = { viewModel.updatePort(it) },
                            label = { Text("MAVLink Port (UDP/TCP)") },
                            placeholder = { Text("14550") },
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
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Bluetooth fields
                AnimatedVisibility(visible = uiState.activeConnectionType == ConnectionType.BLUETOOTH) {
                    Column {
                        OutlinedTextField(
                            value = uiState.bluetoothAddress,
                            onValueChange = { viewModel.updateBluetoothAddress(it) },
                            label = { Text("Target Bluetooth Device / MAC") },
                            placeholder = { Text("ESP32-MAVLINK-BRIDGE") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.cardBorder,
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Simulator Info Banner
                AnimatedVisibility(visible = uiState.activeConnectionType == ConnectionType.SIMULATOR) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.primary.copy(alpha = if (colors.isDark) 0.12f else 0.08f))
                            .border(1.dp, colors.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Simulator mode engages internal 10Hz aerodynamics, GPS drift, battery curve, and autonomous state machine. No external hardware or network needed.",
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Connect / Disconnect Action Button
                val isConnected = uiState.connectionState.isConnected
                Button(
                    onClick = { viewModel.toggleConnect() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) colors.error else colors.success,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) "DISCONNECT LINK" else "CONNECT LINK",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transport Debug Packet Log
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "TRANSPORT COMMUNICATIONS LOG",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = colors.textMuted
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (colors.isDark) Color(0xFF070B14) else Color(0xFFE2E8F0))
                .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            if (uiState.consoleLogs.isEmpty()) {
                Text(
                    text = "Awaiting transport packet traffic...",
                    fontSize = 11.sp,
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
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = logColor,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionTypeOption(
    type: ConnectionType,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GcsTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) colors.primary.copy(alpha = if (colors.isDark) 0.15f else 0.10f) else colors.cardBackground)
            .border(
                1.dp,
                if (isSelected) colors.primary else colors.cardBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) colors.primary else colors.textMuted,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = type.title.split(" ")[0].uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isSelected) colors.primary else colors.textPrimary
            )
        }
    }
}
