package com.omniflow.data.repository

import com.omniflow.data.local.TripPanePreferencesEntity
import com.omniflow.data.local.dao.TripPanePreferencesDao
import javax.inject.Inject
import javax.inject.Singleton

data class TripPanePreferences(
    val detaylarFraction: Float,
    val mapFraction: Float,
    val displayMode: String,
    val selectedDayIndex: Int,
    val landscapeTimelineFraction: Float = 0.40f,
    val landscapeDetaylarFraction: Float = 0.30f,
)

interface TripPanePreferencesRepository {
    suspend fun get(tripId: String): TripPanePreferences?
    suspend fun save(tripId: String, preferences: TripPanePreferences)
}

@Singleton
class TripPanePreferencesRepositoryImpl @Inject constructor(
    private val dao: com.omniflow.data.local.dao.TripPanePreferencesDao,
) : TripPanePreferencesRepository {

    override suspend fun get(tripId: String): TripPanePreferences? {
        return dao.get(tripId)?.toDomain()
    }

    override suspend fun save(tripId: String, preferences: TripPanePreferences) {
        dao.upsert(
            TripPanePreferencesEntity(
                tripId = tripId,
                detaylarFraction = preferences.detaylarFraction,
                mapFraction = preferences.mapFraction,
                displayMode = preferences.displayMode,
                selectedDayIndex = preferences.selectedDayIndex,
                landscapeTimelineFraction = preferences.landscapeTimelineFraction,
                landscapeDetaylarFraction = preferences.landscapeDetaylarFraction,
            ),
        )
    }

    private fun TripPanePreferencesEntity.toDomain(): TripPanePreferences =
        TripPanePreferences(
            detaylarFraction = detaylarFraction,
            mapFraction = mapFraction,
            displayMode = displayMode,
            selectedDayIndex = selectedDayIndex,
            landscapeTimelineFraction = landscapeTimelineFraction,
            landscapeDetaylarFraction = landscapeDetaylarFraction,
        )
}