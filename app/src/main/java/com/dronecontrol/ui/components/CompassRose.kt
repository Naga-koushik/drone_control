package com.dronecontrol.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCrimson
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsTextMuted
import com.dronecontrol.ui.theme.GcsTextPrimary
import com.dronecontrol.utils.Formatters

@Composable
fun CompassRose(
    heading: Float,
    size: Dp = 110.dp,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(0xFF0F172A))
                .border(1.5.dp, GcsCardBorder, CircleShape)
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val radius = this.size.width / 2f

                // Outer tick marks every 30 degrees
                for (angle in 0 until 360 step 30) {
                    val isCardinal = angle % 90 == 0
                    val tickLen = if (isCardinal) 8.dp.toPx() else 4.dp.toPx()
                    val rad = Math.toRadians(angle.toDouble())

                    val startX = (center.x + (radius - tickLen) * Math.sin(rad)).toFloat()
                    val startY = (center.y - (radius - tickLen) * Math.cos(rad)).toFloat()
                    val endX = (center.x + (radius - 2.dp.toPx()) * Math.sin(rad)).toFloat()
                    val endY = (center.y - (radius - 2.dp.toPx()) * Math.cos(rad)).toFloat()

                    drawLine(
                        color = if (isCardinal) GcsCyan else GcsCardBorder,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (isCardinal) 2.dp.toPx() else 1.dp.toPx()
                    )
                }

                // Drone orientation needle pointing in direction of current heading
                rotate(degrees = heading, pivot = center) {
                    val needleLen = radius - 14.dp.toPx()
                    val needleBaseWidth = 7.dp.toPx()

                    // North tip (Red)
                    val northPath = Path().apply {
                        moveTo(center.x, center.y - needleLen)
                        lineTo(center.x + needleBaseWidth, center.y)
                        lineTo(center.x - needleBaseWidth, center.y)
                        close()
                    }
                    drawPath(northPath, color = GcsCrimson)

                    // South tip (White / Cyan)
                    val southPath = Path().apply {
                        moveTo(center.x, center.y + needleLen)
                        lineTo(center.x + needleBaseWidth, center.y)
                        lineTo(center.x - needleBaseWidth, center.y)
                        close()
                    }
                    drawPath(southPath, color = Color.White.copy(alpha = 0.7f))

                    // Center pivot pin
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${Formatters.formatHeading(heading)} (${Formatters.getCardinalDirection(heading)})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = GcsTextPrimary
        )
    }
}
