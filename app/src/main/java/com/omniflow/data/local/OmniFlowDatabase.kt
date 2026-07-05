package com.omniflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.omniflow.data.local.dao.TripPanePreferencesDao

@Database(
    entities = [
        CachePlaceholderEntity::class,
        TripPanePreferencesEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class OmniFlowDatabase : RoomDatabase() {
    abstract fun tripPanePreferencesDao(): TripPanePreferencesDao
}