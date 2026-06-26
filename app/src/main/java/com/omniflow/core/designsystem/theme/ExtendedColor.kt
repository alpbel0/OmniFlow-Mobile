package com.omniflow.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class OmniFlowColors(
    val hint: Color,
    val divider: Color,
    val fieldBorder: Color,
    val fieldContainer: Color,
    val success: Color,
    val glow: Color,
    val iconContainer: Color,
    val logoGradientEnd: Color,
    val buttonShadow: Color,
    val heroShade: Color,
    val cardShadow: Color,
    val indicatorInactive: Color,
)

internal val LightOmniFlowColors = OmniFlowColors(
    hint = Color(0xE66F7F95),
    divider = Color(0xFFE8EEF5),
    fieldBorder = Color(0xFFD9E2EC),
    fieldContainer = LightSurface,
    success = Color(0xFF16A34A),
    glow = Color(0x1F007BFF),
    iconContainer = Color(0xFFE2F1FF),
    logoGradientEnd = Color(0xFF4DB3FF),
    buttonShadow = Color(0x2E005CC7),
    heroShade = Color(0x8A0A1C2E),
    cardShadow = Color(0x1F0D1F33),
    indicatorInactive = Color(0xFFDCECFF),
)

internal val DarkOmniFlowColors = OmniFlowColors(
    hint = DarkTextSecondary,
    divider = DarkBorder,
    fieldBorder = DarkBorder,
    fieldContainer = DarkSurface,
    success = Color(0xFF22C55E),
    glow = Color(0x33007BFF),
    iconContainer = Color(0x1F007BFF),
    logoGradientEnd = Color(0xFF38A1FF),
    buttonShadow = Color(0x33007BFF),
    heroShade = Color(0xB30A1C2E),
    cardShadow = Color(0x33000000),
    indicatorInactive = Color(0xFF1E3A5F),
)

internal val LocalOmniFlowColors = staticCompositionLocalOf { LightOmniFlowColors }
