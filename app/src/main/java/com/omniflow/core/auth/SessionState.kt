package com.omniflow.core.auth

sealed interface SessionState {
    data object Unknown : SessionState
    data object SignedOut : SessionState
    data object SignedIn : SessionState
}
