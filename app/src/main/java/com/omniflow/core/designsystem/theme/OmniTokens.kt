package com.omniflow.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object OmniTokens {
    val spacing: OmniFlowSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalOmniFlowSpacing.current

    val colors: OmniFlowColors
        @Composable
        @ReadOnlyComposable
        get() = LocalOmniFlowColors.current

    val dimens: OmniFlowDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalOmniFlowDimens.current
}
