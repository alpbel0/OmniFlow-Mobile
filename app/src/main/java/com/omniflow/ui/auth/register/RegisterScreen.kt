package com.omniflow.ui.auth.register

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterScreen(
    paddingValues: PaddingValues,
    onRegisterSuccess: (String) -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RegisterEffect.NavigateToVerifyEmail -> onRegisterSuccess(effect.email)
                RegisterEffect.NavigateToLogin -> onLoginClick()
            }
        }
    }

    RegisterContent(
        state = state,
        paddingValues = paddingValues,
        onUsernameChanged = viewModel::onUsernameChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onPasswordVisibilityToggle = viewModel::onPasswordVisibilityToggle,
        onConfirmPasswordVisibilityToggle = viewModel::onConfirmPasswordVisibilityToggle,
        onRegisterClick = viewModel::onRegisterClicked,
        onLoginClick = viewModel::onLoginClicked,
    )
}
