package com.brogrammers.payana.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = OnTeal,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealDark,
    secondary = AccentCoral,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE8DC),
    onSecondaryContainer = Color(0xFF5C2A0E),
    tertiary = TealLight,
    background = WarmOffWhite,
    onBackground = TextPrimary,
    surface = WarmSurface,
    onSurface = TextPrimary,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = OutlineMuted,
    error = ErrorRed,
)

private val DarkColors = darkColorScheme(
    primary = TealLight,
    onPrimary = TealDark,
    primaryContainer = TealDark,
    onPrimaryContainer = TealContainer,
    secondary = Color(0xFFE08A5A),
    onSecondary = Color(0xFF3A1A08),
    background = Color(0xFF10201C),
    onBackground = Color(0xFFE8F2EF),
    surface = Color(0xFF152622),
    onSurface = Color(0xFFE8F2EF),
    surfaceVariant = Color(0xFF243833),
    onSurfaceVariant = Color(0xFFB7C9C3),
    outline = Color(0xFF6A7F79),
    error = Color(0xFFFFB4AB),
)

@Composable
fun PayanaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Prefer brand colours over dynamic Material You so demos look consistent.
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
