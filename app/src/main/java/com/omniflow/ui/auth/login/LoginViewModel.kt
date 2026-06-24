package com.omniflow.ui.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.R
import com.omniflow.core.auth.PendingAuthCredentialsStore
import com.omniflow.core.common.UiText
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialsStore: PendingAuthCredentialsStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<LoginEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    init {
        savedStateHandle.getStateFlow(LOGIN_EMAIL_KEY, "")
            .onEach { email ->
                if (email.isConcreteEmailArgument() && _uiState.value.email.isBlank()) {
                    _uiState.update { it.copy(email = email) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun onPasswordVisibilityToggle() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onForgotPasswordClicked() {
        viewModelScope.launch {
            _effects.send(LoginEffect.NavigateToForgotPassword)
        }
    }

    fun onRegisterClicked() {
        viewModelScope.launch {
            _effects.send(LoginEffect.NavigateToRegister)
        }
    }

    fun onVerifyEmailClicked() {
        val state = _uiState.value
        if (state.email.isNotBlank() && state.password.isNotBlank()) {
            credentialsStore.save(state.email, state.password)
        }
        viewModelScope.launch {
            _effects.send(LoginEffect.NavigateToVerifyEmail(state.email))
        }
    }

    fun onLoginClicked() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        val emailError = when {
            currentState.email.isBlank() -> UiText.StringResource(R.string.login_error_email_required)
            !emailRegex.matches(currentState.email) -> UiText.StringResource(R.string.login_error_email_invalid)
            else -> null
        }

        val passwordError = if (currentState.password.isBlank()) {
            UiText.StringResource(R.string.login_error_password_required)
        } else null

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        _uiState.update { it.copy(isLoading = true, generalError = null, isEmailUnverified = false) }

        viewModelScope.launch {
            when (val result = authRepository.login(currentState.email, currentState.password)) {
                is ApiResult.Success -> {
                    credentialsStore.clear()
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.send(LoginEffect.NavigateToHome)
                }
                is ApiResult.Error -> {
                    val generalError = when (result.code) {
                        401 -> UiText.StringResource(R.string.login_error_invalid_credentials)
                        403 -> UiText.StringResource(R.string.login_error_email_unverified)
                        else -> UiText.StringResource(R.string.login_error_generic)
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalError = generalError,
                            isEmailUnverified = result.code == 403
                        )
                    }
                }
                is ApiResult.Loading -> {
                    // Handled locally
                }
            }
        }
    }
}

private fun String.isConcreteEmailArgument(): Boolean =
    isNotBlank() && !contains("{") && !contains("}")
