package com.omniflow.features.auth.domain.usecase

import com.omniflow.core.network.ApiResult
import com.omniflow.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, token: String): ApiResult<Unit> =
        authRepository.verifyEmail(email, token)
}
