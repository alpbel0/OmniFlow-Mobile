package com.omniflow.data.models.profile

data class ProfileDataModel(
    val id: String,
    val username: String,
    val bio: String?,
    val profilePhotoUrl: String?,
    val karmaScore: Int,
    val followersCount: Int,
    val followingCount: Int,
    val tripCount: Int,
    val postCount: Int,
)

data class ProfileTripModel(
    val id: String,
    val title: String,
    val coverPhotoUrl: String?,
    val upvoteCount: Int,
    val forkCount: Int,
)

data class ProfilePostModel(
    val id: String,
    val username: String,
    val content: String?,
    val photos: List<String>,
    val upvoteCount: Int,
    val commentCount: Int,
    val createdAt: String,
    val profilePhotoUrl: String?,
)

data class ProfileContentModel(
    val profile: ProfileDataModel,
    val trips: List<ProfileTripModel>,
    val posts: List<ProfilePostModel>,
)
