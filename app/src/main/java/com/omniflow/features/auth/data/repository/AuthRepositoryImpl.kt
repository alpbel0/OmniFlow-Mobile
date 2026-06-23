package com.omniflow.features.auth.data.repository

import com.omniflow.core.auth.TokenStore
import com.omniflow.core.network.ApiCallExecutor
import com.omniflow.core.network.ApiResult
import com.omniflow.features.auth.data.mapper.toAuthUser
import com.omniflow.features.auth.data.mapper.toTokens
import com.omniflow.features.auth.data.remote.AuthApi
import com.omniflow.features.auth.data.remote.dto.ForgotPasswordRequestDto
import com.omniflow.features.auth.data.remote.dto.LoginRequestDto
import com.omniflow.features.auth.data.remote.dto.RegisterRequestDto
import com.omniflow.features.auth.data.remote.dto.ResendVerificationRequestDto
import com.omniflow.features.auth.data.remote.dto.ResetPasswordRequestDto
import com.omniflow.features.auth.data.remote.dto.VerifyEmailRequestDto
import com.omniflow.features.auth.domain.model.AuthUser
import com.omniflow.features.auth.domain.model.RegistrationResult
import com.omniflow.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
    private val apiCallExecutor: ApiCallExecutor,
) : AuthRepository {
    override suspend fun login(email: String, password: String): ApiResult<AuthUser> {
        return apiCallExecutor.execute {
            val response = authApi.login(LoginRequestDto(email, password))
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
            val response = authApi.register(
                RegisterRequestDto(username, email, password, confirmPassword),
            )
            RegistrationResult(response.requiresEmailVerification)
        }
    }

    override suspend fun verifyEmail(email: String, token: String): ApiResult<Unit> {
        return apiCallExecutor.execute {
            authApi.verifyEmail(VerifyEmailRequestDto(email, token))
            Unit
        }
    }

    override suspend fun resendVerification(email: String): ApiResult<Unit> {
        return apiCallExecutor.execute {
            authApi.resendVerification(ResendVerificationRequestDto(email))
            Unit
        }
    }

    override suspend fun forgotPassword(email: String): ApiResult<Unit> {
        return apiCallExecutor.execute {
            authApi.forgotPassword(ForgotPasswordRequestDto(email))
            Unit
        }
    }

    override suspend fun resetPassword(
        email: String,
        token: String,
        newPassword: String,
    ): ApiResult<Unit> {
        return apiCallExecutor.execute {
            authApi.resetPassword(ResetPasswordRequestDto(email, token, newPassword))
            Unit
        }
    }
}
