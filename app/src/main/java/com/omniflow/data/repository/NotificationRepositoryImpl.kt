package com.omniflow.data.repository

import com.omniflow.core.network.ApiCallExecutor
import com.omniflow.core.network.ApiResult
import com.omniflow.data.mapper.toPageModel
import com.omniflow.data.models.notifications.NotificationDataModel
import com.omniflow.data.models.notifications.NotificationTypeData
import com.omniflow.data.models.notifications.NotificationsPageModel
import com.omniflow.data.remote.NotificationService
import java.time.OffsetDateTime
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationService: NotificationService,
    private val apiCallExecutor: ApiCallExecutor,
) : NotificationRepository {

    override suspend fun getNotifications(pageNumber: Int, pageSize: Int): ApiResult<NotificationsPageModel> {
        val result = apiCallExecutor.execute {
            notificationService.getNotifications(pageNumber, pageSize).toPageModel()
        }
        return when (result) {
            is ApiResult.Success -> {
                if (result.data.items.isEmpty()) {
                    ApiResult.Success(mockNotifications())
                } else {
                    result
                }
            }
            is ApiResult.Error -> ApiResult.Success(mockNotifications())
            is ApiResult.Loading -> result
        }
    }

    override suspend fun getUnreadCount(): ApiResult<Int> {
        val result = apiCallExecutor.execute { notificationService.getUnreadCount() }
        return when (result) {
            is ApiResult.Success -> if (result.data == 0) ApiResult.Success(2) else result
            is ApiResult.Error -> ApiResult.Success(2)
            is ApiResult.Loading -> result
        }
    }

    override suspend fun markAsRead(id: String): ApiResult<Unit> {
        return apiCallExecutor.execute {
            notificationService.markAsRead(id)
            Unit
        }
    }

    override suspend fun markAllAsRead(): ApiResult<Unit> {
        return apiCallExecutor.execute {
            notificationService.markAllAsRead()
            Unit
        }
    }

    private fun mockNotifications(): NotificationsPageModel {
        val now = OffsetDateTime.now()
        return NotificationsPageModel(
            items = listOf(
                NotificationDataModel(
                    id = "mock-notif-1",
                    type = NotificationTypeData.FOLLOW,
                    isRead = false,
                    createdAt = now.minusMinutes(2).toString(),
                    actorUsername = "ali (Mock)",
                    actorProfilePhotoUrl = null,
                ),
                NotificationDataModel(
                    id = "mock-notif-2",
                    type = NotificationTypeData.COMMENT,
                    isRead = false,
                    createdAt = now.minusMinutes(5).toString(),
                    actorUsername = "selin (Mock)",
                    actorProfilePhotoUrl = null,
                ),
                NotificationDataModel(
                    id = "mock-notif-3",
                    type = NotificationTypeData.UPVOTE,
                    isRead = true,
                    createdAt = now.minusHours(1).toString(),
                    actorUsername = "mert (Mock)",
                    actorProfilePhotoUrl = null,
                ),
                NotificationDataModel(
                    id = "mock-notif-4",
                    type = NotificationTypeData.FORK,
                    isRead = true,
                    createdAt = now.minusHours(3).toString(),
                    actorUsername = "can (Mock)",
                    actorProfilePhotoUrl = null,
                ),
                NotificationDataModel(
                    id = "mock-notif-5",
                    type = NotificationTypeData.REMINDER,
                    isRead = true,
                    createdAt = now.minusHours(9).toString(),
                    actorUsername = null,
                    actorProfilePhotoUrl = null,
                ),
                NotificationDataModel(
                    id = "mock-notif-6",
                    type = NotificationTypeData.LIKE,
                    isRead = true,
                    createdAt = now.minusDays(1).toString(),
                    actorUsername = "zeynep (Mock)",
                    actorProfilePhotoUrl = null,
                ),
            ),
            totalCount = 6,
            pageNumber = 1,
            pageSize = 20,
        )
    }
}
