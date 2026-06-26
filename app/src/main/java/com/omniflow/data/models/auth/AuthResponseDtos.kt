package com.omniflow.data.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String? = null,
    val id: String,
    val username: String,
    val email: String,
    val role: String,
)

@Serializable
data class RegistrationVerificationResponseDto(
    val message: String,
    val requiresEmailVerification: Boolean,
)

@Serializable
data class MessageResponseDto(
    val message: String,
)
