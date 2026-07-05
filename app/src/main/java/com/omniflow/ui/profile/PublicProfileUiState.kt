package com.omniflow.ui.profile

import androidx.compose.ui.graphics.Color
import com.omniflow.core.common.UiState

enum class PublicProfileTab { ALL, TRIPS, POSTS }

data class PublicProfileUiModel(
    val userId: String,
    val username: String,
    val handle: String,
    val bio: String,
    val location: String,
    val karma: Int,
    val followerCount: Int,
    val followingCount: Int,
    val avatarInitial: String,
    val avatarColors: List<Color>,
    val profilePhotoUrl: String?,
    val trips: List<ProfileTripUiModel>,
    val posts: List<ProfilePostUiModel>,
)

data class PublicProfileUiState(
    val contentState: UiState<PublicProfileUiModel> = UiState.Loading,
    val activeTab: PublicProfileTab = PublicProfileTab.ALL,
    val isFollowing: Boolean = false,
    val isBlockedByMe: Boolean = false,
    val followActionLoading: Boolean = false,
    val blockActionLoading: Boolean = false,
)
