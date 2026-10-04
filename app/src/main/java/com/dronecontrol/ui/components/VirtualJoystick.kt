package com.dronecontrol.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsTextMuted
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Interactive virtual analog joystick with spring auto-centering,
 * emitting normalized values from -1.0f to 1.0f.
 */
@Composable
fun VirtualJoystick(
    label: String,
    size: Dp = 140.dp,
    accentColor: Color = GcsCyan,
    onMoved: (x: Float, y: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }
    var normalizedX by remember { mutableStateOf(0f) }
    var normalizedY by remember { mutableStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(0xFF0F1626))
                .border(2.dp, GcsCardBorder, CircleShape)
                .pointerInput(Unit) {
                    val maxRadius = (size.toPx() / 2f) - 24.dp.toPx()

                    detectDragGestures(
                        onDragStart = { offset ->
                            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                            val delta = offset - center
                            val dist = sqrt(delta.x * delta.x + delta.y * delta.y)
                            val clampedDist = minOf(dist, maxRadius)
                            val angle = atan2(delta.y, delta.x)

                            val clampedOffset = Offset(
                                clampedDist * cos(angle),
                                clampedDist * sin(angle)
                            )
                            thumbOffset = clampedOffset
                            normalizedX = (clampedOffset.x / maxRadius).coerceIn(-1f, 1f)
                            normalizedY = (-clampedOffset.y / maxRadius).coerceIn(-1f, 1f) // Invert Y so up is +
                            onMoved(normalizedX, normalizedY)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = thumbOffset + dragAmount
                            val dist = sqrt(newOffset.x * newOffset.x + newOffset.y * newOffset.y)
                            val clampedDist = minOf(dist, maxRadius)
                            val angle = atan2(newOffset.y, newOffset.x)

                            val clampedOffset = if (dist > maxRadius) {
                                Offset(clampedDist * cos(angle), clampedDist * sin(angle))
                            } else {
                                newOffset
                            }
                            thumbOffset = clampedOffset
                            normalizedX = (clampedOffset.x / maxRadius).coerceIn(-1f, 1f)
                            normalizedY = (-clampedOffset.y / maxRadius).coerceIn(-1f, 1f)
                            onMoved(normalizedX, normalizedY)
                        },
                        onDragEnd = {
                            // Auto-center spring back
                            thumbOffset = Offset.Zero
                            normalizedX = 0f
                            normalizedY = 0f
                            onMoved(0f, 0f)
                        },
                        onDragCancel = {
                            thumbOffset = Offset.Zero
                            normalizedX = 0f
                            normalizedY = 0f
                            onMoved(0f, 0f)
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val outerRadius = (this.size.width / 2f) - 6.dp.toPx()
                val midRadius = outerRadius * 0.6f

                // Concentric guide circles
                drawCircle(
                    color = GcsCardBorder.copy(alpha = 0.5f),
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = GcsCardBorder.copy(alpha = 0.3f),
                    radius = midRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                // Crosshair guide lines
                drawLine(
                    color = GcsCardBorder.copy(alpha = 0.6f),
                    start = Offset(center.x, 12.dp.toPx()),
                    end = Offset(center.x, this.size.height - 12.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = GcsCardBorder.copy(alpha = 0.6f),
                    start = Offset(12.dp.toPx(), center.y),
                    end = Offset(this.size.width - 12.dp.toPx(), center.y),
                    strokeWidth = 1.dp.toPx()
                )

                // Outer glow connection line
                val thumbCenter = center + thumbOffset
                drawLine(
                    color = accentColor.copy(alpha = 0.4f),
                    start = center,
                    end = thumbCenter,
                    strokeWidth = 2.dp.toPx()
                )

                // Thumb knob with gradient
                val thumbRadius = 22.dp.toPx()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accentColor, accentColor.copy(alpha = 0.6f), Color(0xFF1E293B)),
                        center = thumbCenter,
                        radius = thumbRadius
                    ),
                    radius = thumbRadius,
                    center = thumbCenter
                )
                drawCircle(
                    color = accentColor,
                    radius = thumbRadius,
                    center = thumbCenter,
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = thumbCenter
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = GcsTextMuted
        )
        Text(
            text = String.format(Locale.US, "X:%+.2f Y:%+.2f", normalizedX, normalizedY),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = accentColor.copy(alpha = 0.8f)
        )
    }
}
