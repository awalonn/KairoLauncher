package com.kairo.launcher.ui.theme

import android.graphics.Color.parseColor
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun KairoTheme(
    accentHex: String = "#7C5CFF",
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val accent = remember(accentHex) {
        // safe parse with fallback
        runCatching { Color(parseColor(accentHex)) }.getOrElse { Color(0xFF7C5CFF) }
    }

    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = accent,
            onPrimary = Color.White,
            secondary = accent,
            tertiary = accent,
        )
    } else {
        lightColorScheme(
            primary = accent,
            secondary = accent,
            tertiary = accent,
        )
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}
