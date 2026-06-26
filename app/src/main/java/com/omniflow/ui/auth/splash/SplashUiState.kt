package com.omniflow.ui.auth.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data class Ready(val destination: SplashDestination) : SplashUiState
}

enum class SplashDestination {
    Home,
    Onboarding,
    Login,
}
