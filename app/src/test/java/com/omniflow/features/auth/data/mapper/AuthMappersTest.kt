package com.omniflow.features.auth.data.mapper

import com.omniflow.features.auth.data.remote.dto.AuthResponseDto
import com.omniflow.features.auth.domain.model.Tokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class AuthMappersTest {
    @Test
    fun `auth response maps user and tokens`() {
        val response = AuthResponseDto(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            id = "user-id",
            username = "yigit",
            email = "yigit@example.com",
            role = "User",
        )

        assertEquals(
            listOf("user-id", "yigit", "yigit@example.com", "User"),
            response.toAuthUser().let { listOf(it.id, it.username, it.email, it.role) },
        )
        assertEquals(Tokens("access-token", "refresh-token"), response.toTokens())
    }

    @Test
    fun `missing mobile refresh token is rejected`() {
        val response = AuthResponseDto(
            accessToken = "access-token",
            refreshToken = null,
            id = "user-id",
            username = "yigit",
            email = "yigit@example.com",
            role = "User",
        )

        assertThrows(IllegalArgumentException::class.java) { response.toTokens() }
    }

    @Test
    fun `tokens map to refresh request`() {
        val request = Tokens("access-token", "refresh-token").toRefreshTokenRequestDto()

        assertEquals("refresh-token", request.refreshToken)
    }
}
