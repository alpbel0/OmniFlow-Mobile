package com.omniflow.data.repository

import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.auth.AuthUser
import com.omniflow.data.models.auth.RegistrationResult

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
    suspend fun changeVerificationEmail(
        oldEmail: String,
        newEmail: String,
        password: String,
    ): ApiResult<Unit>
    suspend fun forgotPassword(email: String): ApiResult<Unit>
    suspend fun resetPassword(email: String, token: String, newPassword: String): ApiResult<Unit>
}
