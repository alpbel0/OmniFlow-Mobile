package com.omniflow.features.auth.domain.usecase

import com.omniflow.core.network.ApiResult
import com.omniflow.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        token: String,
        newPassword: String,
    ): ApiResult<Unit> = authRepository.resetPassword(email, token, newPassword)
}
