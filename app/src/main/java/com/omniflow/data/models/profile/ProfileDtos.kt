package com.omniflow.data.models.profile

import com.omniflow.data.models.common.PagedResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val username: String,
    val email: String? = null,
    val bio: String? = null,
    val profilePhotoUrl: String? = null,
    val karmaScore: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false,
    val tripCount: Int = 0,
    val postCount: Int = 0,
    val isBlocked: Boolean = false,
    val isBlockedByMe: Boolean = false,
)

@Serializable
data class ProfileTripDto(
    val id: String,
    val title: String,
    val coverPhotoUrl: String? = null,
    val status: String,
    val origin: String,
    val originCountry: String,
    val startDate: String,
    val endDate: String,
    val ownerUsername: String,
    val forkCount: Int = 0,
    val upvoteCount: Int = 0,
    val popularityScore: Double = 0.0,
)

@Serializable
data class ProfilePostDto(
    val id: String,
    val userId: String,
    val tripId: String? = null,
    val placeId: String? = null,
    val postType: String,
    val content: String? = null,
    val photos: List<String> = emptyList(),
    val city: String? = null,
    val country: String? = null,
    val upvoteCount: Int = 0,
    val commentCount: Int = 0,
    val createdAt: String,
    val username: String,
    val profilePhotoUrl: String? = null,
    val karmaScore: Int = 0,
    val isUpvoted: Boolean = false,
)

@Serializable
data class UpdateProfileRequestDto(
    val bio: String? = null,
    val profilePhotoUrl: String? = null,
)

typealias ProfileTripsPageDto = PagedResponseDto<ProfileTripDto>
typealias ProfilePostsPageDto = PagedResponseDto<ProfilePostDto>
