package com.omniflow.data.mapper

import com.omniflow.data.models.auth.RefreshTokenRequestDto
import com.omniflow.data.models.auth.AuthUser
import com.omniflow.data.models.auth.Tokens
import com.omniflow.data.models.auth.AuthResponseDto

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
