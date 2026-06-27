package com.omniflow.data.remote

import com.omniflow.data.models.notifications.NotificationsPageDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationService {
    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("isRead") isRead: Boolean? = null,
    ): NotificationsPageDto

    @GET("api/v1/notifications/unread-count")
    suspend fun getUnreadCount(): Int

    @POST("api/v1/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String)

    @POST("api/v1/notifications/read-all")
    suspend fun markAllAsRead()
}
