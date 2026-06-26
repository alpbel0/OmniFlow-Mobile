package com.omniflow.core.network

import com.omniflow.core.common.UiText
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorParserTest {
    private val parser = ErrorParser(Json { ignoreUnknownKeys = true })

    @Test
    fun `parse preserves backend validation details`() {
        val body = """
            {
              "message": "One or more validation failures have occurred.",
              "errors": [
                {
                  "field": "Email",
                  "message": "Email is invalid.",
                  "code": "VALIDATION_ERROR",
                  "attemptedValue": "invalid"
                }
              ]
            }
        """.trimIndent()

        val parsed = parser.parse(body)

        assertEquals(
            "One or more validation failures have occurred.",
            (parsed.message as UiText.DynamicString).value,
        )
        assertEquals("Email", parsed.validationErrors.single().field)
        assertEquals("Email is invalid.", parsed.validationErrors.single().message)
        assertEquals("VALIDATION_ERROR", parsed.validationErrors.single().code)
        assertEquals("invalid", parsed.validationErrors.single().attemptedValue)
    }

    @Test
    fun `parse preserves api exception message when backend sends null errors`() {
        val body = """
            {
              "message": "Username is already taken.",
              "errors": null
            }
        """.trimIndent()

        val parsed = parser.parse(body)

        assertEquals("Username is already taken.", (parsed.message as UiText.DynamicString).value)
        assertEquals(emptyList<Any>(), parsed.validationErrors)
    }
}
