package com.omniflow.ui.notifications

import androidx.compose.ui.graphics.Color
import com.omniflow.core.common.UiState

data class NotificationsUiState(
    val contentState: UiState<List<NotificationUiItem>> = UiState.Loading,
    val activeFilter: NotifFilter = NotifFilter.ALL,
    val isSelectMode: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val isRefreshing: Boolean = false,
)

enum class NotifFilter { ALL, SOCIAL, TRIP, REMINDER }

enum class NotifTypeUi { FOLLOW, COMMENT, UPVOTE, FORK, REMINDER, LIKE }

data class NotificationUiItem(
    val id: String,
    val type: NotifTypeUi,
    val text: String,
    val timeAgo: String,
    val isRead: Boolean,
    val avatarInitial: String?,
    val avatarColors: List<Color>?,
    val thumbColors: List<Color>?,
)
