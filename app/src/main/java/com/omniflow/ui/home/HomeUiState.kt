package com.omniflow.ui.home

import com.omniflow.core.common.UiState

data class HomeUiState(
    val contentState: UiState<HomeUiModel> = UiState.Loading,
    val isRefreshing: Boolean = false,
)

data class HomeUiModel(
    val userName: String,
    val userInitial: String,
    val profilePhotoUrl: String?,
    val hasUnreadNotifications: Boolean,
    val heroTrips: List<HomeHeroTripUiModel>,
    val inspirationTrips: List<HomeInspirationUiModel>,
    val featuredSection: HomeSectionUiModel<HomeFeaturedTripUiModel>,
    val communitySection: HomeSectionUiModel<HomeCommunityPreviewUiModel>,
)

data class HomeHeroTripUiModel(
    val id: String,
    val title: String,
    val city: String,
    val dates: String,
    val statusLine: String,
    val chipText: String,
    val coverPhotoUrl: String?,
    val progressFraction: Float,
)

data class HomeInspirationUiModel(
    val city: String,
    val imageUrl: String,
)

data class HomeFeaturedTripUiModel(
    val id: String,
    val title: String,
    val author: String,
    val coverPhotoUrl: String?,
    val upvoteCount: Int,
    val isSaved: Boolean,
)

data class HomeCommunityPreviewUiModel(
    val id: String,
    val username: String,
    val profilePhotoUrl: String?,
    val content: String,
    val photoUrl: String?,
    val timeAgo: String,
)

sealed interface HomeSectionUiModel<out T> {
    data class Content<T>(val items: List<T>) : HomeSectionUiModel<T>
    data object Hidden : HomeSectionUiModel<Nothing>
    data class Error(val message: String) : HomeSectionUiModel<Nothing>
}
