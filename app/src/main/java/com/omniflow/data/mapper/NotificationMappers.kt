package com.omniflow.data.mapper

import com.omniflow.data.models.notifications.NotificationDataModel
import com.omniflow.data.models.notifications.NotificationTypeData
import com.omniflow.data.models.notifications.NotificationResponseDto
import com.omniflow.data.models.notifications.NotificationsPageDto
import com.omniflow.data.models.notifications.NotificationsPageModel

fun NotificationResponseDto.toDataModel(): NotificationDataModel {
    return NotificationDataModel(
        id = id,
        type = mapType(type),
        isRead = isRead,
        createdAt = createdAt,
        actorUsername = actorUsername,
        actorProfilePhotoUrl = actorProfilePhotoUrl,
    )
}

fun NotificationsPageDto.toPageModel(): NotificationsPageModel {
    return NotificationsPageModel(
        items = data.map { it.toDataModel() },
        totalCount = totalCount,
        pageNumber = pageNumber,
        pageSize = pageSize,
    )
}

private fun mapType(backendType: String): NotificationTypeData {
    return when (backendType) {
        "Follow" -> NotificationTypeData.FOLLOW
        "Comment", "Mention" -> NotificationTypeData.COMMENT
        "PostUpvote" -> NotificationTypeData.LIKE
        "TripUpvote", "CommentUpvote", "TipUpvote" -> NotificationTypeData.UPVOTE
        "Fork" -> NotificationTypeData.FORK
        else -> NotificationTypeData.UPVOTE
    }
}
