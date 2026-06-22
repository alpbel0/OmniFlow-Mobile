package com.omniflow.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {
    private val accessTokenState = MutableStateFlow<String?>(null)
    private val refreshTokenState = MutableStateFlow<String?>(null)
    private val sessionStateFlow = MutableStateFlow<SessionState>(SessionState.SignedOut)

    val sessionState: StateFlow<SessionState> = sessionStateFlow.asStateFlow()

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        accessTokenState.value = accessToken
        refreshTokenState.value = refreshToken
        sessionStateFlow.value = SessionState.SignedIn(accessToken)
    }

    suspend fun clearSession() {
        accessTokenState.value = null
        refreshTokenState.value = null
        sessionStateFlow.value = SessionState.SignedOut
    }

    suspend fun getAccessToken(): String? = accessTokenState.value

    suspend fun getRefreshToken(): String? = refreshTokenState.value
}
