package com.dronecontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.ui.theme.GcsCardBorder
import com.dronecontrol.ui.theme.GcsCyan
import com.dronecontrol.ui.theme.GcsEmerald
import com.dronecontrol.ui.theme.GcsTextMuted
import com.dronecontrol.ui.theme.GcsTextPrimary

/**
 * Tactical D-Pad button controller that replaces difficult analog joysticks.
 * Uses press-and-hold gestures: the drone moves while the button is pressed,
 * and immediately auto-brakes to a rock-solid hover the moment the finger is released.
 */
@Composable
fun DirectionalButtonPad(
    title: String,
    accentColor: Color,
    upLabel: String,
    upIcon: ImageVector,
    downLabel: String,
    downIcon: ImageVector,
    leftLabel: String,
    leftIcon: ImageVector,
    rightLabel: String,
    rightIcon: ImageVector,
    centerLabel: String = "BRAKE",
    onUpPressed: (Boolean) -> Unit,
    onDownPressed: (Boolean) -> Unit,
    onLeftPressed: (Boolean) -> Unit,
    onRightPressed: (Boolean) -> Unit,
    onCenterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = GcsTextMuted
        )
        Spacer(modifier = Modifier.height(8.dp))

        // D-Pad Cross Layout
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F1626))
                .border(1.5.dp, GcsCardBorder, CircleShape)
        ) {
            // TOP BUTTON (UP / FORWARD / CLIMB)
            PadButton(
                icon = upIcon,
                label = upLabel,
                color = accentColor,
                onStateChanged = onUpPressed,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )

            // BOTTOM BUTTON (DOWN / BACKWARD / DESCEND)
            PadButton(
                icon = downIcon,
                label = downLabel,
                color = accentColor,
                onStateChanged = onDownPressed,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )

            // LEFT BUTTON (LEFT / YAW CCW / ROLL LEFT)
            PadButton(
                icon = leftIcon,
                label = leftLabel,
                color = accentColor,
                onStateChanged = onLeftPressed,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            )

            // RIGHT BUTTON (RIGHT / YAW CW / ROLL RIGHT)
            PadButton(
                icon = rightIcon,
                label = rightLabel,
                color = accentColor,
                onStateChanged = onRightPressed,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            )

            // CENTER BUTTON (HOVER / BRAKE / LOCK)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E293B))
                    .border(1.5.dp, accentColor.copy(alpha = 0.6f), CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onCenterClick() }
                        )
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PanTool,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = centerLabel,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = GcsTextPrimary
                    )
                }
            }
        }
    }
}

/**
 * Individual tactile direction button with press-and-hold reactive feedback.
 */
@Composable
fun PadButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onStateChanged: (isPressed: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(width = 50.dp, height = 44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPressed) color.copy(alpha = 0.35f) else Color(0xFF162032))
            .border(
                1.dp,
                if (isPressed) color else GcsCardBorder,
                RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onStateChanged(true)
                        tryAwaitRelease()
                        isPressed = false
                        onStateChanged(false)
                    }
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isPressed) color else GcsTextPrimary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isPressed) color else GcsTextMuted
            )
        }
    }
}
