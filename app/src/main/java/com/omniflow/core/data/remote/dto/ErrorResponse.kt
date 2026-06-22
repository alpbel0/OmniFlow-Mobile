package com.omniflow.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("message") val message: String,
    @SerialName("errors") val errors: List<ValidationErrorDetail> = emptyList(),
)

@Serializable
data class ValidationErrorDetail(
    @SerialName("field") val field: String,
    @SerialName("message") val message: String,
    @SerialName("code") val code: String,
    @SerialName("attemptedValue") val attemptedValue: String? = null,
)
