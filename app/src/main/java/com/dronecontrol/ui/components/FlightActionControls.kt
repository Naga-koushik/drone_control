package com.dronecontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.ui.theme.GcsAmber
import com.dronecontrol.ui.theme.GcsCardBackground
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCrimson
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsEmerald
import com.dronecontrol.ui.theme.GcsTextMuted
import com.dronecontrol.ui.theme.GcsTextPrimary
import com.dronecontrol.ui.theme.GcsTextSecondary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlightActionControls(
    isArmed: Boolean,
    isConnected: Boolean,
    onArmClick: () -> Unit,
    onTakeoffClick: () -> Unit,
    onLandClick: () -> Unit,
    onRtlClick: () -> Unit,
    onHoldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. ARM / DISARM
        if (!isArmed) {
            ActionButton(
                label = "ARM",
                icon = Icons.Default.LockOpen,
                color = GcsEmerald,
                enabled = isConnected,
                onClick = onArmClick,
                modifier = Modifier.weight(1f, fill = false)
            )
        } else {
            ActionButton(
                label = "DISARM",
                icon = Icons.Default.Lock,
                color = GcsCrimson,
                enabled = isConnected,
                onClick = onArmClick,
                modifier = Modifier.weight(1f, fill = false)
            )
        }

        // 2. TAKEOFF
        ActionButton(
            label = "TAKEOFF",
            icon = Icons.Default.FlightTakeoff,
            color = GcsCyan,
            enabled = isConnected,
            onClick = onTakeoffClick,
            modifier = Modifier.weight(1f, fill = false)
        )

        // 3. LAND
        ActionButton(
            label = "LAND",
            icon = Icons.Default.FlightLand,
            color = GcsAmber,
            enabled = isConnected,
            onClick = onLandClick,
            modifier = Modifier.weight(1f, fill = false)
        )

        // 4. RTL
        ActionButton(
            label = "RTL",
            icon = Icons.Default.Home,
            color = Color(0xFF818CF8),
            enabled = isConnected,
            onClick = onRtlClick,
            modifier = Modifier.weight(1f, fill = false)
        )

        // 5. HOLD
        ActionButton(
            label = "HOLD",
            icon = Icons.Default.PanTool,
            color = Color(0xFF38BDF8),
            enabled = isConnected,
            onClick = onHoldClick,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
fun ActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color,
            disabledContainerColor = Color(0xFF1E293B).copy(alpha = 0.4f),
            disabledContentColor = GcsTextMuted
        ),
        modifier = modifier
            .border(
                1.dp,
                if (enabled) color.copy(alpha = 0.5f) else GcsCardBorder,
                RoundedCornerShape(8.dp)
            )
            .height(44.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ArmConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GcsCardBackground,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = GcsCrimson,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "ARM PROPULSION SYSTEM?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = GcsTextPrimary
            )
        },
        text = {
            Text(
                text = "Warning: Arming will spin drone motors and engage flight control surfaces. Ensure the perimeter is clear of personnel and obstacles before confirming.",
                fontSize = 13.sp,
                color = GcsTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = GcsCrimson)
            ) {
                Text("CONFIRM ARM", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = GcsTextSecondary)
            }
        }
    )
}

@Composable
fun TakeoffAltitudeDialog(
    initialAltitude: Double = 10.0,
    onConfirm: (altitude: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var altSlider by remember { mutableFloatStateOf(initialAltitude.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GcsCardBackground,
        icon = {
            Icon(
                imageVector = Icons.Default.FlightTakeoff,
                contentDescription = null,
                tint = GcsCyan,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "COMMAND AUTONOMOUS TAKEOFF",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = GcsTextPrimary
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Select target altitude above ground level (AGL):",
                    fontSize = 13.sp,
                    color = GcsTextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = String.format(Locale.US, "%.1f m", altSlider),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = GcsCyan
                )
                Slider(
                    value = altSlider,
                    onValueChange = { altSlider = it },
                    valueRange = 2f..50f,
                    steps = 47,
                    colors = SliderDefaults.colors(
                        thumbColor = GcsCyan,
                        activeTrackColor = GcsCyan
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(altSlider.toDouble()) },
                colors = ButtonDefaults.buttonColors(containerColor = GcsCyan, contentColor = Color.Black)
            ) {
                Text("TAKEOFF", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = GcsTextSecondary)
            }
        }
    )
}
