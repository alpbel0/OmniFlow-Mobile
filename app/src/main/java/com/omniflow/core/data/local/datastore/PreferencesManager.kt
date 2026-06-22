package com.omniflow.core.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.omniflow.core.auth.TokenCipher
import com.omniflow.core.auth.TokenPair
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "omniflow_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenCipher: TokenCipher,
) {
    private val onboardingSeenKey = booleanPreferencesKey("onboarding_seen")
    private val accessTokenKey = stringPreferencesKey("access_token_encrypted")
    private val refreshTokenKey = stringPreferencesKey("refresh_token_encrypted")

    val onboardingSeen: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[onboardingSeenKey] ?: false
    }

    val tokenPair: Flow<TokenPair?> = context.dataStore.data.map { preferences ->
        val encryptedAccessToken = preferences[accessTokenKey] ?: return@map null
        val encryptedRefreshToken = preferences[refreshTokenKey] ?: return@map null
        runCatching {
            TokenPair(
                accessToken = tokenCipher.decrypt(encryptedAccessToken),
                refreshToken = tokenCipher.decrypt(encryptedRefreshToken),
            )
        }.getOrNull()
    }

    suspend fun setOnboardingSeen(seen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[onboardingSeenKey] = seen
        }
    }

    suspend fun saveTokens(tokenPair: TokenPair) {
        val encryptedAccessToken = tokenCipher.encrypt(tokenPair.accessToken)
        val encryptedRefreshToken = tokenCipher.encrypt(tokenPair.refreshToken)
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = encryptedAccessToken
            preferences[refreshTokenKey] = encryptedRefreshToken
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
    }
}
