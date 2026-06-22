package com.omniflow.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refreshToken") val refreshToken: String,
)

@Serializable
data class AuthenticationResponseDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String? = null,
)
