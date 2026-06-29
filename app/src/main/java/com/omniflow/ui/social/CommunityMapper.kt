package com.omniflow.ui.social

import com.omniflow.core.designsystem.theme.ProfilePalette
import com.omniflow.data.models.profile.SuggestedFollowDto
import com.omniflow.data.models.profile.TopContributorDto
import kotlin.math.absoluteValue

internal fun List<SuggestedFollowDto>.toSuggestedUiModels() = mapIndexed { _, dto ->
    SuggestedUserUiModel(
        id = dto.id,
        username = dto.username,
        handle = "@${dto.username}",
        location = "",
        karma = dto.karmaScore,
        avatarInitial = dto.username.take(1).uppercase(),
        avatarColors = avatarGradient(dto.username),
        isFollowing = dto.isFollowing,
    )
}

internal fun List<TopContributorDto>.toContributorUiModels() = mapIndexed { index, dto ->
    ContributorUiModel(
        rank = index + 1,
        id = dto.id,
        username = dto.username,
        handle = "@${dto.username}",
        karma = dto.karmaScore,
        tripCount = dto.tripCount,
        avatarInitial = dto.username.take(1).uppercase(),
        avatarColors = avatarGradient(dto.username),
        isFollowing = false,
    )
}

private fun avatarGradient(username: String): List<androidx.compose.ui.graphics.Color> {
    val i = username.hashCode().absoluteValue % ProfilePalette.postAvatarGradients.size
    return ProfilePalette.postAvatarGradients[i]
}
