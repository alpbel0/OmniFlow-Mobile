package com.omniflow.features.auth.domain.usecase

import com.omniflow.features.auth.domain.model.AuthUser
import com.omniflow.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(name: String, email: String): AuthUser {
        return authRepository.register(name = name, email = email)
    }
}
