package com.omniflow.ui.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.omniflow.R
import com.omniflow.core.common.asString
import com.omniflow.uicomponents.OmniButton

@Composable
internal fun RegisterContent(
    state: RegisterUiState,
    paddingValues: PaddingValues = PaddingValues(),
    onUsernameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onConfirmPasswordVisibilityToggle: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RegisterBackground)
            .padding(paddingValues),
    ) {
        RegisterGlow()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            RegisterHeader()
            Spacer(Modifier.height(28.dp))
            RegisterField(
                label = stringResource(R.string.register_username),
                value = state.username,
                onValueChange = onUsernameChanged,
                error = state.usernameError?.asString(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                enabled = !state.isLoading,
            )
            RegisterField(
                label = stringResource(R.string.register_email),
                value = state.email,
                onValueChange = onEmailChanged,
                error = state.emailError?.asString(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                enabled = !state.isLoading,
            )
            RegisterPasswordField(
                label = stringResource(R.string.register_password),
                value = state.password,
                onValueChange = onPasswordChanged,
                isVisible = state.isPasswordVisible,
                onVisibilityToggle = onPasswordVisibilityToggle,
                error = state.passwordError?.asString(),
                enabled = !state.isLoading,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )
            RegisterPasswordField(
                label = stringResource(R.string.register_confirm_password),
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChanged,
                isVisible = state.isConfirmPasswordVisible,
                onVisibilityToggle = onConfirmPasswordVisibilityToggle,
                error = state.confirmPasswordError?.asString(),
                enabled = !state.isLoading,
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    onRegisterClick()
                },
            )
            PasswordChecklist(state.passwordRequirements)
            state.generalError?.let { error ->
                Text(
                    text = error.resolve(context),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            Spacer(Modifier.height(22.dp))
            OmniButton(
                text = stringResource(R.string.register_action),
                onClick = {
                    focusManager.clearFocus()
                    onRegisterClick()
                },
                loading = state.isLoading,
                enabled = !state.isLoading,
                containerColor = RegisterBlue,
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 14,
            )
            RegisterSocialLogin()
            RegisterFooter(enabled = !state.isLoading, onLoginClick = onLoginClick)
            Spacer(Modifier.height(24.dp))
        }
    }
}
