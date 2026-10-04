package com.dronecontrol.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GcsCyan,
    onPrimary = GcsDarkBackground,
    primaryContainer = GcsSurfaceVariant,
    onPrimaryContainer = GcsCyan,
    secondary = GcsEmerald,
    onSecondary = GcsDarkBackground,
    error = GcsCrimson,
    background = GcsDarkBackground,
    surface = GcsCardBackground,
    onBackground = GcsTextPrimary,
    onSurface = GcsTextPrimary,
    surfaceVariant = GcsSurfaceVariant,
    onSurfaceVariant = GcsTextSecondary,
    outline = GcsCardBorder
)

@Composable
fun DroneControlTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = GcsDarkBackground.toArgb()
                window.navigationBarColor = GcsDarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
