package com.dronecontrol.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GcsCyan,
    onPrimary = GcsDarkBackground,
    primaryContainer = GcsDarkSurfaceVariant,
    onPrimaryContainer = GcsCyan,
    secondary = GcsEmerald,
    onSecondary = GcsDarkBackground,
    error = GcsCrimson,
    background = GcsDarkBackground,
    surface = GcsDarkCard,
    onBackground = GcsDarkTextPrimary,
    onSurface = GcsDarkTextPrimary,
    surfaceVariant = GcsDarkSurfaceVariant,
    onSurfaceVariant = GcsDarkTextSecondary,
    outline = GcsDarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = GcsCyan,
    onPrimary = GcsLightBackground,
    primaryContainer = GcsLightSurfaceVariant,
    onPrimaryContainer = GcsCyan,
    secondary = GcsEmerald,
    onSecondary = GcsLightBackground,
    error = GcsCrimson,
    background = GcsLightBackground,
    surface = GcsLightCard,
    onBackground = GcsLightTextPrimary,
    onSurface = GcsLightTextPrimary,
    surfaceVariant = GcsLightSurfaceVariant,
    onSurfaceVariant = GcsLightTextSecondary,
    outline = GcsLightCardBorder
)

@Composable
fun DroneControlTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val gcsColors = if (darkTheme) DarkGcsColors else LightGcsColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = gcsColors.background.toArgb()
                window.navigationBarColor = gcsColors.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalGcsColors provides gcsColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
