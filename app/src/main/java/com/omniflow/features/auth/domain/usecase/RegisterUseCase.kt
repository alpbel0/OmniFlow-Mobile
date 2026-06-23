package com.omniflow.features.auth.domain.usecase

import com.omniflow.core.network.ApiResult
import com.omniflow.features.auth.domain.model.RegistrationResult
import com.omniflow.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): ApiResult<RegistrationResult> {
        return authRepository.register(username, email, password, confirmPassword)
    }
}
