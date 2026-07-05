package com.omniflow.data.models.notifications

import com.omniflow.data.models.common.PagedResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponseDto(
    val id: String,
    val type: String,
    val targetId: String? = null,
    val targetType: String? = null,
    val isRead: Boolean,
    val readAt: String? = null,
    val createdAt: String,
    val actorUsername: String? = null,
    val actorProfilePhotoUrl: String? = null,
)

typealias NotificationsPageDto = PagedResponseDto<NotificationResponseDto>
