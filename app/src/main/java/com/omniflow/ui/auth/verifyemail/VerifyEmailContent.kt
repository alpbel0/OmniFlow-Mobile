package com.omniflow.ui.auth.verifyemail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import com.omniflow.core.designsystem.theme.OmniTokens

@Composable
fun VerifyEmailContent(
    state: VerifyEmailUiState,
    paddingValues: PaddingValues,
    onVerifiedLoginClick: () -> Unit,
    onOpenMailClick: () -> Unit,
    onResendClick: () -> Unit,
    onChangeEmailClick: () -> Unit,
    onNewEmailChange: (String) -> Unit,
    onSubmitEmailChangeClick: () -> Unit,
    onCancelEmailChangeClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
) {
    val colors = OmniTokens.colors
    val spacing = OmniTokens.spacing

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = paddingValues.calculateBottomPadding()),
    ) {
        Box(
            modifier = Modifier
                .size(OmniTokens.dimens.authLargeGlow)
                .align(Alignment.TopEnd)
                .offset(x = OmniTokens.dimens.verifyGlowOffsetX, y = OmniTokens.dimens.verifyGlowOffsetY)
                .blur(spacing.section - spacing.s + spacing.tiny, BlurredEdgeTreatment.Unbounded)
                .background(colors.glow, CircleShape),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = OmniTokens.dimens.authFrameMaxWidth)
                    .fillMaxWidth()
                    .height(OmniTokens.dimens.authFrameHeight),
            ) {
                VerifyLogo(Modifier.offset(x = OmniTokens.dimens.verifyLogoOffsetX, y = OmniTokens.dimens.verifyLogoOffsetY))
                EmailHero(Modifier.align(Alignment.TopCenter).offset(y = OmniTokens.dimens.verifyHeroOffsetY))
                VerificationCopy(state.email, Modifier.offset(x = OmniTokens.dimens.verifyCopyOffsetX, y = OmniTokens.dimens.verifyCopyOffsetY))
                VerificationActions(
                    state = state,
                    onVerifiedLoginClick = onVerifiedLoginClick,
                    onOpenMailClick = onOpenMailClick,
                    onResendClick = onResendClick,
                    modifier = Modifier.offset(x = OmniTokens.dimens.verifyActionsOffsetX, y = OmniTokens.dimens.verifyActionsOffsetY),
                )
                FooterLinks(
                    state = state,
                    onChangeEmailClick = onChangeEmailClick,
                    onNewEmailChange = onNewEmailChange,
                    onSubmitEmailChangeClick = onSubmitEmailChangeClick,
                    onCancelEmailChangeClick = onCancelEmailChangeClick,
                    onBackToLoginClick = onBackToLoginClick,
                    modifier = Modifier.offset(x = OmniTokens.dimens.verifyFooterOffsetX, y = OmniTokens.dimens.verifyFooterOffsetY),
                )
            }
        }
    }
}
