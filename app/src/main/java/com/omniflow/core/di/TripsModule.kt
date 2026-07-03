package com.omniflow.core.di

import com.omniflow.data.remote.TripService
import com.omniflow.data.repository.TripRepository
import com.omniflow.data.repository.TripRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class TripsModule {
    @Binds
    @Singleton
    abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository

    companion object {
        @Provides
        @Singleton
        fun provideTripService(retrofit: Retrofit): TripService =
            retrofit.create(TripService::class.java)
    }
}
