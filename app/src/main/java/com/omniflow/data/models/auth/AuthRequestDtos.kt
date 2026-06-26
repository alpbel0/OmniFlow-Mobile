package com.omniflow.data.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
)

@Serializable
data class VerifyEmailRequestDto(
    val email: String,
    val token: String,
)

@Serializable
data class ResendVerificationRequestDto(
    val email: String,
)

@Serializable
data class ChangeVerificationEmailRequestDto(
    val oldEmail: String,
    val newEmail: String,
    val password: String,
)

@Serializable
data class ForgotPasswordRequestDto(
    val email: String,
)

@Serializable
data class ResetPasswordRequestDto(
    val email: String,
    val token: String,
    val newPassword: String,
)
