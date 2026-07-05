package com.omniflow.ui.trips

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.omniflow.core.designsystem.theme.TripsPalette

enum class MyTripsTab(val label: String) {
    DRAFT("Taslak"),
    PUBLISHED("Yayında"),
    SAVED("Kaydedilenler"),
}

enum class PublishedBadge { UPCOMING, COMPLETED, ARCHIVED }

data class DraftTrip(
    val id: String,
    val title: String,
    val dateLabel: String,
    val peopleCount: Int,
    val destinationCount: Int,
    val progressPercent: Int,
    val gradient: List<Color>,
)

data class PublishedTrip(
    val id: String,
    val title: String,
    val dateLabel: String,
    val peopleCount: Int,
    val destinationCount: Int,
    val dayCount: Int,
    val rating: Double,
    val forkCount: Int,
    val badge: PublishedBadge,
    val daysLeftLabel: String? = null,
    val gradient: List<Color>,
)

data class SavedTrip(
    val id: String,
    val title: String,
    val username: String,
    val destinationCount: Int,
    val dayCount: Int,
    val likeCount: String,
    val gradient: List<Color>,
)

data class PublishSummary(
    val routeCount: Int,
    val viewCount: Int,
    val forkCount: Int,
    val avgRating: Double,
)

val defaultCollections = listOf("Tümü", "Avrupa", "Yaz 2026")

@Immutable
data class MyTripsUiState(
    val selectedTab: MyTripsTab = MyTripsTab.DRAFT,
    val draftTrips: List<DraftTrip> = emptyList(),
    val publishedTrips: List<PublishedTrip> = emptyList(),
    val publishSummary: PublishSummary = PublishSummary(0, 0, 0, 0.0),
    val savedTrips: List<SavedTrip> = emptyList(),
    val collections: List<String> = defaultCollections,
    val selectedCollection: String = defaultCollections.first(),
    val isDraftsLoading: Boolean = false,
    val isPublishedLoading: Boolean = false,
    val isSavedLoading: Boolean = false,
    val error: String? = null,
)
