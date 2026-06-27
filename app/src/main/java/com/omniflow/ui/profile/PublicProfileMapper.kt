package com.omniflow.ui.profile

import androidx.compose.ui.graphics.Color
import com.omniflow.core.designsystem.theme.ProfilePalette
import com.omniflow.data.models.profile.ProfileContentModel
import com.omniflow.data.models.profile.ProfileDataModel
import com.omniflow.data.models.profile.ProfilePostModel
import com.omniflow.data.models.profile.ProfileTripModel
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

internal fun ProfileContentModel.toPublicProfileUiModel(): PublicProfileUiModel {
    val model = this
    val avatarColors = deterministicAvatarGradient(model.profile.username)
    return PublicProfileUiModel(
        userId = model.profile.id,
        username = model.profile.username,
        handle = "@${model.profile.username}",
        bio = model.profile.bio.orEmpty(),
        location = "",
        karma = model.profile.karmaScore,
        followerCount = model.profile.followersCount,
        followingCount = model.profile.followingCount,
        avatarInitial = model.profile.username.take(1).uppercase(),
        avatarColors = avatarColors,
        profilePhotoUrl = model.profile.profilePhotoUrl,
        trips = model.trips.map { it.toUiModel() },
        posts = model.posts.map { it.toUiModel() },
    )
}

internal fun ProfileDataModel.toMockPublicProfileUiModel(): PublicProfileUiModel {
    val avatarColors = deterministicAvatarGradient(username)
    return PublicProfileUiModel(
        userId = id,
        username = username,
        handle = "@$username",
        bio = if (bio.isNullOrBlank()) "" else "$bio (Mock)",
        location = "Barcelona, İspanya (Mock)",
        karma = karmaScore,
        followerCount = followersCount,
        followingCount = followingCount,
        avatarInitial = username.take(1).uppercase(),
        avatarColors = avatarColors,
        profilePhotoUrl = profilePhotoUrl,
        trips = emptyList(),
        posts = emptyList(),
    )
}

internal fun ProfileTripModel.toUiModel() = ProfileTripUiModel(
    id = id,
    title = title,
    coverPhotoUrl = coverPhotoUrl,
    upvoteCount = upvoteCount,
    gradientColors = tripCardGradient(id),
)

internal fun ProfilePostModel.toUiModel() = ProfilePostUiModel(
    id = id,
    handle = "@$username",
    timeAgo = createdAt.toRelativeTime(),
    body = content.orEmpty(),
    upvotes = upvoteCount,
    comments = commentCount,
    avatarColors = deterministicAvatarGradient(username),
    profilePhotoUrl = profilePhotoUrl,
)

private fun String.toRelativeTime(): String {
    return try {
        val created = OffsetDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val now = OffsetDateTime.now()
        val minutes = Duration.between(created, now).toMinutes()
        when {
            minutes < 1 -> "az önce"
            minutes < 60 -> "${minutes}dk"
            else -> {
                val hours = minutes / 60
                if (hours < 24) "${hours}s"
                else "${hours / 24}g"
            }
        }
    } catch (_: Exception) {
        this
    }
}

private fun deterministicAvatarGradient(username: String): List<Color> {
    val index = username.hashCode().absoluteValue % ProfilePalette.postAvatarGradients.size
    return ProfilePalette.postAvatarGradients[index]
}

private fun tripCardGradient(tripId: String): List<Color> {
    val index = tripId.hashCode().absoluteValue % ProfilePalette.tripCardGradients.size
    return ProfilePalette.tripCardGradients[index]
}
