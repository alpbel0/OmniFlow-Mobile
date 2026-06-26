package com.omniflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CachePlaceholderEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class OmniFlowDatabase : RoomDatabase()
