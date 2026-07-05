package com.omniflow.ui.notifications

import com.omniflow.core.designsystem.theme.NotificationPalette
import com.omniflow.data.models.notifications.NotificationDataModel
import com.omniflow.data.models.notifications.NotificationTypeData
import java.time.Duration
import java.time.OffsetDateTime

fun NotificationDataModel.toUiItem(): NotificationUiItem {
    val actor = actorUsername ?: "Sistem"
    val text = buildText(type, actor)
    val initial = if (type == NotificationTypeData.FOLLOW) actor.firstOrNull()?.uppercase() else null
    val avatarColors = if (type == NotificationTypeData.FOLLOW) {
        NotificationPalette.followAvatarGradients[
            actor.hashCode().mod(NotificationPalette.followAvatarGradients.size)
        ]
    } else null
    val thumbColors = if (type != NotificationTypeData.FOLLOW && type != NotificationTypeData.REMINDER) {
        NotificationPalette.notificationThumbGradients[
            id.hashCode().mod(NotificationPalette.notificationThumbGradients.size)
        ]
    } else null

    return NotificationUiItem(
        id = id,
        type = type.toUiType(),
        text = text,
        timeAgo = createdAt.toRelativeTime(),
        isRead = isRead,
        avatarInitial = initial,
        avatarColors = avatarColors,
        thumbColors = thumbColors,
    )
}

fun NotificationTypeData.toUiType(): NotifTypeUi = when (this) {
    NotificationTypeData.FOLLOW -> NotifTypeUi.FOLLOW
    NotificationTypeData.COMMENT -> NotifTypeUi.COMMENT
    NotificationTypeData.UPVOTE -> NotifTypeUi.UPVOTE
    NotificationTypeData.FORK -> NotifTypeUi.FORK
    NotificationTypeData.REMINDER -> NotifTypeUi.REMINDER
    NotificationTypeData.LIKE -> NotifTypeUi.LIKE
}

fun NotifTypeUi.filterGroup(): NotifFilter = when (this) {
    NotifTypeUi.FOLLOW, NotifTypeUi.COMMENT, NotifTypeUi.UPVOTE, NotifTypeUi.LIKE -> NotifFilter.SOCIAL
    NotifTypeUi.FORK -> NotifFilter.TRIP
    NotifTypeUi.REMINDER -> NotifFilter.REMINDER
}

private fun buildText(type: NotificationTypeData, actor: String): String = when (type) {
    NotificationTypeData.FOLLOW -> "@$actor seni takip etmeye başladı"
    NotificationTypeData.COMMENT -> "@$actor yorumladı: \"Harika bir plan!\""
    NotificationTypeData.UPVOTE -> "@$actor gezini beğendi"
    NotificationTypeData.FORK -> "@$actor gezini kopyaladı"
    NotificationTypeData.REMINDER -> "Gezin yaklaşıyor!"
    NotificationTypeData.LIKE -> "@$actor ve diğerleri gezini beğendi"
}

private fun String.toRelativeTime(now: OffsetDateTime = OffsetDateTime.now()): String {
    val timestamp = runCatching { OffsetDateTime.parse(this) }.getOrNull() ?: return "Az önce"
    val duration = Duration.between(timestamp, now).abs()
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()

    return when {
        minutes < 1 -> "Az önce"
        minutes < 60 -> "$minutes dk önce"
        hours < 24 -> "$hours saat önce"
        days < 7 -> "$days gün önce"
        else -> "${days / 7} hafta önce"
    }
}
