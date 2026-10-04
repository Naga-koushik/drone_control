package com.dronecontrol.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dronecontrol.ui.theme.GcsAmber
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.HorizonGround
import com.dronecontrol.ui.theme.HorizonSky

/**
 * High-precision avionics Attitude Director Indicator (ADI) / Artificial Horizon.
 * Renders roll and pitch dynamics on a circular cockpit gauge.
 */
@Composable
fun ArtificialHorizon(
    roll: Float,      // degrees (-180 to +180)
    pitch: Float,    // degrees (-90 to +90)
    size: Dp = 150.dp,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF0F172A))
            .border(2.dp, GcsCardBorder, CircleShape)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = this.size.width / 2f

            // Rotate canvas for drone roll
            rotate(degrees = -roll, pivot = center) {
                // Pitch offset: 1 degree pitch = ~1.5 pixels shift
                val pitchPixelShift = (pitch * 1.5f).coerceIn(-radius, radius)
                val horizonY = center.y + pitchPixelShift

                // 1. Draw Sky (top hemisphere)
                drawRect(
                    color = HorizonSky,
                    topLeft = Offset(-radius * 2, -radius * 2),
                    size = Size(radius * 6, radius * 2 + horizonY)
                )

                // 2. Draw Ground (bottom hemisphere)
                drawRect(
                    color = HorizonGround,
                    topLeft = Offset(-radius * 2, horizonY),
                    size = Size(radius * 6, radius * 6)
                )

                // 3. Horizon separator line
                drawLine(
                    color = Color.White,
                    start = Offset(-radius * 2, horizonY),
                    end = Offset(radius * 4, horizonY),
                    strokeWidth = 2.dp.toPx()
                )

                // 4. Pitch ladder lines (+10°, +20°, -10°, -20°)
                val pitchSteps = listOf(10f, 20f, -10f, -20f)
                pitchSteps.forEach { p ->
                    val ladderY = center.y + ((pitch - p) * 1.5f)
                    val halfWidth = if (kotlin.math.abs(p) == 10f) 20.dp.toPx() else 30.dp.toPx()
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f),
                        start = Offset(center.x - halfWidth, ladderY),
                        end = Offset(center.x + halfWidth, ladderY),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }

            // Fixed aircraft reticle in center (yellow / amber wings and pip)
            val wingSpan = 22.dp.toPx()
            val wingDrop = 6.dp.toPx()
            val reticleColor = GcsAmber

            // Left wing
            drawLine(
                color = reticleColor,
                start = Offset(center.x - wingSpan - 6.dp.toPx(), center.y),
                end = Offset(center.x - 8.dp.toPx(), center.y),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color = reticleColor,
                start = Offset(center.x - 8.dp.toPx(), center.y),
                end = Offset(center.x - 8.dp.toPx(), center.y + wingDrop),
                strokeWidth = 3.dp.toPx()
            )

            // Right wing
            drawLine(
                color = reticleColor,
                start = Offset(center.x + 8.dp.toPx(), center.y),
                end = Offset(center.x + wingSpan + 6.dp.toPx(), center.y),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color = reticleColor,
                start = Offset(center.x + 8.dp.toPx(), center.y),
                end = Offset(center.x + 8.dp.toPx(), center.y + wingDrop),
                strokeWidth = 3.dp.toPx()
            )

            // Center pip
            drawCircle(
                color = reticleColor,
                radius = 3.dp.toPx(),
                center = center
            )

            // Outer bezel ring
            drawCircle(
                color = GcsCyan.copy(alpha = 0.5f),
                radius = radius - 1.dp.toPx(),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}
