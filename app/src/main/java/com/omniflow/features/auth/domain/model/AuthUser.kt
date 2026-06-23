package com.omniflow.features.auth.domain.model

data class AuthUser(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
)
