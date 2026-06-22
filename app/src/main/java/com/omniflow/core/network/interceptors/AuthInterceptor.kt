package com.omniflow.core.network.interceptors

import com.omniflow.core.auth.TokenStore
import com.omniflow.core.common.Constants
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStore: TokenStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { tokenStore.getAccessToken() }
        val request = chain.request().newBuilder().apply {
            header(Constants.PlatformHeader, Constants.MobilePlatform)
            if (!accessToken.isNullOrBlank()) {
                header("Authorization", "Bearer $accessToken")
            }
        }.build()

        return chain.proceed(request)
    }
}
