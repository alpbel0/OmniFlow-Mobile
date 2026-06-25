package com.omniflow.ui.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.R
import com.omniflow.core.common.EmailValidator
import com.omniflow.core.common.UiText
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<ForgotPasswordEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var cooldownJob: Job? = null

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onSendResetLinkClicked() {
        val email = _uiState.value.email.trim()
        val validationError = validateEmail(email)
        if (validationError != null) {
            _uiState.update { it.copy(emailError = validationError) }
            return
        }

        sendResetLink(email)
    }

    fun onResendClicked() {
        val email = _uiState.value.sentEmail.ifBlank { _uiState.value.email.trim() }
        if (!_uiState.value.canResend || email.isBlank()) return

        sendResetLink(email, isResend = true)
    }

    fun onOpenMailClicked() {
        if (!_uiState.value.isLoading) sendEffect(ForgotPasswordEffect.OpenMailApp)
    }

    fun onBackToLoginClicked() {
        if (!_uiState.value.isLoading) sendEffect(ForgotPasswordEffect.NavigateLogin)
    }

    private fun sendResetLink(email: String, isResend: Boolean = false) {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, emailError = null) }
        viewModelScope.launch {
            when (authRepository.forgotPassword(email)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            email = email,
                            sentEmail = email,
                            isLoading = false,
                            isSuccess = true,
                            cooldownSeconds = COOLDOWN_SECONDS,
                        )
                    }
                    startCooldown()
                    if (isResend) {
                        _effects.send(
                            ForgotPasswordEffect.ShowSnackbar(
                                UiText.StringResource(R.string.forgot_password_resend_success),
                                isError = false,
                            ),
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.send(
                        ForgotPasswordEffect.ShowSnackbar(
                            UiText.StringResource(R.string.forgot_password_send_error),
                            isError = true,
                        ),
                    )
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    private fun validateEmail(email: String): UiText? =
        when {
            email.isBlank() -> UiText.StringResource(R.string.login_error_email_required)
            !EmailValidator.isValid(email) -> UiText.StringResource(R.string.forgot_password_error_email_invalid)
            else -> null
        }

    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            while (_uiState.value.cooldownSeconds > 0) {
                delay(1_000)
                _uiState.update { state ->
                    state.copy(cooldownSeconds = (state.cooldownSeconds - 1).coerceAtLeast(0))
                }
            }
        }
    }

    private fun sendEffect(effect: ForgotPasswordEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private companion object {
        const val COOLDOWN_SECONDS = 60
    }
}
