package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonMagenta,
    onPrimary = Color.White,
    primaryContainer = CardSurfaceElevated,
    onPrimaryContainer = NeonMagenta,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = CardSurface,
    onSecondaryContainer = NeonCyan,
    tertiary = GoldCoin,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextSecondary,
    outline = BorderHighlight,
    error = LiveRed,
    onError = Color.White
)

@Composable
fun LumiLiveTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
