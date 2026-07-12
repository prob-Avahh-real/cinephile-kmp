// CinephileTheme.kt — 影迷 Material3 暗色主题
package com.cinephile.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CinephileRed = Color(0xFFE50914)
val SurfaceDark = Color(0xFF1A1A2E)

private val CinephileDarkColorScheme = darkColorScheme(
    primary = CinephileRed,
    secondary = Color(0xFFE87A5D),
    surface = SurfaceDark,
    onSurface = Color(0xFFE0E0E0),
    background = Color(0xFF0F0F23)
)

@Composable
fun CinephileTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CinephileDarkColorScheme,
        content = content
    )
}
