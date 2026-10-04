package com.dronecontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PanTool
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dronecontrol.ui.theme.GcsTheme

/**
 * Modern borderless tactile D-Pad controller for simple, stable drone operations.
 * - Clean borderless buttons (no weird borders).
 * - Icons only inside buttons (no text clutter).
 * - Press-and-hold activation with automatic hover braking upon finger release.
 */
@Composable
fun DirectionalButtonPad(
    title: String,
    accentColor: Color,
    upIcon: ImageVector,
    downIcon: ImageVector,
    leftIcon: ImageVector,
    rightIcon: ImageVector,
    padSize: Dp = 160.dp,
    buttonSize: Dp = 48.dp,
    centerLabel: String = "BRAKE",
    onUpPressed: (Boolean) -> Unit,
    onDownPressed: (Boolean) -> Unit,
    onLeftPressed: (Boolean) -> Unit,
    onRightPressed: (Boolean) -> Unit,
    onCenterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = GcsTheme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = colors.textMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // D-Pad Cross Layout with subtle circular backdrop (no hard borders)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(padSize)
                .clip(CircleShape)
                .background(
                    if (colors.isDark) Color(0xFF141B2D) else Color(0xFFE2E8F0).copy(alpha = 0.7f)
                )
        ) {
            // TOP BUTTON (UP / FORWARD / CLIMB)
            BorderlessPadButton(
                icon = upIcon,
                color = accentColor,
                buttonSize = buttonSize,
                onStateChanged = onUpPressed,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
            )

            // BOTTOM BUTTON (DOWN / BACKWARD / DESCEND)
            BorderlessPadButton(
                icon = downIcon,
                color = accentColor,
                buttonSize = buttonSize,
                onStateChanged = onDownPressed,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
            )

            // LEFT BUTTON (LEFT / YAW CCW / ROLL LEFT)
            BorderlessPadButton(
                icon = leftIcon,
                color = accentColor,
                buttonSize = buttonSize,
                onStateChanged = onLeftPressed,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 4.dp)
            )

            // RIGHT BUTTON (RIGHT / YAW CW / ROLL RIGHT)
            BorderlessPadButton(
                icon = rightIcon,
                color = accentColor,
                buttonSize = buttonSize,
                onStateChanged = onRightPressed,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp)
            )

            // CENTER BUTTON (HOVER / BRAKE / LOCK)
            val centerSize = (buttonSize.value * 0.95f).dp
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(centerSize)
                    .clip(CircleShape)
                    .background(
                        if (colors.isDark) Color(0xFF1E293B) else Color(0xFFCBD5E1)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onCenterClick() })
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PanTool,
                        contentDescription = centerLabel,
                        tint = accentColor,
                        modifier = Modifier.size((buttonSize.value * 0.38f).dp)
                    )
                    Text(
                        text = centerLabel,
                        fontSize = if (buttonSize > 55.dp) 9.sp else 7.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textPrimary
                    )
                }
            }
        }
    }
}

/**
 * Clean, borderless touch button with smooth rounded feedback.
 */
@Composable
fun BorderlessPadButton(
    icon: ImageVector,
    color: Color,
    buttonSize: Dp = 48.dp,
    onStateChanged: (isPressed: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GcsTheme.colors
    var isPressed by remember { mutableStateOf(false) }

    val defaultBg = if (colors.isDark) Color(0xFF1E2738) else Color(0xFFF1F5F9)
    val activeBg = color.copy(alpha = if (colors.isDark) 0.40f else 0.25f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(buttonSize)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isPressed) activeBg else defaultBg)
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isPressed) color else (if (colors.isDark) colors.textPrimary else colors.textSecondary),
            modifier = Modifier.size((buttonSize.value * 0.52f).dp)
        )
    }
}
