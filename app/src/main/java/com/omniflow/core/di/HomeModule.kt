package com.omniflow.core.di

import com.omniflow.data.remote.HomeService
import com.omniflow.data.repository.HomeRepository
import com.omniflow.data.repository.HomeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {
    @Binds
    @Singleton
    abstract fun bindHomeRepository(implementation: HomeRepositoryImpl): HomeRepository

    companion object {
        @Provides
        @Singleton
        fun provideHomeService(retrofit: Retrofit): HomeService =
            retrofit.create(HomeService::class.java)
    }
}
