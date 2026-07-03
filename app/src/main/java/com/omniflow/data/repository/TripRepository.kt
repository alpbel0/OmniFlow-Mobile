package com.omniflow.data.repository

import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.trips.SavedTripPageModel
import com.omniflow.data.models.trips.TripModel
import com.omniflow.data.models.trips.TripPageModel

interface TripRepository {
    suspend fun getMyTrips(status: Int, page: Int = 1): ApiResult<TripPageModel>
    suspend fun getSavedTrips(page: Int = 1): ApiResult<SavedTripPageModel>
    suspend fun publishTrip(tripId: String): ApiResult<Unit>
}
