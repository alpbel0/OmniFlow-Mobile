package com.omniflow.features.auth.domain.repository

import com.omniflow.features.auth.domain.model.AuthUser
import com.omniflow.features.auth.domain.model.Tokens

interface AuthRepository {
    suspend fun login(email: String, password: String): Tokens
    suspend fun register(name: String, email: String): AuthUser
}
