package com.omniflow.ui.profile

import com.omniflow.core.designsystem.theme.ProfilePalette
import com.omniflow.data.models.profile.ProfileContentModel
import java.time.Duration
import java.time.OffsetDateTime

fun ProfileContentModel.toUiModel(): ProfileUiModel {
    return ProfileUiModel(
        id = profile.id,
        handle = "@${profile.username}",
        bio = profile.bio ?: "",
        karma = profile.karmaScore,
        followerCount = profile.followersCount,
        followingCount = profile.followingCount,
        avatarInitial = profile.username.firstOrNull()?.uppercase() ?: "O",
        profilePhotoUrl = profile.profilePhotoUrl,
        trips = trips.mapIndexed { index, trip ->
            ProfileTripUiModel(
                id = trip.id,
                title = trip.title,
                coverPhotoUrl = trip.coverPhotoUrl,
                upvoteCount = trip.upvoteCount,
                gradientColors = ProfilePalette.tripCardGradients[
                    index % ProfilePalette.tripCardGradients.size
                ],
            )
        },
        posts = posts.mapIndexed { index, post ->
            ProfilePostUiModel(
                id = post.id,
                handle = "@${post.username}",
                timeAgo = post.createdAt.toRelativeTime(),
                body = post.content ?: "",
                upvotes = post.upvoteCount,
                comments = post.commentCount,
                avatarColors = ProfilePalette.postAvatarGradients[
                    index % ProfilePalette.postAvatarGradients.size
                ],
                profilePhotoUrl = post.profilePhotoUrl,
            )
        },
    )
}

private fun String.toRelativeTime(now: OffsetDateTime = OffsetDateTime.now()): String {
    val timestamp = runCatching { OffsetDateTime.parse(this) }.getOrNull() ?: return "Az önce"
    val duration = Duration.between(timestamp, now).abs()
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()

    return when {
        minutes < 1 -> "Az önce"
        minutes < 60 -> "$minutes dk önce"
        hours < 24 -> "$hours saat önce"
        days < 7 -> "$days gün önce"
        else -> "${days / 7} hafta önce"
    }
}
