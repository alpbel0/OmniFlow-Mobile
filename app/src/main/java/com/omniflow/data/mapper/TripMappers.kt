package com.omniflow.data.mapper

import com.omniflow.data.models.trips.GetMyTripsPageDto
import com.omniflow.data.models.trips.SavedTripModel
import com.omniflow.data.models.trips.SavedTripPageModel
import com.omniflow.data.models.trips.SavedTripResponseDto
import com.omniflow.data.models.trips.SavedTripsPageDto
import com.omniflow.data.models.trips.TripModel
import com.omniflow.data.models.trips.TripPageModel
import com.omniflow.data.models.trips.TripResponseDto
import com.omniflow.data.models.trips.TripStatusDto
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

fun TripResponseDto.toModel(): TripModel = TripModel(
    id = id,
    title = title.orEmpty(),
    coverPhotoUrl = coverPhotoUrl,
    status = TripStatusDto.fromInt(status),
    origin = origin,
    originCountry = originCountry,
    startDate = startDate,
    endDate = endDate,
    personCount = personCount,
    destinationCount = destinations.size,
    destinationList = destinations.map { it.city ?: it.country ?: "" },
    forkCount = forkCount,
    upvoteCount = upvoteCount,
    viewCount = viewCount,
    popularityScore = popularityScore,
    ownerUsername = ownerUsername,
    isSaved = isSaved ?: false,
    isUpvoted = isUpvoted ?: false,
    tags = tags,
)

fun GetMyTripsPageDto.toModel(): TripPageModel = TripPageModel(
    data = data.map { it.toModel() },
    pageNumber = pageNumber,
    pageSize = pageSize,
    totalCount = totalCount,
)

fun SavedTripResponseDto.toModel(): SavedTripModel {
    val dayCount = if (startDate != null && endDate != null) {
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)
        maxOf(1, ChronoUnit.DAYS.between(start, end).toInt() + 1)
    } else {
        0
    }
    return SavedTripModel(
        tripId = tripId,
        title = title.orEmpty(),
        coverPhotoUrl = coverPhotoUrl,
        username = ownerUsername.orEmpty(),
        profilePhotoUrl = ownerProfilePhotoUrl,
        destinationCount = 0,
        dayCount = dayCount,
        forkCount = forkCount,
        upvoteCount = upvoteCount,
        viewCount = viewCount,
        popularityScore = popularityScore,
        city = city,
        country = country,
        startDate = startDate,
        endDate = endDate,
    )
}

fun SavedTripsPageDto.toModel(): SavedTripPageModel = SavedTripPageModel(
    data = data.map { it.toModel() },
    pageNumber = pageNumber,
    pageSize = pageSize,
    totalCount = totalCount,
)
