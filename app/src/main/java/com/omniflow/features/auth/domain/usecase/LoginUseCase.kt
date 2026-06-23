package com.omniflow.features.auth.domain.usecase

import com.omniflow.core.network.ApiResult
import com.omniflow.features.auth.domain.model.AuthUser
import com.omniflow.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): ApiResult<AuthUser> {
        return authRepository.login(email = email, password = password)
    }
}
