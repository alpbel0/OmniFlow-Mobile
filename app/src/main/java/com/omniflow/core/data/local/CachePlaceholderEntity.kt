package com.omniflow.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_placeholder")
data class CachePlaceholderEntity(
    @PrimaryKey val key: String,
    val payload: String,
)
