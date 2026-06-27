package com.omniflow.ui.home

import com.omniflow.data.models.home.HomeCommunityPreviewModel
import com.omniflow.data.models.home.HomeDataModel
import com.omniflow.data.models.home.HomeFeaturedTripModel
import com.omniflow.data.models.home.HomeSectionModel
import com.omniflow.data.models.home.HomeTripModel
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val shortMonthFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("tr", "TR"))

fun HomeDataModel.toUiModel(today: LocalDate = LocalDate.now()): HomeUiModel {
    return HomeUiModel(
        userName = profile.username,
        userInitial = profile.username.firstOrNull()?.uppercase() ?: "O",
        profilePhotoUrl = profile.profilePhotoUrl,
        hasUnreadNotifications = hasUnreadNotifications,
        heroTrips = trips
            .filterNot { it.status.equals("Archived", ignoreCase = true) }
            .sortedWith(compareBy<HomeTripModel> { it.priority(today) }.thenBy { it.startDate })
            .take(5)
            .map { it.toHeroUiModel(today) },
        inspirationTrips = defaultInspirationTrips,
        featuredSection = featuredSection.toFeaturedUiModel(),
        communitySection = communitySection.toCommunityUiModel(),
    )
}

private fun HomeTripModel.priority(today: LocalDate): Int {
    val start = runCatching { LocalDate.parse(startDate) }.getOrNull()
    val end = runCatching { LocalDate.parse(endDate) }.getOrNull()
    val isPublished = status.equals("Published", ignoreCase = true)
    val isDraft = status.equals("Draft", ignoreCase = true)

    return when {
        isPublished && start != null && end != null && !today.isBefore(start) && !today.isAfter(end) -> 0
        isPublished && start != null && today.isBefore(start) -> 1
        isDraft -> 2
        else -> 3
    }
}

private fun HomeTripModel.toHeroUiModel(today: LocalDate): HomeHeroTripUiModel {
    val start = runCatching { LocalDate.parse(startDate) }.getOrNull()
    val end = runCatching { LocalDate.parse(endDate) }.getOrNull()
    val tripDays = if (start != null && end != null) {
        (end.toEpochDay() - start.toEpochDay()).toInt() + 1
    } else {
        1
    }
    val cityLabel = listOfNotNull(primaryCity, primaryCountry).joinToString(", ").ifBlank { "OmniFlow" }
    val datesLabel = if (start != null && end != null) {
        "${start.format(shortMonthFormatter)}-${end.format(shortMonthFormatter)}"
    } else {
        "Tarihler hazirlaniyor"
    }

    val isPublished = status.equals("Published", ignoreCase = true)
    val isDraft = status.equals("Draft", ignoreCase = true)
    val statusLine = when {
        isPublished && start != null && end != null && !today.isBefore(start) && !today.isAfter(end) -> "Devam ediyor"
        isPublished && start != null && today.isEqual(start) -> "Bugun basliyor"
        isPublished && start != null && today.isBefore(start) -> {
            val days = start.toEpochDay() - today.toEpochDay()
            "$days gun sonra basliyor"
        }
        isDraft -> "Taslak gezi"
        else -> "Hazirlaniyor"
    }
    val chipText = when {
        isPublished && start != null && end != null && !today.isBefore(start) && !today.isAfter(end) -> {
            val elapsed = (today.toEpochDay() - start.toEpochDay()).toInt() + 1
            "$elapsed/$tripDays gun"
        }
        isPublished && start != null && today.isEqual(start) -> "Bugun"
        isPublished && start != null && today.isBefore(start) -> {
            val days = start.toEpochDay() - today.toEpochDay()
            "$days gun kaldi"
        }
        else -> "Taslak"
    }
    val progressFraction = when {
        isPublished && start != null && end != null && !today.isBefore(start) && !today.isAfter(end) -> {
            val elapsed = (today.toEpochDay() - start.toEpochDay()).toFloat() + 1f
            (elapsed / tripDays.toFloat()).coerceIn(0.12f, 1f)
        }
        isPublished && start != null && today.isBefore(start) -> 0.28f
        else -> 0.5f
    }

    return HomeHeroTripUiModel(
        id = id,
        title = title,
        city = cityLabel,
        dates = datesLabel,
        statusLine = statusLine,
        chipText = chipText,
        coverPhotoUrl = coverPhotoUrl,
        progressFraction = progressFraction,
    )
}

private fun HomeSectionModel<HomeFeaturedTripModel>.toFeaturedUiModel(): HomeSectionUiModel<HomeFeaturedTripUiModel> {
    return when (this) {
        is HomeSectionModel.Content -> HomeSectionUiModel.Content(
            items.map { trip ->
                HomeFeaturedTripUiModel(
                    id = trip.id,
                    title = trip.title,
                    author = "@${trip.ownerUsername}",
                    coverPhotoUrl = trip.coverPhotoUrl,
                    upvoteCount = trip.upvoteCount,
                    isSaved = trip.isSaved,
                )
            },
        )
        is HomeSectionModel.Error -> HomeSectionUiModel.Error(message.resolveValue())
        HomeSectionModel.Empty -> HomeSectionUiModel.Hidden
    }
}

private fun HomeSectionModel<HomeCommunityPreviewModel>.toCommunityUiModel(): HomeSectionUiModel<HomeCommunityPreviewUiModel> {
    return when (this) {
        is HomeSectionModel.Content -> HomeSectionUiModel.Content(
            items.map { item ->
                HomeCommunityPreviewUiModel(
                    id = item.id,
                    username = item.username,
                    profilePhotoUrl = item.profilePhotoUrl,
                    content = item.content.orEmpty().ifBlank { "Yeni bir seyahat paylasti." },
                    photoUrl = item.photoUrl,
                    timeAgo = item.createdAt.toRelativeTime(),
                )
            },
        )
        is HomeSectionModel.Error -> HomeSectionUiModel.Error(message.resolveValue())
        HomeSectionModel.Empty -> HomeSectionUiModel.Hidden
    }
}

private fun com.omniflow.core.common.UiText.resolveValue(): String {
    return when (this) {
        is com.omniflow.core.common.UiText.DynamicString -> value
        is com.omniflow.core.common.UiText.StringResource -> "Bu bolum su anda yuklenemedi."
    }
}

private fun String.toRelativeTime(now: OffsetDateTime = OffsetDateTime.now()): String {
    val timestamp = runCatching { OffsetDateTime.parse(this) }.getOrNull() ?: return "Simdi"
    val duration = Duration.between(timestamp, now).abs()
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()

    return when {
        minutes < 1 -> "Simdi"
        minutes < 60 -> "${minutes}dk"
        hours < 24 -> "${hours}s"
        else -> "${days}g"
    }
}

private val defaultInspirationTrips = listOf(
    HomeInspirationUiModel(
        city = "Roma",
        imageUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=320&h=420&fit=crop",
    ),
    HomeInspirationUiModel(
        city = "Bali",
        imageUrl = "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=320&h=420&fit=crop",
    ),
    HomeInspirationUiModel(
        city = "Tokyo",
        imageUrl = "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=320&h=420&fit=crop",
    ),
    HomeInspirationUiModel(
        city = "Barselona",
        imageUrl = "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?w=320&h=420&fit=crop",
    ),
)
