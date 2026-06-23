package com.omniflow.ui.auth.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.preferences.OnboardingStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingStore: OnboardingStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val navigationChannel = Channel<OnboardingDestination>(Channel.BUFFERED)
    val effects = navigationChannel.receiveAsFlow()

    fun onPageChanged(page: Int) {
        _uiState.update { state -> state.copy(currentPage = page.coerceIn(0, LAST_PAGE)) }
    }

    fun onNext() {
        onPageChanged(_uiState.value.currentPage + 1)
    }

    fun onSkip() {
        completeOnboarding(OnboardingDestination.Login)
    }

    fun onGetStarted() {
        completeOnboarding(OnboardingDestination.Register)
    }

    private fun completeOnboarding(destination: OnboardingDestination) {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true, showPersistenceError = false) }

        viewModelScope.launch {
            try {
                onboardingStore.setOnboardingSeen(true)
                navigationChannel.send(destination)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.update { it.copy(showPersistenceError = true) }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private companion object {
        const val LAST_PAGE = 2
    }
}
