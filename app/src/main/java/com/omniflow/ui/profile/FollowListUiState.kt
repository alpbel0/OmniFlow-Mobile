package com.omniflow.ui.profile

import androidx.compose.ui.graphics.Color
import com.omniflow.core.common.UiState

enum class FollowListMode { FOLLOWERS, FOLLOWING }

data class FollowUserUiModel(
    val id: String,
    val username: String,
    val handle: String,
    val karma: Int,
    val avatarInitial: String,
    val avatarColors: List<Color>,
    val isFollowing: Boolean,
)

data class FollowListUiState(
    val contentState: UiState<List<FollowUserUiModel>> = UiState.Loading,
    val mode: FollowListMode = FollowListMode.FOLLOWERS,
    val totalCount: Int = 0,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val followLoadingIds: Set<String> = emptySet(),
)
