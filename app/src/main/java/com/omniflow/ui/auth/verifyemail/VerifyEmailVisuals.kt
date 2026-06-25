package com.omniflow.ui.auth.verifyemail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.omniflow.R
import com.omniflow.core.designsystem.theme.OmniTokens

@Composable
internal fun VerifyLogo(modifier: Modifier = Modifier) {
    val colors = OmniTokens.colors
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .size(OmniTokens.dimens.authLogoSmall)
            .shadow(
                elevation = OmniTokens.spacing.m,
                shape = MaterialTheme.shapes.small,
                ambientColor = colorScheme.primary.copy(alpha = 0.18f),
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(colorScheme.primary, colors.logoGradientEnd),
                    start = Offset.Zero,
                    end = Offset(44f, 44f),
                ),
                shape = MaterialTheme.shapes.small,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "O",
            color = colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
internal fun EmailHero(modifier: Modifier = Modifier) {
    val colors = OmniTokens.colors
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .size(OmniTokens.dimens.authIconContainer)
            .shadow(OmniTokens.spacing.xl, CircleShape, ambientColor = colorScheme.primary.copy(alpha = 0.12f))
            .background(colors.iconContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Email,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(OmniTokens.dimens.authIconLarge),
        )
    }
}

@Composable
internal fun VerificationCopy(email: String, modifier: Modifier = Modifier) {
    val spacing = OmniTokens.spacing
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = OmniTokens.dimens.verifyContentEndPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.verify_email_title),
            color = colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(spacing.m))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = colorScheme.primary, fontWeight = FontWeight.SemiBold)) {
                    append(email)
                }
                append(" adresine bir doğrulama linki gönderdik.")
            },
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(spacing.m))
        Text(
            text = stringResource(R.string.verify_email_direction),
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
