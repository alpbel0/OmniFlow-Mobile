package com.omniflow.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider

private val LightColors = lightColorScheme(
    primary = OmniBlue,
    onPrimary = LightSurface,
    primaryContainer = OmniBlue.copy(alpha = 0.12f),
    onPrimaryContainer = LightTextPrimary,
    secondary = LightTextSecondary,
    onSecondary = LightSurface,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBackground,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = LightBorder,
)

private val DarkColors = darkColorScheme(
    primary = OmniBlue,
    onPrimary = DarkTextPrimary,
    primaryContainer = OmniBlue.copy(alpha = 0.20f),
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkTextSecondary,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
)

@Composable
fun OmniFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val omniFlowColors = if (darkTheme) DarkOmniFlowColors else LightOmniFlowColors

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = OmniFlowTypography,
        shapes = OmniFlowShapes,
    ) {
        CompositionLocalProvider(
            LocalOmniFlowColors provides omniFlowColors,
            LocalOmniFlowDimens provides OmniFlowDimens(),
            LocalOmniFlowSpacing provides OmniFlowSpacing(),
            content = content,
        )
    }
}
