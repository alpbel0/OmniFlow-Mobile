package com.omniflow.data.remote

import com.omniflow.data.models.trips.BudgetSummaryResponseDto
import com.omniflow.data.models.trips.ChecklistResponseDto
import com.omniflow.data.models.trips.ChecklistUpdateDto
import com.omniflow.data.models.trips.GetMyTripsPageDto
import com.omniflow.data.models.trips.RouteResponseDto
import com.omniflow.data.models.trips.SavedTripsPageDto
import com.omniflow.data.models.trips.TimelineResponseDto
import com.omniflow.data.models.trips.TripResponseDto
import com.omniflow.data.models.trips.UnlockEntryDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TripService {
    @GET("api/v1/Trips")
    suspend fun getMyTrips(
        @Query("status") status: Int? = null,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
    ): GetMyTripsPageDto

    @GET("api/v1/Trips/{id}")
    suspend fun getTripDetail(@Path("id") tripId: String): TripResponseDto

    @GET("api/v1/Trips/{id}/route")
    suspend fun getRoute(@Path("id") tripId: String): RouteResponseDto

    @PUT("api/v1/Trips/{id}/checklist/{itemKey}")
    suspend fun updateChecklistItem(
        @Path("id") tripId: String,
        @Path("itemKey") itemKey: String,
        @Body body: ChecklistUpdateDto,
    )

    @PUT("api/v1/Trips/{id}")
    suspend fun updateTrip(
        @Path("id") tripId: String,
        @retrofit2.http.Body request: Any,
    ): Unit

    @DELETE("api/v1/Trips/{id}")
    suspend fun deleteTrip(@Path("id") tripId: String)

    @POST("api/v1/Trips/{id}/publish")
    suspend fun publishTrip(@Path("id") tripId: String)

    @POST("api/v1/Trips/{id}/archive")
    suspend fun archiveTrip(@Path("id") tripId: String)

    @POST("api/v1/Trips/{id}/unpublish")
    suspend fun unpublishTrip(@Path("id") tripId: String)

    @POST("api/v1/Trips/{id}/unarchive")
    suspend fun unarchiveTrip(@Path("id") tripId: String)

    @POST("api/v1/Trips/{id}/upvote")
    suspend fun upvoteTrip(@Path("id") tripId: String)

    @DELETE("api/v1/Trips/{id}/upvote")
    suspend fun removeUpvote(@Path("id") tripId: String)

    @POST("api/v1/Trips/{id}/save")
    suspend fun saveTrip(@Path("id") tripId: String)

    @DELETE("api/v1/Trips/{id}/save")
    suspend fun unsaveTrip(@Path("id") tripId: String)

    @POST("api/v1/Trips/{id}/fork")
    suspend fun forkTrip(@Path("id") tripId: String): String

    @GET("api/v1/saved-trips")
    suspend fun getSavedTrips(
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 30,
    ): SavedTripsPageDto

    @PUT("api/v1/trips/{tripId}/timeline/entry/{entryId}")
    suspend fun unlockTimelineEntry(
        @Path("tripId") tripId: String,
        @Path("entryId") entryId: String,
        @Body body: UnlockEntryDto,
    )

    @DELETE("api/v1/trips/{tripId}/timeline/entry/{entryId}")
    suspend fun deleteTimelineEntry(
        @Path("tripId") tripId: String,
        @Path("entryId") entryId: String,
    )

    @GET("api/v1/trips/{tripId}/timeline")
    suspend fun getTimeline(@Path("tripId") tripId: String): TimelineResponseDto

    @GET("api/v1/Trips/{id}/budget-summary")
    suspend fun getBudgetSummary(@Path("id") tripId: String): BudgetSummaryResponseDto

    @GET("api/v1/Trips/{id}/checklist")
    suspend fun getChecklist(@Path("id") tripId: String): ChecklistResponseDto
}
