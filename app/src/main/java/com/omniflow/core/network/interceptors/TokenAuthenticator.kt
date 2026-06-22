package com.omniflow.core.network.interceptors

import com.omniflow.core.auth.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        runBlocking {
            tokenManager.clearSession()
        }
        return null
    }
}
