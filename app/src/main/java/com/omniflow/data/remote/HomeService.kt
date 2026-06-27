package com.omniflow.data.remote

import com.omniflow.data.models.home.HomeFeedResponseDto
import com.omniflow.data.models.home.HomeFeaturedTripDto
import com.omniflow.data.models.home.HomeTripsPageDto
import com.omniflow.data.models.home.HomeUserProfileDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): HomeUserProfileDto

    @GET("api/v1/trips")
    suspend fun getMyTrips(
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
    ): HomeTripsPageDto

    @GET("api/v1/explore/featured")
    suspend fun getFeaturedTrips(
        @Query("limit") limit: Int = 6,
    ): List<HomeFeaturedTripDto>

    @GET("api/v1/feed")
    suspend fun getFeed(
        @Query("pageSize") pageSize: Int = 2,
    ): HomeFeedResponseDto

    @GET("api/v1/notifications/unread-count")
    suspend fun getUnreadCount(): Int
}
