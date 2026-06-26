package com.omniflow.ui.auth.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.omniflow.R
import com.omniflow.core.designsystem.theme.OmniTokens
import com.omniflow.uicomponents.OmniTextField

@Composable
internal fun HeaderCopy(title: String, description: String) {
    val spacing = OmniTokens.spacing

    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(spacing.s + spacing.tiny))
    Text(
        text = description,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
internal fun EmailInput(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    enabled: Boolean,
    onDone: () -> Unit,
) {
    OmniTextField(
        value = value,
        onValueChange = onValueChange,
        label = null,
        placeholder = stringResource(R.string.forgot_password_email_placeholder),
        enabled = enabled,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = Modifier.fillMaxWidth().height(OmniTokens.dimens.authControlHeight),
    )
}

@Composable
internal fun ReservedErrorText(message: String?) {
    Text(
        text = message.orEmpty(),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth().height(OmniTokens.dimens.sentCheckBadgeSize).padding(top = OmniTokens.spacing.s),
    )
}

@Composable
internal fun ResendLink(uiState: ForgotPasswordUiState, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val text = if (uiState.cooldownSeconds > 0) {
        stringResource(R.string.forgot_password_resend_countdown, uiState.cooldownSeconds)
    } else {
        stringResource(R.string.forgot_password_resend)
    }
    Text(
        text = text,
        color = if (uiState.canResend) colorScheme.primary else colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().clickable(enabled = uiState.canResend, onClick = onClick),
    )
}

@Composable
internal fun FooterLoginLink(enabled: Boolean, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.forgot_password_footer_prefix))
            withStyle(SpanStyle(color = colorScheme.primary, fontWeight = FontWeight.SemiBold)) {
                append(stringResource(R.string.forgot_password_footer_action))
            }
        },
        color = colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().clickable(enabled = enabled, onClick = onClick),
    )
}

@Composable
internal fun BoxScope.TopGlow() {
    Box(
        modifier = Modifier
            .size(OmniTokens.dimens.authLargeGlow)
            .align(Alignment.TopEnd)
            .offset(x = OmniTokens.dimens.verifyGlowOffsetX, y = OmniTokens.dimens.verifyGlowOffsetY)
            .blur(OmniTokens.spacing.xxxl + OmniTokens.spacing.tiny, BlurredEdgeTreatment.Unbounded)
            .background(OmniTokens.colors.glow, CircleShape),
    )
}

@Composable
internal fun LogoMark() {
    val colors = OmniTokens.colors
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .offset(x = OmniTokens.dimens.verifyLogoOffsetX, y = OmniTokens.dimens.verifyLogoOffsetY)
            .size(OmniTokens.dimens.authLogoSmall)
            .shadow(
                elevation = OmniTokens.spacing.l,
                shape = MaterialTheme.shapes.small,
                ambientColor = colorScheme.primary.copy(alpha = 0.14f),
                spotColor = colorScheme.primary.copy(alpha = 0.14f),
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(colorScheme.primary, colors.logoGradientEnd),
                    start = Offset(4f, 32f),
                    end = Offset(44f, 0f),
                ),
                shape = MaterialTheme.shapes.small,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text("O", color = colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun LockHero() {
    HeroCircle {
        Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(OmniTokens.dimens.authIconLarge))
    }
}

@Composable
internal fun SentHero() {
    Box(contentAlignment = Alignment.Center) {
        HeroCircle {
            Icon(Icons.Filled.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(OmniTokens.dimens.authIconLarge))
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(OmniTokens.dimens.sentCheckBadgeSize)
                .background(OmniTokens.colors.success, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(OmniTokens.dimens.authLogoInner))
        }
    }
}

@Composable
private fun HeroCircle(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(OmniTokens.dimens.authIconContainer)
            .shadow(
                elevation = OmniTokens.spacing.xl,
                shape = CircleShape,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            )
            .background(OmniTokens.colors.iconContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
