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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VerifyBackground)
            .padding(bottom = paddingValues.calculateBottomPadding()),
    ) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .offset(x = 115.dp, y = (-132).dp)
                .blur(50.dp, BlurredEdgeTreatment.Unbounded)
                .background(VerifyGlow, CircleShape),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 393.dp)
                    .fillMaxWidth()
                    .height(852.dp),
            ) {
                VerifyLogo(Modifier.offset(x = 24.dp, y = 58.dp))
                EmailHero(Modifier.align(Alignment.TopCenter).offset(y = 126.dp))
                VerificationCopy(state.email, Modifier.offset(x = 24.dp, y = 246.dp))
                VerificationActions(
                    state = state,
                    onVerifiedLoginClick = onVerifiedLoginClick,
                    onOpenMailClick = onOpenMailClick,
                    onResendClick = onResendClick,
                    modifier = Modifier.offset(x = 24.dp, y = 430.dp),
                )
                FooterLinks(
                    state = state,
                    onChangeEmailClick = onChangeEmailClick,
                    onNewEmailChange = onNewEmailChange,
                    onSubmitEmailChangeClick = onSubmitEmailChangeClick,
                    onCancelEmailChangeClick = onCancelEmailChangeClick,
                    onBackToLoginClick = onBackToLoginClick,
                    modifier = Modifier.offset(x = 24.dp, y = 744.dp),
                )
            }
        }
    }
}

internal val VerifyBlue = Color(0xFF007BFF)
internal val VerifyBackground = Color(0xFFF5F7F8)
internal val VerifyGlow = Color(0x1F007BFF)
internal val VerifyTextPrimary = Color(0xFF0F172A)
internal val VerifyTextSecondary = Color(0xFF6F7F95)
internal val VerifyBorder = Color(0xFFD8E2EF)
internal val VerifyError = Color(0xFFD14343)
internal val VerifySuccess = Color(0xFF16865C)
