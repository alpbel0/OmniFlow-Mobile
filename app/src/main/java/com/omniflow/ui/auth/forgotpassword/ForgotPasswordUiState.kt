package com.omniflow.ui.auth.forgotpassword

import com.omniflow.core.common.UiText

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val sentEmail: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val cooldownSeconds: Int = 0,
) {
    val canSubmit: Boolean
        get() = !isLoading

    val canResend: Boolean
        get() = isSuccess && !isLoading && cooldownSeconds == 0 && sentEmail.isNotBlank()
}

sealed interface ForgotPasswordEffect {
    data object NavigateLogin : ForgotPasswordEffect
    data object OpenMailApp : ForgotPasswordEffect
    data class ShowSnackbar(
        val message: UiText,
        val isError: Boolean,
    ) : ForgotPasswordEffect
}
