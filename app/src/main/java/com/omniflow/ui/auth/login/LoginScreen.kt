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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omniflow.R
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
            .background(LoginBackground)
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 77.dp, y = (-70).dp)
                .blur(
                    radius = 35.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded,
                )
                .background(Color(0x1F007BFF), CircleShape),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(54.dp))

            // Logo Box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = LoginBlue.copy(alpha = 0.14f),
                        spotColor = LoginBlue.copy(alpha = 0.14f),
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(LoginBlue, Color(0xFF4DB3FF)),
                            start = Offset(7.7f, 19.9f),
                            end = Offset(50f, -10f),
                        ),
                        shape = RoundedCornerShape(18.dp),
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "O",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Title & Subtitle
            Text(
                text = stringResource(R.string.login_title),
                color = LoginTextPrimary,
                fontSize = 31.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.login_subtitle),
                color = LoginHint,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Form container
            Column(
                modifier = Modifier
                    .widthIn(max = 345.dp)
                    .fillMaxWidth(),
            ) {
                // Email input
                OmniTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = null,
                    placeholder = stringResource(R.string.login_email),
                    placeholderColor = LoginHint,
                    placeholderFontSize = 15.sp,
                    shape = RoundedCornerShape(18.dp),
                    focusedBorderColor = LoginBlue,
                    unfocusedBorderColor = LoginFieldBorder,
                    enabled = !uiState.isLoading,
                    errorMessage = uiState.emailError?.asString(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.height(57.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Password input
                Column(modifier = Modifier.fillMaxWidth()) {
                    OmniTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        label = null,
                        placeholder = stringResource(R.string.login_password),
                        placeholderColor = LoginHint,
                        placeholderFontSize = 15.sp,
                        shape = RoundedCornerShape(18.dp),
                        focusedBorderColor = LoginBlue,
                        unfocusedBorderColor = LoginFieldBorder,
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
                                    tint = LoginHint
                                )
                            }
                        },
                        modifier = Modifier.height(57.dp),
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = stringResource(R.string.login_forgot_password),
                            color = LoginBlue,
                            fontSize = 13.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .height(16.dp)
                                .clickable(
                                    enabled = !uiState.isLoading,
                                    onClick = viewModel::onForgotPasswordClicked,
                                ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                // General error display (with unverified email redirect)
                AnimatedVisibility(visible = uiState.generalError != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
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
                                color = Color(0xFF007BFF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(top = 4.dp)
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
                    containerColor = LoginBlue,
                    shape = RoundedCornerShape(18.dp),
                    fontSize = 17,
                    fontWeight = FontWeight.Bold,
                    shadowElevation = 28,
                )

                Spacer(modifier = Modifier.height(32.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.height(18.dp))

                // Google Button (Placeholder M7 - no-op click)
                Button(
                    onClick = {},
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(57.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = LoginTextPrimary,
                        disabledContainerColor = Color.White.copy(alpha = 0.5f),
                        disabledContentColor = LoginTextPrimary.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, LoginFieldBorder)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_google_g),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.login_continue_google),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

        }

        Box(
            modifier = Modifier
                .offset(x = 24.dp, y = 539.dp)
                .size(width = 345.dp, height = 24.dp),
        ) {
            Box(
                modifier = Modifier
                    .offset(y = 12.dp)
                    .size(width = 98.dp, height = 1.dp)
                    .background(LoginDivider),
            )
            Text(
                text = stringResource(R.string.login_social_divider),
                color = Color(0xFF6F7F95),
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(x = 109.dp, y = 2.dp)
                    .size(width = 100.dp, height = 16.dp),
            )
            Box(
                modifier = Modifier
                    .offset(x = 247.dp, y = 12.dp)
                    .size(width = 98.dp, height = 1.dp)
                    .background(LoginDivider),
            )
        }

        Box(
            modifier = Modifier
                .offset(x = 87.dp, y = 727.dp)
                .size(width = 220.dp, height = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.login_no_account),
                color = Color(0xFF6F7F95),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .offset(y = 2.dp)
                    .size(width = 154.dp, height = 18.dp),
            )
            Text(
                text = stringResource(R.string.login_register),
                color = LoginBlue,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .offset(x = 154.dp, y = 2.dp)
                    .size(width = 57.dp, height = 18.dp)
                    .clickable(
                        enabled = !uiState.isLoading,
                        onClick = viewModel::onRegisterClicked,
                    ),
            )
        }
    }
}

private val LoginBlue = Color(0xFF007BFF)
private val LoginBackground = Color(0xFFF5F7F8)
private val LoginFieldBorder = Color(0xFFD9E2EC)
private val LoginDivider = Color(0xFFE8EEF5)
private val LoginHint = Color(0xE66F7F95)
private val LoginTextPrimary = Color(0xFF102033)
