package com.omniflow.core.auth

import kotlinx.coroutines.flow.StateFlow

interface TokenStore {
    val sessionState: StateFlow<SessionState>

    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearSession()
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
}
