package com.omniflow.data.models.common

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponseDto<T>(
    val data: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalCount: Int,
)
