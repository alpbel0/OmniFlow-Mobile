package com.omniflow.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.R
import com.omniflow.core.auth.PendingAuthCredentialsStore
import com.omniflow.core.common.EmailValidator
import com.omniflow.core.common.UiText
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialsStore: PendingAuthCredentialsStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<RegisterEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val usernameRegex = "^[A-Za-z0-9_.-]{3,50}\$".toRegex()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null, generalError = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { state ->
            state.copy(
                password = password,
                passwordError = null,
                confirmPasswordError = mismatchError(password, state.confirmPassword),
                passwordRequirements = password.requirements(),
                generalError = null,
            )
        }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { state ->
            state.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = mismatchError(state.password, confirmPassword),
                generalError = null,
            )
        }
    }

    fun onPasswordVisibilityToggle() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onConfirmPasswordVisibilityToggle() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onLoginClicked() {
        viewModelScope.launch { _effects.send(RegisterEffect.NavigateToLogin) }
    }

    fun onRegisterClicked() {
        val state = _uiState.value
        if (state.isLoading) return

        val validatedState = state.validate()
        if (validatedState.hasValidationError()) {
            _uiState.value = validatedState
            return
        }

        _uiState.update { it.copy(isLoading = true, generalError = null) }
        viewModelScope.launch {
            when (
                val result = authRepository.register(
                    state.username.trim(),
                    state.email.trim(),
                    state.password,
                    state.confirmPassword,
                )
            ) {
                is ApiResult.Success -> {
                    credentialsStore.save(state.email.trim(), state.password)
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.send(RegisterEffect.NavigateToVerifyEmail(state.email.trim()))
                }
                is ApiResult.Error -> applyApiError(result)
                is ApiResult.Loading -> Unit
            }
        }
    }

    private fun applyApiError(error: ApiResult.Error) {
        val fields = error.toRegisterFieldErrors()
        _uiState.update {
            it.copy(
                isLoading = false,
                usernameError = fields.username,
                emailError = fields.email,
                passwordError = fields.password,
                confirmPasswordError = fields.confirmPassword,
                generalError = if (fields.hasFieldError) null else error.message,
            )
        }
    }

    private fun RegisterUiState.validate(): RegisterUiState {
        val usernameValidation = when {
            username.isBlank() -> UiText.StringResource(R.string.register_error_username_required)
            !usernameRegex.matches(username.trim()) ->
                UiText.StringResource(R.string.register_error_username_invalid)
            else -> null
        }
        val emailValidation = when {
            email.isBlank() -> UiText.StringResource(R.string.register_error_email_required)
            !EmailValidator.isValid(email) -> UiText.StringResource(R.string.register_error_email_invalid)
            else -> null
        }
        val passwordValidation = when {
            password.isBlank() -> UiText.StringResource(R.string.register_error_password_required)
            !password.requirements().isSatisfied -> UiText.StringResource(R.string.register_error_password_weak)
            else -> null
        }
        val confirmationValidation = when {
            confirmPassword.isBlank() -> UiText.StringResource(R.string.register_error_confirm_required)
            confirmPassword != password -> UiText.StringResource(R.string.register_error_password_mismatch)
            else -> null
        }
        return copy(
            usernameError = usernameValidation,
            emailError = emailValidation,
            passwordError = passwordValidation,
            confirmPasswordError = confirmationValidation,
        )
    }

    private fun RegisterUiState.hasValidationError(): Boolean =
        usernameError != null || emailError != null || passwordError != null || confirmPasswordError != null

    private fun String.requirements() = PasswordRequirements(
        hasMinimumLength = length >= 8,
        hasUppercase = any(Char::isUpperCase),
        hasLowercase = any(Char::isLowerCase),
        hasDigit = any(Char::isDigit),
        hasSpecialCharacter = any { !it.isLetterOrDigit() },
    )

    private val PasswordRequirements.isSatisfied: Boolean
        get() = hasMinimumLength && hasUppercase && hasLowercase && hasDigit && hasSpecialCharacter

    private fun mismatchError(password: String, confirmation: String): UiText? =
        if (confirmation.isNotEmpty() && password != confirmation) {
            UiText.StringResource(R.string.register_error_password_mismatch)
        } else {
            null
        }
}
