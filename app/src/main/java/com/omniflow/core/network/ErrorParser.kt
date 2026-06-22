package com.omniflow.core.network

import com.omniflow.core.common.UiText
import com.omniflow.core.data.remote.dto.ErrorResponse
import kotlinx.serialization.json.Json

class ErrorParser(
    private val json: Json,
) {
    fun parse(rawBody: String?): UiText {
        if (rawBody.isNullOrBlank()) {
            return UiText.DynamicString("An unexpected error occurred.")
        }

        return runCatching {
            json.decodeFromString<ErrorResponse>(rawBody)
        }.fold(
            onSuccess = { errorResponse ->
                UiText.DynamicString(errorResponse.detail ?: errorResponse.title ?: "Request failed.")
            },
            onFailure = {
                UiText.DynamicString("An unexpected error occurred.")
            },
        )
    }
}
