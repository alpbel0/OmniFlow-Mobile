package com.omniflow.features.auth.data.repository

import com.omniflow.features.auth.domain.model.AuthUser
import com.omniflow.features.auth.domain.model.Tokens
import com.omniflow.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    override suspend fun login(email: String, password: String): Tokens {
        return Tokens(accessToken = "$email-token", refreshToken = "$password-refresh")
    }

    override suspend fun register(name: String, email: String): AuthUser {
        return AuthUser(id = "pending", username = name, email = email, role = "User")
    }
}
