package com.omniflow.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = OceanBlue,
    secondary = Seafoam,
    tertiary = Coral,
    background = Mist,
    surface = Sand,
    onPrimary = Mist,
    onSecondary = Ink,
    onTertiary = Mist,
    onBackground = Ink,
    onSurface = Ink,
)

private val DarkColors = darkColorScheme(
    primary = Seafoam,
    secondary = OceanBlue,
    tertiary = Coral,
    background = MidnightBlue,
    surface = Ink,
)

@Composable
fun OmniFlowTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = OmniFlowTypography,
        shapes = OmniFlowShapes,
        content = content,
    )
}
