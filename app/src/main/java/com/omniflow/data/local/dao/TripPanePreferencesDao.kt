package com.omniflow.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.omniflow.data.local.TripPanePreferencesEntity

@Dao
interface TripPanePreferencesDao {
    @Query("SELECT * FROM trip_pane_preferences WHERE tripId = :tripId LIMIT 1")
    suspend fun get(tripId: String): TripPanePreferencesEntity?

    @Upsert
    suspend fun upsert(entity: TripPanePreferencesEntity)
}