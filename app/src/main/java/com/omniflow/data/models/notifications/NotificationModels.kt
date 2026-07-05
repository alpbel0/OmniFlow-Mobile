package com.omniflow.data.models.notifications

data class NotificationDataModel(
    val id: String,
    val type: NotificationTypeData,
    val isRead: Boolean,
    val createdAt: String,
    val actorUsername: String?,
    val actorProfilePhotoUrl: String?,
)

enum class NotificationTypeData {
    FOLLOW,
    COMMENT,
    UPVOTE,
    FORK,
    REMINDER,
    LIKE,
}

data class NotificationsPageModel(
    val items: List<NotificationDataModel>,
    val totalCount: Int,
    val pageNumber: Int,
    val pageSize: Int,
)
