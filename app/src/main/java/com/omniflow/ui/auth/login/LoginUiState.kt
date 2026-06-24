package com.omniflow.ui.auth.login

import com.omniflow.core.common.UiText

data class LoginUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val generalError: UiText? = null,
    val isEmailUnverified: Boolean = false,
)

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data object NavigateToRegister : LoginEffect
    data object NavigateToForgotPassword : LoginEffect
    data class NavigateToVerifyEmail(val email: String) : LoginEffect
}
