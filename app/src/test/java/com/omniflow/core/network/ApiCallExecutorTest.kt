package com.omniflow.core.network

import com.omniflow.core.common.UiText
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ApiCallExecutorTest {
    private val executor = ApiCallExecutor(ErrorParser(Json { ignoreUnknownKeys = true }))

    @Test
    fun `http error preserves status and validation details`() = runTest {
        val body = """{"message":"Invalid request","errors":[{"field":"email","message":"Invalid email","code":"EMAIL_INVALID"}]}"""
        val exception = HttpException(
            Response.error<Unit>(422, body.toResponseBody("application/json".toMediaType())),
        )

        val result = executor.execute<Unit> { throw exception }

        val error = result as ApiResult.Error
        assertEquals(422, error.code)
        assertEquals(UiText.DynamicString("Invalid request"), error.message)
        assertEquals("email", error.validationErrors.single().field)
    }

    @Test
    fun `network error returns safe fallback`() = runTest {
        val result = executor.execute<Unit> { throw IOException("socket closed") }

        val error = result as ApiResult.Error
        assertEquals(null, error.code)
        assertEquals(emptyList<Any>(), error.validationErrors)
    }

    @Test
    fun `cancellation is never converted to api error`() {
        assertThrows(CancellationException::class.java) {
            runTest {
                executor.execute<Unit> { throw CancellationException("cancelled") }
            }
        }
    }
}
