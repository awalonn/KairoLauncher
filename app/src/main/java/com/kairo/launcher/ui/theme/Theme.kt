package com.kairo.launcher.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7C5CFF),
    background = Color(0xFF0F1115),
    surface = Color(0xFF13151B),
    onSurface = Color(0xFFE6E6E6)
)

@Composable
fun KairoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, typography = Typography(), content = content)
}