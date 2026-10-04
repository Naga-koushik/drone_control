package com.dronecontrol.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Dark Palette (Professional deep titanium / aviation slate)
val GcsDarkBackground = Color(0xFF0A0F1D)
val GcsDarkCard = Color(0xFF12192C)
val GcsDarkCardBorder = Color(0xFF1E293B)
val GcsDarkSurfaceVariant = Color(0xFF1A233A)
val GcsDarkTextPrimary = Color(0xFFF8FAFC)
val GcsDarkTextSecondary = Color(0xFF94A3B8)
val GcsDarkTextMuted = Color(0xFF64748B)

// Light Palette (Professional aerospace clean light)
val GcsLightBackground = Color(0xFFF1F5F9)
val GcsLightCard = Color(0xFFFFFFFF)
val GcsLightCardBorder = Color(0xFFE2E8F0)
val GcsLightSurfaceVariant = Color(0xFFF8FAFC)
val GcsLightTextPrimary = Color(0xFF0F172A)
val GcsLightTextSecondary = Color(0xFF475569)
val GcsLightTextMuted = Color(0xFF94A3B8)

// Professional Accent Colors
val GcsCyan = Color(0xFF0284C7)         // Avionics precision blue
val GcsCyanMuted = Color(0x330284C7)
val GcsEmerald = Color(0xFF10B981)      // Telemetry healthy / armed green
val GcsEmeraldMuted = Color(0x3310B981)
val GcsAmber = Color(0xFFF59E0B)        // Aviation caution amber
val GcsAmberMuted = Color(0x33F59E0B)
val GcsCrimson = Color(0xFFEF4444)      // Warning / live motors red
val GcsCrimsonMuted = Color(0x33EF4444)
val GcsIndigo = Color(0xFF6366F1)

val HorizonSky = Color(0xFF0284C7)
val HorizonGround = Color(0xFF78350F)

// Legacy compatibility aliases pointing to dynamic theme or default dark
val GcsCardBackground = GcsDarkCard
val GcsCardBorder = GcsDarkCardBorder
val GcsSurfaceVariant = GcsDarkSurfaceVariant
val GcsTextPrimary = GcsDarkTextPrimary
val GcsTextSecondary = GcsDarkTextSecondary
val GcsTextMuted = GcsDarkTextMuted

@Immutable
data class GcsColors(
    val background: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val secondary: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val isDark: Boolean
)

val DarkGcsColors = GcsColors(
    background = GcsDarkBackground,
    cardBackground = GcsDarkCard,
    cardBorder = GcsDarkCardBorder,
    surfaceVariant = GcsDarkSurfaceVariant,
    textPrimary = GcsDarkTextPrimary,
    textSecondary = GcsDarkTextSecondary,
    textMuted = GcsDarkTextMuted,
    primary = GcsCyan,
    secondary = GcsIndigo,
    success = GcsEmerald,
    warning = GcsAmber,
    error = GcsCrimson,
    isDark = true
)

val LightGcsColors = GcsColors(
    background = GcsLightBackground,
    cardBackground = GcsLightCard,
    cardBorder = GcsLightCardBorder,
    surfaceVariant = GcsLightSurfaceVariant,
    textPrimary = GcsLightTextPrimary,
    textSecondary = GcsLightTextSecondary,
    textMuted = GcsLightTextMuted,
    primary = GcsCyan,
    secondary = GcsIndigo,
    success = GcsEmerald,
    warning = GcsAmber,
    error = GcsCrimson,
    isDark = false
)

val LocalGcsColors = staticCompositionLocalOf { DarkGcsColors }

object GcsTheme {
    val colors: GcsColors
        @Composable
        get() = LocalGcsColors.current
}
