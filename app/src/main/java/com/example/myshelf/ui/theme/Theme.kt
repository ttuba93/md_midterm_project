package com.example.myshelf.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6B4F3A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF2DFCF),
    onPrimaryContainer = Color(0xFF2A1A0E),
    secondary = Color(0xFF3F6B61),
    background = Color(0xFFFFF8F2),
    surface = Color(0xFFFFF8F2),
    surfaceVariant = Color(0xFFF1E6DC),
    onSurfaceVariant = Color(0xFF51443B)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE6BFA0),
    onPrimary = Color(0xFF3F2A1B),
    primaryContainer = Color(0xFF553D2A),
    onPrimaryContainer = Color(0xFFF2DFCF),
    secondary = Color(0xFF9FD0C4),
    background = Color(0xFF1B1511),
    surface = Color(0xFF1B1511),
    surfaceVariant = Color(0xFF3D332B),
    onSurfaceVariant = Color(0xFFD4C4B7)
)

@Composable
fun MyShelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
