package com.omniflow.features.auth.domain.usecase

import com.omniflow.core.network.ApiResult
import com.omniflow.features.auth.domain.model.AuthUser
import com.omniflow.features.auth.domain.model.RegistrationResult
import com.omniflow.features.auth.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthUseCasesTest {
    private val repository = mockk<AuthRepository>()

    @Test
    fun `login and register use cases preserve parameters and results`() = runTest {
        val userResult = ApiResult.Success(AuthUser("id", "yigit", "user@example.com", "User"))
        val registrationResult = ApiResult.Success(RegistrationResult(true))
        coEvery { repository.login("user@example.com", "secret") } returns userResult
        coEvery {
            repository.register("yigit", "user@example.com", "secret", "secret")
        } returns registrationResult

        assertEquals(userResult, LoginUseCase(repository)("user@example.com", "secret"))
        assertEquals(
            registrationResult,
            RegisterUseCase(repository)("yigit", "user@example.com", "secret", "secret"),
        )
    }

    @Test
    fun `verification use cases preserve parameters`() = runTest {
        coEvery { repository.verifyEmail("user@example.com", "token") } returns ApiResult.Success(Unit)
        coEvery { repository.resendVerification("user@example.com") } returns ApiResult.Success(Unit)

        VerifyEmailUseCase(repository)("user@example.com", "token")
        ResendVerificationUseCase(repository)("user@example.com")

        coVerify { repository.verifyEmail("user@example.com", "token") }
        coVerify { repository.resendVerification("user@example.com") }
    }

    @Test
    fun `password use cases preserve parameters`() = runTest {
        coEvery { repository.forgotPassword("user@example.com") } returns ApiResult.Success(Unit)
        coEvery {
            repository.resetPassword("user@example.com", "token", "new-password")
        } returns ApiResult.Success(Unit)

        ForgotPasswordUseCase(repository)("user@example.com")
        ResetPasswordUseCase(repository)("user@example.com", "token", "new-password")

        coVerify { repository.forgotPassword("user@example.com") }
        coVerify { repository.resetPassword("user@example.com", "token", "new-password") }
    }
}
