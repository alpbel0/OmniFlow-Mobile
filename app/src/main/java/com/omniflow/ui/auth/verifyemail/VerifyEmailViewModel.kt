package com.omniflow.ui.auth.verifyemail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.R
import com.omniflow.core.auth.PendingAuthCredentialsStore
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialsStore: PendingAuthCredentialsStore,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialSource = savedStateHandle.get<String>(VERIFY_EMAIL_SOURCE_KEY)
        ?.let { value -> runCatching { VerifyEmailSource.valueOf(value) }.getOrNull() }
        ?: VerifyEmailSource.LOGIN

    private val _uiState = MutableStateFlow(
        VerifyEmailUiState(
            email = savedStateHandle.get<String>(VERIFY_EMAIL_KEY).orEmpty(),
            source = initialSource,
            cooldownSeconds = if (initialSource == VerifyEmailSource.REGISTER) COOLDOWN_SECONDS else 0,
        ),
    )
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<VerifyEmailEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    private var cooldownJob: Job? = null

    init {
        if (_uiState.value.cooldownSeconds > 0) startCooldown()
        observeNavigationState(savedStateHandle)
    }

    fun onVerifiedLoginClicked() {
        val state = _uiState.value
        if (state.isLoading) return

        val credentials = credentialsStore.get()
        if (credentials == null || credentials.email != state.email) {
            sendEffect(VerifyEmailEffect.NavigateLogin(state.email))
            return
        }

        _uiState.update { it.copy(isVerifying = true, verificationError = null) }
        viewModelScope.launch {
            when (val result = authRepository.login(credentials.email, credentials.password)) {
                is ApiResult.Success -> {
                    credentialsStore.clear()
                    _uiState.update { it.copy(isVerifying = false) }
                    _effects.send(VerifyEmailEffect.NavigateHome)
                }
                is ApiResult.Error -> handleLoginError(result)
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onResendClicked() {
        val state = _uiState.value
        if (!state.canResend) return

        _uiState.update { it.copy(isResending = true) }
        viewModelScope.launch {
            when (val result = authRepository.resendVerification(state.email)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isResending = false, cooldownSeconds = COOLDOWN_SECONDS) }
                    startCooldown()
                    _effects.send(
                        VerifyEmailEffect.ShowSnackbar(
                            UiText.StringResource(R.string.verify_email_resend_success),
                            isError = false,
                        ),
                    )
                }
                is ApiResult.Error -> handleResendError(result)
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onOpenMailClicked() {
        if (!_uiState.value.isLoading) sendEffect(VerifyEmailEffect.OpenMailApp)
    }

    fun onChangeEmailClicked() {
        if (_uiState.value.isLoading) return
        _uiState.update {
            it.copy(
                isChangingEmail = true,
                newEmailInput = it.newEmailInput.ifBlank { it.email },
                newEmailError = null,
                verificationError = null,
            )
        }
    }

    fun onNewEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                newEmailInput = email,
                newEmailError = null,
                verificationError = null,
            )
        }
    }

    fun onCancelEmailChangeClicked() {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isChangingEmail = false, newEmailInput = "", newEmailError = null) }
    }

    fun onSubmitEmailChangeClicked() {
        val state = _uiState.value
        if (state.isLoading) return

        val newEmail = state.newEmailInput.trim()
        val validationError = when {
            newEmail.isBlank() -> UiText.StringResource(R.string.login_error_email_required)
            !emailRegex.matches(newEmail) -> UiText.StringResource(R.string.login_error_email_invalid)
            newEmail.equals(state.email, ignoreCase = true) ->
                UiText.StringResource(R.string.verify_email_change_same_email)
            else -> null
        }

        if (validationError != null) {
            _uiState.update { it.copy(newEmailError = validationError) }
            return
        }

        val credentials = credentialsStore.get()
        if (credentials == null || credentials.email != state.email) {
            sendEffect(VerifyEmailEffect.NavigateLogin(state.email))
            return
        }

        _uiState.update { it.copy(isUpdatingEmail = true, newEmailError = null) }
        viewModelScope.launch {
            when (
                val result = authRepository.changeVerificationEmail(
                    oldEmail = state.email,
                    newEmail = newEmail,
                    password = credentials.password,
                )
            ) {
                is ApiResult.Success -> {
                    credentialsStore.save(newEmail, credentials.password)
                    savedStateHandle[VERIFY_EMAIL_KEY] = newEmail
                    _uiState.update {
                        it.copy(
                            email = newEmail,
                            isUpdatingEmail = false,
                            isChangingEmail = false,
                            newEmailInput = "",
                            newEmailError = null,
                            cooldownSeconds = COOLDOWN_SECONDS,
                        )
                    }
                    startCooldown()
                    _effects.send(
                        VerifyEmailEffect.ShowSnackbar(
                            UiText.StringResource(R.string.verify_email_change_success),
                            isError = false,
                        ),
                    )
                }
                is ApiResult.Error -> handleChangeEmailError(result)
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onBackToLoginClicked() {
        if (_uiState.value.isLoading) return
        credentialsStore.clear()
        sendEffect(VerifyEmailEffect.NavigateLogin(_uiState.value.email))
    }

    private fun handleLoginError(error: ApiResult.Error) {
        if (error.code == 403) {
            _uiState.update {
                it.copy(
                    isVerifying = false,
                    verificationError = UiText.StringResource(R.string.verify_email_not_verified),
                )
            }
            return
        }

        credentialsStore.clear()
        _uiState.update { it.copy(isVerifying = false) }
        sendEffect(VerifyEmailEffect.NavigateLogin(_uiState.value.email))
    }

    private fun handleResendError(error: ApiResult.Error) {
        val cooldown = if (error.code == 429) COOLDOWN_SECONDS else _uiState.value.cooldownSeconds
        _uiState.update { it.copy(isResending = false, cooldownSeconds = cooldown) }
        if (cooldown > 0) startCooldown()
        sendEffect(
            VerifyEmailEffect.ShowSnackbar(
                UiText.StringResource(R.string.verify_email_resend_error),
                isError = true,
            ),
        )
    }

    private fun handleChangeEmailError(error: ApiResult.Error) {
        val message = when (error.code) {
            429 -> UiText.StringResource(R.string.verify_email_change_rate_limited)
            else -> error.message
        }

        _uiState.update {
            it.copy(
                isUpdatingEmail = false,
                newEmailError = message,
                cooldownSeconds = if (error.code == 429) COOLDOWN_SECONDS else it.cooldownSeconds,
            )
        }

        if (error.code == 401 || error.code == 404) {
            credentialsStore.clear()
            sendEffect(VerifyEmailEffect.NavigateLogin(_uiState.value.email))
            return
        }

        if (error.code == 429) startCooldown()
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

    private fun observeNavigationState(savedStateHandle: SavedStateHandle) {
        combine(
            savedStateHandle.getStateFlow(VERIFY_EMAIL_KEY, _uiState.value.email),
            savedStateHandle.getStateFlow(VERIFY_EMAIL_SOURCE_KEY, _uiState.value.source.name),
        ) { email, sourceName ->
            email to (runCatching { VerifyEmailSource.valueOf(sourceName) }.getOrNull() ?: VerifyEmailSource.LOGIN)
        }
            .distinctUntilChanged()
            .onEach { (email, source) ->
                _uiState.update { state ->
                    val shouldStartRegisterCooldown = source == VerifyEmailSource.REGISTER &&
                        state.source != VerifyEmailSource.REGISTER &&
                        state.cooldownSeconds == 0

                    state.copy(
                        email = email,
                        source = source,
                        cooldownSeconds = if (shouldStartRegisterCooldown) COOLDOWN_SECONDS else state.cooldownSeconds,
                    )
                }
                if (_uiState.value.cooldownSeconds > 0 && cooldownJob?.isActive != true) {
                    startCooldown()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun sendEffect(effect: VerifyEmailEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private companion object {
        const val COOLDOWN_SECONDS = 60
    }
}
