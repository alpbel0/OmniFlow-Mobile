package com.omniflow.ui.auth.register

import com.omniflow.core.common.UiText

data class PasswordRequirements(
    val hasMinimumLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasLowercase: Boolean = false,
    val hasDigit: Boolean = false,
    val hasSpecialCharacter: Boolean = false,
)

data class RegisterUiState(
    val username: String = "",
    val usernameError: UiText? = null,
    val email: String = "",
    val emailError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: UiText? = null,
    val passwordRequirements: PasswordRequirements = PasswordRequirements(),
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val generalError: UiText? = null,
)

sealed interface RegisterEffect {
    data class NavigateToVerifyEmail(val email: String) : RegisterEffect
    data object NavigateToLogin : RegisterEffect
}
