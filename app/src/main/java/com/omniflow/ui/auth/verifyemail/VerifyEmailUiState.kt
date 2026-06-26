package com.omniflow.ui.auth.verifyemail

import com.omniflow.core.common.UiText

const val VERIFY_EMAIL_KEY = "verify_email_address"
const val VERIFY_EMAIL_SOURCE_KEY = "verify_email_source"

enum class VerifyEmailSource {
    REGISTER,
    LOGIN,
}

data class VerifyEmailUiState(
    val email: String = "",
    val source: VerifyEmailSource = VerifyEmailSource.LOGIN,
    val cooldownSeconds: Int = 0,
    val isVerifying: Boolean = false,
    val isResending: Boolean = false,
    val isChangingEmail: Boolean = false,
    val isUpdatingEmail: Boolean = false,
    val newEmailInput: String = "",
    val newEmailError: UiText? = null,
    val verificationError: UiText? = null,
) {
    val isLoading: Boolean
        get() = isVerifying || isResending || isUpdatingEmail

    val canResend: Boolean
        get() = email.isNotBlank() && cooldownSeconds == 0 && !isLoading
}

sealed interface VerifyEmailEffect {
    data object NavigateHome : VerifyEmailEffect
    data object NavigateBack : VerifyEmailEffect
    data class NavigateLogin(val email: String) : VerifyEmailEffect
    data object OpenMailApp : VerifyEmailEffect
    data class ShowSnackbar(
        val message: UiText,
        val isError: Boolean,
    ) : VerifyEmailEffect
}
