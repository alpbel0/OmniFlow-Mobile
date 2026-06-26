package com.omniflow.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private object SpacingTokens {
    val none = 0.dp
    val tiny = 2.dp
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val base = 16.dp
    val l = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 40.dp
    val section = 56.dp
}

@Immutable
data class OmniFlowSpacing(
    val none: Dp = SpacingTokens.none,
    val tiny: Dp = SpacingTokens.tiny,
    val xs: Dp = SpacingTokens.xs,
    val s: Dp = SpacingTokens.s,
    val m: Dp = SpacingTokens.m,
    val base: Dp = SpacingTokens.base,
    val l: Dp = SpacingTokens.l,
    val xl: Dp = SpacingTokens.xl,
    val xxl: Dp = SpacingTokens.xxl,
    val xxxl: Dp = SpacingTokens.xxxl,
    val section: Dp = SpacingTokens.section,
)

internal val LocalOmniFlowSpacing = staticCompositionLocalOf { OmniFlowSpacing() }
