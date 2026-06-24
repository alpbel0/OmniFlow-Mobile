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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omniflow.R
import com.omniflow.core.common.asString
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
    Column(modifier = modifier.fillMaxWidth().padding(end = 48.dp)) {
        OmniButton(
            text = stringResource(R.string.verify_email_confirm),
            onClick = onVerifiedLoginClick,
            enabled = !state.isLoading,
            loading = state.isVerifying,
            containerColor = VerifyBlue,
            shape = RoundedCornerShape(20.dp),
            fontSize = 16,
            fontWeight = FontWeight.SemiBold,
            shadowElevation = 22,
        )
        Spacer(Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(18.dp), contentAlignment = Alignment.Center) {
            Text(
                text = state.verificationError?.asString().orEmpty(),
                color = VerifyError,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onOpenMailClick,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, VerifyBorder),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = VerifyTextPrimary,
                disabledContainerColor = Color.White.copy(alpha = 0.55f),
                disabledContentColor = VerifyTextPrimary.copy(alpha = 0.45f),
            ),
        ) {
            Text(
                text = stringResource(R.string.verify_email_open_mail),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(12.dp))
        ResendRow(state, onResendClick)
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.verify_email_spam_help),
            color = VerifyTextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ResendRow(state: VerifyEmailUiState, onResendClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.verify_email_resend_prompt) + " ",
            color = VerifyTextSecondary,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )
        Text(
            text = if (state.cooldownSeconds > 0) {
                stringResource(R.string.verify_email_resend_countdown, state.cooldownSeconds)
            } else {
                stringResource(R.string.verify_email_resend)
            },
            color = if (state.canResend) VerifyBlue else VerifyTextSecondary,
            fontSize = 13.sp,
            lineHeight = 20.sp,
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
    Column(
        modifier = modifier.fillMaxWidth().padding(end = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
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
            Spacer(Modifier.height(6.dp))
            OmniButton(
                text = stringResource(R.string.verify_email_update_email),
                onClick = onSubmitEmailChangeClick,
                enabled = !state.isLoading,
                loading = state.isUpdatingEmail,
                containerColor = VerifyBlue,
                shape = RoundedCornerShape(18.dp),
                fontSize = 14,
                fontWeight = FontWeight.SemiBold,
                shadowElevation = 14,
            )
            Text(
                text = stringResource(R.string.verify_email_cancel_change),
                color = VerifyTextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(enabled = !state.isLoading, onClick = onCancelEmailChangeClick),
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.verify_email_change_prompt) + " ",
                    color = VerifyTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                )
                Text(
                    text = stringResource(R.string.verify_email_change),
                    color = VerifyBlue,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(enabled = !state.isLoading, onClick = onChangeEmailClick),
                )
            }
        }
        Text(
            text = stringResource(R.string.verify_email_back_login),
            color = VerifyBlue,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(enabled = !state.isLoading, onClick = onBackToLoginClick),
        )
    }
}
