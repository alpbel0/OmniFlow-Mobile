package com.omniflow.core.network

import com.omniflow.core.common.UiText
import com.omniflow.data.models.common.ErrorResponse
import com.omniflow.data.models.common.ValidationErrorDetail
import kotlinx.serialization.json.Json

class ErrorParser(
    private val json: Json,
) {
    fun parse(rawBody: String?): ParsedApiError {
        if (rawBody.isNullOrBlank()) {
            return ParsedApiError(UiText.DynamicString(DEFAULT_ERROR_MESSAGE))
        }

        return runCatching {
            json.decodeFromString<ErrorResponse>(rawBody)
        }.fold(
            onSuccess = { errorResponse ->
                ParsedApiError(
                    message = UiText.DynamicString(errorResponse.message),
                    validationErrors = errorResponse.errors.orEmpty(),
                )
            },
            onFailure = {
                ParsedApiError(UiText.DynamicString(DEFAULT_ERROR_MESSAGE))
            },
        )
    }

    private companion object {
        const val DEFAULT_ERROR_MESSAGE = "Beklenmeyen bir hata oluştu. Lütfen tekrar dene."
    }
}

data class ParsedApiError(
    val message: UiText,
    val validationErrors: List<ValidationErrorDetail> = emptyList(),
)
