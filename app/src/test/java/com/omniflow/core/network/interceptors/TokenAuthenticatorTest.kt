package com.omniflow.core.network.interceptors

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.omniflow.core.auth.TokenStore
import com.omniflow.core.network.RefreshTokenApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class TokenAuthenticatorTest {
    private lateinit var server: MockWebServer
    private lateinit var tokenStore: TokenStore
    private lateinit var authenticator: TokenAuthenticator

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        tokenStore = mockk(relaxed = true)
        coEvery { tokenStore.getAccessToken() } returns EXPIRED_ACCESS_TOKEN
        coEvery { tokenStore.getRefreshToken() } returns REFRESH_TOKEN

        val json = Json { ignoreUnknownKeys = true }
        val refreshApi = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RefreshTokenApi::class.java)
        authenticator = TokenAuthenticator(tokenStore, refreshApi)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `successful refresh retries request and rotates tokens`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """{"accessToken":"new-access","refreshToken":"new-refresh"}""",
                ),
        )

        val retriedRequest = authenticator.authenticate(null, unauthorizedResponse())

        assertEquals("Bearer new-access", retriedRequest?.header("Authorization"))
        val refreshRequest = server.takeRequest(1, TimeUnit.SECONDS)
        assertEquals("/api/account/refresh-token", refreshRequest?.path)
        assertEquals("mobile", refreshRequest?.getHeader("X-Platform"))
        coVerify(exactly = 1) { tokenStore.saveTokens("new-access", "new-refresh") }
        coVerify(exactly = 0) { tokenStore.clearSession() }
    }

    @Test
    fun `failed refresh clears session and stops retry`() {
        server.enqueue(MockResponse().setResponseCode(401))

        val retriedRequest = authenticator.authenticate(null, unauthorizedResponse())

        assertNull(retriedRequest)
        coVerify(exactly = 1) { tokenStore.clearSession() }
        coVerify(exactly = 0) { tokenStore.saveTokens(any(), any()) }
    }

    private fun unauthorizedResponse(): Response {
        val request = Request.Builder()
            .url(server.url("/api/protected"))
            .header("Authorization", "Bearer $EXPIRED_ACCESS_TOKEN")
            .build()
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()
    }

    private companion object {
        const val EXPIRED_ACCESS_TOKEN = "expired-access"
        const val REFRESH_TOKEN = "refresh-token"
    }
}
