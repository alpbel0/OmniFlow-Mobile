package com.omniflow.features.auth.data.mapper

import com.omniflow.core.data.remote.dto.RefreshTokenRequestDto
import com.omniflow.features.auth.data.remote.dto.AuthResponseDto
import com.omniflow.features.auth.domain.model.AuthUser
import com.omniflow.features.auth.domain.model.Tokens

fun AuthResponseDto.toAuthUser(): AuthUser = AuthUser(
    id = id,
    username = username,
    email = email,
    role = role,
)

fun AuthResponseDto.toTokens(): Tokens {
    require(accessToken.isNotBlank()) { "Access token cannot be blank" }
    val mobileRefreshToken = requireNotNull(refreshToken?.takeIf(String::isNotBlank)) {
        "Refresh token is required for mobile authentication"
    }
    return Tokens(accessToken = accessToken, refreshToken = mobileRefreshToken)
}

fun Tokens.toRefreshTokenRequestDto(): RefreshTokenRequestDto =
    RefreshTokenRequestDto(refreshToken = refreshToken)
