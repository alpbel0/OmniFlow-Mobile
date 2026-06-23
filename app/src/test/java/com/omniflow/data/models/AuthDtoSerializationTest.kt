package com.omniflow.data.models

import com.omniflow.data.models.auth.LoginRequestDto
import com.omniflow.data.models.auth.RegisterRequestDto
import com.omniflow.data.models.auth.AuthResponseDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthDtoSerializationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `register and login requests use backend field names`() {
        val registerJson = json.encodeToString(
            RegisterRequestDto("yigit", "yigit@example.com", "secret", "secret"),
        )
        val loginJson = json.encodeToString(LoginRequestDto("yigit@example.com", "secret"))

        assertTrue(registerJson.contains("\"confirmPassword\":\"secret\""))
        assertTrue(registerJson.contains("\"username\":\"yigit\""))
        assertEquals(
            "{\"email\":\"yigit@example.com\",\"password\":\"secret\"}",
            loginJson,
        )
    }

    @Test
    fun `authentication response decodes complete mobile payload`() {
        val response = json.decodeFromString<AuthResponseDto>(
            """{"accessToken":"access","refreshToken":"refresh","id":"42","username":"yigit","email":"yigit@example.com","role":"User","ignored":true}""",
        )

        assertEquals("access", response.accessToken)
        assertEquals("refresh", response.refreshToken)
        assertEquals("42", response.id)
        assertEquals("yigit", response.username)
        assertEquals("User", response.role)
    }
}
