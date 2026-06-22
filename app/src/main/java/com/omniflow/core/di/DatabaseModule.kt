package com.omniflow.core.di

import android.content.Context
import androidx.room.Room
import com.omniflow.core.data.local.OmniFlowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOmniFlowDatabase(@ApplicationContext context: Context): OmniFlowDatabase {
        return Room.databaseBuilder(
            context,
            OmniFlowDatabase::class.java,
            "omniflow.db",
        ).build()
    }
}
