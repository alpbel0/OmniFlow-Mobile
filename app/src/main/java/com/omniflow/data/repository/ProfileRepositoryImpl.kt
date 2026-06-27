package com.omniflow.data.repository

import com.omniflow.core.network.ApiCallExecutor
import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.profile.ProfileContentModel
import com.omniflow.data.models.profile.ProfileDataModel
import com.omniflow.data.models.profile.ProfilePostModel
import com.omniflow.data.models.profile.ProfileTripModel
import com.omniflow.data.models.profile.UpdateProfileRequestDto
import com.omniflow.data.remote.ProfileService
import okhttp3.MultipartBody
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService,
    private val apiCallExecutor: ApiCallExecutor,
) : ProfileRepository {

    override suspend fun getMyProfileContent(): ApiResult<ProfileContentModel> = coroutineScope {
        val profileDeferred = async { apiCallExecutor.execute { profileService.getMyProfile() } }
        val profileResult = profileDeferred.await()

        val profile = when (profileResult) {
            is ApiResult.Success -> profileResult.data
            is ApiResult.Error -> return@coroutineScope ApiResult.Success(mockProfileContent())
            is ApiResult.Loading -> error("Loading is not expected from ApiCallExecutor")
        }

        val tripsDeferred = async {
            apiCallExecutor.execute { profileService.getUserTrips(profile.id) }
        }
        val postsDeferred = async {
            apiCallExecutor.execute { profileService.getMyPosts() }
        }

        val tripsResult = tripsDeferred.await()
        val postsResult = postsDeferred.await()

        val trips = (tripsResult as? ApiResult.Success)?.data?.data?.map {
            ProfileTripModel(
                id = it.id,
                title = it.title,
                coverPhotoUrl = it.coverPhotoUrl,
                upvoteCount = it.upvoteCount,
                forkCount = it.forkCount,
            )
        } ?: emptyList()

        val posts = (postsResult as? ApiResult.Success)?.data?.data?.map {
            ProfilePostModel(
                id = it.id,
                username = it.username,
                content = it.content,
                photos = it.photos,
                upvoteCount = it.upvoteCount,
                commentCount = it.commentCount,
                createdAt = it.createdAt,
                profilePhotoUrl = it.profilePhotoUrl,
            )
        } ?: emptyList()

        val hasContent = trips.isNotEmpty() || posts.isNotEmpty()
        if (!hasContent && profile.tripCount == 0 && profile.postCount == 0) {
            ApiResult.Success(mockProfileContent())
        } else {
            ApiResult.Success(
                ProfileContentModel(
                    profile = profile.toDataModel(),
                    trips = trips,
                    posts = posts,
                ),
            )
        }
    }

    override suspend fun getMyProfile(): ApiResult<ProfileDataModel> {
        return apiCallExecutor.execute { profileService.getMyProfile().toDataModel() }
    }

    override suspend fun updateBio(bio: String): ApiResult<ProfileDataModel> {
        return apiCallExecutor.execute {
            profileService.updateProfile(UpdateProfileRequestDto(bio = bio)).toDataModel()
        }
    }

    override suspend fun uploadProfilePhoto(file: MultipartBody.Part): ApiResult<ProfileDataModel> {
        return apiCallExecutor.execute {
            profileService.uploadProfilePhoto(file).toDataModel()
        }
    }

    private fun com.omniflow.data.models.profile.UserProfileDto.toDataModel() = ProfileDataModel(
        id = id,
        username = username,
        bio = bio,
        profilePhotoUrl = profilePhotoUrl,
        karmaScore = karmaScore,
        followersCount = followersCount,
        followingCount = followingCount,
        tripCount = tripCount,
        postCount = postCount,
    )

    private fun mockProfileContent(): ProfileContentModel {
        val now = OffsetDateTime.now().toString()
        return ProfileContentModel(
            profile = ProfileDataModel(
                id = "mock-user",
                username = "yigitalpbel (Mock)",
                bio = "Seyahat tutkunu 🌍 (Mock)",
                profilePhotoUrl = null,
                karmaScore = 1240,
                followersCount = 142,
                followingCount = 89,
                tripCount = 2,
                postCount = 1,
            ),
            trips = listOf(
                ProfileTripModel("mock-trip-1", "Roma & Floransa Turu (Mock)", null, 42, 5),
                ProfileTripModel("mock-trip-2", "Bali 2025 (Mock)", null, 28, 3),
            ),
            posts = listOf(
                ProfilePostModel(
                    id = "mock-post-1",
                    username = "yigitalpbel (Mock)",
                    content = "Roma'da harika bir deneyimdi 🗺 (Mock)",
                    photos = emptyList(),
                    upvoteCount = 24,
                    commentCount = 3,
                    createdAt = now,
                    profilePhotoUrl = null,
                ),
            ),
        )
    }
}
