package com.omniflow.data.repository

import com.omniflow.core.network.ApiCallExecutor
import com.omniflow.core.network.ApiResult
import com.omniflow.data.mapper.toModel
import com.omniflow.data.models.trips.BudgetSummaryResponseDto
import com.omniflow.data.models.trips.ChecklistResponseDto
import com.omniflow.data.models.trips.ChecklistUpdateDto
import com.omniflow.data.models.trips.SavedTripModel
import com.omniflow.data.models.trips.SavedTripPageModel
import com.omniflow.data.models.trips.TimelineResponseDto
import com.omniflow.data.models.trips.TripModel
import com.omniflow.data.models.trips.TripPageModel
import com.omniflow.data.models.trips.TripStatusDto
import com.omniflow.data.models.trips.UnlockEntryDto
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
            is ApiResult.Success -> if (result.data.data.isEmpty()) fallbackTrips(status) else result
            else -> result
        }
    }

    override suspend fun getTripDetail(tripId: String): ApiResult<TripModel> {
        val result = apiCallExecutor.execute { tripService.getTripDetail(tripId).toModel() }
        return when (result) {
            is ApiResult.Error -> fallbackTripDetail()
            else -> result
        }
    }

    /**
     * ORS rota proxy'si — backend B0.15 (BACKEND_ROADMAP_V2.md) gelene kadar
     * mock yok. Spec istiyor: backend başarısız dönerse "sessizce Kuş Bakışı'na
     * dön" davranışı. Bu metot hiçbir mock fallback yapmaz; ViewModel
     * `ApiResult.Error` aldığında `mapMode = BIRDS_EYE` + `routeUnavailable = true`
     * uygular.
     *
     * Backend B0.15 geldiğinde bu imza korunur, sadece backend gerçek veri döner
     * ve routeUnavailable durumu tetiklenmez.
     */
    override suspend fun getRoute(tripId: String): ApiResult<List<Pair<Double, Double>>> =
        apiCallExecutor.execute {
            tripService.getRoute(tripId).points
                .filter { it.size >= 2 }
                .map { it[1] to it[0] } // GeoJSON [lng, lat] → (lat, lng)
        }

    /**
     * Checklist toggle — backend B0.9 gelene kadar mock fallback yok. Kullanıcı kararı:
     * hata durumunda ViewModel bilerek geri almaz (revert), lokal state kalıcı görünür.
     */
    override suspend fun updateChecklistItem(tripId: String, itemKey: String, isConfirmed: Boolean) =
        apiCallExecutor.execute { tripService.updateChecklistItem(tripId, itemKey, ChecklistUpdateDto(isConfirmed)) }

    override suspend fun updateTrip(tripId: String, request: Any) = apiCallExecutor.execute { tripService.updateTrip(tripId, request) }
    override suspend fun deleteTrip(tripId: String) = apiCallExecutor.execute { tripService.deleteTrip(tripId) }
    override suspend fun publishTrip(tripId: String) = apiCallExecutor.execute { tripService.publishTrip(tripId) }
    override suspend fun archiveTrip(tripId: String) = apiCallExecutor.execute { tripService.archiveTrip(tripId) }
    override suspend fun unpublishTrip(tripId: String) = apiCallExecutor.execute { tripService.unpublishTrip(tripId) }
    override suspend fun unarchiveTrip(tripId: String) = apiCallExecutor.execute { tripService.unarchiveTrip(tripId) }
    override suspend fun upvoteTrip(tripId: String) = apiCallExecutor.execute { tripService.upvoteTrip(tripId) }
    override suspend fun removeUpvote(tripId: String) = apiCallExecutor.execute { tripService.removeUpvote(tripId) }
    override suspend fun saveTrip(tripId: String) = apiCallExecutor.execute { tripService.saveTrip(tripId) }
    override suspend fun unsaveTrip(tripId: String) = apiCallExecutor.execute { tripService.unsaveTrip(tripId) }
    override suspend fun forkTrip(tripId: String) = apiCallExecutor.execute { tripService.forkTrip(tripId) }

    override suspend fun getSavedTrips(page: Int): ApiResult<SavedTripPageModel> {
        val result = apiCallExecutor.execute { tripService.getSavedTrips(pageNumber = page).toModel() }
        return when (result) {
            is ApiResult.Error -> fallbackSavedTrips()
            is ApiResult.Success -> if (result.data.data.isEmpty()) fallbackSavedTrips() else result
            else -> result
        }
    }

    override suspend fun unlockTimelineEntry(tripId: String, entryId: String) =
        apiCallExecutor.execute { tripService.unlockTimelineEntry(tripId, entryId, UnlockEntryDto()) }

    override suspend fun deleteTimelineEntry(tripId: String, entryId: String) =
        apiCallExecutor.execute { tripService.deleteTimelineEntry(tripId, entryId) }

    override suspend fun getTimeline(tripId: String) =
        apiCallExecutor.execute { tripService.getTimeline(tripId) }

    override suspend fun getBudgetSummary(tripId: String) =
        apiCallExecutor.execute { tripService.getBudgetSummary(tripId) }

    override suspend fun getChecklist(tripId: String) =
        apiCallExecutor.execute { tripService.getChecklist(tripId) }

    private fun fallbackTripDetail() = ApiResult.Success(
        TripModel(
            id = "detail-mock-1", title = "Roma & Floransa (Mock)", coverPhotoUrl = null,
            status = TripStatusDto.Published, origin = "İstanbul", originCountry = "Türkiye",
            startDate = "2026-07-15", endDate = "2026-07-22", personCount = 2,
            destinationCount = 3, destinationList = listOf("Roma", "Floransa", "Venedik"),
            forkCount = 5, upvoteCount = 12, viewCount = 340, popularityScore = 4.5,
            ownerId = "mock-user", ownerUsername = "gezgin_test",
            isSaved = false, isUpvoted = false, tags = listOf("italya", "yaz"),
        )
    )

    private fun fallbackTrips(status: Int): ApiResult<TripPageModel> {
        val drafts = listOf(
            mockTrip("draft-1", "Barselona Kaçamağı", TripStatusDto.Draft, null, null, 2, listOf("Barselona", "Madrid"), 0, 0),
            mockTrip("draft-2", "Tokyo Macerası", TripStatusDto.Draft, null, null, 1, listOf("Tokyo", "Kyoto", "Osaka"), 0, 0),
        )
        val pub = listOf(
            mockTrip("pub-1", "Amalfi Sahili Rüyası", TripStatusDto.Published, "2026-07-15", "2026-07-22", 2, listOf("Napoli", "Positano"), 4, 7),
            mockTrip("pub-2", "İskoçya Yaylaları", TripStatusDto.Archived, "2025-09-01", "2025-09-10", 4, listOf("Edinburgh", "Inverness", "Skye"), 2, 5),
        )
        return when (TripStatusDto.fromInt(status)) {
            TripStatusDto.Draft -> ApiResult.Success(TripPageModel(drafts, 1, 20, drafts.size))
            TripStatusDto.Published -> ApiResult.Success(TripPageModel(pub.filter { it.status != TripStatusDto.Archived }, 1, 20, pub.count { it.status != TripStatusDto.Archived }))
            TripStatusDto.Archived -> ApiResult.Success(TripPageModel(pub.filter { it.status == TripStatusDto.Archived }, 1, 20, pub.count { it.status == TripStatusDto.Archived }))
        }
    }

    private fun fallbackSavedTrips() = ApiResult.Success(
        SavedTripPageModel(
            listOf(
                SavedTripModel("saved-1", "Toskana Bağ Yolu", null, "gezgin_ayse", null, 3, 5, 12, 34, 230, 4.7, "Floransa", "İtalya", "2026-05-10", "2026-05-15"),
                SavedTripModel("saved-2", "Norveç Fiyortları", null, "nordik_kasif", null, 4, 8, 8, 21, 156, 4.5, "Oslo", "Norveç", "2026-08-01", "2026-08-08"),
            ), 1, 30, 2,
        )
    )

    private fun mockTrip(
        id: String, title: String, status: TripStatusDto,
        startDate: String?, endDate: String?, personCount: Int,
        dest: List<String>, forkCount: Int, upvoteCount: Int,
    ) = TripModel(
        id = id, title = "$title (Mock)", coverPhotoUrl = null,
        status = status, origin = "İstanbul", originCountry = "Türkiye",
        startDate = startDate, endDate = endDate, personCount = personCount,
        destinationCount = dest.size, destinationList = dest,
        forkCount = forkCount, upvoteCount = upvoteCount, viewCount = 0,
        popularityScore = 0.0, ownerId = null, ownerUsername = null,
        isSaved = false, isUpvoted = false, tags = emptyList(),
    )
}
