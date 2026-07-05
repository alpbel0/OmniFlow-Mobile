package com.omniflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_pane_preferences")
data class TripPanePreferencesEntity(
    @PrimaryKey val tripId: String,
    val detaylarFraction: Float = 0.30f,
    val mapFraction: Float = 0.30f,
    val displayMode: String = "CATEGORY",
    val selectedDayIndex: Int = 0,
    val landscapeTimelineFraction: Float = 0.40f,
    val landscapeDetaylarFraction: Float = 0.30f,
)