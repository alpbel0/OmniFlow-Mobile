package com.omniflow.data.repository

import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.profile.FollowUserDto
import com.omniflow.data.models.profile.ProfileContentModel
import com.omniflow.data.models.profile.ProfileDataModel
import com.omniflow.data.models.profile.SuggestedFollowDto
import com.omniflow.data.models.profile.TopContributorDto
import okhttp3.MultipartBody

interface ProfileRepository {
    suspend fun getMyProfileContent(): ApiResult<ProfileContentModel>
    suspend fun getMyProfile(): ApiResult<ProfileDataModel>
    suspend fun updateBio(bio: String): ApiResult<ProfileDataModel>
    suspend fun uploadProfilePhoto(file: MultipartBody.Part): ApiResult<ProfileDataModel>
    suspend fun getUserProfile(username: String): ApiResult<ProfileContentModel>
    suspend fun followUser(userId: String): ApiResult<Unit>
    suspend fun unfollowUser(userId: String): ApiResult<Unit>
    suspend fun blockUser(userId: String): ApiResult<Unit>
    suspend fun unblockUser(userId: String): ApiResult<Unit>
    suspend fun getFollowers(
        userId: String,
        page: Int = 1,
        search: String? = null,
    ): ApiResult<List<FollowUserDto>>

    suspend fun getFollowing(
        userId: String,
        page: Int = 1,
        search: String? = null,
    ): ApiResult<List<FollowUserDto>>

    suspend fun getSuggestedFollows(): ApiResult<List<SuggestedFollowDto>>
    suspend fun getTopContributors(): ApiResult<List<TopContributorDto>>
}
