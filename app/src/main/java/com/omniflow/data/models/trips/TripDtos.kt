package com.omniflow.data.models.trips

import kotlinx.serialization.Serializable

@Serializable
data class TripResponseDto(
    val id: String,
    val title: String? = null,
    val description: String? = null,
    val coverPhotoUrl: String? = null,
    val status: Int = 0,
    val origin: String? = null,
    val originCountry: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val personCount: Int = 0,
    val destinations: List<TripDestinationDto> = emptyList(),
    val forkCount: Int = 0,
    val upvoteCount: Int = 0,
    val viewCount: Int = 0,
    val popularityScore: Double = 0.0,
    val ownerId: String = "",
    val ownerUsername: String? = null,
    val ownerProfilePhotoUrl: String? = null,
    val isUpvoted: Boolean? = null,
    val isSaved: Boolean? = null,
    val tags: List<String> = emptyList(),
    val travelStyles: List<Int> = emptyList(),
)

@Serializable
data class TripDestinationDto(
    val id: String,
    val city: String? = null,
    val country: String? = null,
    val arrivalDate: String? = null,
    val departureDate: String? = null,
    val orderIndex: Int = 0,
)

@Serializable
data class GetMyTripsPageDto(
    val data: List<TripResponseDto> = emptyList(),
    val pageNumber: Int = 1,
    val pageSize: Int = 10,
    val totalCount: Int = 0,
)

@Serializable
data class SavedTripResponseDto(
    val tripId: String,
    val savedAt: String? = null,
    val title: String? = null,
    val description: String? = null,
    val coverPhotoUrl: String? = null,
    val status: Int = 0,
    val city: String? = null,
    val country: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val personCount: Int = 0,
    val forkCount: Int = 0,
    val upvoteCount: Int = 0,
    val viewCount: Int = 0,
    val popularityScore: Double = 0.0,
    val ownerId: String = "",
    val ownerUsername: String? = null,
    val ownerProfilePhotoUrl: String? = null,
)

@Serializable
data class SavedTripsPageDto(
    val data: List<SavedTripResponseDto> = emptyList(),
    val pageNumber: Int = 1,
    val pageSize: Int = 10,
    val totalCount: Int = 0,
)

/**
 * ORS rota proxy cevabı — backend B0.15 (BACKEND_ROADMAP_V2.md) gelene kadar
 * bu şekil tahminidir. Backend gerçek şekli belli olunca bu nota göre
 * düzeltilecek. `points`: [ [lng, lat], [lng, lat], ... ] — GeoJSON
 * koordinat sırası (longitude önce).
 */
@Serializable
data class RouteResponseDto(
    val points: List<List<Double>> = emptyList(),
)

/**
 * Checklist toggle isteği gövdesi — backend B0.9 (BACKEND_ROADMAP_V2.md) gelene kadar
 * bu şekil tahminidir.
 */
@Serializable
data class ChecklistUpdateDto(
    val isConfirmed: Boolean,
)

/**
 * Unlock entry isteği gövdesi — backend B0.14 (BACKEND_ROADMAP_V2.md) gelene kadar
 * bu şekil tahminidir.
 */
@Serializable
data class UnlockEntryDto(
    val isLocked: Boolean = false,
)

/**
 * Timeline cevabı — backend B0.13 (BACKEND_ROADMAP_V2.md) gelene kadar
 * bu şekil tahminidir.
 */
@Serializable
data class TimelineResponseDto(
    val entries: List<TimelineEntryDto> = emptyList(),
)

@Serializable
data class TimelineEntryDto(
    val id: String = "",
    val title: String = "",
)

/**
 * Bütçe özeti cevabı — backend B0.9 ile gelecek (BACKEND_ROADMAP_V2.md).
 * Şekil tahminidir.
 */
@Serializable
data class BudgetSummaryResponseDto(
    val spent: Double = 0.0,
    val total: Double = 0.0,
)

/**
 * Checklist cevabı — backend B0.9 (BACKEND_ROADMAP_V2.md) gelene kadar
 * bu şekil tahminidir.
 */
@Serializable
data class ChecklistResponseDto(
    val items: List<ChecklistItemDto> = emptyList(),
)

@Serializable
data class ChecklistItemDto(
    val itemKey: String = "",
    val isConfirmed: Boolean = false,
)

enum class TripStatusDto(val value: Int) {
    Draft(0),
    Published(1),
    Archived(2);

    companion object {
        fun fromInt(value: Int): TripStatusDto = entries.first { it.value == value }
    }
}
