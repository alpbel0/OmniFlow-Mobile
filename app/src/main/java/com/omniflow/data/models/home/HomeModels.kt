package com.omniflow.data.models.home

import com.omniflow.core.common.UiText

data class HomeDataModel(
    val profile: HomeProfileModel,
    val trips: List<HomeTripModel>,
    val featuredSection: HomeSectionModel<HomeFeaturedTripModel>,
    val communitySection: HomeSectionModel<HomeCommunityPreviewModel>,
    val hasUnreadNotifications: Boolean,
)

data class HomeProfileModel(
    val id: String,
    val username: String,
    val profilePhotoUrl: String?,
)

data class HomeTripModel(
    val id: String,
    val title: String,
    val coverPhotoUrl: String?,
    val status: String,
    val startDate: String,
    val endDate: String,
    val primaryCity: String?,
    val primaryCountry: String?,
    val isSaved: Boolean,
)

data class HomeFeaturedTripModel(
    val id: String,
    val title: String,
    val ownerUsername: String,
    val coverPhotoUrl: String?,
    val upvoteCount: Int,
    val isSaved: Boolean,
)

data class HomeCommunityPreviewModel(
    val id: String,
    val username: String,
    val profilePhotoUrl: String?,
    val content: String?,
    val photoUrl: String?,
    val createdAt: String,
)

sealed interface HomeSectionModel<out T> {
    data class Content<T>(val items: List<T>) : HomeSectionModel<T>
    data object Empty : HomeSectionModel<Nothing>
    data class Error(val message: UiText) : HomeSectionModel<Nothing>
}
