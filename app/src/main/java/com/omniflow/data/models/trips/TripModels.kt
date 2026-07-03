package com.omniflow.data.models.trips

data class TripModel(
    val id: String,
    val title: String,
    val coverPhotoUrl: String?,
    val status: TripStatusDto,
    val origin: String?,
    val originCountry: String?,
    val startDate: String?,
    val endDate: String?,
    val personCount: Int,
    val destinationCount: Int,
    val destinationList: List<String>,
    val forkCount: Int,
    val upvoteCount: Int,
    val viewCount: Int,
    val popularityScore: Double,
    val ownerUsername: String?,
    val isSaved: Boolean,
    val isUpvoted: Boolean,
    val tags: List<String>,
)

data class TripPageModel(
    val data: List<TripModel>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalCount: Int,
)

data class SavedTripModel(
    val tripId: String,
    val title: String,
    val coverPhotoUrl: String?,
    val username: String,
    val profilePhotoUrl: String?,
    val destinationCount: Int,
    val dayCount: Int,
    val forkCount: Int,
    val upvoteCount: Int,
    val viewCount: Int,
    val popularityScore: Double,
    val city: String?,
    val country: String?,
    val startDate: String?,
    val endDate: String?,
)

data class SavedTripPageModel(
    val data: List<SavedTripModel>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalCount: Int,
)
