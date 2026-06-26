package com.omniflow.ui.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.omniflow.core.common.asString
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omniflow.R
import com.omniflow.core.designsystem.theme.OmniTokens
import com.omniflow.uicomponents.OmniButton
import com.omniflow.uicomponents.OmniTextField

@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onVerifyEmailClick: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val tokens = OmniTokens
    val colors = tokens.colors
    val spacing = tokens.spacing
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> {
                    keyboardController?.hide()
                    onLoginSuccess()
                }
                is LoginEffect.NavigateToRegister -> {
                    onRegisterClick()
                }
                is LoginEffect.NavigateToForgotPassword -> {
                    onForgotPasswordClick()
                }
                is LoginEffect.NavigateToVerifyEmail -> {
                    onVerifyEmailClick(effect.email)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .size(OmniTokens.dimens.authGlowLarge)
                .align(Alignment.TopEnd)
                .offset(x = OmniTokens.dimens.loginGlowOffsetX, y = OmniTokens.dimens.loginGlowOffsetY)
                .blur(
                    radius = spacing.xxxl - spacing.xs,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded,
                )
                .background(colors.glow, CircleShape),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(spacing.section))

            // Logo Box
            Box(
                modifier = Modifier
                    .size(OmniTokens.dimens.authLogoMedium)
                    .shadow(
                        elevation = spacing.xl,
                        shape = MaterialTheme.shapes.medium,
                        ambientColor = colorScheme.primary.copy(alpha = 0.14f),
                        spotColor = colorScheme.primary.copy(alpha = 0.14f),
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(colorScheme.primary, colors.logoGradientEnd),
                            start = Offset(7.7f, 19.9f),
                            end = Offset(50f, -10f),
                        ),
                        shape = MaterialTheme.shapes.medium,
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "O",
                    color = colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(spacing.xxxl - spacing.xs))

            // Title & Subtitle
            Text(
                text = stringResource(R.string.login_title),
                color = colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(spacing.s - spacing.tiny))
            Text(
                text = stringResource(R.string.login_subtitle),
                color = colors.hint,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing.section + spacing.s))

            // Form container
            Column(
                modifier = Modifier
                    .widthIn(max = OmniTokens.dimens.authContentMaxWidth)
                    .fillMaxWidth(),
            ) {
                // Email input
                OmniTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = null,
                    placeholder = stringResource(R.string.login_email),
                    placeholderColor = colors.hint,
                    shape = MaterialTheme.shapes.medium,
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colors.fieldBorder,
                    enabled = !uiState.isLoading,
                    errorMessage = uiState.emailError?.asString(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.height(OmniTokens.dimens.authControlHeight),
                )
                Spacer(modifier = Modifier.height(spacing.base))

                // Password input
                Column(modifier = Modifier.fillMaxWidth()) {
                    OmniTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        label = null,
                        placeholder = stringResource(R.string.login_password),
                        placeholderColor = colors.hint,
                        shape = MaterialTheme.shapes.medium,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colors.fieldBorder,
                        enabled = !uiState.isLoading,
                        errorMessage = uiState.passwordError?.asString(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.onLoginClicked()
                            }
                        ),
                        visualTransformation = if (uiState.isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = viewModel::onPasswordVisibilityToggle) {
                                Icon(
                                    imageVector = if (uiState.isPasswordVisible) {
                                        Icons.Filled.VisibilityOff
                                    } else {
                                        Icons.Filled.Visibility
                                    },
                                    contentDescription = stringResource(
                                        if (uiState.isPasswordVisible) {
                                            R.string.login_hide_password
                                        } else {
                                            R.string.login_show_password
                                        }
                                    ),
                                    tint = colors.hint
                                )
                            }
                        },
                        modifier = Modifier.height(OmniTokens.dimens.authControlHeight),
                    )

                    Spacer(modifier = Modifier.height(spacing.s + spacing.tiny))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = stringResource(R.string.login_forgot_password),
                            color = colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .height(spacing.base)
                                .clickable(
                                    enabled = !uiState.isLoading,
                                    onClick = viewModel::onForgotPasswordClicked,
                                ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.xl + spacing.tiny))

                // General error display (with unverified email redirect)
                AnimatedVisibility(visible = uiState.generalError != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.xs),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = uiState.generalError?.asString() ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (uiState.isEmailUnverified) {
                            Text(
                                text = stringResource(R.string.login_verify_email),
                                color = colorScheme.primary,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(top = spacing.xs)
                                    .clickable(onClick = viewModel::onVerifyEmailClicked)
                            )
                        }
                    }
                }

                // Login Button
                OmniButton(
                    text = stringResource(R.string.login_action),
                    onClick = viewModel::onLoginClicked,
                    loading = uiState.isLoading,
                    enabled = !uiState.isLoading,
                    containerColor = colorScheme.primary,
                    shape = MaterialTheme.shapes.medium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(spacing.xxl))
                Spacer(modifier = Modifier.height(spacing.xl))
                Spacer(modifier = Modifier.height(spacing.l - spacing.tiny))

                // Google Button (Placeholder M7 - no-op click)
                Button(
                    onClick = {},
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(OmniTokens.dimens.authControlHeight),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.surface,
                        contentColor = colorScheme.onSurface,
                        disabledContainerColor = colorScheme.surface.copy(alpha = 0.5f),
                        disabledContentColor = colorScheme.onSurface.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(OmniTokens.dimens.hairline, colors.fieldBorder)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_google_g),
                            contentDescription = null,
                            modifier = Modifier.size(OmniTokens.dimens.googleIconSize),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(spacing.s + spacing.tiny))
                        Text(
                            text = stringResource(R.string.login_continue_google),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            )
                        )
                    }
                }
            }

        }

        Box(
            modifier = Modifier
                .offset(x = spacing.xl, y = OmniTokens.dimens.loginDividerOffsetY)
                .size(width = OmniTokens.dimens.authContentMaxWidth, height = spacing.xl),
        ) {
            Box(
                modifier = Modifier
                    .offset(y = spacing.m)
                    .size(width = OmniTokens.dimens.dividerLineWidth, height = OmniTokens.dimens.hairline)
                    .background(colors.divider),
            )
            Text(
                text = stringResource(R.string.login_social_divider),
                color = colors.hint,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(x = OmniTokens.dimens.loginDividerTextOffsetX, y = spacing.tiny)
                    .size(width = OmniTokens.dimens.loginDividerTextWidth, height = spacing.base),
            )
            Box(
                modifier = Modifier
                    .offset(x = OmniTokens.dimens.authContentMaxWidth - OmniTokens.dimens.dividerLineWidth, y = spacing.m)
                    .size(width = OmniTokens.dimens.dividerLineWidth, height = OmniTokens.dimens.hairline)
                    .background(colors.divider),
            )
        }

        Box(
            modifier = Modifier
                .offset(x = OmniTokens.dimens.loginFooterOffsetX, y = OmniTokens.dimens.loginFooterOffsetY)
                .size(width = OmniTokens.dimens.authGlowLarge, height = spacing.xl),
        ) {
            Text(
                text = stringResource(R.string.login_no_account),
                color = colors.hint,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .offset(y = spacing.tiny)
                    .size(width = OmniTokens.dimens.loginFooterPromptWidth, height = OmniTokens.dimens.loginFooterTextHeight),
            )
            Text(
                text = stringResource(R.string.login_register),
                color = colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .offset(x = OmniTokens.dimens.loginFooterRegisterOffsetX, y = spacing.tiny)
                    .size(width = OmniTokens.dimens.loginFooterRegisterWidth, height = OmniTokens.dimens.loginFooterTextHeight)
                    .clickable(
                        enabled = !uiState.isLoading,
                        onClick = viewModel::onRegisterClicked,
                    ),
            )
        }
    }
}
