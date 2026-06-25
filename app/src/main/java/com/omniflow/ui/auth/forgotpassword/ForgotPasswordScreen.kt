package com.omniflow.ui.auth.forgotpassword

import android.content.ActivityNotFoundException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omniflow.R
import com.omniflow.core.common.asString
import com.omniflow.core.designsystem.theme.OmniTokens
import com.omniflow.ui.auth.verifyemail.createEmailAppIntent
import com.omniflow.uicomponents.OmniButton

@Composable
fun ForgotPasswordScreen(
    paddingValues: PaddingValues,
    onLoginClick: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val mailAppMissingMessage = stringResource(R.string.verify_email_mail_app_missing)
    val spacing = OmniTokens.spacing

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ForgotPasswordEffect.NavigateLogin -> onLoginClick()
                is ForgotPasswordEffect.OpenMailApp -> {
                    keyboardController?.hide()
                    try {
                        context.startActivity(createEmailAppIntent(context))
                    } catch (_: ActivityNotFoundException) {
                        snackbarHostState.showSnackbar(mailAppMissingMessage)
                    }
                }
                is ForgotPasswordEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .imePadding()
                    .padding(horizontal = spacing.xl, vertical = OmniTokens.dimens.snackbarVerticalPadding),
            )
        },
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(scaffoldPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            TopGlow()
            LogoMark()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(OmniTokens.dimens.verifyHeroOffsetY))

                if (uiState.isSuccess) {
                    SentHero()
                    Spacer(modifier = Modifier.height(spacing.xl))
                    SuccessContent(uiState = uiState, viewModel = viewModel)
                } else {
                    LockHero()
                    Spacer(modifier = Modifier.height(spacing.xl))
                    RequestContent(uiState = uiState, viewModel = viewModel)
                }

                Spacer(modifier = Modifier.height(OmniTokens.dimens.verifyHeroOffsetY + spacing.tiny))
                FooterLoginLink(
                    enabled = !uiState.isLoading,
                    onClick = viewModel::onBackToLoginClicked,
                )
                Spacer(modifier = Modifier.height(spacing.xxxl + spacing.tiny))
            }
        }
    }
}

@Composable
private fun RequestContent(
    uiState: ForgotPasswordUiState,
    viewModel: ForgotPasswordViewModel,
) {
    val focusManager = LocalFocusManager.current
    val spacing = OmniTokens.spacing

    Column(
        modifier = Modifier
            .widthIn(max = OmniTokens.dimens.authContentMaxWidth)
            .fillMaxWidth(),
    ) {
        HeaderCopy(
            title = stringResource(R.string.forgot_password_title),
            description = stringResource(R.string.forgot_password_description),
        )
        Spacer(modifier = Modifier.height(spacing.xxxl - spacing.tiny))
        Text(
            text = stringResource(R.string.forgot_password_email_label),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(spacing.s))
        EmailInput(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            isError = uiState.emailError != null,
            enabled = !uiState.isLoading,
            onDone = {
                focusManager.clearFocus()
                viewModel.onSendResetLinkClicked()
            },
        )
        ReservedErrorText(uiState.emailError?.asString())
        OmniButton(
            text = stringResource(R.string.forgot_password_send),
            onClick = viewModel::onSendResetLinkClicked,
            loading = uiState.isLoading,
            enabled = uiState.canSubmit,
            containerColor = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.large,
            fontWeight = FontWeight.SemiBold,
            shadowElevation = OmniTokens.spacing.none,
        )
    }
}

@Composable
private fun SuccessContent(
    uiState: ForgotPasswordUiState,
    viewModel: ForgotPasswordViewModel,
) {
    val spacing = OmniTokens.spacing
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .widthIn(max = OmniTokens.dimens.authContentMaxWidth)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.forgot_password_success_title),
            color = colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(spacing.m))
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.forgot_password_success_prefix))
                withStyle(SpanStyle(color = colorScheme.primary, fontWeight = FontWeight.SemiBold)) {
                    append(uiState.sentEmail)
                }
                append(stringResource(R.string.forgot_password_success_suffix))
            },
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(spacing.m))
        Text(
            text = stringResource(R.string.forgot_password_success_direction),
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.height(spacing.section - spacing.xs))
        OmniButton(
            text = stringResource(R.string.forgot_password_open_mail),
            onClick = viewModel::onOpenMailClicked,
            enabled = !uiState.isLoading,
            containerColor = colorScheme.primary,
            shape = MaterialTheme.shapes.medium,
            fontWeight = FontWeight.SemiBold,
            shadowElevation = OmniTokens.spacing.none,
        )
        Spacer(modifier = Modifier.height(spacing.m))
        ResendLink(uiState = uiState, onClick = viewModel::onResendClicked)
        Spacer(modifier = Modifier.height(spacing.m))
        Text(
            text = stringResource(R.string.forgot_password_spam_help),
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
