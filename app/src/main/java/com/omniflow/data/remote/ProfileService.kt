package com.omniflow.data.remote

import com.omniflow.data.models.profile.FollowUsersPageDto
import com.omniflow.data.models.profile.ProfilePostsPageDto
import com.omniflow.data.models.profile.ProfileTripsPageDto
import com.omniflow.data.models.profile.UpdateProfileRequestDto
import com.omniflow.data.models.profile.UserProfileDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileService {
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): UserProfileDto

    @GET("api/v1/users/me/posts")
    suspend fun getMyPosts(
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
    ): ProfilePostsPageDto

    @GET("api/v1/users/{userId}/trips")
    suspend fun getUserTrips(
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
    ): ProfileTripsPageDto

    @GET("api/v1/users/{userId}/posts")
    suspend fun getUserPosts(
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
    ): ProfilePostsPageDto

    @GET("api/v1/users/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): UserProfileDto

    @POST("api/v1/users/{userId}/follow")
    suspend fun followUser(@Path("userId") userId: String)

    @DELETE("api/v1/users/{userId}/follow")
    suspend fun unfollowUser(@Path("userId") userId: String)

    @POST("api/v1/users/{userId}/block")
    suspend fun blockUser(@Path("userId") userId: String)

    @DELETE("api/v1/users/{userId}/block")
    suspend fun unblockUser(@Path("userId") userId: String)

    @GET("api/v1/users/{userId}/followers")
    suspend fun getFollowers(
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null,
    ): FollowUsersPageDto

    @GET("api/v1/users/{userId}/following")
    suspend fun getFollowing(
        @Path("userId") userId: String,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null,
    ): FollowUsersPageDto

    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): UserProfileDto

    @Multipart
    @POST("api/v1/users/me/profile-photo")
    suspend fun uploadProfilePhoto(@Part file: MultipartBody.Part): UserProfileDto
}
