package com.omniflow.ui.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.auth.SessionState
import com.omniflow.core.auth.TokenStore
import com.omniflow.core.preferences.OnboardingStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SplashViewModel @Inject constructor(
    tokenStore: TokenStore,
    onboardingStore: OnboardingStore,
    @Named("alwaysShowOnboarding") private val alwaysShowOnboarding: Boolean,
) : ViewModel() {
    val uiState: StateFlow<SplashUiState> = combine(
        tokenStore.sessionState,
        onboardingStore.onboardingSeen,
        minimumDisplayElapsed(),
    ) { sessionState, onboardingSeen, _ ->
        resolveSplashState(sessionState, onboardingSeen, alwaysShowOnboarding)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SplashUiState.Loading,
    )
}

private fun minimumDisplayElapsed() = flow {
    delay(MINIMUM_SPLASH_DURATION_MILLIS)
    emit(Unit)
}

private fun resolveSplashState(
    sessionState: SessionState,
    onboardingSeen: Boolean,
    alwaysShowOnboarding: Boolean,
): SplashUiState = when {
    alwaysShowOnboarding -> SplashUiState.Ready(SplashDestination.Onboarding)
    sessionState == SessionState.Unknown -> SplashUiState.Loading
    sessionState == SessionState.SignedIn -> SplashUiState.Ready(SplashDestination.Home)
    else -> SplashUiState.Ready(
        if (onboardingSeen) SplashDestination.Login else SplashDestination.Onboarding,
    )
}

private const val MINIMUM_SPLASH_DURATION_MILLIS = 1_000L
