package com.omniflow.core.auth

import com.omniflow.core.data.local.datastore.PreferencesManager
import com.omniflow.core.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val preferencesManager: PreferencesManager,
    @ApplicationScope applicationScope: CoroutineScope,
) : TokenStore {
    override val sessionState: StateFlow<SessionState> = preferencesManager.tokenPair
        .map { tokenPair ->
            if (tokenPair == null) SessionState.SignedOut else SessionState.SignedIn
        }
        .stateIn(applicationScope, SharingStarted.Eagerly, SessionState.Unknown)

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        preferencesManager.saveTokens(TokenPair(accessToken, refreshToken))
    }

    override suspend fun clearSession() {
        preferencesManager.clearTokens()
    }

    override suspend fun getAccessToken(): String? = preferencesManager.tokenPair.first()?.accessToken

    override suspend fun getRefreshToken(): String? = preferencesManager.tokenPair.first()?.refreshToken
}
