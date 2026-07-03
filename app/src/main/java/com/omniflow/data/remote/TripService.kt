package com.omniflow.data.remote

import com.omniflow.data.models.trips.GetMyTripsPageDto
import com.omniflow.data.models.trips.SavedTripsPageDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TripService {
    @GET("api/v1/Trips")
    suspend fun getMyTrips(
        @Query("status") status: Int? = null,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
    ): GetMyTripsPageDto

    @GET("api/v1/saved-trips")
    suspend fun getSavedTrips(
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 30,
    ): SavedTripsPageDto

    @POST("api/v1/Trips/{id}/publish")
    suspend fun publishTrip(@Path("id") tripId: String)
}
