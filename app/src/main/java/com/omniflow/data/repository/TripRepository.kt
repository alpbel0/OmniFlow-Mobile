package com.omniflow.data.repository

import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.trips.BudgetSummaryResponseDto
import com.omniflow.data.models.trips.ChecklistResponseDto
import com.omniflow.data.models.trips.SavedTripPageModel
import com.omniflow.data.models.trips.TimelineResponseDto
import com.omniflow.data.models.trips.TripModel
import com.omniflow.data.models.trips.TripPageModel

interface TripRepository {
    suspend fun getMyTrips(status: Int, page: Int = 1): ApiResult<TripPageModel>
    suspend fun getTripDetail(tripId: String): ApiResult<TripModel>
    suspend fun getRoute(tripId: String): ApiResult<List<Pair<Double, Double>>>
    suspend fun updateChecklistItem(tripId: String, itemKey: String, isConfirmed: Boolean): ApiResult<Unit>
    suspend fun updateTrip(tripId: String, request: Any): ApiResult<Unit>
    suspend fun deleteTrip(tripId: String): ApiResult<Unit>
    suspend fun publishTrip(tripId: String): ApiResult<Unit>
    suspend fun archiveTrip(tripId: String): ApiResult<Unit>
    suspend fun unpublishTrip(tripId: String): ApiResult<Unit>
    suspend fun unarchiveTrip(tripId: String): ApiResult<Unit>
    suspend fun upvoteTrip(tripId: String): ApiResult<Unit>
    suspend fun removeUpvote(tripId: String): ApiResult<Unit>
    suspend fun saveTrip(tripId: String): ApiResult<Unit>
    suspend fun unsaveTrip(tripId: String): ApiResult<Unit>
    suspend fun forkTrip(tripId: String): ApiResult<String>
    suspend fun getSavedTrips(page: Int = 1): ApiResult<SavedTripPageModel>
    suspend fun unlockTimelineEntry(tripId: String, entryId: String): ApiResult<Unit>
    suspend fun deleteTimelineEntry(tripId: String, entryId: String): ApiResult<Unit>
    suspend fun getTimeline(tripId: String): ApiResult<TimelineResponseDto>
    suspend fun getBudgetSummary(tripId: String): ApiResult<BudgetSummaryResponseDto>
    suspend fun getChecklist(tripId: String): ApiResult<ChecklistResponseDto>
}
