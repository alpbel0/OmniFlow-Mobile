package com.omniflow.ui.profile

import androidx.compose.ui.graphics.Color
import com.omniflow.core.common.UiState

enum class ProfileVariant { VIEW, EMPTY, EDIT }
enum class ProfileTab { ALL, TRIPS, POSTS }

data class ProfileUiState(
    val contentState: UiState<ProfileUiModel> = UiState.Loading,
    val variant: ProfileVariant = ProfileVariant.VIEW,
    val activeTab: ProfileTab = ProfileTab.ALL,
    val editBio: String = "",
    val editLocation: String = "",
    val editSelectedStyles: Set<String> = emptySet(),
    val isSaving: Boolean = false,
    val isRefreshing: Boolean = false,
)

data class ProfileUiModel(
    val id: String = "",
    val handle: String,
    val bio: String,
    val karma: Int,
    val followerCount: Int,
    val followingCount: Int,
    val avatarInitial: String,
    val profilePhotoUrl: String?,
    val trips: List<ProfileTripUiModel>,
    val posts: List<ProfilePostUiModel>,
)

data class ProfileTripUiModel(
    val id: String,
    val title: String,
    val coverPhotoUrl: String?,
    val upvoteCount: Int,
    val gradientColors: List<Color>,
)

data class ProfilePostUiModel(
    val id: String,
    val handle: String,
    val timeAgo: String,
    val body: String,
    val upvotes: Int,
    val comments: Int,
    val avatarColors: List<Color>,
    val profilePhotoUrl: String?,
)
