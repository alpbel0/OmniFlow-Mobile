package com.omniflow.data.models.auth

data class AuthUser(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
)
