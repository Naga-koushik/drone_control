package com.dronecontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.data.FlightMode
import com.dronecontrol.ui.theme.GcsCardBackground
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsTextMuted
import com.dronecontrol.ui.theme.GcsTextPrimary
import com.dronecontrol.ui.theme.GcsTextSecondary

@Composable
fun ModeSelectorDialog(
    currentMode: FlightMode,
    onSelectMode: (FlightMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GcsCardBackground,
        icon = {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = GcsCyan
            )
        },
        title = {
            Text(
                text = "SELECT FLIGHT MODE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = GcsTextPrimary
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FlightMode.values()) { mode ->
                    val isSelected = mode == currentMode
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GcsCyan.copy(alpha = 0.15f) else Color(0xFF162032))
                            .border(
                                1.dp,
                                if (isSelected) GcsCyan else GcsCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelectMode(mode) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected) GcsCyan else GcsTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = mode.description,
                                    fontSize = 11.sp,
                                    color = GcsTextSecondary
                                )
                            }
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = GcsCyan
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = GcsTextSecondary)
            }
        }
    )
}
