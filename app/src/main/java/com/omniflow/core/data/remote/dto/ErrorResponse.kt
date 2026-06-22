package com.omniflow.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("title") val title: String? = null,
    @SerialName("detail") val detail: String? = null,
)
