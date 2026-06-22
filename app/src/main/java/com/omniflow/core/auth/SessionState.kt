package com.omniflow.core.auth

sealed interface SessionState {
    data object Unknown : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val accessToken: String) : SessionState
}
