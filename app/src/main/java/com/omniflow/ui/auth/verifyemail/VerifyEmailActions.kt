package com.omniflow.ui.auth.verifyemail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.omniflow.R
import com.omniflow.core.common.asString
import com.omniflow.core.designsystem.theme.OmniTokens
import com.omniflow.uicomponents.OmniButton
import com.omniflow.uicomponents.OmniTextField

@Composable
internal fun VerificationActions(
    state: VerifyEmailUiState,
    onVerifiedLoginClick: () -> Unit,
    onOpenMailClick: () -> Unit,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OmniTokens.colors
    val spacing = OmniTokens.spacing
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth().padding(end = OmniTokens.dimens.verifyContentEndPadding)) {
        OmniButton(
            text = stringResource(R.string.verify_email_confirm),
            onClick = onVerifiedLoginClick,
            enabled = !state.isLoading,
            loading = state.isVerifying,
            containerColor = colorScheme.primary,
            shape = MaterialTheme.shapes.large,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(spacing.m))
        Box(modifier = Modifier.fillMaxWidth().height(OmniTokens.dimens.loginFooterTextHeight), contentAlignment = Alignment.Center) {
            Text(
                text = state.verificationError?.asString().orEmpty(),
                color = colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(spacing.m))
        Button(
            onClick = onOpenMailClick,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(OmniTokens.dimens.authControlHeight),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(OmniTokens.dimens.hairline, colors.fieldBorder),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.onSurface,
                disabledContainerColor = colorScheme.surface.copy(alpha = 0.55f),
                disabledContentColor = colorScheme.onSurface.copy(alpha = 0.45f),
            ),
        ) {
            Text(
                text = stringResource(R.string.verify_email_open_mail),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(spacing.m))
        ResendRow(state, onResendClick)
        Spacer(Modifier.height(spacing.m))
        Text(
            text = stringResource(R.string.verify_email_spam_help),
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ResendRow(state: VerifyEmailUiState, onResendClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth().height(OmniTokens.spacing.l),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.verify_email_resend_prompt) + " ",
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = if (state.cooldownSeconds > 0) {
                stringResource(R.string.verify_email_resend_countdown, state.cooldownSeconds)
            } else {
                stringResource(R.string.verify_email_resend)
            },
            color = if (state.canResend) colorScheme.primary else colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(enabled = state.canResend, onClick = onResendClick),
        )
    }
}

@Composable
internal fun FooterLinks(
    state: VerifyEmailUiState,
    onChangeEmailClick: () -> Unit,
    onNewEmailChange: (String) -> Unit,
    onSubmitEmailChangeClick: () -> Unit,
    onCancelEmailChangeClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OmniTokens.spacing
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier.fillMaxWidth().padding(end = OmniTokens.dimens.verifyContentEndPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.s - spacing.tiny),
    ) {
        if (state.isChangingEmail) {
            OmniTextField(
                value = state.newEmailInput,
                onValueChange = onNewEmailChange,
                label = null,
                placeholder = stringResource(R.string.verify_email_new_email),
                enabled = !state.isLoading,
                errorMessage = state.newEmailError?.asString(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSubmitEmailChangeClick() },
                ),
            )
            Spacer(Modifier.height(spacing.s - spacing.tiny))
            OmniButton(
                text = stringResource(R.string.verify_email_update_email),
                onClick = onSubmitEmailChangeClick,
                enabled = !state.isLoading,
                loading = state.isUpdatingEmail,
                containerColor = colorScheme.primary,
                shape = MaterialTheme.shapes.medium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.verify_email_cancel_change),
                color = colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(enabled = !state.isLoading, onClick = onCancelEmailChangeClick),
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.verify_email_change_prompt) + " ",
                    color = colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = stringResource(R.string.verify_email_change),
                    color = colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(enabled = !state.isLoading, onClick = onChangeEmailClick),
                )
            }
        }
        Text(
            text = stringResource(R.string.verify_email_back_login),
            color = colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(enabled = !state.isLoading, onClick = onBackToLoginClick),
        )
    }
}
