package com.omniflow.data.repository

import com.omniflow.core.network.ApiCallExecutor
import com.omniflow.core.network.ApiResult
import com.omniflow.data.mapper.toModel
import com.omniflow.data.models.trips.SavedTripModel
import com.omniflow.data.models.trips.SavedTripPageModel
import com.omniflow.data.models.trips.TripModel
import com.omniflow.data.models.trips.TripPageModel
import com.omniflow.data.models.trips.TripStatusDto
import com.omniflow.data.remote.TripService
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val tripService: TripService,
    private val apiCallExecutor: ApiCallExecutor,
) : TripRepository {

    override suspend fun getMyTrips(status: Int, page: Int): ApiResult<TripPageModel> {
        val result = apiCallExecutor.execute {
            tripService.getMyTrips(status = status, pageNumber = page).toModel()
        }
        return when (result) {
            is ApiResult.Error -> fallbackTrips(status)
            is ApiResult.Success -> {
                if (result.data.data.isEmpty()) fallbackTrips(status) else result
            }
            else -> result
        }
    }

    override suspend fun getSavedTrips(page: Int): ApiResult<SavedTripPageModel> {
        val result = apiCallExecutor.execute {
            tripService.getSavedTrips(pageNumber = page).toModel()
        }
        return when (result) {
            is ApiResult.Error -> fallbackSavedTrips()
            is ApiResult.Success -> {
                if (result.data.data.isEmpty()) fallbackSavedTrips() else result
            }
            else -> result
        }
    }

    override suspend fun publishTrip(tripId: String): ApiResult<Unit> {
        return apiCallExecutor.execute {
            tripService.publishTrip(tripId)
        }
    }

    private fun fallbackTrips(status: Int): ApiResult<TripPageModel> {
        val gradients = listOf(
            listOf("FF1565C0", "FF42A5F5"),
            listOf("FF004D40", "FF26A69A"),
            listOf("FF4A148C", "FFAB47BC"),
            listOf("FF0D3B6E", "FF1976D2"),
            listOf("FFBF360C", "FFFF7043"),
        )
        val trips = listOf(
            TripModel(
                id = "draft-1", title = "Barselona Kaçamağı", coverPhotoUrl = null,
                status = TripStatusDto.Draft, origin = "İstanbul", originCountry = "Türkiye",
                startDate = null, endDate = null, personCount = 2, destinationCount = 2,
                destinationList = listOf("Barselona", "Madrid"), forkCount = 0, upvoteCount = 0,
                viewCount = 0, popularityScore = 0.0, ownerUsername = null,
                isSaved = false, isUpvoted = false, tags = emptyList(),
            ),
            TripModel(
                id = "draft-2", title = "Tokyo Macerası (Mock)", coverPhotoUrl = null,
                status = TripStatusDto.Draft, origin = "Ankara", originCountry = "Türkiye",
                startDate = null, endDate = null, personCount = 1, destinationCount = 3,
                destinationList = listOf("Tokyo", "Kyoto", "Osaka"), forkCount = 0, upvoteCount = 0,
                viewCount = 0, popularityScore = 0.0, ownerUsername = null,
                isSaved = false, isUpvoted = false, tags = emptyList(),
            ),
        )
        val published = listOf(
            TripModel(
                id = "pub-1", title = "Amalfi Sahili Rüyası (Mock)", coverPhotoUrl = null,
                status = TripStatusDto.Published, origin = "İstanbul", originCountry = "Türkiye",
                startDate = "2026-07-15", endDate = "2026-07-22", personCount = 2,
                destinationCount = 2, destinationList = listOf("Napoli", "Positano"),
                forkCount = 4, upvoteCount = 7, viewCount = 120, popularityScore = 4.3,
                ownerUsername = null, isSaved = false, isUpvoted = false, tags = emptyList(),
            ),
            TripModel(
                id = "pub-2", title = "İskoçya Yaylaları (Mock)", coverPhotoUrl = null,
                status = TripStatusDto.Archived, origin = "Londra", originCountry = "BK",
                startDate = "2025-09-01", endDate = "2025-09-10", personCount = 4,
                destinationCount = 3, destinationList = listOf("Edinburgh", "Inverness", "Skye"),
                forkCount = 2, upvoteCount = 5, viewCount = 89, popularityScore = 4.0,
                ownerUsername = null, isSaved = false, isUpvoted = false, tags = emptyList(),
            ),
        )
        return when (TripStatusDto.fromInt(status)) {
            TripStatusDto.Draft -> ApiResult.Success(TripPageModel(trips, 1, 20, trips.size))
            TripStatusDto.Published -> ApiResult.Success(TripPageModel(
                published.filter { it.status != TripStatusDto.Archived }, 1, 20,
                published.count { it.status != TripStatusDto.Archived },
            ))
            TripStatusDto.Archived -> ApiResult.Success(TripPageModel(
                published.filter { it.status == TripStatusDto.Archived }, 1, 20,
                published.count { it.status == TripStatusDto.Archived },
            ))
        }
    }

    private fun fallbackSavedTrips(): ApiResult<SavedTripPageModel> {
        val saved = listOf(
            SavedTripModel(
                tripId = "saved-1", title = "Toskana Bağ Yolu (Mock)", coverPhotoUrl = null,
                username = "gezgin_ayse", profilePhotoUrl = null,
                destinationCount = 3, dayCount = 5, forkCount = 12, upvoteCount = 34,
                viewCount = 230, popularityScore = 4.7, city = "Floransa", country = "İtalya",
                startDate = "2026-05-10", endDate = "2026-05-15",
            ),
            SavedTripModel(
                tripId = "saved-2", title = "Norveç Fiyortları (Mock)", coverPhotoUrl = null,
                username = "nordik_kasif", profilePhotoUrl = null,
                destinationCount = 4, dayCount = 8, forkCount = 8, upvoteCount = 21,
                viewCount = 156, popularityScore = 4.5, city = "Oslo", country = "Norveç",
                startDate = "2026-08-01", endDate = "2026-08-08",
            ),
        )
        return ApiResult.Success(SavedTripPageModel(saved, 1, 30, saved.size))
    }
}
