package com.omniflow.data.repository

import com.omniflow.core.auth.TokenStore
import com.omniflow.core.network.ApiCallExecutor
import com.omniflow.core.network.ApiResult
import com.omniflow.core.network.ErrorParser
import com.omniflow.data.models.auth.AuthUser
import com.omniflow.data.models.auth.RegistrationResult
import com.omniflow.data.models.auth.LoginRequestDto
import com.omniflow.data.models.auth.RegisterRequestDto
import com.omniflow.data.models.auth.AuthResponseDto
import com.omniflow.data.models.auth.MessageResponseDto
import com.omniflow.data.models.auth.RegistrationVerificationResponseDto
import com.omniflow.data.remote.AuthService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {
    private lateinit var authService: AuthService
    private lateinit var tokenStore: TokenStore
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        authService = mockk()
        tokenStore = mockk(relaxed = true)
        repository = AuthRepositoryImpl(
            authService = authService,
            tokenStore = tokenStore,
            apiCallExecutor = ApiCallExecutor(ErrorParser(Json { ignoreUnknownKeys = true })),
        )
    }

    @Test
    fun `login maps user and saves token pair once`() = runTest {
        coEvery { authService.login(LoginRequestDto("user@example.com", "secret")) } returns authResponse()

        val result = repository.login("user@example.com", "secret")

        assertEquals(
            ApiResult.Success(AuthUser("user-id", "yigit", "user@example.com", "User")),
            result,
        )
        coVerify(exactly = 1) { tokenStore.saveTokens("access", "refresh") }
    }

    @Test
    fun `token storage failure does not report login success`() = runTest {
        coEvery { authService.login(any()) } returns authResponse()
        coEvery { tokenStore.saveTokens(any(), any()) } throws IllegalStateException("disk failure")

        val result = repository.login("user@example.com", "secret")

        assertTrue(result is ApiResult.Error)
    }

    @Test
    fun `register sends complete backend request and maps result`() = runTest {
        val request = slot<RegisterRequestDto>()
        coEvery { authService.register(capture(request)) } returns
            RegistrationVerificationResponseDto("Check email", true)

        val result = repository.register(
            username = "yigit",
            email = "user@example.com",
            password = "secret",
            confirmPassword = "secret",
        )

        assertEquals(RegisterRequestDto("yigit", "user@example.com", "secret", "secret"), request.captured)
        assertEquals(ApiResult.Success(RegistrationResult(requiresEmailVerification = true)), result)
    }

    @Test
    fun `message operations delegate to every auth endpoint`() = runTest {
        coEvery { authService.verifyEmail(any()) } returns MessageResponseDto("verified")
        coEvery { authService.resendVerification(any()) } returns MessageResponseDto("resent")
        coEvery { authService.forgotPassword(any()) } returns MessageResponseDto("sent")
        coEvery { authService.resetPassword(any()) } returns MessageResponseDto("reset")

        assertEquals(ApiResult.Success(Unit), repository.verifyEmail("user@example.com", "token"))
        assertEquals(ApiResult.Success(Unit), repository.resendVerification("user@example.com"))
        assertEquals(ApiResult.Success(Unit), repository.forgotPassword("user@example.com"))
        assertEquals(
            ApiResult.Success(Unit),
            repository.resetPassword("user@example.com", "token", "new-password"),
        )
        coVerify(exactly = 1) { authService.verifyEmail(match { it.email == "user@example.com" && it.token == "token" }) }
        coVerify(exactly = 1) { authService.resendVerification(match { it.email == "user@example.com" }) }
        coVerify(exactly = 1) { authService.forgotPassword(match { it.email == "user@example.com" }) }
        coVerify(exactly = 1) {
            authService.resetPassword(
                match { it.email == "user@example.com" && it.token == "token" && it.newPassword == "new-password" },
            )
        }
    }

    private fun authResponse() = AuthResponseDto(
        accessToken = "access",
        refreshToken = "refresh",
        id = "user-id",
        username = "yigit",
        email = "user@example.com",
        role = "User",
    )
}
