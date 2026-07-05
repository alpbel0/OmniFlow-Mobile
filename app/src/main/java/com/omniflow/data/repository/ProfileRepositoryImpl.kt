package com.omniflow.data.repository

import com.omniflow.core.network.ApiCallExecutor
import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.profile.FollowUserDto
import com.omniflow.data.models.profile.ProfileContentModel
import com.omniflow.data.models.profile.ProfileDataModel
import com.omniflow.data.models.profile.ProfilePostModel
import com.omniflow.data.models.profile.ProfileTripModel
import com.omniflow.data.models.profile.SuggestedFollowDto
import com.omniflow.data.models.profile.TopContributorDto
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

    override suspend fun getUserProfile(username: String): ApiResult<ProfileContentModel> = coroutineScope {
        val profileResult = apiCallExecutor.execute { profileService.getUserByUsername(username) }

        val profile = when (profileResult) {
            is ApiResult.Success -> profileResult.data
            is ApiResult.Error -> return@coroutineScope ApiResult.Success(
                mockPublicProfileContent(username).toDataModel().let { mockProfile ->
                    ProfileContentModel(
                        profile = mockProfile,
                        trips = mockProfileContent().trips,
                        posts = mockProfileContent().posts,
                    )
                }
            )
            is ApiResult.Loading -> error("Loading is not expected from ApiCallExecutor")
        }

        val tripsDeferred = async {
            apiCallExecutor.execute { profileService.getUserTrips(profile.id) }
        }
        val postsDeferred = async {
            apiCallExecutor.execute { profileService.getUserPosts(profile.id) }
        }

        val trips = (tripsDeferred.await() as? ApiResult.Success)?.data?.data?.map {
            ProfileTripModel(
                id = it.id,
                title = it.title,
                coverPhotoUrl = it.coverPhotoUrl,
                upvoteCount = it.upvoteCount,
                forkCount = it.forkCount,
            )
        } ?: emptyList()

        val posts = (postsDeferred.await() as? ApiResult.Success)?.data?.data?.map {
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

        ApiResult.Success(
            ProfileContentModel(
                profile = profile.toDataModel(),
                trips = trips,
                posts = posts,
            ),
        )
    }

    override suspend fun followUser(userId: String): ApiResult<Unit> {
        return apiCallExecutor.execute { profileService.followUser(userId) }
    }

    override suspend fun unfollowUser(userId: String): ApiResult<Unit> {
        return apiCallExecutor.execute { profileService.unfollowUser(userId) }
    }

    override suspend fun blockUser(userId: String): ApiResult<Unit> {
        return apiCallExecutor.execute { profileService.blockUser(userId) }
    }

    override suspend fun unblockUser(userId: String): ApiResult<Unit> {
        return apiCallExecutor.execute { profileService.unblockUser(userId) }
    }

    override suspend fun getFollowers(
        userId: String,
        page: Int,
        search: String?,
    ): ApiResult<List<FollowUserDto>> {
        val result = apiCallExecutor.execute {
            profileService.getFollowers(userId, page, pageSize = 20, search = search)
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Success(mockFollowUsers())
            is ApiResult.Loading -> result
        }
    }

    override suspend fun getFollowing(
        userId: String,
        page: Int,
        search: String?,
    ): ApiResult<List<FollowUserDto>> {
        val result = apiCallExecutor.execute {
            profileService.getFollowing(userId, page, pageSize = 20, search = search)
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.data)
            is ApiResult.Error -> ApiResult.Success(mockFollowUsers())
            is ApiResult.Loading -> result
        }
    }

    override suspend fun getSuggestedFollows(): ApiResult<List<SuggestedFollowDto>> {
        val result = apiCallExecutor.execute { profileService.getSuggestedFollows() }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data)
            is ApiResult.Error -> ApiResult.Success(mockSuggestedFollows())
            is ApiResult.Loading -> result
        }
    }

    override suspend fun getTopContributors(): ApiResult<List<TopContributorDto>> {
        val result = apiCallExecutor.execute { profileService.getTopContributors() }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data)
            is ApiResult.Error -> ApiResult.Success(mockTopContributors())
            is ApiResult.Loading -> result
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
        isFollowing = isFollowing,
        isBlockedByMe = isBlockedByMe,
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
                followersCount = 6,
                followingCount = 6,
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

    private fun mockPublicProfileContent(username: String) = com.omniflow.data.models.profile.UserProfileDto(
        id = "mock-pub-$username",
        username = username,
        bio = "Dünyayı keşfediyorum ✈️",
        profilePhotoUrl = null,
        karmaScore = 3840,
        followersCount = 284,
        followingCount = 147,
        tripCount = 2,
        postCount = 1,
        isFollowing = false,
        isBlockedByMe = false,
    )

    private fun mockFollowUsers(): List<FollowUserDto> = listOf(
        FollowUserDto("mock-f1", "selin.k", null, isFollowing = true, karmaScore = 2340),
        FollowUserDto("mock-f2", "alptravel", null, isFollowing = false, karmaScore = 3840),
        FollowUserDto("mock-f3", "mert.y", null, isFollowing = true, karmaScore = 890),
        FollowUserDto("mock-f4", "ece.world", null, isFollowing = false, karmaScore = 1120),
        FollowUserDto("mock-f5", "can.exp", null, isFollowing = false, karmaScore = 560),
        FollowUserDto("mock-f6", "zeynep.t", null, isFollowing = true, karmaScore = 1780),
    )

    private fun mockSuggestedFollows(): List<SuggestedFollowDto> = listOf(
        SuggestedFollowDto("mock-s1", "alptravel", null, tripCount = 28, karmaScore = 3840, isFollowing = false, suggestionReason = "Popüler gezgin"),
        SuggestedFollowDto("mock-s2", "selin.k", null, tripCount = 19, karmaScore = 2340, isFollowing = true, suggestionReason = "Seni takip ediyor"),
        SuggestedFollowDto("mock-s3", "ece.world", null, tripCount = 11, karmaScore = 1890, isFollowing = false, suggestionReason = "Popüler gezgin"),
        SuggestedFollowDto("mock-s4", "can.exp", null, tripCount = 5, karmaScore = 560, isFollowing = false, suggestionReason = "Paylaşımlarını beğendin"),
    )

    private fun mockTopContributors(): List<TopContributorDto> = listOf(
        TopContributorDto("mock-t1", "ceydagezgin", null, karmaScore = 12480, tripCount = 34),
        TopContributorDto("mock-t2", "alptravel", null, karmaScore = 8920, tripCount = 28),
        TopContributorDto("mock-t3", "selin.k", null, karmaScore = 6340, tripCount = 19),
        TopContributorDto("mock-t4", "mert.y", null, karmaScore = 4210, tripCount = 15),
        TopContributorDto("mock-t5", "ece.world", null, karmaScore = 3180, tripCount = 11),
    )
}
