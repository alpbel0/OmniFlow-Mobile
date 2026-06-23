package com.omniflow.core.network

import com.omniflow.core.common.UiText
import com.omniflow.data.models.common.ValidationErrorDetail

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(
        val code: Int? = null,
        val message: UiText,
        val validationErrors: List<ValidationErrorDetail> = emptyList(),
    ) : ApiResult<Nothing>

    data object Loading : ApiResult<Nothing>
}
