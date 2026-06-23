package com.omniflow.features.auth.domain.repository

import com.omniflow.core.network.ApiResult
import com.omniflow.features.auth.domain.model.AuthUser
import com.omniflow.features.auth.domain.model.RegistrationResult

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiResult<AuthUser>

    suspend fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): ApiResult<RegistrationResult>

    suspend fun verifyEmail(email: String, token: String): ApiResult<Unit>
    suspend fun resendVerification(email: String): ApiResult<Unit>
    suspend fun forgotPassword(email: String): ApiResult<Unit>
    suspend fun resetPassword(email: String, token: String, newPassword: String): ApiResult<Unit>
}
