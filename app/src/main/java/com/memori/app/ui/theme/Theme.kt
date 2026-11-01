package com.memori.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    secondary = YellowSecondary,
    tertiary = GreenTertiary,
    background = BackgroundLight,
    onBackground = TextColorDark,
    surface = BackgroundLight,
    onSurface = TextColorDark
)

@Composable
fun MemoriTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}