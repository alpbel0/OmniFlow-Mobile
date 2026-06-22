package com.omniflow.core.network.interceptors

import com.omniflow.core.auth.TokenStore
import com.omniflow.core.data.remote.dto.RefreshTokenRequestDto
import com.omniflow.core.network.RefreshTokenApi
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val refreshTokenApi: RefreshTokenApi,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.retryCount() >= MAX_RETRY_COUNT) return null

        return synchronized(this) {
            runBlocking {
                val failedToken = response.request.bearerToken()
                val currentToken = tokenStore.getAccessToken()
                if (!currentToken.isNullOrBlank() && currentToken != failedToken) {
                    return@runBlocking response.request.withBearerToken(currentToken)
                }

                refreshSession(response.request)
            }
        }
    }

    private suspend fun refreshSession(failedRequest: Request): Request? {
        val refreshToken = tokenStore.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            tokenStore.clearSession()
            return null
        }

        val refreshResponse = runCatching {
            refreshTokenApi.refreshToken(RefreshTokenRequestDto(refreshToken)).execute()
        }.getOrNull()
        val responseBody = refreshResponse?.takeIf { it.isSuccessful }?.body()
        val newRefreshToken = responseBody?.refreshToken

        if (
            responseBody == null ||
            responseBody.accessToken.isBlank() ||
            newRefreshToken.isNullOrBlank()
        ) {
            tokenStore.clearSession()
            return null
        }

        tokenStore.saveTokens(responseBody.accessToken, newRefreshToken)
        return failedRequest.withBearerToken(responseBody.accessToken)
    }

    private fun Request.bearerToken(): String? = header("Authorization")
        ?.removePrefix(BEARER_PREFIX)
        ?.takeIf { it.isNotBlank() }

    private fun Request.withBearerToken(token: String): Request = newBuilder()
        .header("Authorization", "$BEARER_PREFIX$token")
        .build()

    private fun Response.retryCount(): Int {
        var count = 1
        var prior = priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val BEARER_PREFIX = "Bearer "
        const val MAX_RETRY_COUNT = 2
    }
}
