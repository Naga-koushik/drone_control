package com.dronecontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsTextMuted
import com.dronecontrol.ui.theme.GcsTextPrimary

@Composable
fun TelemetryBadge(
    title: String,
    value: String,
    unit: String = "",
    icon: ImageVector? = null,
    accentColor: Color = GcsCyan,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF131B2C))
            .border(1.dp, GcsCardBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = title.uppercase(),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = GcsTextMuted
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = GcsTextPrimary
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GcsTextMuted,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                }
            }
        }
    }
}
