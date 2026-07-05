package com.omniflow.data.models.home

import com.omniflow.data.models.common.PagedResponseDto
import kotlinx.serialization.Serializable

@Serializable
data class HomeUserProfileDto(
    val id: String,
    val username: String,
    val email: String,
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
data class HomeTripResponseDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val coverPhotoUrl: String? = null,
    val status: String,
    val origin: String,
    val originCountry: String,
    val startDate: String,
    val endDate: String,
    val ownerUsername: String,
    val isSaved: Boolean? = null,
    val destinations: List<HomeTripDestinationResponseDto> = emptyList(),
)

@Serializable
data class HomeTripDestinationResponseDto(
    val id: String,
    val city: String,
    val country: String,
    val arrivalDate: String,
    val departureDate: String,
    val orderIndex: Int,
)

@Serializable
data class HomeTripsPageDto(
    val data: List<HomeTripResponseDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalCount: Int,
)

@Serializable
data class HomeFeaturedTripDto(
    val id: String,
    val title: String,
    val coverPhotoUrl: String? = null,
    val origin: String,
    val originCountry: String,
    val forkCount: Int = 0,
    val upvoteCount: Int = 0,
    val popularityScore: Double = 0.0,
    val startDate: String,
    val endDate: String,
    val ownerId: String,
    val ownerUsername: String,
    val ownerProfilePhotoUrl: String? = null,
)

@Serializable
data class HomePostDto(
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
data class HomeFeedResponseDto(
    val data: List<HomePostDto>,
    val nextCursor: String? = null,
    val hasMore: Boolean = false,
)

typealias HomePostsPageDto = PagedResponseDto<HomePostDto>
