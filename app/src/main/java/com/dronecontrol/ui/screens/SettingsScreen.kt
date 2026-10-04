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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.ui.components.SimulationBadge
import com.dronecontrol.ui.theme.GcsAmber
import com.dronecontrol.ui.theme.GcsCardBackground
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsDarkBackground
import com.dronecontrol.ui.theme.GcsEmerald
import com.dronecontrol.ui.theme.GcsTextMuted
import com.dronecontrol.ui.theme.GcsTextPrimary
import com.dronecontrol.ui.theme.GcsTextSecondary
import com.dronecontrol.viewmodel.DroneUiState

@Composable
fun SettingsScreen(
    uiState: DroneUiState,
    modifier: Modifier = Modifier
) {
    var autoCenterJoysticks by remember { mutableStateOf(true) }
    var audioAnnouncements by remember { mutableStateOf(true) }
    var metricUnits by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GcsDarkBackground)
            .verticalScroll(rememberScrollState())
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
                    text = "GCS CONFIGURATION",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    color = GcsTextPrimary
                )
                Text(
                    text = "Control parameters and hardware bridge architecture",
                    fontSize = 12.sp,
                    color = GcsTextMuted
                )
            }
            if (uiState.isSimulation) {
                SimulationBadge()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Architecture & ESP32 Pipeline Card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GcsCardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GcsCardBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeveloperBoard,
                        contentDescription = null,
                        tint = GcsCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COMMUNICATION ARCHITECTURE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = GcsCyan
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0C1424))
                        .border(1.dp, GcsCardBorder, RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "UI (Compose)\n  ↓\nViewModel (StateFlow)\n  ↓\nDroneRepository\n  ↓\nDroneConnection (MockDroneConnection / ESP32 Bridge)\n  ↓\nTransport (Mock / Wi-Fi UDP / Bluetooth SPP)",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GcsEmerald,
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The communication layer is decoupled from the UI. ESP32 Wi-Fi/Bluetooth sockets and MAVLink serializers can be plugged into Transport & DroneConnection without altering Compose components.",
                    fontSize = 11.sp,
                    color = GcsTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Safety & Failsafe Limits Card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GcsCardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GcsCardBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = GcsAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAFETY LIMITS & FAILSAFE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = GcsAmber
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                SettingsRow("Max Allowed Altitude (Ceiling)", "120.0 m (400 ft)")
                SettingsRow("Default RTL Altitude", "15.0 m")
                SettingsRow("Low Battery Warning", "20%")
                SettingsRow("Critical Battery RTL", "10%")
                SettingsRow("GPS Satellite Threshold", "8 Satellites (3D Fix)")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Joystick & Controls Configuration
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GcsCardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GcsCardBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = Color(0xFF818CF8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CONTROL CONFIGURATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF818CF8)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Auto-Center Joysticks", fontSize = 13.sp, color = GcsTextPrimary)
                        Text("Return thumb stick to center on release", fontSize = 11.sp, color = GcsTextMuted)
                    }
                    Switch(
                        checked = autoCenterJoysticks,
                        onCheckedChange = { autoCenterJoysticks = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GcsCyan,
                            checkedTrackColor = GcsCyan.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Metric Units", fontSize = 13.sp, color = GcsTextPrimary)
                        Text("Display meters and meters/sec instead of feet", fontSize = 11.sp, color = GcsTextMuted)
                    }
                    Switch(
                        checked = metricUnits,
                        onCheckedChange = { metricUnits = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GcsCyan,
                            checkedTrackColor = GcsCyan.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. About App Card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GcsCardBackground),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GcsCardBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = GcsTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APPLICATION INFORMATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = GcsTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                SettingsRow("Application", "DroneControl GCS")
                SettingsRow("Target Bridge", "ESP32 MAVLink Transceiver")
                SettingsRow("Version", "1.0.0 (Build 1)")
                SettingsRow("Architecture", "MVVM + Coroutines Flow")
            }
        }
    }
}

@Composable
fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = GcsTextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = GcsTextPrimary
        )
    }
}
