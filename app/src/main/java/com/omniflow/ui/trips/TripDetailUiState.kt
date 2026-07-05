package com.omniflow.ui.trips

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.omniflow.data.models.trips.TripStatusDto

enum class MapMode { BIRDS_EYE, ROAD }

enum class EntryCategory { FLIGHT, HOTEL, FOOD, ACTIVITY }

data class CategoryEntry(
    val id: String,
    val dayIndex: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val time: String = "",
    val itemKey: String? = null,
    val isConfirmed: Boolean = false,
    val subgroupLabel: String? = null,
    val category: EntryCategory = EntryCategory.FLIGHT,
    val hasLinkedEntry: Boolean = true,
    val isLocked: Boolean = false,
    val price: Double? = null,
    val durationLabel: String? = null,
)

data class CategoryCard(
    val category: EntryCategory,
    val label: String,
    val icon: ImageVector,
    val entries: List<CategoryEntry>,
    val hasEntries: Boolean,
    val expectedCount: Int = 0,
)

data class MapPin(
    val label: String,
    val color: Color,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

data class TripDay(
    val index: Int,
    val label: String,
    val activityCount: Int,
    val accentColor: Color,
    val cityLabel: String? = null,
)

@Immutable
data class TripDetailUiState(
    val tripId: String = "",
    val title: String = "",
    val status: String = "",
    val tripStatusEnum: TripStatusDto? = null,
    val coverPhotoUrl: String? = null,
    val dateRange: String = "",
    val country: String = "",
    val peopleCount: Int = 0,
    val dayCount: Int = 0,
    val activityCount: Int = 0,
    val progressPercent: Int = 0,
    val upvoteCount: Int = 0,
    val forkCount: Int = 0,
    val isUpvoted: Boolean = false,
    val isSaved: Boolean = false,
    val pins: List<MapPin> = emptyList(),
    val days: List<TripDay> = emptyList(),
    val isOwner: Boolean = false,
    val isLoading: Boolean = false,
    val variant: TripDetailVariant = TripDetailVariant.V1_DEFAULT,
    val mapMode: MapMode = MapMode.BIRDS_EYE,
    val selectedDayIndex: Int = 0,
    val displayMode: DisplayMode = DisplayMode.CATEGORY,
    val detaylarFraction: Float = 0.30f,
    val mapFraction: Float = 0.30f,
    val landscapeTimelineFraction: Float = 0.40f,
    val landscapeDetaylarFraction: Float = 0.30f,
    val routePoints: List<Pair<Double, Double>> = emptyList(),
    val routeUnavailable: Boolean = false,
    val budgetSpent: Double = 0.0,
    val budgetTotal: Double = 0.0,
    val categoryCards: List<CategoryCard> = emptyList(),
    val dayEntries: List<CategoryEntry> = emptyList(),
    val selectedEntryId: String? = null,
    val actionInProgress: String? = null,
    val error: String? = null,
    val showMoveToDraftDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showLoginRequiredDialog: Boolean = false,
    val showCollectionPicker: Boolean = false,
) {
    val timelineFraction: Float
        get() = (1f - detaylarFraction - mapFraction).coerceAtLeast(0f)
}

enum class TripDetailVariant { V1_DEFAULT, V2_SCROLLED }

enum class DisplayMode { CATEGORY, DAY }
