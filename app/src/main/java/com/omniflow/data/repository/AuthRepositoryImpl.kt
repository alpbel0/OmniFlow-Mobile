package com.omniflow.data.repository

import com.omniflow.core.auth.TokenStore
import com.omniflow.core.network.ApiCallExecutor
import com.omniflow.core.network.ApiResult
import com.omniflow.data.mapper.toAuthUser
import com.omniflow.data.mapper.toTokens
import com.omniflow.data.models.auth.AuthUser
import com.omniflow.data.models.auth.ChangeVerificationEmailRequestDto
import com.omniflow.data.models.auth.RegistrationResult
import com.omniflow.data.models.auth.ForgotPasswordRequestDto
import com.omniflow.data.models.auth.LoginRequestDto
import com.omniflow.data.models.auth.RegisterRequestDto
import com.omniflow.data.models.auth.ResendVerificationRequestDto
import com.omniflow.data.models.auth.ResetPasswordRequestDto
import com.omniflow.data.models.auth.VerifyEmailRequestDto
import com.omniflow.data.remote.AuthService
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenStore: TokenStore,
    private val apiCallExecutor: ApiCallExecutor,
) : AuthRepository {
    override suspend fun login(email: String, password: String): ApiResult<AuthUser> {
        return apiCallExecutor.execute {
            val response = authService.login(LoginRequestDto(email, password))
            val tokens = response.toTokens()
            tokenStore.saveTokens(tokens.accessToken, tokens.refreshToken)
            response.toAuthUser()
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): ApiResult<RegistrationResult> {
        return apiCallExecutor.execute {
            val response = authService.register(
                RegisterRequestDto(username, email, password, confirmPassword),
            )
            RegistrationResult(response.requiresEmailVerification)
        }
    }

    override suspend fun verifyEmail(email: String, token: String): ApiResult<Unit> {
        return apiCallExecutor.execute {
            authService.verifyEmail(VerifyEmailRequestDto(email, token))
            Unit
        }
    }

    override suspend fun resendVerification(email: String): ApiResult<Unit> {
        return apiCallExecutor.execute {
            authService.resendVerification(ResendVerificationRequestDto(email))
            Unit
        }
    }

    override suspend fun changeVerificationEmail(
        oldEmail: String,
        newEmail: String,
        password: String,
    ): ApiResult<Unit> {
        return apiCallExecutor.execute {
            authService.changeVerificationEmail(
                ChangeVerificationEmailRequestDto(oldEmail, newEmail, password),
            )
            Unit
        }
    }

    override suspend fun forgotPassword(email: String): ApiResult<Unit> {
        return apiCallExecutor.execute {
            authService.forgotPassword(ForgotPasswordRequestDto(email))
            Unit
        }
    }

    override suspend fun resetPassword(
        email: String,
        token: String,
        newPassword: String,
    ): ApiResult<Unit> {
        return apiCallExecutor.execute {
            authService.resetPassword(ResetPasswordRequestDto(email, token, newPassword))
            Unit
        }
    }
}
