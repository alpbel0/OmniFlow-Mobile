package com.omniflow.ui.profile

import com.omniflow.data.models.profile.FollowUserDto

internal fun FollowUserDto.toUiModel() = FollowUserUiModel(
    id = id,
    username = username,
    handle = "@$username",
    karma = karmaScore,
    avatarInitial = username.take(1).uppercase(),
    avatarColors = deterministicAvatarGradient(username),
    isFollowing = isFollowing == true,
)

internal fun List<FollowUserDto>.toUiModels() = map { it.toUiModel() }
