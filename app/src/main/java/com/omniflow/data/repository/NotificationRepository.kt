package com.omniflow.data.repository

import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.notifications.NotificationsPageModel

interface NotificationRepository {
    suspend fun getNotifications(pageNumber: Int = 1, pageSize: Int = 20): ApiResult<NotificationsPageModel>
    suspend fun getUnreadCount(): ApiResult<Int>
    suspend fun markAsRead(id: String): ApiResult<Unit>
    suspend fun markAllAsRead(): ApiResult<Unit>
}
