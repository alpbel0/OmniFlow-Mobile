package com.omniflow.ui.social

import androidx.compose.ui.graphics.Color
import com.omniflow.core.common.UiState

data class SuggestedUserUiModel(
    val id: String,
    val username: String,
    val handle: String,
    val location: String,
    val karma: Int,
    val avatarInitial: String,
    val avatarColors: List<Color>,
    val isFollowing: Boolean = false,
)

data class ContributorUiModel(
    val rank: Int,
    val id: String,
    val username: String,
    val handle: String,
    val karma: Int,
    val tripCount: Int,
    val avatarInitial: String,
    val avatarColors: List<Color>,
    val isFollowing: Boolean = false,
)

data class CommunityUiState(
    val suggestedContent: UiState<List<SuggestedUserUiModel>> = UiState.Loading,
    val contributorsContent: UiState<List<ContributorUiModel>> = UiState.Loading,
    val followLoadingIds: Set<String> = emptySet(),
)
